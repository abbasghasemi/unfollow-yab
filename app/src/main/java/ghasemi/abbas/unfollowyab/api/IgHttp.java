package ghasemi.abbas.unfollowyab.api;


import android.net.Uri;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import io.michaelrocks.paranoid.Obfuscate;


@Obfuscate
public class IgHttp {

    public static final String GET = "GET";
    public static final String POST = "POST";
    private String contentEncoding = null;

    String[] requestPOST(String url, Map<String, String> header, String body, HeaderListener headerListener) {
        return requestPOST(url, header, body == null ? null : body.getBytes(StandardCharsets.UTF_8), headerListener);
    }

    String[] requestPOST(String url, Map<String, String> header, byte[] body, HeaderListener headerListener) {
        return connection(POST, url, header, body, headerListener);
    }

    String[] requestGET(String url, Map<String, String> header, HeaderListener headerListener) {
        return connection(GET, url, header, null, headerListener);
    }

    public String[] request(String method, String url, String body, Map<String, String> headers, HeaderListener headerListener) {
        if (method.equals(POST)) {
            return requestPOST(url, headers, body, headerListener);
        }
        return requestGET(url, headers, headerListener);
    }

    private String[] connection(String method, String url, Map<String, String> header, byte[] body, HeaderListener headerListener) {
        String[] response = new String[2];
        try {
            HttpsURLConnection con = createURLConnection(method, url, header, body);
            InputStream inputStream = getInputStream(con, headerListener);
            response[0] = String.valueOf(con.getResponseCode());
            response[1] = parseInputStream(inputStream);
            try {
                con.disconnect();
            } catch (Exception e) {
                //
            }
            return response;
        } catch (Exception var12) {
            //
        }
        return null;
    }

    private HttpsURLConnection createURLConnection(String method, String url, Map<String, String> header, byte[] body) throws IOException {
        URL e = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) e.openConnection();
        con.setRequestMethod(method);
        for (String key : header.keySet()) {
            con.addRequestProperty(key, removeBadCharacter(header.get(key)));
        }
        if (method.equals(POST) && !header.containsKey("Content-Type")) {
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        }
        if (method.equals(POST) && !header.containsKey("Content-Length")) {
            con.addRequestProperty("Content-Length", String.valueOf(body.length));
        }
        con.addRequestProperty("Host", Uri.parse(url).getHost());
        con.setConnectTimeout(20000);
        con.setReadTimeout(20000);
        if (body != null) {
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(body);
            wr.flush();
            wr.close();
        }
        return con;
    }

    private InputStream getInputStream(HttpsURLConnection connection, HeaderListener headerListener) {
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
            if (inputStream == null) {
                inputStream = connection.getErrorStream();
            }
            if (headerListener != null) {
                Map<String, List<String>> headers = connection.getHeaderFields();
                for (String key : headers.keySet()) {
                    if (TextUtils.isEmpty(key)) continue;
                    List<String> values = Objects.requireNonNull(headers.get(key));
                    if (values.isEmpty()) continue;
                    if (key.equalsIgnoreCase("set-cookie") || key.equalsIgnoreCase("cookie")) {
                        for (String cookie : values) {
                            if (!TextUtils.isEmpty(cookie)) {
                                headerListener.onHeader("cookie", removeBadCharacter(cookie));
                            }
                        }
                    } else {
                        String value = values.get(values.size() - 1);
                        if (TextUtils.isEmpty(value)) continue;
                        key = key.toLowerCase();
                        value = removeBadCharacter(value);
                        if ("content-encoding".equals(key)) {
                            contentEncoding = value.toLowerCase();
                        } else {
                            headerListener.onHeader(key, value);
                        }
                    }
                }
            }
        } catch (IOException e1) {
            inputStream = connection.getErrorStream();
        }
        return inputStream;
    }

    private String parseInputStream(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(decompressStream(inputStream)));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        inputStream.close();
        return sb.toString();
    }

    private InputStream decompressStream(InputStream input) throws IOException {
        if ("gzip".equals(contentEncoding)) {
            return new GZIPInputStream(input);
        }
        PushbackInputStream pb = new PushbackInputStream(input, 2); //we need a pushbackstream to look ahead
        byte[] signature = new byte[2];
        int len = pb.read(signature); //read the signature
        pb.unread(signature, 0, len); //push back the signature to the stream
        if (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b) //check if matches standard gzip magic number
            return new GZIPInputStream(pb);
        else
            return pb;
    }

    private String removeBadCharacter(String str) {
        if (TextUtils.isEmpty(str)) return "";
        return str.replaceAll("([\\r\\n\\t])", "");
    }

    public interface HeaderListener {
        void onHeader(String key, String value);
    }
}