package ghasemi.abbas.unfollowyab.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import ghasemi.abbas.unfollowyab.ApplicationLoader;
import ghasemi.abbas.unfollowyab.builder.Store;
import im.dacer.androidcharts.LineView;

public class SQL extends SQLiteOpenHelper {
    private static SQL sql;

    private SQL() {
        super(ApplicationLoader.getContext(), "i_user_accounts", null, 6);
    }
//     volatile native transient strictfp;

    public static SQL getSql() {
        if (sql == null || !sql.getWritableDatabase().isOpen()) {
            sql = new SQL();
        }
//        while (sql.getWritableDatabase().isDbLockedByCurrentThread()){
//
//        }
        return sql;
    }

    @Override
    public synchronized void close() {
        super.close();
        sql = null;
    }

    synchronized boolean isUserLastLogin(String user_id) {
        Cursor cursor = getWritableDatabase().rawQuery("select * from accounts where user_id = " + user_id, null);
        boolean is = cursor.moveToFirst();
        cursor.close();
        return is;
    }

    public synchronized boolean isUserLogin() {
        Cursor cursor = getWritableDatabase().rawQuery("select * from accounts", null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table accounts (id INTEGER PRIMARY KEY AUTOINCREMENT,user_id TEXT,username TEXT,full_name TEXT,profile_pic_url TEXT,media_count TEXT," +
                "follower_count TEXT,following_count TEXT,biography TEXT,authorization TEXT,mid TEXT,claim TEXT,cookie TEXT," +
                "rur TEXT,shbts TEXT,shbid TEXT,drh TEXT,status INTEGER,phone_id TEXT,device_id TEXT," +
                "session_id TEXT,ad_id TEXT,android_id TEXT,user_agent TEXT)");

        db.execSQL("create table users (id INTEGER PRIMARY KEY AUTOINCREMENT,ui TEXT,pk TEXT,username TEXT,full_name TEXT,profile_pic_url TEXT," +
                "type TEXT,time TEXT,follow TEXT,white_list TEXT,status TEXT)");

        db.execSQL("create table posts (ui TEXT,pk TEXT,pic_url TEXT,like_count INTEGER,comment_count INTEGER,view_count INTEGER," +
                "media_type TEXT,caption TEXT)");

        db.execSQL("create table lc (ui TEXT,pk TEXT,username TEXT,full_name TEXT,profile_pic_url TEXT," +
                "like_count INTEGER,comment_count INTEGER,like_media_id TEXT,comment_media_id TEXT)");

        db.execSQL("create table details (id INTEGER PRIMARY KEY AUTOINCREMENT,ui TEXT,follower_count INTEGER,following_count INTEGER)");

        db.execSQL("create index ui on users(ui)");
        db.execSQL("create index pk on users(pk)");
        db.execSQL("create index type on users(type)");
        db.execSQL("create index follow on users(follow)");
        db.execSQL("create index white_list on users(white_list)");
        db.execSQL("create index status on users(status)");

        db.execSQL("create index like_count2 on posts(like_count)");
        db.execSQL("create index comment_count2 on posts(comment_count)");

        db.execSQL("create index ui2 on lc(ui)");
        db.execSQL("create index pk2 on lc(pk)");
        db.execSQL("create index like_count on lc(like_count)");
        db.execSQL("create index comment_count on lc(comment_count)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("create index ui on users(ui)");
            db.execSQL("create index pk on users(pk)");
            db.execSQL("create index type on users(type)");
            db.execSQL("create index follow on users(follow)");
            db.execSQL("create index white_list on users(white_list)");
            db.execSQL("create index status on users(status)");

            db.execSQL("create index like_count2 on posts(like_count)");
            db.execSQL("create index comment_count2 on posts(comment_count)");

            db.execSQL("create index ui2 on lc(ui)");
            db.execSQL("create index pk2 on lc(pk)");
            db.execSQL("create index like_count on lc(like_count)");
            db.execSQL("create index comment_count on lc(comment_count)");
        }
        if (oldVersion < 4) {
            db.execSQL("alter table accounts add column biography TEXT default ''");
            db.execSQL("alter table accounts add column authorization TEXT default ''");
            db.execSQL("alter table accounts add column mid TEXT default ''");
            db.execSQL("alter table accounts add column claim TEXT default '0'");
        }
        if (oldVersion < 5) {
            db.execSQL("alter table posts add column view_count INTEGER default 0");
        }
        if (oldVersion < 6) {
            db.execSQL("alter table accounts add column rur TEXT default ''");
            db.execSQL("alter table accounts add column shbts TEXT default ''");
            db.execSQL("alter table accounts add column shbid TEXT default ''");
            db.execSQL("alter table accounts add column drh TEXT default ''");
            db.execSQL("alter table accounts add column status INTEGER default 0");
            db.execSQL("alter table accounts add column session_id TEXT default ''");
            db.execSQL("alter table accounts add column phone_id TEXT default ''");
            db.execSQL("alter table accounts add column user_agent TEXT default ''");
            db.execSQL("alter table accounts add column ad_id TEXT default ''");
            db.execSQL("alter table accounts add column device_id TEXT default ''");
            db.execSQL("alter table accounts add column android_id TEXT default ''");
            Cursor cursor = db.rawQuery("select user_id from accounts", null);
            if (cursor.moveToFirst()) {
                do {
                    updateSession(db, cursor.getString(0), UUID.randomUUID().toString(),
                            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                            Connection.androidID(), IgLogin.userAgent());
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    private void updateSession(SQLiteDatabase db, String user_id, String phone_id, String device_id,
                               String session_id, String ad_id, String android_id, String user_agent) {
        ContentValues values = new ContentValues();
        values.put("user_agent", user_agent);
        values.put("session_id", session_id);
        values.put("ad_id", ad_id);
        values.put("phone_id", phone_id);
        values.put("android_id", android_id);
        values.put("device_id", device_id);
        if (db == null) {
            db = getWritableDatabase();
        }
        db.update("accounts", values, "user_id = ?", new String[]{user_id});
    }

    public Bundle getStatusDetails() {
        Cursor count_graph_img = getWritableDatabase().rawQuery("select count(ui) from posts where ui = '" + Utilities.userID() + "' and media_type = '1'", null);
        Cursor count_graph_video = getWritableDatabase().rawQuery("select count(ui) from posts where ui = '" + Utilities.userID() + "' and media_type = '2'", null);
        Cursor count_graph_sidecar = getWritableDatabase().rawQuery("select count(ui) from posts where ui = '" + Utilities.userID() + "' and media_type >= 3", null);
        Cursor sum_likes = getWritableDatabase().rawQuery("select IFNULL(SUM(like_count), 0) from posts where ui = '" + Utilities.userID() + "'", null);
        Cursor sum_comments = getWritableDatabase().rawQuery("select IFNULL(SUM(comment_count), 0) from posts where ui = '" + Utilities.userID() + "'", null);
        Cursor count_block = getWritableDatabase().rawQuery("select count(id) from users where status = '1' and ui = '" + Utilities.userID() + "'", null);
        count_graph_img.moveToFirst();
        count_graph_video.moveToFirst();
        count_graph_sidecar.moveToFirst();
        sum_likes.moveToFirst();
        sum_comments.moveToFirst();
        count_block.moveToFirst();
        Bundle bundle = new Bundle();
        bundle.putString("count_graph_img", count_graph_img.getString(0));
        bundle.putString("count_graph_video", count_graph_video.getString(0));
        bundle.putString("count_graph_sidecar", count_graph_sidecar.getString(0));
        int media_count = Integer.parseInt(Store.data().getString("media_count"));
        bundle.putString("sum_likes", avg(Integer.parseInt(sum_likes.getString(0)), media_count));
        bundle.putString("sum_comments", avg(Integer.parseInt(sum_comments.getString(0)), media_count));
        bundle.putString("count_mblock", count_block.getString(0));
        bundle.putString("count_block", Store.data().getString("c_b_l_u_l_" + Utilities.userID()));
        count_graph_img.close();
        count_graph_video.close();
        count_graph_sidecar.close();
        sum_likes.close();
        sum_comments.close();
        count_block.close();
        return bundle;
    }

    private String avg(int total, int count) {
        float avg = total * 1f / count;
        return String.format("%.2f", avg);
    }

    public ArrayList<Bundle> getPosts() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pic_url,like_count,comment_count,view_count,media_type from posts where ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pic_url", cursor.getString(0));
                bundle.putString("like_count", String.valueOf(cursor.getInt(1)));
                bundle.putString("comment_count", String.valueOf(cursor.getInt(2)));
                bundle.putString("view_count", String.valueOf(cursor.getInt(3)));
                bundle.putBoolean("is_video", cursor.getString(4).equals("2"));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getFollowing() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username from users where ui = '" + Utilities.userID() + "' and (type = '1' or type = '2')", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getFollower() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and (type = '0' or type = '1')", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUserDontFollowAndComment() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username from lc where ui = '" + Utilities.userID() + "' and comment_count != 0 and not exists(select * from users where users.ui = lc.ui and users.pk = lc.pk and type in ('0','1'))";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUserDontFollowAndLike() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username from lc where ui = '" + Utilities.userID() + "' and like_count != 0 and not exists(select * from users where users.ui = lc.ui and users.pk = lc.pk and type in ('0','1'))";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getRecUserDontComment() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and type = '1' and not exists(select * from lc where lc.pk = users.pk and lc.ui = users.ui and comment_count != 0)";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getRecUserDontLike() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and type = '1' and not exists(select * from lc where lc.pk = users.pk and lc.ui = users.ui and like_count != 0)";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUserDontComment() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and type in ('0','1') and not exists(select * from lc where lc.pk = users.pk and lc.ui = users.ui and comment_count != 0)";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUserDontCommentAndLike() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and type in ('0','1') and not exists(select * from lc where lc.pk = users.pk and lc.ui = users.ui and (like_count != 0 or comment_count != 0))";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUserDontLike() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and type in ('0','1') and not exists(select * from lc where lc.pk = users.pk and lc.ui = users.ui and like_count != 0)";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUserCommentCount() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username,comment_count from lc where comment_count != 0 and ui = '" + Utilities.userID() + "' order by comment_count desc", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("comment_count", String.valueOf(cursor.getInt(4)));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUserLikeCount() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username,like_count from lc where like_count != 0 and ui = '" + Utilities.userID() + "' order by like_count desc", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("like_count", String.valueOf(cursor.getInt(4)));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getWhiteList() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username from users where white_list = '1' and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getFollowers() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        bundles.add(null);
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username from users where type = '0' and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getNewFollower() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String time = String.valueOf((System.currentTimeMillis() / 1000) - 86400);
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username,type from users where (type = '0' or type = '1') and time >= " + time + " and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putBoolean("isFollower", cursor.getString(4).equals("0"));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUnfollow() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username,type,status from users where follow = '0' and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("username", cursor.getString(3));
                bundle.putBoolean("available", cursor.getString(5).equals("0") || cursor.getString(5).equals("2"));
                if (cursor.getString(5).equals("1")) {
                    bundle.putString("full_name", "‼️ شما از طرف این کاربر بلاک شده اید ‼️");
                } else if (cursor.getString(5).equals("3")) {
                    bundle.putString("full_name", "⛔ حساب کاربر در دسترس نیست ⛔");
                } else {
                    bundle.putString("full_name", cursor.getString(2));
                }
                bundle.putBoolean("isFollowing", cursor.getString(4).equals("2"));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getDontFollow() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        bundles.add(null);
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username from users where type = '2' and white_list = '0' and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putBoolean("no_white_list", true);
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getRecFollow() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username from users where type = '1' and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getLikePosts() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select like_count,pic_url,pk,media_type from posts where like_count != 0 and ui = '" + Utilities.userID() + "' order by like_count desc", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("like_count", String.valueOf(cursor.getInt(0)));
                bundle.putString("pic_url", cursor.getString(1));
                bundle.putString("pk", cursor.getString(2));
                bundle.putBoolean("is_video", cursor.getString(3).equals("2"));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getCommentPosts() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select comment_count,pic_url,pk,media_type from posts where comment_count != 0 and ui = '" + Utilities.userID() + "' order by comment_count desc", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("comment_count", String.valueOf(cursor.getInt(0)));
                bundle.putString("pic_url", cursor.getString(1));
                bundle.putString("pk", cursor.getString(2));
                bundle.putBoolean("is_video", cursor.getString(3).equals("2"));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getViewPosts() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select view_count,pic_url from posts where view_count != 0 and ui = '" + Utilities.userID() + "' order by view_count desc", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("view_count", String.valueOf(cursor.getInt(0)));
                bundle.putString("pic_url", cursor.getString(1));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public void setLike(String pk, String username, String full_name, String profile_pic_url, String mediaID) {

        Cursor cursor = getWritableDatabase().rawQuery(String.format("select like_count,like_media_id from lc where pk='%s' and ui='%s'", pk, Utilities.userID()), null);
        boolean count = cursor.moveToFirst();
        ContentValues values = new ContentValues();
        if (count) {
            boolean isNull = null == cursor.getString(1);
            if (isNull || !cursor.getString(1).contains("|" + mediaID + "|")) {
                values.put("like_count", cursor.getInt(0) + 1);
                values.put("like_media_id", isNull ? ("|" + mediaID + "|") : (cursor.getString(1) + mediaID + "|"));
                getWritableDatabase().update("lc", values, "pk = ? and ui = ?", new String[]{pk, Utilities.userID()});
            }
        } else {
            values.put("ui", Utilities.userID());
            values.put("pk", pk);
            values.put("profile_pic_url", profile_pic_url);
            values.put("username", username);
            values.put("full_name", full_name);
            values.put("like_count", 1);
            values.put("comment_count", 0);
            values.put("like_media_id", "|" + mediaID + "|");
            getWritableDatabase().insert("lc", null, values);
        }

//        ContentValues insert = new ContentValues();
//        insert.put("ui", Utilities.userID());
//        insert.put("pk", pk);
//        insert.put("media_id", mediaID);
//        insert.put("transaction_type", 0);
//        getWritableDatabase().insert("transactions", null, insert);

        cursor.close();
    }

    public void setComment(String pk, String username, String full_name, String profile_pic_url, String mediaID) {
        Cursor cursor = getWritableDatabase().rawQuery(String.format("select comment_count,comment_media_id from lc where pk='%s' and ui='%s'", pk, Utilities.userID()), null);
        boolean count = cursor.moveToFirst();
        ContentValues values = new ContentValues();
        if (count) {
            boolean isNull = null == cursor.getString(1);
            if (isNull || !cursor.getString(1).contains("|" + mediaID + "|")) {
                values.put("comment_count", cursor.getInt(0) + 1);
                values.put("comment_media_id", isNull ? ("|" + mediaID + "|") : (cursor.getString(1) + mediaID + "|"));
                getWritableDatabase().update("lc", values, "pk = ? and ui = ?", new String[]{pk, Utilities.userID()});
            }
        } else {
            values.put("ui", Utilities.userID());
            values.put("pk", pk);
            values.put("profile_pic_url", profile_pic_url);
            values.put("username", username);
            values.put("full_name", full_name);
            values.put("like_count", 0);
            values.put("comment_count", 1);
            values.put("comment_media_id", "|" + mediaID + "|");
            getWritableDatabase().insert("lc", null, values);
        }

//        ContentValues insert = new ContentValues();
//        insert.put("ui", Utilities.userID());
//        insert.put("pk", pk);
//        insert.put("media_id", mediaID);
//        insert.put("transaction_type", 1);
//        getWritableDatabase().insert("transactions", null, insert);

        cursor.close();
    }

    public void setDetails() {
        ContentValues values = new ContentValues();
        values.put("ui", Utilities.userID());
        values.put("follower_count", Integer.parseInt(Store.data().getString("follower_count")));
        values.put("following_count", Integer.parseInt(Store.data().getString("following_count")));
        getWritableDatabase().insert("details", null, values);

    }

    public void dropAndCreateTablePosts() {
        getWritableDatabase().delete("posts", "ui = ?", new String[]{Utilities.userID()});
//        getWritableDatabase().delete("lc", "ui = ?", new String[]{Utilities.userID()});
        Store.data().putString("l_t_u_s_" + Utilities.userID(), "");

    }

    public void setPost(String pk, String pic_url, String like_count, String comment_count,
                        String view_count, String media_type, String caption) {
        ContentValues values = new ContentValues();
        values.put("ui", Utilities.userID());
        values.put("pk", pk);
        values.put("pic_url", pic_url);
        values.put("like_count", like_count);
        values.put("comment_count", comment_count);
        values.put("view_count", view_count);
        values.put("media_type", media_type);
        values.put("caption", caption);
        getWritableDatabase().insert("posts", null, values);

    }

    public void unfollowAllUsers() {

        ContentValues values = new ContentValues();
        values.put("follow", "0");
        values.put("type", "-1");
        getWritableDatabase().update("users", values, "ui = ? and follow != '-1'", new String[]{Utilities.userID()});
        values = new ContentValues();
        values.put("type", "-1");
        getWritableDatabase().update("users", values, "ui = ? and follow = '-1'", new String[]{Utilities.userID()});

    }

    public void setFollower(String pk, String pic_url, String username, String full_name) {
        Cursor cursor = getWritableDatabase().rawQuery(String.format("select id,time from users where pk='%s' and ui='%s'", pk, Utilities.userID()), null);
        boolean count = cursor.moveToFirst();
        ContentValues values = new ContentValues();
        if (count) {
            values.put("type", "0");
            values.put("status", "0");
            values.put("profile_pic_url", pic_url);
            values.put("username", username);
            values.put("full_name", full_name);
            values.put("follow", "1");
            if (cursor.getString(1).equals("0")) {
                values.put("time", String.valueOf(System.currentTimeMillis() / 1000));
            }
            getWritableDatabase().update("users", values, "id = ?", new String[]{cursor.getString(0)});
        } else {
            values.put("ui", Utilities.userID());
            values.put("type", "0");
            values.put("pk", pk);
            values.put("profile_pic_url", pic_url);
            values.put("username", username);
            values.put("full_name", full_name);
            values.put("time", String.valueOf(System.currentTimeMillis() / 1000));
            values.put("follow", "1");
            values.put("white_list", "0");
            values.put("status", "0");
            getWritableDatabase().insert("users", null, values);
        }

        cursor.close();
    }

    public void setFollowing(String pk, String pic_url, String username, String full_name) {

        Cursor cursor = getWritableDatabase().rawQuery(String.format("select id,type from users where pk='%s' and ui='%s'", pk, Utilities.userID()), null);
        boolean count = cursor.moveToFirst();
        ContentValues values = new ContentValues();
        if (count) {
            values.put("status", "0");
            if (cursor.getString(1).equals("0")) {
                values.put("type", "1");
            } else {
                values.put("type", "2");
            }
            getWritableDatabase().update("users", values, "id = ?", new String[]{cursor.getString(0)});
        } else {
            values.put("ui", Utilities.userID());
            values.put("type", "2");
            values.put("pk", pk);
            values.put("profile_pic_url", pic_url);
            values.put("username", username);
            values.put("full_name", full_name);
            values.put("time", "0");
            values.put("follow", "-1");
            values.put("white_list", "0");
            values.put("status", "0");
            getWritableDatabase().insert("users", null, values);
        }
        cursor.close();
    }

    void addAccount(String user_id, String cookie) {
        if (isUserLastLogin(user_id)) {
            updateStatus(user_id, 0);
            updateAccount(user_id, "cookie", cookie);
            updateSession(null, user_id, IgLogin.getPhoneID(), IgLogin.getDeviceID(),
                    IgLogin.getSessionID(), IgLogin.getAdID(),
                    IgLogin.getAndroidID(), IgLogin.getUserAgent());
        } else {
            ContentValues values = new ContentValues();
            values.put("user_id", user_id);
            values.put("username", "");
            values.put("full_name", "");
            values.put("profile_pic_url", "");
            values.put("media_count", "0");
            values.put("follower_count", "0");
            values.put("following_count", "0");
            values.put("biography", "");
            values.put("authorization", "");
            values.put("mid", "");
            values.put("claim", "0");
            values.put("cookie", cookie);
            values.put("rur", "");
            values.put("shbts", "");
            values.put("shbid", "");
            values.put("drh", "");
            values.put("phone_id", IgLogin.getPhoneID());
            values.put("device_id", IgLogin.getDeviceID());
            values.put("session_id", IgLogin.getSessionID());
            values.put("ad_id", IgLogin.getAdID());
            values.put("android_id", IgLogin.getAndroidID());
            values.put("user_agent", IgLogin.getUserAgent());
            values.put("status", 0);
            getWritableDatabase().insert("accounts", null, values);
        }
    }

    public void updateStatus(String user_id, int status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        getWritableDatabase().update("accounts", values, "user_id = ?", new String[]{user_id});
    }

    public void removeAccount(boolean deleteData, String user_id, boolean setAccount) {

        getWritableDatabase().delete("accounts", "user_id = ?", new String[]{user_id});
        if (deleteData) {
            getWritableDatabase().delete("details", "ui = ?", new String[]{user_id});
            getWritableDatabase().delete("users", "ui = ?", new String[]{user_id});
            getWritableDatabase().delete("posts", "ui = ?", new String[]{user_id});
            getWritableDatabase().delete("lc", "ui = ?", new String[]{user_id});
            Store.data().putString("l_t_u_s_" + user_id, "");
            Store.data().putString("l_t_u_h_" + user_id, "");
        }

        if (setAccount && isUserLogin()) {
            Utilities.setUserID(getAccounts().get(0).getString("user_id"));
        }
    }

    public void removeAccount() {
        removeAccount(false, Utilities.userID(), true);
    }

    public void updateAccount(String user_id, String username, String full_name,
                              String profile_pic_url, String media_count, String follower_count,
                              String following_count, String biography) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("full_name", full_name);
        values.put("profile_pic_url", profile_pic_url);
        values.put("media_count", media_count);
        values.put("follower_count", follower_count);
        values.put("following_count", following_count);
        values.put("biography", biography);
        getWritableDatabase().update("accounts", values, "user_id = ?", new String[]{user_id});
    }

    public void updateAccount(String user_id, String key, String value) {
        ContentValues values = new ContentValues();
        values.put(key, value);
        getWritableDatabase().update("accounts", values, "user_id = ?", new String[]{user_id});
    }

    Bundle getUserInfo(String user_id) {
        Cursor cursor = getWritableDatabase().rawQuery("select username,full_name,profile_pic_url,media_count,follower_count," +
                "following_count,biography,authorization,mid,claim,cookie," +
                "rur,shbts,shbid,drh,status,session_id,phone_id,user_agent,ad_id," +
                "device_id,android_id" +
                " from accounts where user_id = " + user_id, null);
        Bundle bundle = new Bundle();
        if (cursor.moveToFirst()) {
            bundle.putString("username", cursor.getString(0));
            bundle.putString("full_name", cursor.getString(1));
            bundle.putString("profile_pic_url", cursor.getString(2));
            bundle.putString("media_count", cursor.getString(3));
            bundle.putString("follower_count", cursor.getString(4));
            bundle.putString("following_count", cursor.getString(5));
            bundle.putString("biography", cursor.getString(6));
            bundle.putString("authorization", cursor.getString(7));
            bundle.putString("mid", cursor.getString(8));
            bundle.putString("claim", cursor.getString(9));
            bundle.putString("cookie", cursor.getString(10));
            bundle.putString("rur", cursor.getString(11));
            bundle.putString("shbts", cursor.getString(12));
            bundle.putString("shbid", cursor.getString(13));
            bundle.putString("drh", cursor.getString(14));
            bundle.putInt("status", cursor.getInt(15));
            bundle.putString("session_id", cursor.getString(16));
            bundle.putString("phone_id", cursor.getString(17));
            bundle.putString("user_agent", cursor.getString(18));
            bundle.putString("ad_id", cursor.getString(19));
            bundle.putString("device_id", cursor.getString(20));
            bundle.putString("android_id", cursor.getString(21));
        }
        cursor.close();
        return bundle;
    }

    public ArrayList<Bundle> getAccounts() {
        ArrayList<Bundle> arrayList = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select user_id,full_name,username,profile_pic_url from accounts", null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return arrayList;
        }
        do {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", cursor.getString(0));
            bundle.putString("full_name", cursor.getString(1));
            bundle.putString("username", cursor.getString(2));
            bundle.putString("profile_pic_url", cursor.getString(3));
            arrayList.add(bundle);
        } while (cursor.moveToNext());

        cursor.close();
        return arrayList;
    }

    public void addDataWhiteList(String pk) {
        ContentValues values = new ContentValues();
        values.put("white_list", "1");
        getWritableDatabase().update("users", values, "pk = ? and ui = ?", new String[]{pk, Utilities.userID()});

    }

    public void removeWhiteList(String pk) {
        ContentValues values = new ContentValues();
        values.put("white_list", "0");
        getWritableDatabase().update("users", values, "pk = ? and ui = ?", new String[]{pk, Utilities.userID()});

    }

    public void setType(String pk, String type) {
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("status", "0");
        getWritableDatabase().update("users", values, "pk = ? and ui = ?", new String[]{pk, Utilities.userID()});

    }

    public void setFollow(String pk, String follow) {
        ContentValues values = new ContentValues();
        values.put("follow", follow);
        getWritableDatabase().update("users", values, "pk = ? and ui = ?", new String[]{pk, Utilities.userID()});

    }

    public void delete(String pk) {
        getWritableDatabase().delete("users", "pk = ? and ui = ?", new String[]{pk, Utilities.userID()});

    }

    public ArrayList<Bundle> getUsersDontLikePost(String mediaID) {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and type in ('0','1') and not exists(select * from lc where lc.pk = users.pk and lc.ui = users.ui and lc.like_media_id like '%|" + mediaID + "|%')";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getUsersDontCommentPost(String mediaID) {
        ArrayList<Bundle> bundles = new ArrayList<>();
        String query = "select pk,profile_pic_url,full_name,username,type from users where ui = '" + Utilities.userID() + "' and type in ('0','1') and not exists(select * from lc where lc.pk = users.pk and lc.ui = users.ui and lc.comment_media_id like '%|" + mediaID + "|%')";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundle.putString("type", cursor.getString(4));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public void Details(LineView lineView, View view) {
        Cursor cursor = getWritableDatabase().rawQuery("select follower_count,following_count from details where ui = '" + Utilities.userID() + "' order by id desc limit 40", null);
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        ArrayList<Integer> followers = new ArrayList<>();
        ArrayList<Integer> following = new ArrayList<>();
        if (cursor.moveToFirst()) {
            view.setVisibility(View.GONE);
            do {
                strings.add("");
                followers.add(cursor.getInt(0));
                following.add(cursor.getInt(1));
            } while (cursor.moveToNext());
            lineView.setBottomTextList(strings);
            Collections.reverse(followers);
            Collections.reverse(following);
            dataLists.add(followers);
            dataLists.add(following);
            lineView.setDataList(dataLists);
        }
        cursor.close();

        if (!strings.isEmpty() && strings.size() % 4 == 0) {
//            new StarApp(MainActivity.activity).show();
        }
    }

    public ArrayList<Bundle> getUserForCheck() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select id,pk from users where status = '0' and (follow = '0' or (follow = '-1' and type = '-1')) and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("id", cursor.getString(0));
                bundle.putString("pk", cursor.getString(1));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

    public void setStatus(String id, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        getWritableDatabase().update("users", values, "id = ?", new String[]{id});
    }

    public ArrayList<Bundle> getUnfollowing() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username,status from users where follow = '-1' and type = '-1' and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("username", cursor.getString(3));
                if (cursor.getString(4).equals("1")) {
                    bundle.putString("full_name", "‼️ شما از طرف این کاربر بلاک شده اید ‼️");
                } else if (cursor.getString(4).equals("3")) {
                    bundle.putString("full_name", "⛔ حساب کاربر در دسترس نیست ⛔");
                } else {
                    bundle.putString("full_name", cursor.getString(2));
                }
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bundles;
    }

    public ArrayList<Bundle> getMyblock() {
        ArrayList<Bundle> bundles = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("select pk,profile_pic_url,full_name,username from users where status = '1' and ui = '" + Utilities.userID() + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Bundle bundle = new Bundle();
                bundle.putString("pk", cursor.getString(0));
                bundle.putString("profile_pic_url", cursor.getString(1));
                bundle.putString("full_name", cursor.getString(2));
                bundle.putString("username", cursor.getString(3));
                bundles.add(bundle);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bundles;
    }

}