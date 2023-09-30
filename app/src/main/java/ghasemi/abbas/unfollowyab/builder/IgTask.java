package ghasemi.abbas.unfollowyab.builder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.api.CheckNetworkState;
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.api.IgUser;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.api.Utilities;
import ghasemi.abbas.unfollowyab.components.ProgressDialog;

public class IgTask {

    private ProgressDialog progressDialog;
    private String max_id = "", c_max_id = "";
    private OnUpdateAccount onUpdateAccount;
    private int countPosts = 1, countFollowers = 0, countFollowing = 0;
    private int posts, followers, following;
    private ArrayList<Bundle> bundles;
    private final ArrayList<Bundle> orders = new ArrayList<>();
    private Bundle order;
    private int blocked = 0;
    //
    private Handler handler;
    private static int COUNTER = 0;
    private static final int UPDATE = COUNTER++;
    private static final int UPDATE_PROGRESS = COUNTER++;
    private static final int UPDATE_AND_FINISH = COUNTER++;
    private static final int JUST_FINISH = COUNTER++;
    private static final int JSON_EXCEPTION = COUNTER++;
    private static final int DIALOG_LOGIN_REQUEST = COUNTER++;
    private static final int DIALOG_CHALLENGE_REQUEST = COUNTER++;
    private static final int ERROR_MSG = COUNTER++;
    private static final int SHOW_DIALOG = COUNTER++;
    //
    private UserStaticThread userStaticThread;

    private static PowerManager.WakeLock wakeLock;
    public static boolean isStarted;
    private final Activity mActivity;
    private int countUnknownError;

