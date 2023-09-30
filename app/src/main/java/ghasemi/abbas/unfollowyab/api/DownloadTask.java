package ghasemi.abbas.unfollowyab.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.BuildApp;

public class DownloadTask extends AsyncTask<String, Integer, String> {

    private String name;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog progressDialog;
    private boolean isVideo;

    @SuppressLint("StaticFieldLeak")
    private final Activity activity;

    public DownloadTask(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("SdCardPath")
    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            isVideo = sUrl[1].equals("true");
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            byte[] data = new byte[4096];
            long total = 0;
            int count;
            output = getOutputStream();
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) ((float) total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
//            Log.e("abbas", e.toString());
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        name = String.valueOf(System.currentTimeMillis());
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("در حال دانلود ...");
        progressDialog.show();
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
        progressDialog.getWindow().setWindowAnimations(R.style.anim_dialog);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        progressDialog.setProgress(progress[0]);
    }

    @SuppressLint({"IntentReset", "SdCardPath"})
    @Override
    protected void onPostExecute(final String result) {
        mWakeLock.release();
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            if (result == null) {
                BuildApp.Toast((isVideo ? "ویدئو" : "تصویر") + " با موفقیت در پوشه ای " + ApplicationLoader.getContext().getResources().getString(R.string.folder) + " ذخیره شد.");
            } else {
                BuildApp.Toast("خطائی در دانلود به وجود آمده است.");
            }
        }, 250);
    }

    private String createName() {
        return name + (isVideo ? ".mp4" : ".jpg");
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        ContentResolver resolver = activity.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, isVideo ? "video/mp4" : "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + ApplicationLoader.getContext().getResources().getString(R.string.folder));
        Uri imageUri = resolver.insert(isVideo ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        return resolver.openOutputStream(imageUri);
    }
}