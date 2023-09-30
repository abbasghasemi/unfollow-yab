package ghasemi.abbas.unfollowyab;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.farasource.billing.Payment;
import com.farasource.billing.communication.OnPaymentResultListener;
import com.farasource.billing.util.Inventory;
import com.farasource.billing.util.Purchase;
import com.farasource.billing.util.TableCodes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.api.CheckNetworkState;
import ghasemi.abbas.unfollowyab.api.DownloadTask;
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.api.IgUser;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.api.Utilities;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.IgTask;
import ghasemi.abbas.unfollowyab.builder.StarApp;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GradientButton;
import ghasemi.abbas.unfollowyab.components.Permission;
import ghasemi.abbas.unfollowyab.components.PostsView;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.ViewPager;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;
import ghasemi.abbas.unfollowyab.firebase.messaging.Notification;
import ghasemi.abbas.unfollowyab.firebase.messaging.ParsCustomContent;
import ghasemi.abbas.unfollowyab.fragment.Account;
import ghasemi.abbas.unfollowyab.fragment.BaseFragment;
import ghasemi.abbas.unfollowyab.fragment.FAQ;
import ghasemi.abbas.unfollowyab.fragment.MainFollowers;
import ghasemi.abbas.unfollowyab.fragment.MainPosts;
import ghasemi.abbas.unfollowyab.fragment.PrivacyPolicy;
import ghasemi.abbas.unfollowyab.fragment.Search;
import ghasemi.abbas.unfollowyab.fragment.Settings;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class MainActivity extends AppCompatActivity {

    public static boolean isRunning = false;

    @SuppressLint("StaticFieldLeak")
    public static Payment payment;
    AlertDialog alertDialog;
    private long time;

    private TextView pageTitle;
    private DrawerLayout drawerLayout;
    private AppCompatImageView buttonLeft, buttonRight;
    private final View.OnClickListener clickDrawerLayout = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    };
    private FragmentContainerView fragmentRoot;
    private BottomNavigationView navigationView;
    private RoundedImageView profile;
    private TextView username;
    private TextView bio;
    private static final ArrayList<Bundle> cashed = new ArrayList<>();

    private long timeUserInfo = System.currentTimeMillis() / 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(0xffffffff);
            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            windowInsetsController.setAppearanceLightNavigationBars(true);
        } else {
            getWindow().setStatusBarColor(Color.GRAY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkIgTask();
//        startService(new Intent(this, ClipboardService.class));
        drawerLayout = findViewById(R.id.drawerLayout);
        fragmentRoot = findViewById(R.id.fragmentRoot);
        buttonRight = findViewById(R.id.buttonRight);
        buttonLeft = findViewById(R.id.buttonLeft);
        pageTitle = findViewById(R.id.title);
        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.user_bio);
        buttonRight.setOnClickListener(clickDrawerLayout);
        buttonLeft.setOnClickListener(view -> onBackPressed());

        navigationView = findViewById(R.id.navigationBar);
        navigationView.setOnItemSelectedListener(item -> {
            checkUserInfo();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.followerItem) {
                transaction.replace(R.id.fragmentRoot, new MainFollowers());
            } else {
                transaction.replace(R.id.fragmentRoot, new MainPosts());
            }
            transaction.commit();
            ParsCustomContent.run(this);
            return true;
        });
        navigationView.setSelectedItemId(R.id.followerItem);

        findViewById(R.id.accounts).setOnClickListener(new OnDrawerItemClick("accounts"));
        findViewById(R.id.settings).setOnClickListener(new OnDrawerItemClick("settings"));
        findViewById(R.id.search).setOnClickListener(new OnDrawerItemClick("search"));
        findViewById(R.id.downloader).setOnClickListener(new OnDrawerItemClick("downloader"));
        findViewById(R.id.star).setOnClickListener(new OnDrawerItemClick("star"));
        findViewById(R.id.share).setOnClickListener(new OnDrawerItemClick("share"));
        findViewById(R.id.telegram).setOnClickListener(new OnDrawerItemClick("telegram"));
        findViewById(R.id.instagram).setOnClickListener(new OnDrawerItemClick("instagram"));
        findViewById(R.id.privacyPolicy).setOnClickListener(new OnDrawerItemClick("privacyPolicy"));
        findViewById(R.id.faq).setOnClickListener(new OnDrawerItemClick("faq"));
        findViewById(R.id.otherApps).setOnClickListener(new OnDrawerItemClick("otherApps"));
        if (Store.data().getBool("u_a_upgrade")) {
            findViewById(R.id.upgrade).setVisibility(View.GONE);
        } else {
            findViewById(R.id.upgrade).setOnClickListener(new OnDrawerItemClick("upgrade"));
        }

        TextView appVersion = findViewById(R.id.appVersion);
        appVersion.setText("V" + BuildConfig.VERSION_NAME + "β - " + BuildConfig.FLAVOR);

        onState();

        if (Store.data().getInt("updateTarget") == BuildConfig.VERSION_CODE) {
            findViewById(R.id.update).setVisibility(View.VISIBLE);
            findViewById(R.id.update).setOnClickListener(new OnDrawerItemClick("update"));
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.alert_update);
            dialog.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
            dialog.findViewById(R.id.update).setOnClickListener(v -> {
                Intent update = new Intent(Intent.ACTION_VIEW);
                update.setData(Uri.parse("https://" + BuildConfig.FLAVOR + ".ir/app/" + BuildConfig.APPLICATION_ID));
                startActivity(update);
                dialog.dismiss();
            });
            TextView message = dialog.findViewById(R.id.message);
            String update = Store.data().getString("updateMessage");
            if (!TextUtils.isEmpty(update)) {
                message.setText(update);
            }
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
            dialog.getWindow().setWindowAnimations(R.style.anim_dialog);
        } else if (!Store.data().getBool("rate_app")) {
            int loginCount = Store.data().getInt("loginCount");
            if (loginCount % 3 == 1) {
                new StarApp(this).show();
            }
            Store.data().putInt("loginCount", loginCount + 1);
        } else if (!new Notification().areNotificationsEnabled(this)) {
            int loginCount = Store.data().getInt("loginCount");
            if (loginCount > 8) {
                loginCount = 0;
                new Permission(this, v1 -> {
                    Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                    startActivity(intent);
                }, "مجوز notification", "برنامه برای اطلاع رسانی ها، بروزرسانی ها و فعالیت های برنامه، نیاز به دسترسی اعلان دارد.\nبا صدور مجوز اعلان سریع تر از دیگران با خبر شوید.", R.drawable.ic_round_notifications_24);
            }
            Store.data().putInt("loginCount", loginCount + 1);
        }
    }

    private void onState() {
        Glide.with(this).load(Store.data().getString("profile_pic_url")).into(profile);
        username.setText(Store.data().getString(Store.data().getString("full_name").isEmpty() ? "username" : "full_name"));
        bio.setText(Store.data().getString("biography"));
    }

    public void reState() {
        cashed.clear();
        try {
            navigationView.setSelectedItemId(R.id.followerItem);
            onState();
        } catch (Exception e) {
            //
        }
        checkIgTask();
    }

    private void checkIgTask() {
        if (isFinishing()) {
            return;
        }
        if (null == IgUser.currentIgUser) IgUser.initial();
        if (IgUser.currentIgUser.status != -1) {
            new IgTask(this, IgUser.currentIgUser);
            IgUser.currentIgUser.confirmStatus();
            IgUser.currentIgUser.status = -1;
        }
    }

    public void startFragment(BaseFragment baseFragment) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        baseFragment.setPageTitle(pageTitle);
        baseFragment.onCreateButtonRight(buttonRight);
        if (fragmentRoot.getPaddingBottom() != 0) fragmentRoot.setPadding(0, 0, 0, 0);
        if (navigationView.getVisibility() != View.INVISIBLE)
            navigationView.setVisibility(View.INVISIBLE);
        buttonLeft.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentRoot, baseFragment).addToBackStack("back").commit();
    }

    private String getMarket() {
        return BuildConfig.FLAVOR;
    }

    private void download() {
        View view = getLayoutInflater().inflate(R.layout.alert_download, null);
        alertDialog = new AlertDialog.Builder(MainActivity.this).setView(view).show();
        BuildApp.setCustomFontDialog(alertDialog);
        final EditText editText = view.findViewById(R.id.url);
        view.findViewById(R.id.download).setOnClickListener(v -> {
            String url = editText.getText().toString();
            if (TextUtils.isEmpty(url)) {
                BuildApp.Toast("آدرس نمی تواند خالی باشد.");
            } else if (url.startsWith("https://instagram.com/p/") || url.startsWith("https://www.instagram.com/p/") || url.startsWith("https://instagram.com/tv/") || url.startsWith("https://www.instagram.com/tv/") || url.startsWith("https://instagram.com/igtv/") || url.startsWith("https://www.instagram.com/igtv/") || url.startsWith("https://instagram.com/reel/") || url.startsWith("https://www.instagram.com/reel/")) {
                alertDialog.dismiss();
                String[] part = url.split("/");
                if (CheckNetworkState.isOnline() && part.length > 4 && !part[4].isEmpty()) {
                    BuildApp.Toast("چند لحظه صبر کنید...");
                    getMediaByCode(part[4]);
                } else {
                    BuildApp.Toast("خطا در برقراری ارتباط.");
                }
            } else {
                BuildApp.Toast("آدرس اشتباه است.");
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(v -> alertDialog.dismiss());
    }

    private void getMediaByCode(String code) {
        IgApi.instance().mediaSearch(code, (status, object) -> {
            if (status == null) {
                try {
                    object = object.getJSONArray("items").getJSONObject(0);
                    String text = "";
                    try {
                        text = object.getJSONObject("caption").getString("text");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (text.isEmpty()) {
                        text = "این پست بدون متن می باشد.";
                    }
                    JSONObject result = new JSONObject();
                    result.put("text", text);
                    if (object.has("image_versions2")) {
                        result.put("is_slider", false);
                        JSONArray jSONArray2 = object.getJSONObject("image_versions2").getJSONArray("candidates");
                        result.put("display_url", jSONArray2.getJSONObject(0).getString("url"));
                        if (object.has("video_versions")) {
                            String video = object.getJSONArray("video_versions").getJSONObject(0).getString("url");
                            result.put("video_url", video);
                            result.put("is_video", true);
                        } else {
                            result.put("is_video", false);
                        }
                    } else if (object.has("carousel_media")) {
                        result.put("is_slider", true);
                        JSONArray jSONArray2 = object.getJSONArray("carousel_media");
                        JSONArray array1 = new JSONArray();
                        for (int i = 0; i < jSONArray2.length(); i++) {
                            JSONObject result1 = new JSONObject();
                            JSONObject object2 = jSONArray2.getJSONObject(i);
                            JSONArray jsonArray3 = object2.getJSONObject("image_versions2").getJSONArray("candidates");
                            result1.put("display_url", jsonArray3.getJSONObject(0).getString("url"));
                            if (object2.has("video_versions")) {
                                String video = object2.getJSONArray("video_versions").getJSONObject(0).getString("url");
                                result1.put("video_url", video);
                                result1.put("is_video", true);
                            } else {
                                result1.put("is_video", false);
                            }
                            array1.put(result1);
                        }
                        result.put("edges", array1);
                    }
                    ////////////////////////////////////////////////////////
                    final View view = getLayoutInflater().inflate(R.layout.post_download, null);
                    final GradientButton download = view.findViewById(R.id.download);
                    ViewPager viewPager = view.findViewById(R.id.viewPager);
                    ScrollingPagerIndicator indicator = view.findViewById(R.id.indicator);
                    final PostsView postsView = new PostsView(viewPager, indicator, download, result);
                    TextView textView = view.findViewById(R.id.text);
                    textView.setText(result.getString("text"));
                    view.findViewById(R.id.cancel).setOnClickListener(v -> alertDialog.dismiss());
                    alertDialog = new AlertDialog.Builder(MainActivity.this).setView(view).setCancelable(false).show();
                    BuildApp.setCustomFontDialog(alertDialog);
                    download.setOnClickListener(v -> new DownloadTask(MainActivity.this).execute(postsView.dl(), postsView.type()));

                } catch (JSONException e) {
                    //
                }
            } else if (!status.serverError) {
                if (!IgResponse.showDialogMessage(MainActivity.this, status)) {
                    BuildApp.Toast("خطا در دریافت اطلاعات");
                }
            } else {
                BuildApp.Toast("خطا در دریافت اطلاعات");
            }
        });
    }

    public boolean popFragment() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            Fragment fragment = null;
            if (getSupportFragmentManager().getFragments().size() > 1) {
                fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 2);
            }
            getSupportFragmentManager().popBackStack();
            if (count == 1) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                fragmentRoot.setPadding(0, 0, 0, BuildApp.dp(60));
                navigationView.setVisibility(View.VISIBLE);
                buttonRight.setImageResource(R.drawable.ic_round_menu_24);
                buttonRight.setOnClickListener(clickDrawerLayout);
                buttonRight.setVisibility(View.VISIBLE);
                pageTitle.setText(R.string.app_name);
                buttonLeft.setVisibility(View.INVISIBLE);
            } else {
                if (fragment instanceof BaseFragment) {
                    BaseFragment baseFragment = (BaseFragment) fragment;
                    baseFragment.setPageTitle(pageTitle);
                    baseFragment.onCreateButtonRight(buttonRight);
                    baseFragment.onResumeFragment();
                } else {
                    pageTitle.setText("");
                    buttonRight.setVisibility(View.INVISIBLE);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
            return;
        }

        if (popFragment()) {
            return;
        }

        onBack();
    }

    private void onBack() {
        long time = System.currentTimeMillis() - this.time;
        if (2000 >= time) {
            super.onBackPressed();
        } else {
            this.time = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (payment != null) {
            payment.dispose();
            payment = null;
        }
        SQL.getSql().close();
        isRunning = false;
    }

    public class OnDrawerItemClick implements View.OnClickListener {

        private final String Tag;
        Intent intent = new Intent(Intent.ACTION_VIEW);

        public OnDrawerItemClick(String tag) {
            Tag = tag;
        }

        @Override
        public void onClick(View view) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            switch (Tag) {
                case "accounts":
                    startFragment(new Account());
                    break;
                case "settings":
                    startFragment(new Settings());
                    break;
                case "search":
                    startFragment(new Search());
                    break;
                case "downloader":
                    download();
                    break;
                case "star":
                    Intent rate = new Intent(BuildConfig.FLAVOR.equals("cafebazaar") ? Intent.ACTION_EDIT : Intent.ACTION_VIEW);
                    String uri;
                    if (BuildConfig.FLAVOR.equals("cafebazaar")) {
                        uri = "bazaar://details?id=" + getPackageName();
                    } else {
                        uri = "myket://comment?id=" + getPackageName();
                    }
                    rate.setData(Uri.parse(uri));
                    try {
                        startActivity(rate);
                    } catch (Exception e) {
                        BuildApp.Toast("ابتدا اپ استور " + BuildConfig.FLAVOR + " را نصب نمایید.");
                    }
                    break;
                case "update":
                    Intent update = new Intent(Intent.ACTION_VIEW);
                    update.setData(Uri.parse("https://" + BuildConfig.FLAVOR + ".ir/app/" + BuildConfig.APPLICATION_ID));
                    try {
                        startActivity(update);
                    } catch (Exception e) {
                        BuildApp.Toast(update.getData().toString());
                    }
                    break;
                case "share":
                    Intent send = new Intent(Intent.ACTION_SEND);
                    send.setType("text/*");
                    send.putExtra(Intent.EXTRA_TEXT, "دانلود رایگان آنفالویاب قدرتمند اینستاگرام \n " + "https://" + getMarket() + ".ir/app/" + getPackageName());
                    send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(Intent.createChooser(send, "برنامه ای را جهت ارسال انتخاب نمائید."));
                    } catch (Exception e) {
                        BuildApp.Toast(send.getData().toString());
                    }
                    break;
                case "telegram":
                    intent.setData(Uri.parse("https://t.me/farasource"));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        BuildApp.Toast("@farasource");
                    }
                    break;
                case "instagram":
                    intent.setData(Uri.parse("https://instagram.com/farasource/"));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        BuildApp.Toast("farasource");
                    }
                    break;
                case "privacyPolicy":
                    startFragment(new PrivacyPolicy());
                    break;
                case "upgrade":
                    MainActivity.canUseItem(MainActivity.this);
                    break;
                case "faq":
                    startFragment(new FAQ());
                    break;
                case "otherApps": {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(BuildConfig.FLAVOR.equals("cafebazaar") ? "https://cafebazaar.ir/developer/654337025886" : "https://myket.ir/developer/dev-74572"));
                        startActivity(intent);
                    } catch (Exception e) {
                        //
                    }
                }
                break;
            }
        }
    }

    private static String getSDK() {
        if (BuildConfig.FLAVOR.equals("cafebazaar")) {
            return "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDNOcGXLm6IzUNS+B+3rdFtZHIEvx5IYxxjXTZOjP9bWUrBOzDJZOYNEPrUapMjMrTlK3ALgFqc+VB8WRvR4MkEt/o8X1z8jh3/fd4kjA+DGwdUtpamjLX11MaxbS/Tqva0tddr8AUn7AgnXrKJBqyjRgT2pzZJm3x8csqJaTpljl094XmbYJrqjrAhKwLg6CXMUZKX6pgfwbXJM9XcA+nWZo0aVdBClhrKpsdSIvECAwEAAQ==";
        } else {
            return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChpVeqbC46MSEbr51TkHDmqmvPcsmKbLIU6Xvo6LRuMn+mdloGi6fQg4NvH/8rn6JsByN7+qPzpak4ESrnHmHzXFrieS4ekq9c3Esv/SopYoap2lxv3fMbnZNNLLTr2nz2xZZngdW071Q6pk3wrI1YaPnbmKJ7I5nIXAZgAwWh+wIDAQAB";
        }
    }

    public static boolean autoFollow;

    public static boolean canUseItem(MainActivity activity) {
        if (Store.data().getBool("u_a_upgrade")) {
            return true;
        }
        initPayment(activity);
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.alert_upgrade);
        dialog.findViewById(R.id.upgrade).setOnClickListener(v -> {
            if (MainActivity.payment == null) {
                initPayment(activity);
            } else {
                MainActivity.payment.launchPayment("level_3");
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
        dialog.getWindow().setWindowAnimations(R.style.anim_dialog);
        return false;
    }

    private static void initPayment(MainActivity activity) {
        if (MainActivity.payment == null) {
            MainActivity.payment = new Payment(activity.getActivityResultRegistry(), activity, getSDK());
            MainActivity.payment.setOnPaymentResultListener(new OnPaymentResultListener() {
                @Override
                public void onBillingSuccess(Purchase purchase) {
                    Store.data().putBool("u_a_upgrade", true);
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.alert_success);
                    dialog.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
                    dialog.getWindow().setWindowAnimations(R.style.anim_dialog);
                    try {
                        activity.findViewById(R.id.upgrade).setVisibility(View.GONE);
                    } catch (Exception e) {
                        //
                    }
                }

                @Override
                public void onConsumeFinished(Purchase purchase, boolean b) {
                }

                @Override
                public void onBillingStatus(int i) {
                    if (i == TableCodes.NO_NETWORK) {
                        BuildApp.Toast("اینترنت در دسترس نیست.");
                    } else if (i == TableCodes.MARKET_NOT_INSTALLED) {
                        BuildApp.Toast("ابتدا اپ استور " + BuildConfig.FLAVOR + " را نصب نمایید.");
                    } else if (i == TableCodes.SETUP_FAILED) {
                        payment.dispose();
                        payment = null;
                        BuildApp.Toast("ابتدا یکبار مارکت بازار یا مایکت را باز نمایید.");
                    } else if (i == TableCodes.PAYMENT_FAILED) {
                        new Handler().postDelayed(() -> BuildApp.Toast("پرداخت ناموفق بود، لطفا مجددا تلاش نمایید."), 1500);
                    }
                }

                @Override
                public void onQueryInventoryFinished(Inventory inventory) {
                }
            });
        }
    }

    public void reOrderAccount() {
        if (SQL.getSql().isUserLogin()) {
            IgUser.initial();
            boolean finish = popFragment();
            if (finish) {
                new Handler().postDelayed(this::popFragment, 500);
            }
            reState();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    public static Bundle foundCashed(String pk) {
        for (Bundle b : cashed) {
            if (b.getString("pk").equals(pk)) return b;
        }
        return null;
    }

    public static void addCashed(Bundle bundle) {
        cashed.add(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        ParsCustomContent.run(this);
        IgTask.endWL();
        checkUserInfo();
    }

    private void checkUserInfo() {
        long now = System.currentTimeMillis() / 1000;
        if (null != IgUser.currentIgUser && now - timeUserInfo > 60) {
            timeUserInfo = now;
            IgApi.instance().getUserInfo(IgUser.currentIgUser.userID, (status, object) -> {
                try {
                    JSONObject jsonObject = object.getJSONObject("user");
                    SQL.getSql().updateAccount(Utilities.userID(), jsonObject.getString("username"), jsonObject.getString("full_name"), jsonObject.getString("profile_pic_url"), jsonObject.getString("media_count"), jsonObject.getString("follower_count"), jsonObject.getString("following_count"), jsonObject.getString("biography"));
                    onState();
                } catch (Exception e) {
                    //
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IgTask.startWL(this);
    }
}