    public IgTask(Activity activity) {
        mActivity = activity;
        bundles = SQL.getSql().getUserForCheck();
        if (bundles.isEmpty()) {
            return;
        }
        isStarted = true;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("درحال بررسی وضعیت کاربران از دست رفته، برای بررسی در پس زمینه 'غیرفعال' را لمس کنید، لطفا تا اطلاع پایان بررسی اینترنت خود را روشن نگه دارید.");
        progressDialog.setButton(v -> {
            if (progressDialog != null) {
                isStarted = false;
                endWL();
                progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                progressDialog.dismiss();
                progressDialog = null;
            }
        });
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new Handler(msg -> {
            if (msg.arg1 == UPDATE) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setProgress(msg.arg1);
                }
            } else {
                if (progressDialog != null && progressDialog.isShowing()) {
                    isStarted = false;
                    endWL();
                    progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                if (userStaticThread != null) {
                    userStaticThread.close();
                    userStaticThread = null;
                }
                BuildApp.Toast("بررسی وضعیت کاربران انجام شد.");
            }
            return true;
        });
        userStaticThread = new UserStaticThread();
        userStaticThread.start();
    }

    public IgTask(Activity activity, OnUpdateAccount onUpdateAccount) {
        mActivity = activity;
        this.onUpdateAccount = onUpdateAccount;
        isStarted = true;
        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Handler.Callback callback = msg -> {
            if (msg.what == UPDATE) {
                if (progressDialog == null || !progressDialog.isShowing()) {
                    finish();
                    return false;
                }
                if (msg.arg2 == UPDATE_PROGRESS) {
                    progressDialog.setProgress(msg.arg1);
                }
                if (msg.obj != null) {
                    progressDialog.setMessage(msg.obj.toString());
                }
                return true;
            } else if (msg.what == UPDATE_AND_FINISH) {
                new Handler().postDelayed(this::update, 500);
                return true;
            } else if (msg.what == JUST_FINISH) {
                BuildApp.Toast("خطا در برقرای ارتباط");
                finish();
                return true;
            } else if (msg.what == ERROR_MSG) {
                if (msg.arg1 == JUST_FINISH && msg.arg2 == SHOW_DIALOG) {
                    finish();
                    BuildApp.setCustomFontDialog(new AlertDialog.Builder(activity)
                            .setMessage(msg.obj.toString())
                            .setTitle(ApplicationLoader.getContext().getString(R.string.app_name))
                            .setCancelable(false)
                            .setPositiveButton("باشه", null)
                            .show());
                } else {
//                        BuildApp.Toast(msg.obj.toString());
                }
                return true;
            } else if (msg.what == JSON_EXCEPTION) {
                finish();
                IgTask.this.onUpdateAccount.error(msg.obj.toString());
                return true;
            } else if (msg.what == DIALOG_LOGIN_REQUEST) {
                finish();
                IgResponse.loginRequired(activity);
                return true;
            } else if (msg.what == DIALOG_CHALLENGE_REQUEST) {
                finish();
                IgResponse.challengeRequired(activity);
                return true;
            }
            return false;
        };
        handler = new Handler(callback);
        if (onUpdateAccount.isHomePage()) {
            getBlocked();
        } else {
            getInfo();
        }
    }

    public IgTask(Activity activity, IgUser user) {
        mActivity = activity;
        if (user.loginRequired) {
            IgResponse.loginRequired(activity);
            return;
        }
        if (user.status == 0) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("در حال دریافت اطلاعات حساب");
            progressDialog.show();
            progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            test1(0);
        } else {
            test2(0);
        }
    }

    private void test1(int index) {
        int max = 26;
        if (index < max && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setProgress((index + 1) * 100 / max);
        }
        switch (index) {
            case 0:
                IgApi.instance().zrToken2((status, object) -> {
                });
                break;
            case 1:
                IgApi.instance().bGetAccountFamily((status, object) -> {
                });
                break;
            case 2:
                IgApi.instance().bLauncherSync3((status, object) -> {
                });
                break;
            case 3:
                IgApi.instance().bSync2((status, object) -> {
                });
                break;
            case 4:
                IgApi.instance().bBanyan((status, object) -> {
                });
                break;
            case 5:
                IgApi.instance().bReelsTray(true, (status, object) -> {
                });
                break;
            case 6:
                IgApi.instance().notifications((status, object) -> {
                });
                break;
            case 7:
                IgApi.instance().news((status, object) -> {
                });
                break;
            case 8:
                IgApi.instance().fetchConfig((status, object) -> {
                });
                break;
            case 9:
                IgApi.instance().bootstrap((status, object) -> {
                });
                break;
            case 10:
                IgApi.instance().arlinkDownloadInfo((status, object) -> {
                });
                break;
            case 11:
                IgApi.instance().blocked((status, object) -> {
                });
                break;
            case 12:
                IgApi.instance().getCooldowns((status, object) -> {
                });
                break;
            case 13:
                IgApi.instance().inbox((status, object) -> {
                });
                break;
            case 14:
                IgApi.instance().getPresenceDisabled((status, object) -> {
                });
                break;
            case 15:
                IgApi.instance().batchFetch((status, object) -> {
                });
                break;
            case 16:
                IgApi.instance().hasInteropUpgraded((status, object) -> {
                });
                break;
            case 17:
                IgApi.instance().getPresence((status, object) -> {
                });
                break;
            case 18:
                IgApi.instance().processContactPointSignals((status, object) -> {
                });
                break;
            case 19:
                IgApi.instance().storeClientPushPermissions((status, object) -> {
                });
                break;
            case 20:
                IgApi.instance().getLinkageStatus((status, object) -> {
                });
                break;
            case 21:
                IgApi.instance().writeSupportedCapabilities((status, object) -> {
                });
                break;
            case 22:
                IgApi.instance().logResurrectAttribution((status, object) -> {
                });
                break;
            case 23:
                IgApi.instance().banyan((status, object) -> {
                });
                break;
            case 24:
                IgApi.instance().userXpostingDestination((status, object) -> {
                });
                break;
            case 25:
                IgApi.instance().wwwGraphQL2((status, object) -> {
                });
                break;
            default:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                return;
        }
        BuildApp.runOnUIThread(() -> test1(index + 1), 200);
    }

    private void test2(int index) {
        int max = 13;
        if (index < max && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setProgress((index + 1) * 100 / max);
        }
        switch (index) {
            case 0:
                IgApi.instance().bReelsTray(false, (status, object) -> {
                });
                break;
            case 1:
                IgApi.instance().notifications((status, object) -> {
                });
                break;
            case 2:
                IgApi.instance().bLauncherSync3((status, object) -> {
                });
                break;
            case 3:
                IgApi.instance().bSync2((status, object) -> {
                });
                break;
            case 4:
                IgApi.instance().batchFetch((status, object) -> {
                });
                break;
            case 5:
                IgApi.instance().sync2((status, object) -> {
                });
                break;
            case 6:
                IgApi.instance().bGetAccountFamily((status, object) -> {
                });
                break;
            case 7:
                IgApi.instance().bBanyan((status, object) -> {
                });
                break;
            case 8:
                IgApi.instance().fetchConfig((status, object) -> {
                });
                break;
            case 9:
                IgApi.instance().getPresence((status, object) -> {
                });
                break;
            case 10:
                IgApi.instance().inbox((status, object) -> {
                });
                break;
            case 11:
                IgApi.instance().notifications((status, object) -> {
                });
                break;
            case 12:
                if (new SecureRandom().nextBoolean()) {
                    IgApi.instance().profileArchiveBadge((status, object) -> {
                    });
                } else {
                    IgApi.instance().getViewableStatuses((status, object) -> {
                    });
                }
                break;
            default:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                return;
        }
        BuildApp.runOnUIThread(() -> test2(index + 1), 100);
    }

    private void countResetError() {
        countUnknownError = 0;
    }

    private boolean countUnknownError() {
        countUnknownError++;
        if (countUnknownError > 2) {
            if (handler != null) {
                handler.obtainMessage(ERROR_MSG, JUST_FINISH, SHOW_DIALOG, "پاسخی از اینستاگرام دریافت نشد.\n\nلطفا کارهایی زیر را بررسی نمایید:\n- ورود مجدد به این حساب در برنامه، ممکن است نیازمند لاگین مجدد هستید.\n- بررسی شبکه و تلاش در دقایقی دیگر\n- اگر کماکان مشکل داشتید با پشتیبانی تماس حاصل نمایید.").sendToTarget();
            }
            return true;
        }
        return false;
    }

    private void staticUser(final Bundle bundle) {
        IgApi.instance().staticUserThread(bundle.getString("pk"), (status, object) -> {
            blocked++;
            if (handler == null) {
                return;
            }
            handler.obtainMessage(UPDATE, (int) ((float) blocked / bundles.size() * 100), UPDATE_PROGRESS).sendToTarget();
            if (blocked >= bundles.size()) {
                handler.obtainMessage(JUST_FINISH).sendToTarget();
            }
            try {
                SQL.getSql().setStatus(bundle.getString("id"), object.getString("status"));
            } catch (JSONException e) {
                //
            }
        });
    }

    private void getBlocked() {
        IgApi.instance().getBlocked((status, object) -> {
            if (status == null) {
                try {
                    JSONArray array = object.getJSONArray("user_ids");
                    Store.data().putString("c_b_l_u_l_" + Utilities.userID(), String.valueOf(array.length()));
                } catch (Exception e) {
                    //
                }
                if (handler != null) {
                    handler.obtainMessage(UPDATE, 1, UPDATE_PROGRESS, null).sendToTarget();
                }
                BuildApp.runOnUIThread(this::getInfo, 1000 * (new Random().nextInt(2) + 1));
            } else if (!status.serverError) {
                check(status, this::getInfo);
            } else {
                getInfo();
            }
        });
    }

    private void getInfo() {
        if (handler == null) {
            return;
        }
        IgApi.instance().getUserInfo(Utilities.userID(), (status, object) -> {
            if (status == null) {
                countResetError();
                try {
                    JSONObject jsonObject = object.getJSONObject("user");
                    if (jsonObject.has("full_name")) {
                        SQL.getSql().updateAccount(Utilities.userID(), jsonObject.getString("username"),
                                jsonObject.getString("full_name"), jsonObject.getString("profile_pic_url"),
                                jsonObject.getString("media_count"), jsonObject.getString("follower_count"),
                                jsonObject.getString("following_count"), jsonObject.getString("biography"));
                        Store.data().putString("username", jsonObject.getString("username"));
                        Store.data().putString("full_name", jsonObject.getString("full_name"));
                        Store.data().putString("profile_pic_url", jsonObject.getString("profile_pic_url"));
                        Store.data().putString("media_count", jsonObject.getString("media_count"));
                        Store.data().putString("follower_count", jsonObject.getString("follower_count"));
                        Store.data().putString("following_count", jsonObject.getString("following_count"));
                        Store.data().putString("biography", jsonObject.getString("biography"));
                        if (handler != null)
                            handler.obtainMessage(UPDATE, 2, UPDATE_PROGRESS, null).sendToTarget();
                        if (onUpdateAccount.isHomePage()) {
                            followers = jsonObject.getInt("follower_count");
                            following = jsonObject.getInt("following_count");
                            if (handler != null) {
                                handler.obtainMessage(UPDATE, "درحال دریافت لیست فالوورها...").sendToTarget();
                            }
                            SQL.getSql().unfollowAllUsers();
                            BuildApp.runOnUIThread(this::getFollowers, 1000 * (new Random().nextInt(3) + 1));
                        } else {
                            posts = jsonObject.getInt("media_count");
                            if (Store.data().getInt("limitCheckPosts", 1) != 0) {
                                if (posts > Store.data().getInt("limitCheckPosts", 1) * 18) {
                                    posts = Store.data().getInt("limitCheckPosts", 1) * 18;
                                }
                            }
                            if (handler != null) {
                                handler.obtainMessage(UPDATE, "درحال دریافت پست ها ...").sendToTarget();
                            }
                            SQL.getSql().dropAndCreateTablePosts();
                            BuildApp.runOnUIThread(this::getPosts, 1000 * (new Random().nextInt(3) + 1));
                        }
                    } else {
                        if (handler != null)
                            handler.obtainMessage(DIALOG_LOGIN_REQUEST).sendToTarget();
                    }
                } catch (Exception e) {
                    JSONException(e, object.toString());
                }
            } else if (!status.serverError) {
                check(status, this::getInfo);
            } else {
                if (countUnknownError()) {
                    return;
                }
                if (handler != null)
                    handler.obtainMessage(ERROR_MSG, "error:: null").sendToTarget();
                if (CheckNetworkState.isOnline()) {
                    getInfo();
                } else {
                    if (handler != null) handler.obtainMessage(JUST_FINISH).sendToTarget();
                }
            }
        });
    }

    private void getFollowing() {
        if (handler == null) {
            return;
        }
        if (following == 0) {
            handler.obtainMessage(UPDATE, "درحال آماده سازی ...").sendToTarget();
            handler.obtainMessage(UPDATE_AND_FINISH).sendToTarget();
            return;
        }
        IgApi.instance().getUserFollowing(max_id, (status, jsonObject) -> {
            if (status == null) {
                countResetError();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        SQL.getSql().setFollowing(object.getString("pk"), object.getString("profile_pic_url"),
                                object.getString("username"), object.getString("full_name"));
                        countFollowing++;
                        if (handler != null)
                            handler.obtainMessage(UPDATE, (int) ((float) countFollowing / following * 100), UPDATE_PROGRESS).sendToTarget();
                    }
                    if (jsonObject.has("big_list") && jsonObject.getBoolean("big_list")) {
                        max_id = jsonObject.getString("next_max_id");
                        BuildApp.runOnUIThread(this::getFollowing, 1000 * (new Random().nextInt(5) + 1));
                    } else {
                        max_id = "";
                        if (handler != null) {
                            handler.obtainMessage(UPDATE, "درحال آماده سازی ...").sendToTarget();
                        }
                        if (handler != null) {
                            handler.obtainMessage(UPDATE_AND_FINISH).sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    JSONException(e, jsonObject.toString());
                }
            } else if (!status.serverError) {
                check(status, this::getFollowing);
            } else {
                if (countUnknownError()) {
                    return;
                }
                if (handler != null) {
                    handler.obtainMessage(ERROR_MSG, "error::fg ep").sendToTarget();
                }
                getFollowing();
            }
        });
    }

    private void getFollowers() {
        if (handler == null) {
            return;
        }
        if (followers == 0) {
            handler.obtainMessage(UPDATE, 1, UPDATE_PROGRESS, "درحال دریافت لیست فالوئینگ ها...").sendToTarget();
            BuildApp.runOnUIThread(this::getFollowing, 200);
            return;
        }
        IgApi.instance().getUserFollowers(max_id, (status, jsonObject) -> {
            if (status == null) {
                countResetError();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        SQL.getSql().setFollower(object.getString("pk"), object.getString("profile_pic_url"),
                                object.getString("username"), object.getString("full_name"));
                        countFollowers++;
                        if (handler != null)
                            handler.obtainMessage(UPDATE, (int) ((float) countFollowers / followers * 100), UPDATE_PROGRESS).sendToTarget();
                    }
                    if (jsonObject.has("big_list") && jsonObject.getBoolean("big_list")) {
                        max_id = jsonObject.getString("next_max_id");
                        BuildApp.runOnUIThread(this::getFollowers, 1000 * (new Random().nextInt(5) + 1));
                    } else {
                        max_id = "";
                        if (handler != null) {
                            handler.obtainMessage(UPDATE, 1, UPDATE_PROGRESS, "درحال دریافت لیست فالوئینگ ها...").sendToTarget();
                        }
                        BuildApp.runOnUIThread(this::getFollowing, 1000 * (new Random().nextInt(3) + 1));
                    }
                } catch (Exception e) {
                    JSONException(e, jsonObject.toString());
                }
            } else if (!status.serverError) {
                check(status, this::getFollowers);
            } else {
                if (countUnknownError()) {
                    return;
                }
                if (handler != null)
                    handler.obtainMessage(ERROR_MSG, "error::fo null").sendToTarget();
                if (CheckNetworkState.isOnline()) {
                    run(this::getFollowers);
                } else {
                    if (handler != null) handler.obtainMessage(JUST_FINISH).sendToTarget();
                }
            }
        });
    }

    private void getPosts() {
        if (handler == null) {
            return;
        }
        if (posts == 0) {
            handler.obtainMessage(UPDATE, "درحال آماده سازی ...").sendToTarget();
            handler.obtainMessage(UPDATE_AND_FINISH).sendToTarget();
            return;
        }
        IgApi.instance().getMyPosts(max_id, (status, jsonObject) -> {
            if (status == null) {
                countResetError();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    orders.clear();
                    for (int i = 0, l = jsonArray.length(); i < l; i++, countPosts++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String caption = "";
                        if (object.has("caption") && !object.getString("caption").equals("null")) {
                            caption = object.getJSONObject("caption").getString("text");
                        }
                        String like_count = "0";
                        if (object.has("like_count")) {
                            like_count = object.getString("like_count");
                        }
                        String view_count = "0";
                        if (object.has("view_count")) {
                            view_count = object.getString("view_count");
                        }
                        String comment_count = "0";
                        boolean comments_disabled = false;
                        if (object.has("comments_disabled")) {
                            comments_disabled = object.getBoolean("comments_disabled");
                        }
                        if (object.has("comment_count")) {
                            comment_count = object.getString("comment_count");
                        }
                        String image_url = "";
                        if (object.has("image_versions2")) {
                            JSONArray jSONArray2 = new JSONObject(object.getString("image_versions2")).getJSONArray("candidates");
                            image_url = jSONArray2.getJSONObject(jSONArray2.length() - 1).getString("url");
                        } else if (object.has("carousel_media")) {
                            JSONArray jSONArray2 = new JSONObject(object.getJSONArray("carousel_media").getJSONObject(0).getString("image_versions2")).getJSONArray("candidates");
                            image_url = jSONArray2.getJSONObject(jSONArray2.length() - 1).getString("url");
                        }
                        SQL.getSql().setPost(object.getString("pk"), image_url,
                                like_count, comment_count, view_count, object.getString("media_type"), caption);
                        Bundle bundle = new Bundle();
                        bundle.putString("pk", object.getString("pk"));
                        bundle.putString("like", like_count);
                        bundle.putString("comment", comment_count);
                        bundle.putBoolean("comments_disabled", comments_disabled);
                        bundle.putInt("countPosts", countPosts);
                        orders.add(bundle);
                    }
                    if (jsonObject.has("more_available") && jsonObject.getBoolean("more_available") && posts > countPosts) {
                        max_id = jsonObject.getString("next_max_id");
                    } else {
                        max_id = "";
                    }
                    onOrders();
                } catch (Exception e) {
                    JSONException(e, jsonObject.toString());
                }
            } else if (!status.serverError) {
                check(status, this::getPosts);
            } else {
                if (countUnknownError()) {
                    return;
                }
                if (handler != null)
                    handler.obtainMessage(ERROR_MSG, "error::p null").sendToTarget();
                if (CheckNetworkState.isOnline()) {
                    run(this::getPosts);
                } else {
                    if (handler != null) handler.obtainMessage(JUST_FINISH).sendToTarget();
                }
            }
        });
    }

    private void onOrders() {
        if (orders.isEmpty()) {
            if (!max_id.isEmpty() && !max_id.equals("false")) {
                int sleep = new Random().nextInt(21) + 5;
                if (handler != null) {
                    handler.obtainMessage(UPDATE, sleep + " ثانیه انتظار خودکار ...").sendToTarget();
                }
                BuildApp.runOnUIThread(() -> {
                    if (handler != null) {
                        handler.obtainMessage(UPDATE, "درحال دریافت پست ها ...").sendToTarget();
                    }
                    getPosts();
                }, 1000 * sleep);
            } else {
                if (handler != null)
                    handler.obtainMessage(UPDATE, "درحال آماده سازی ...").sendToTarget();
                if (handler != null)
                    handler.obtainMessage(UPDATE_AND_FINISH).sendToTarget();
            }
        } else {
            order = orders.remove(0);
            int countPosts = order.getInt("countPosts");
            if (!order.getString("like").equals("0")) {
                if (handler != null) {
                    handler.obtainMessage(UPDATE,
                            (int) ((float) countPosts / posts * 100), UPDATE_PROGRESS,
                            " درحال دریافتی اول از پست " + countPosts).sendToTarget();
                }
                BuildApp.runOnUIThread(() -> getLikers(order.getString("pk")), 1000 * (new Random().nextInt(4) + 1));
            } else if (!order.getBoolean("comments_disabled") &&
                    !order.getString("comment").equals("0")) {
                if (handler != null) {
                    handler.obtainMessage(UPDATE, "درحال دریافتی دوم از پست " + countPosts).sendToTarget();
                }
                BuildApp.runOnUIThread(() -> getComments(order.getString("pk")), 1000 * (new Random().nextInt(5) + 1));
            } else {
                onOrders();
            }
        }
    }

    private void getLikers(final String mediaID) {
        if (handler == null) {
            return;
        }
        IgApi.instance().getLikers(mediaID, (status, jsonObject) -> {
            if (status == null) {
                countResetError();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        SQL.getSql().setLike(object.getString("pk"), object.getString("username"),
                                object.getString("full_name"), object.getString("profile_pic_url"), mediaID);
                    }
                    if (handler != null) {
                        handler.obtainMessage(UPDATE, "درحال دریافتی دوم از پست " + order.getInt("countPosts")).sendToTarget();
                    }
                    BuildApp.runOnUIThread(() -> getComments(mediaID), 1000 * (new Random().nextInt(3) + 1));
                } catch (Exception j) {
                    JSONException(j, "liker");
                }
            } else if (!status.serverError) {
                check(status, () -> getLikers(mediaID));
            } else {
                if (countUnknownError()) {
                    return;
                }
                if (handler != null)
                    handler.obtainMessage(ERROR_MSG, "error::l null ").sendToTarget();
                if (CheckNetworkState.isOnline()) {
                    run(() -> getLikers(mediaID));
                } else {
                    if (handler != null) handler.obtainMessage(JUST_FINISH).sendToTarget();
                }
            }
        });
    }

    private void getComments(final String mediaID) {
        if (handler == null) {
            return;
        }
        IgApi.instance().getComments(mediaID, c_max_id, (status, jsonObject) -> {
            if (status == null) {
                countResetError();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("comments");
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        JSONObject object = jsonArray.getJSONObject(i).getJSONObject("user");
                        SQL.getSql().setComment(object.getString("pk"), object.getString("username"),
                                object.getString("full_name"), object.getString("profile_pic_url"), mediaID);
                    }
                    if (jsonObject.has("has_more_comments") && jsonObject.getBoolean("has_more_comments")) {
                        c_max_id = jsonObject.getString("next_max_id");
                        BuildApp.runOnUIThread(() -> getComments(mediaID), 1000 * (new Random().nextInt(5) + 1));
                    } else {
                        c_max_id = "";
                        onOrders();
                    }
                } catch (Exception j) {
                    JSONException(j, "comment");
                }
            } else if (!status.serverError) {
                check(status, () -> getComments(mediaID));
            } else {
                if (countUnknownError()) {
                    return;
                }
                if (handler != null)
                    handler.obtainMessage(ERROR_MSG, "error::c null").sendToTarget();
                if (CheckNetworkState.isOnline()) {
                    run(() -> getComments(mediaID));
                } else {
                    if (handler != null) handler.obtainMessage(JUST_FINISH).sendToTarget();
                }
            }
        });
    }

    private void JSONException(final Exception j, final String json) {
        if (handler != null) handler.obtainMessage(JSON_EXCEPTION, j.toString()
                + "\n\n"
                + Base64.encodeToString(json.getBytes(), 2)).sendToTarget();
    }

    private void check(IgResponse.Status status, Runnable runnable) {
        if (status.requiredLogin) {
            if (handler != null) handler.obtainMessage(DIALOG_LOGIN_REQUEST).sendToTarget();
        } else if (status.challengeRequired || status.checkpointRequired || status.consentRequired) {
            if (handler != null) handler.obtainMessage(DIALOG_LOGIN_REQUEST).sendToTarget();
        } else {
            if (!countUnknownError()) {
                BuildApp.runOnUIThread(runnable, 1000 * (new Random().nextInt(4) + 1));
            }
        }
    }

    private void update() {
        finish();
        onUpdateAccount.ok();
        BuildApp.Toast("بروزرسانی انجام شد.");
    }

    private void finish() {
        isStarted = false;
        endWL();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            progressDialog.dismiss();
            progressDialog = null;
        }
        handler = null;
    }

    public interface OnUpdateAccount {
        boolean isHomePage();

        void ok();

        void error(String error);
    }

    public void close() {
        if (userStaticThread != null) {
            userStaticThread.close();
            userStaticThread = null;
        }
        handler = null;
    }

    public static void startWL(Activity activity) {
        if (wakeLock != null) return;
        if (!isStarted && !MainActivity.autoFollow) {
            return;
        }
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "start::task");
        wakeLock.acquire(2 * 60 * 60 * 1000L);
    }

    public static void endWL() {
        if (wakeLock == null) return;
        wakeLock.release();
        wakeLock = null;
    }

    private class UserStaticThread extends Thread {
        @Override
        public void run() {
            setName("UserStaticThread");
            for (Bundle bundle : bundles) {
                if (handler != null) {
                    staticUser(bundle);
                    try {
                        sleep(1000 * (new Random().nextInt(3) + 1));
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        }

        void close() {
            handler = null;
        }
    }

    private void run(Runnable runnable) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        BuildApp.runOnUIThread(runnable, new Random().nextInt(3000) + 3000);
    }
}