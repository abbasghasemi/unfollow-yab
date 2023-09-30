package ghasemi.abbas.unfollowyab.api;

import android.app.Activity;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class IgResponse {

    public static boolean showDialogMessage(Activity activity, Status status) {
        if (status.requiredLogin) {
            loginRequired(activity);
            return true;
        } else if (status.challengeRequired || status.checkpointRequired) {
            challengeRequired(activity);
            return true;
        } else if (status.consentRequired) {
            challengeRequired(activity);
            return true;
        } else if (status.notFound) {
            BuildApp.Toast("Not found.");
            return true;
        } else if (status.mediaDeleted) {
            BuildApp.Toast("پست حذف شده یا برای این حساب در دسترس نیست.");
            return true;
        } else if (status.followingLimit) {
            BuildApp.Toast("ظرفیت فالوئینگ های شما پر می باشد.");
            return true;
        } else if (status.shortBlock) {
            BuildApp.Toast("درحال حاضر اینستاگرام اجازه فالو/آنفالو بیشتر را نمی دهد، لطفا برای این حساب مدت زمانی صبر نمایید.");
            return true;
        } else if (status.spam) {
            spam(activity);
            return true;
        }
        return false;
    }

    public static void spam(Activity activity) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) return;
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.app_name)
                .setMessage("اینستاگرام حساب شما را برای یک بازه زمانی مسدود کرده است، لطفا بعدا تلاش نمائید.")
                .setCancelable(false)
                .setNegativeButton("بستن", null)
                .show();
        BuildApp.setCustomFontDialog(alertDialog);
    }

    public static void loginRequired(Activity activity) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) return;
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.app_name)
                .setMessage("ورود قبلی به حساب اینستاگرام شما باطل شده است، لطفا برای این اکانت دوباره وارد شوید.")
                .setCancelable(false)
                .setNegativeButton("بستن", null)
                .show();
        BuildApp.setCustomFontDialog(alertDialog);
    }

    public static void challengeRequired(Activity activity) {
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) return;
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.app_name)
                .setMessage("حساب شما با چلینج روبرو شده است، لطفا مدتی صبر کرده یا اگر مشکل برطرف نشد، مجددا به این حساب وارد شوید.")
                .setCancelable(false)
                .setNegativeButton("بستن", null)
                .show();
        BuildApp.setCustomFontDialog(alertDialog);
    }

    public static Status responseServerError(int httpCode) {
        Status status = new Status();
        if (httpCode == -2) {
            status.requiredLogin = true;
        } else {
            status.serverError = httpCode > 0;
        }
        status.notFound = httpCode == 404;
        return status;
    }

    public static Status responseStatus(String response) {
        Status status = new Status();
        String message = "";
        String challenge_context = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("require_login") && jsonObject.getBoolean("require_login")) {
                message = "login_required";
            } else {
                message = jsonObject.has("message") ? jsonObject.getString("message") : "";
            }
            status.spam = jsonObject.has("spam") && jsonObject.getBoolean("spam");
            if (jsonObject.has("challenge") && jsonObject.getJSONObject("challenge").has("challenge_context")) {
                challenge_context = jsonObject.getJSONObject("challenge").getString("challenge_context");
            }
        } catch (JSONException e) {
            //
        }
        if (response.contains("Target user not found") || response.contains("User not found")) {
            status.notFound = true;
        }
        if (message.contains("Sorry, you're following the max limit of accounts")) {
            status.followingLimit = true;
        } else if (message.equals("Please wait a few minutes before you try again.") ||
                message.equals("Sorry, too many requests. Please try again later.")
                || message.contains("It looks like your profile contains a link that is not allowed") ||
                challenge_context.contains("SpacesSessionSeqClassifierFollow") || challenge_context.contains("SourceTargetLocationMismatch") ||
                challenge_context.contains("BlockAbusive") || challenge_context.contains("SourceUserAppGap") ||
                challenge_context.contains("SpacesSessionNoVerified")) {
            status.shortBlock = true;
        } else if (response.contains("Couldn't Post Your Comment") || response.contains("Commenting is Off") ||
                response.contains("Comments on this post have been limited") ||
                response.contains("has been deleted") ||
                response.contains("geoblock_required") ||
                response.contains("The post you were viewing is no longer available") ||
                response.contains("The post you were viewing has been deleted") ||
                response.contains("Invalid media_id ") ||
                response.contains("Media not found or unavailable") ||
                response.contains("Sorry, the media is not available") ||
                response.contains("Sorry, this photo has been deleted") ||
                response.contains("Not authorized to view user") ||
                response.contains("This comment isn't allowed")) {
            status.mediaDeleted = true;
        } else if (!TextUtils.isEmpty(challenge_context)) {
            status.shortBlock = true;
        } else if (message.contains("consent_required")) {
            status.consentRequired = true;
        } else if (message.contains("challenge_required")) {
            status.challengeRequired = true;
        } else if (message.equals("login_required") || message.equals("user_has_logged_out")) {
            status.requiredLogin = true;
        } else if (message.equals("checkpoint_required")) {
            status.checkpointRequired = true;
        }
        return status;
    }

    public static class Status {
        public boolean serverError = false;

        public boolean requiredLogin = false;
        public boolean challengeRequired = false;
        public boolean checkpointRequired = false;
        public boolean consentRequired = false;

        public boolean spam = false;
        public boolean shortBlock = false;
        public boolean followingLimit = false;
        public boolean mediaDeleted = false;

        public boolean notFound = false;
    }

}
