package ghasemi.abbas.unfollowyab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgLogin;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.api.Utilities;
import ghasemi.abbas.unfollowyab.builder.BuildApp;

public class WebLoginActivity extends AppCompatActivity {
    WebView webView;
    View progressBar;
    AlertDialog alertDialog;
    AlertDialog errdialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static boolean isShowingErrDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(0xffffffff);
            getWindow().setNavigationBarColor(0xfffafafa);
            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        } else {
            getWindow().setStatusBarColor(Color.GRAY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_login);
        alertDialog = new AlertDialog.Builder(this).setView(LayoutInflater.from(this).inflate(R.layout.progress, null)).setCancelable(false).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipe);
        webView = findViewById(R.id.web);
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearHistory();
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    progressBar.setVisibility(View.GONE);
                    dialogErrorWeb(error.getDescription() + " -- Err Code: " + error.getErrorCode());
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                dialogErrorWeb(description + " -- Err Code: " + errorCode);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                boolean login = IgLogin.create(CookieManager.getInstance().getCookie(url));

                if (login) {
                    if (isFinishing()) {
                        return;
                    }
                    alertDialog.show();
                    webView.stopLoading();
                    webView.setVisibility(View.GONE);
                    IgApi.instance().getUserInfo(Utilities.userID(), (status, object) -> {
                        if (isFinishing()) {
                            return;
                        }
                        if (status == null) {
                            try {
                                JSONObject jsonObject = object.getJSONObject("user");
                                SQL.getSql().updateAccount(Utilities.userID(), jsonObject.getString("username"), jsonObject.getString("full_name"), jsonObject.getString("profile_pic_url"), jsonObject.getString("media_count"), jsonObject.getString("follower_count"), jsonObject.getString("following_count"), jsonObject.getString("biography"));
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                                startActivity(new Intent(WebLoginActivity.this, MainActivity.class));
                                finishAffinity();
                            } catch (JSONException e) {
                                //
                            }
                        } else if (!status.serverError) {
                            SQL.getSql().removeAccount();
                            BuildApp.Toast("خطا در دریافت اطلاعات از اینستاگرام");
                            finish();
                        } else {
                            SQL.getSql().removeAccount();
                            BuildApp.Toast("خطا در دریافت اطلاعات از اینستاگرام");
                            finish();
                        }
                    });
                }

            }
        });
        HashMap<String, String> map = new HashMap<>();
        map.put("X-REQUESTED-WITH", "");
        String ug = webView.getSettings().getUserAgentString()
                .replace("; wv", "")
                .replaceFirst("Version/[\\d.]+\\s?", "");
        map.put("User-Agent", ug);
        webView.getSettings().setUserAgentString(ug);
        String url = getUrl();
        CookieManager.getInstance().setCookie(url, "ig_did=" + UUID.randomUUID().toString().toUpperCase() + "; path=/");
        webView.loadUrl(url, map);
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                swipeRefreshLayout.setRefreshing(false);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                webView.reload();
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
            }
        });
    }

    private String getUrl() {
        String s = "https://www.A.B/C";
        return s.replace("A", getName()).replace("B", "com").replace("C", "acco" + "unts/" + "lo" + "gin/");
    }

    private String getName() {
        String[] s = new String[]{"i", "nst", "agr", "am"};
        StringBuilder r = new StringBuilder();
        for (String ss : s) {
            r.append(ss);
        }
        return r.toString();
    }

    private void dialogErrorWeb(String error) {
        if (isShowingErrDialog) {
            return;
        }
        if (isFinishing() || (errdialog != null && errdialog.isShowing())) {
            return;
        }
        errdialog = new AlertDialog.Builder(this).setTitle("وجود مشکل در ارتباط با وبسایت اینستاگرام").setMessage(" اگر اطمینان دارید مشکل از شبکه ی شما نیست، ممکن است وبسایت اینستاگرام موقتا قطع  یا فیلترشده باشد،لذا نیاز می باشد برای لاگین حتما از فیلترشکن استفاده نمایید.\n" + "\n" + "غیر این حالت ممکن است شبکه شما برای مدتی ضعیف و یا امکان برقراری ارتباط با وبسایت اینستاگرام را نداشته باشد، لطفا کمی بعد دوباره تلاش نمایید. \n" + "\n" + error).setPositiveButton("بررسی مجدد", (dialog, which) -> {
            progressBar.setVisibility(View.VISIBLE);
            webView.reload();
        }).setOnDismissListener(dialog -> Snackbar.make(swipeRefreshLayout, "انگشت خود را برای رفریش از بالا به پایین بکشید.", Snackbar.LENGTH_INDEFINITE).setAction("بستن", v -> {
        }).setActionTextColor(Color.YELLOW).show()).setNegativeButton("بیخیال", null).show();
        BuildApp.setCustomFontDialog(errdialog);
        isShowingErrDialog = true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception npe) {
            //
        }
    }
}