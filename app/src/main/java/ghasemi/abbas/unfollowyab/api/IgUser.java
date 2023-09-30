package ghasemi.abbas.unfollowyab.api;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ghasemi.abbas.unfollowyab.builder.Store;
import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class IgUser {
    private IgUser(String userID) {
        this.userID = userID;
        Bundle bundle = SQL.getSql().getUserInfo(userID);
        cookie = bundle.getString("cookie");
        Store.data().putString("username", bundle.getString("username"));
        Store.data().putString("full_name", bundle.getString("full_name"));
        Store.data().putString("profile_pic_url", bundle.getString("profile_pic_url"));
        Store.data().putString("media_count", bundle.getString("media_count"));
        Store.data().putString("follower_count", bundle.getString("follower_count"));
        Store.data().putString("following_count", bundle.getString("following_count"));
        Store.data().putString("biography", bundle.getString("biography"));
        authorization = bundle.getString("authorization");
        mid = bundle.getString("mid");
        claim = bundle.getString("claim");
        rur = bundle.getString("rur");
        shbid = bundle.getString("shbid");
        shbts = bundle.getString("shbts");
        drh = bundle.getString("drh");
        sessionID = bundle.getString("session_id");
        phoneID = bundle.getString("phone_id");
        userAgent = bundle.getString("user_agent");
        deviceID = bundle.getString("device_id");
        androidID = bundle.getString("android_id");
        adID = bundle.getString("ad_id");
        status = bundle.getInt("status");
        cookies = Utilities.cookieDecode(cookie);
        csrfToken = Utilities.findFromCookie("csrftoken", cookie);
        sessionToken = Utilities.findFromCookie("sessionid", cookie);
        if (mid.isEmpty()) {
            setMid(Utilities.findFromCookie("mid", cookie));
        }
        loginRequired = authorization.equals(IgLogin.AUTHORITY_START);
        if (authorization.isEmpty()) {
            setAuthorization(Utilities.authorization(userID, sessionToken));
        }
    }

    private final static List<IgUser> IG_USERS = new ArrayList<>();

    public static IgUser globalInstance(String user_id) {
        for (IgUser igUser : IG_USERS) {
            if (igUser.userID.equals(user_id)) return igUser;
        }
        IgUser igUser = new IgUser(user_id);
        IG_USERS.add(igUser);
        return igUser;
    }

    public static IgUser currentIgUser;

    public static void initial() {
        initial(false);
    }

    public static void initial(boolean newUser) {
        String userID = Utilities.getUserID();
        if (newUser) {
            for (IgUser igUser : IG_USERS) {
                if (igUser.userID.equals(userID)) {
                    IG_USERS.remove(igUser);
                    break;
                }
            }
        }
        if (SQL.getSql().isUserLastLogin(userID)) {
            currentIgUser = globalInstance(userID);
            return;
        }
        userID = SQL.getSql().getAccounts().get(0).getString("user_id");
        Utilities.setUserID(userID);
        currentIgUser = globalInstance(userID);
    }

    public final IgHttp.HeaderListener headerListener = (name, value) -> {
        if (name.equals("cookie")) {
            value = value.split(";")[0].trim();
            int index = value.indexOf('=');
            try {
                name = value.substring(0, index);
                value = value.substring(index + 1);
                setCookie(name, value);
            } catch (Exception e) {
                //
            }
        }
        switch (name) {
            case "ig-set-authorization":
                setAuthorization(value);
                break;
            case "ig-set-x-mid":
                setMid(value);
                break;
            case "x-ig-set-www-claim":
                setClaim(value);
                break;
            case "ig-set-ig-u-shbid":
                setShbid(value);
                break;
            case "ig-set-ig-u-shbts":
                setShbts(value);
                break;
            case "ig-set-ig-u-rur":
                setRur(value);
                break;
            case "ig-set-ig-u-ig-direct-region-hint":
                setDrh(value);
                break;
        }
    };
    public boolean loginRequired;
    public final String randomID = UUID.randomUUID().toString();
    public final String userAgent;
    public String sessionToken;
    public final String adID;
    public final String androidID;
    public final String sessionID;
    public final String deviceID;
    public int status;
    public final String phoneID;
    public final String userID;
    public String csrfToken;
    public String cookie;
    public String authorization;
    public String mid;
    public String claim;
    public String rur;
    public String shbts;
    public String shbid;
    private Map<String, String> cookies;
    public String drh;

    public void confirmStatus() {
        if (status == 0) {
            status = 1;
            SQL.getSql().updateStatus(userID, 1);
        }
    }

    public void setCookie(String name, String value) {
        if (cookies.containsKey(name) && value.equals(cookies.get(name))) {
            return;
        }
        if (name.equals("csrftoken")) {
            csrfToken = value;
        }
        cookies.put(name, value);
        cookie = Utilities.cookieEncode(cookies);
        SQL.getSql().updateAccount(userID, "cookie", cookie);
    }

    public void setAuthorization(String authorization) {
        if (this.authorization.equals(authorization)) return;
        loginRequired = authorization.equals(IgLogin.AUTHORITY_START);
        this.authorization = authorization;
        SQL.getSql().updateAccount(userID, "authorization", authorization);
    }

    public void setMid(String mid) {
        if (this.mid.equals(mid)) return;
        this.mid = mid;
        SQL.getSql().updateAccount(userID, "mid", mid);
    }

    public void setShbid(String shbid) {
        if (this.shbid.equals(shbid)) return;
        this.shbid = shbid;
        SQL.getSql().updateAccount(userID, "shbid", shbid);
    }

    public void setShbts(String shbts) {
        if (this.shbts.equals(shbts)) return;
        this.shbts = shbts;
        SQL.getSql().updateAccount(userID, "shbts", shbts);
    }

    public void setRur(String rur) {
        if (this.rur.equals(rur)) return;
        this.rur = rur;
        SQL.getSql().updateAccount(userID, "rur", rur);
    }

    public void setDrh(String drh) {
        if (this.drh.equals(drh)) return;
        this.drh = drh;
        SQL.getSql().updateAccount(userID, "drh", drh);
    }

    public void setClaim(String claim) {
        if (this.claim.equals(claim)) return;
        this.claim = claim;
        SQL.getSql().updateAccount(userID, "claim", claim);
    }
}