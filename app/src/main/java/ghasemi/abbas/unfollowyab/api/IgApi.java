package ghasemi.abbas.unfollowyab.api;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import io.michaelrocks.paranoid.Obfuscate;
import kotlin.text.Charsets;

@Obfuscate
public class IgApi {
    private static IgApi igApi;

    private IgApi() {

    }

    public static IgApi instance() {
        if (igApi == null) {
            igApi = new IgApi();
        }
        return igApi;
    }

    private static final int timezone_offset;
    public static final Map<String, String> DEFAULT_HEADERS = new HashMap<>();

    private static final String bloksVersionId = "ca012d153fcf76e0d3b1f2dd9c0048e4ae44bc4b6f662d3e02d7dc26eab5b6a6";
    private static final String appId = "567067343352427";
    public static final String versionName = "178.1.0.37.123";
    public static final String versionID = "277249249";
    private static final String signed_body = "signed_body=SIGNATURE.&";

    static {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        timezone_offset = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (1000);
        DEFAULT_HEADERS.put("Connection", "keep-alive");
        DEFAULT_HEADERS.put("X-FB-HTTP-Engine", "Liger");
        DEFAULT_HEADERS.put("X-IG-Capabilities", "3brTvx0=");
        DEFAULT_HEADERS.put("X-IG-Connection-Type", "WIFI");
        DEFAULT_HEADERS.put("Accept-Encoding", "gzip, deflate");
        DEFAULT_HEADERS.put("X-Bloks-Version-Id", bloksVersionId);
        DEFAULT_HEADERS.put("X-Ig-Timezone-Offset", String.valueOf(timezone_offset));
        DEFAULT_HEADERS.put("X-FB-Server-Cluster", "true");
        DEFAULT_HEADERS.put("X-Bloks-Is-Panorama-Enabled", "true");
        DEFAULT_HEADERS.put("X-FB-CLIENT-IP", "True");
        DEFAULT_HEADERS.put("X-Bloks-Is-Layout-RTL", "false");
        DEFAULT_HEADERS.put("X-IG-App-ID", appId);
        DEFAULT_HEADERS.put("Priority", "u=3");
    }

    public static Map<String, String> randomHeaders() {
        Map<String, String> HEADERS = new HashMap<>();
        HEADERS.put("X-Pigeon-Rawclienttime", String.valueOf(System.currentTimeMillis()));
        HEADERS.put("X-IG-Mapped-Locale", "en_US");
        HEADERS.put("X-IG-App-Locale", "en_US");
        HEADERS.put("X-IG-Device-Locale", "en_US");
        HEADERS.put("Accept-Language", "en-US");
        HEADERS.put("X-Ig-Bandwidth-Speed-Kbps", "-1.000");
        HEADERS.put("X-Ig-Bandwidth-Totalbytes-B", "0");
        HEADERS.put("X-Ig-Bandwidth-Totaltime-Ms", "0");
        HEADERS.put("X-IG-Connection-Speed", (new Random().nextInt(3000) + 2000) + "kbps");
        HEADERS.put("X-IG-SALT-IDS", String.valueOf(new Random().nextInt(100 * 1000) + 1061162222));
        return HEADERS;
    }

    private Map<String, String> appHeaders(IgUser igUser) {
        Map<String, String> HEADERS = new HashMap<>();
        HEADERS.put("Authorization", igUser.authorization);
        HEADERS.put("X-Mid", igUser.mid);
        HEADERS.put("Ig-U-Rur", igUser.rur);
        HEADERS.put("IG-U-SHBTS", igUser.shbts);
        HEADERS.put("IG-U-SHBID", igUser.shbid);
        HEADERS.put("IG-U-IG-DIRECT-REGION-HINT", igUser.drh);
        HEADERS.put("X-Ig-Www-Claim", igUser.claim);
        if (!TextUtils.isEmpty(igUser.cookie) && !HEADERS.containsKey("Authorization")) {
            HEADERS.put("Cookie", igUser.cookie);
        }
        HEADERS.put("Ig-Intended-User-Id", igUser.userID);
        HEADERS.put("Ig-U-Ds-User-Id", igUser.userID);
        HEADERS.put("User-Agent", igUser.userAgent);
        HEADERS.put("X-IG-Android-ID", igUser.androidID);
        HEADERS.put("X-Pigeon-Session-Id", igUser.randomID);
        HEADERS.put("X-IG-Device-ID", igUser.deviceID);
        HEADERS.put("X-IG-Family-Device-ID", igUser.phoneID);
        HEADERS.putAll(DEFAULT_HEADERS);
        HEADERS.putAll(randomHeaders());
        return HEADERS;
    }

    public void getUserInfo(final String userID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/users/%s/info/", userID), resultConnection);
    }

    public void getBlocked(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/friendships/blocked/", resultConnection);
    }

    /*
      {"items": [{"taken_at": 1507722056, "pk": 1623212720161349049, "id": "1623212720161349049_4191271519", "device_timestamp": 1507722034276, "media_type": 1, "code": "BaGz15sHN25", "client_cache_key": "MTYyMzIxMjcyMDE2MTM0OTA0OQ==.2", "filter_type": 0, "image_versions2": {"candidates": [{"width": 573, "height": 300, "url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/f7547cd83e415481a247dcf0cac6decc/5D8DE6E7/t51.2885-15/e35/22344199_1856945531302375_6505577505733214208_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net\u0026ig_cache_key=MTYyMzIxMjcyMDE2MTM0OTA0OQ%3D%3D.2"}, {"width": 240, "height": 125, "url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/6bc913f9ff8455c37d47dea6bd29163d/5D7AF2DE/t51.2885-15/e35/s240x240/22344199_1856945531302375_6505577505733214208_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net\u0026ig_cache_key=MTYyMzIxMjcyMDE2MTM0OTA0OQ%3D%3D.2"}]}, "original_width": 573, "original_height": 300, "user": {"pk": 4191271519, "username": "farasource", "full_name": "\u0641\u0631\u0627 \u0633\u0648\u0631\u0633 | Fara Source", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/169afd9ed7e915d377657d020a56de52/5D9219C7/t51.2885-19/s150x150/61338376_587064731782813_5790911062396108800_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2057367026155302349_4191271519", "is_verified": false, "has_anonymous_profile_picture": false, "can_boost_post": false, "can_see_organic_insights": false, "show_insights_terms": false, "reel_auto_archive": "unset", "is_unpublished": false, "allowed_commenter_type": "any"}, "can_viewer_reshare": true, "caption_is_edited": false, "direct_reply_to_author_enabled": true, "comment_likes_enabled": true, "comment_threading_enabled": false, "has_more_comments": false, "max_num_visible_preview_comments": 2, "preview_comments": [], "can_view_more_preview_comments": false, "comment_count": 0, "inline_composer_display_condition": "impression_trigger", "like_count": 1, "has_liked": false, "top_likers": [], "likers": [{"pk": 3632926693, "username": "sa.min.wood", "full_name": "woodwork.bnd", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/85bdbea27cec75dd7e1b524906f8e47a/5D8ACDEB/t51.2885-19/s150x150/49671634_2177522029179026_5963669481858269184_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1966793292607865153_3632926693", "is_verified": false}], "photo_of_you": false, "caption": {"pk": 17903602036043094, "user_id": 4191271519, "text": "\u062f\u0627\u0646\u0644\u0648\u062f \u0633\u0648\u0631\u0633 \u0634\u0627\u0631\u0698 \u0633\u0631\u06cc\u0639 \u0628\u0627\u062a\u0631\u06cc + \u0630\u062e\u06cc\u0631\u0647 \u0622\u0646\n#\u0633\u0648\u0631\u0633_\u0627\u0646\u062f\u0631\u0648\u06cc\u062f\n\n\u0627\u0637\u0644\u0627\u0639\u0627\u062a \u0628\u06cc\u0634\u062a\u0631 \u0648 \u062f\u0627\u0646\u0644\u0648\u062f:\nhttps://goo.gl/urS4Mk\n\n\u0633\u0627\u06cc\u062a \u0641\u0631\u0627 \u0633\u0648\u0631\u0633:\nfarasource.ir", "type": 1, "created_at": 1507722057, "created_at_utc": 1507722057, "content_type": "comment", "status": "Active", "bit_flags": 0, "user": {"pk": 4191271519, "username": "farasource", "full_name": "\u0641\u0631\u0627 \u0633\u0648\u0631\u0633 | Fara Source", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/169afd9ed7e915d377657d020a56de52/5D9219C7/t51.2885-19/s150x150/61338376_587064731782813_5790911062396108800_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2057367026155302349_4191271519", "is_verified": false, "has_anonymous_profile_picture": false, "can_boost_post": false, "can_see_organic_insights": false, "show_insights_terms": false, "reel_auto_archive": "unset", "is_unpublished": false, "allowed_commenter_type": "any"}, "did_report_as_spam": false, "share_enabled": false, "media_id": 1623212720161349049, "has_translation": true}, "fb_user_tags": {"in": []}, "can_viewer_save": true, "organic_tracking_token": "eyJ2ZXJzaW9uIjo1LCJwYXlsb2FkIjp7ImlzX2FuYWx5dGljc190cmFja2VkIjp0cnVlLCJ1dWlkIjoiNzJjNTgzODY5NjYxNDQ0Mjg0MjU1YjE4ZDMyOTMwYWUxNjIzMjEyNzIwMTYxMzQ5MDQ5Iiwic2VydmVyX3Rva2VuIjoiMTU1OTg5NjkwNTE1N3wxNjIzMjEyNzIwMTYxMzQ5MDQ5fDQxOTEyNzE1MTl8M2EzNDM2M2JjYzFlM2M5OTQ1ZDZmYzk3YTdlMDZjZDUwMTZkZjNmODQ0YmMyYTEyN2E5YzVhOGU4MDJlMWVhYyJ9LCJzaWduYXR1cmUiOiIifQ=="}, {"taken_at": 1507721838, "pk": 1623210889238803640, "id": "1623210889238803640_4191271519", "device_timestamp": 1507721807281, "media_type": 1, "code": "BaGzbQgn_S4", "client_cache_key": "MTYyMzIxMDg4OTIzODgwMzY0MA==.2", "filter_type": 0, "image_versions2": {"candidates": [{"width": 573, "height": 300, "url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/f0ce6020132925c74be806b6949d5cb0/5D7EF6A6/t51.2885-15/e35/22344957_252796135245336_4442838209637908480_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net\u0026ig_cache_key=MTYyMzIxMDg4OTIzODgwMzY0MA%3D%3D.2"}, {"width": 240, "height": 125, "url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/c1157b35bb49d1cca4fcf55c19f11a62/5D7AB1AE/t51.2885-15/e35/s240x240/22344957_252796135245336_4442838209637908480_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net\u0026ig_cache_key=MTYyMzIxMDg4OTIzODgwMzY0MA%3D%3D.2"}]}, "original_width": 573, "original_height": 300, "user": {"pk": 4191271519, "username": "farasource", "full_name": "\u0641\u0631\u0627 \u0633\u0648\u0631\u0633 | Fara Source", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/169afd9ed7e915d377657d020a56de52/5D9219C7/t51.2885-19/s150x150/61338376_587064731782813_5790911062396108800_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2057367026155302349_4191271519", "is_verified": false, "has_anonymous_profile_picture": false, "can_boost_post": false, "can_see_organic_insights": false, "show_insights_terms": false, "reel_auto_archive": "unset", "is_unpublished": false, "allowed_commenter_type": "any"}, "can_viewer_reshare": true, "caption_is_edited": false, "direct_reply_to_author_enabled": true, "comment_likes_enabled": true, "comment_threading_enabled": false, "has_more_comments": false, "max_num_visible_preview_comments": 2, "preview_comments": [], "can_view_more_preview_comments": false, "comment_count": 0, "inline_composer_display_condition": "impression_trigger", "like_count": 2, "has_liked": false, "top_likers": [], "likers": [{"pk": 3632926693, "username": "sa.min.wood", "full_name": "woodwork.bnd", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/85bdbea27cec75dd7e1b524906f8e47a/5D8ACDEB/t51.2885-19/s150x150/49671634_2177522029179026_5963669481858269184_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1966793292607865153_3632926693", "is_verified": false}, {"pk": 5321778569, "username": "mohammad.r6081", "full_name": "\u2764/\\/\\\u25cb|-|/-\\ /\\/\\ /-\\ |)\u2764\u2664tk\u2664", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/ed481eba770423226fbd8589ba0a84a4/5D7BAD9C/t51.2885-19/s150x150/35575762_2038237983097372_4795148522888364032_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1810960512832217947_5321778569", "is_verified": false}], "photo_of_you": false, "caption": {"pk": 17890730029111857, "user_id": 4191271519, "text": "\u0622\u0645\u0648\u0632\u0634 \u0631\u0641\u0639 \u0645\u0634\u06a9\u0644\u060c\n error loading project cannot load 3 facets details ....\n#\u0631\u0641\u0639_\u062e\u0637\u0627\n\n\u0627\u0637\u0627\u0639\u0627\u062a \u0628\u06cc\u0634\u062a\u0631:\nhttp://bit.ly/2jUMy3w\n\n\u0633\u0627\u06cc\u062a \u0641\u0631\u0627 \u0633\u0648\u0631\u0633:\nfarasource.ir", "type": 1, "created_at": 1507721839, "created_at_utc": 1507721839, "content_type": "comment", "status": "Active", "bit_flags": 0, "user": {"pk": 4191271519, "username": "farasource", "full_name": "\u0641\u0631\u0627 \u0633\u0648\u0631\u0633 | Fara Source", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/169afd9ed7e915d377657d020a56de52/5D9219C7/t51.2885-19/s150x150/61338376_587064731782813_5790911062396108800_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2057367026155302349_4191271519", "is_verified": false, "has_anonymous_profile_picture": false, "can_boost_post": false, "can_see_organic_insights": false, "show_insights_terms": false, "reel_auto_archive": "unset", "is_unpublished": false, "allowed_commenter_type": "any"}, "did_report_as_spam": false, "share_enabled": false, "media_id": 1623210889238803640, "has_translation": true}, "fb_user_tags": {"in": []}, "can_viewer_save": true, "organic_tracking_token": "eyJ2ZXJzaW9uIjo1LCJwYXlsb2FkIjp7ImlzX2FuYWx5dGljc190cmFja2VkIjp0cnVlLCJ1dWlkIjoiNzJjNTgzODY5NjYxNDQ0Mjg0MjU1YjE4ZDMyOTMwYWUxNjIzMjEwODg5MjM4ODAzNjQwIiwic2VydmVyX3Rva2VuIjoiMTU1OTg5NjkwNTE1N3wxNjIzMjEwODg5MjM4ODAzNjQwfDQxOTEyNzE1MTl8ZTYxNmVlNmVkMGQyZTRmMTA4OWVhZjk5Yzg2YTk1NjY3MDk0MWJkNzFhM2Q3ZTg4Y2M2YjViMzkwNTI3NWI5NiJ9LCJzaWduYXR1cmUiOiIifQ=="}], "num_results": 2, "more_available": false, "auto_load_more_enabled": true, "status": "ok"}
    */
    public void getUserPosts(final String userID, final String maxID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/feed/user/%s/?min_timestamp=&max_id=%s&count=18&ranked_content=true&rank_token=%s",
                userID, maxID, IgUser.currentIgUser.userID + "_" + IgUser.currentIgUser.deviceID), resultConnection);
    }

    public void getMyPosts(final String maxID, final ResultConnection resultConnection) {
        getUserPosts(IgUser.currentIgUser.userID, maxID, resultConnection);
    }

    /*
        "sections": null,
        "users": [],
        "big_list": false,
        "next_max_id": null,
        "page_size": 200,
        "status": "ok"
     */
    public void getUserFollowing(final String maxID, final ResultConnection resultConnection) {
        getUserFollowing(IgUser.currentIgUser.userID, maxID, resultConnection);
    }

    public void getUserFollowing(String userID, final String maxID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/friendships/%s/following/?max_id=%s", userID, maxID), resultConnection);
    }

    /*
        {"sections": null, "users": [{"pk": 8931325004, "username": "payamzizi", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/0d1f7c5f58641375781d7257336b78b3/5D90E63D/t51.2885-19/s150x150/42138629_327314568033232_6345306819648290816_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1882886400489946137_8931325004", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 8953957362, "username": "ganoderma_champion", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/43227047208a95771de26fa68c3034f2/5D7D718B/t51.2885-19/s150x150/42745555_326982087942374_1641815299743285248_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1892399167785316665_8953957362", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 8540510280, "username": "parisa____noori", "full_name": "", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/f42e0319d38cd88548beacd38157653d/5D98790A/t51.2885-19/s150x150/60612307_2073832776243439_5018440475288272896_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2055941958422845744_8540510280", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 3660325945, "username": "fn.khoubani", "full_name": "", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/e91952b38c02d40e46d2c9e0911473c5/5D926A6B/t51.2885-19/s150x150/37583465_277542356339185_6427568268139036672_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1837586902947455836_3660325945", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7563827983, "username": "mr_power_amir", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/611fe54acce67cdd4659de082ae93fc3/5D95BBD4/t51.2885-19/s150x150/57488333_1309072802578276_4796874116193845248_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2027831422854386930_7563827983", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7737499077, "username": "ffhjjbvvgjkkbb", "full_name": "", "is_private": true, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 8083339129, "username": "niloofar_abi_17", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 2208923612, "username": "2015haghdst", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/bbe6557a1095f78a6690f750e9ddc7bc/5D7922B7/t51.2885-19/s150x150/32443592_1438036826341553_3590429403454111744_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1788721140615467044_2208923612", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7985684949, "username": "sara__tehranim", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 7623124543, "username": "unfolobasteeeeeee", "full_name": "", "is_private": true, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 3572656387, "username": "alipomvi", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 1765089185, "username": "sa.min.nail.bnd", "full_name": "$@/\\/@$./\\/@\u00a1|\ud83d\udc8d", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/5e1118894f75e9edeb79b53a767f1723/5D78D08C/t51.2885-19/s150x150/47191950_549690782171847_3969650843614969856_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1926777359564557016_1765089185", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 4391156479, "username": "yaghubzakeri", "full_name": "* \u06cc\u0639\u0642\u0648\u0628 \u0630\u0627\u06a9\u0631\u06cc \u062f\u0631\u0628\u0627\u063a\u06cc *", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/5642bdf5fb0c3cc59b25845a30fdd032/5D8D4935/t51.2885-19/s150x150/58468747_473214353491283_9093837906266554368_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2041522475137041964_4391156479", "is_verified": false, "has_anonymous_profile_picture": false, "latest_reel_media": 1559889475}, {"pk": 6705082376, "username": "alii_24713", "full_name": "...", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/9b3849251b2c803877e4309ddf37ba6e/5D8DB3DE/t51.2885-19/s150x150/31757568_230515007703463_7864909387624611840_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1778491761632926355_6705082376", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7831889339, "username": "ahmad.del.garm", "full_name": "@.d", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/51d3f2f349cfc3bd24c3f4da4fef56b7/5D8B39C9/t51.2885-19/s150x150/47487928_217803442452938_2779265146738966528_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1930489701190830781_7831889339", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5671388670, "username": "adeel.4245", "full_name": "@Adeel", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/815d4d1cd265753e5d5404e008ec3e21/5D9742EE/t51.2885-19/s150x150/39209774_271713626993965_6341200581805211648_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1850415238621440032_5671388670", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 4878523509, "username": "_emad_6_6_6", "full_name": "_Emad", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/24e2f8ca2493035760ce0099b5926f46/5D7D57B2/t51.2885-19/s150x150/49682922_1353090654830043_3621639925771796480_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1961627742480755989_4878523509", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 8536003669, "username": "mansuriii_caar", "full_name": "aaaaalllllppppp", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/595f842e8493b83efc19b99e5b612153/5D914A45/t51.2885-19/s150x150/40226207_231999014134812_1375511968588234752_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1860786196838424507_8536003669", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5624055851, "username": "ahmadalsary2012", "full_name": "ahmadalsary", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/d622d68eab6bf141082d7bcc00cddfcb/5D9BB843/t51.2885-19/s150x150/25009290_501766246876012_4532755772341223424_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1673517002281440753_5624055851", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 1216743476, "username": "______________ali__", "full_name": "ali", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/95736b908cabedfc7598cec1c930ac56/5D9DB461/t51.2885-19/s150x150/59204061_677047426094093_4077515506985205760_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2037790468466428677_1216743476", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5839391572, "username": "arsalsnxjs", "full_name": "ariiiii", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 8017805812, "username": "darga0901", "full_name": "dargha0901", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/281311677ab6c4cc72ef559203e9f7b1/5D9C55BD/t51.2885-19/s150x150/34685908_413392119127008_8400472915874676736_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1802154819398452584_8017805812", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 8451458686, "username": "endndjfhd", "full_name": "djdjdj", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 6933553207, "username": "dokhtarak_khas___", "full_name": "dokhtarak_khas", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/dd153cfac4f5c3870c34ebaebf6e618f/5D960087/t51.2885-19/s150x150/26071579_1849814855308608_4218642231046701056_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1693510285934392798_6933553207", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 3646506317, "username": "fardin_bazyar", "full_name": "F\u300aFARDIN\u300bB", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/29961cc512195ff4652b67de29e5768c/5D87165D/t51.2885-19/s150x150/41270707_1350310125103512_2469505564116254720_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1873134777360661691_3646506317", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7690879468, "username": "king_baluch9909", "full_name": "king_baluch9909", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/53a47029f332c2f741625b58568b6594/5D975765/t51.2885-19/s150x150/49315696_474143162989823_8499076044911804416_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1961034044243996407_7690879468", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7497118635, "username": "l.mohaddesew.l", "full_name": "l.mohaddesew.l", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/75e88e377ea8f34409d8ad7e96e7cca4/5D857CC7/t51.2885-19/s150x150/29739204_386545378488705_6576658282474110976_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1756643332781596327_7497118635", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 8505008761, "username": "asdfghasdfgh2217", "full_name": "llllllllll", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 8083600889, "username": "mahsazoo_oo", "full_name": "mahsazoo_oo", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 8071782561, "username": "mamadi435365", "full_name": "mamad", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/ef30984c9d65ed6ce39eb032bf5a3a74/5D90506C/t51.2885-19/s150x150/34602890_169484927241385_5295723490903588864_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1808201675166382897_8071782561", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 4655442847, "username": "mmdm8301", "full_name": "mamale", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/aa6c9e6d3c428f5e156ab8b639c2b8ec/5D9EF3D1/t51.2885-19/s150x150/17817566_270834633363991_3411476876646940672_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1487470251004515779_4655442847", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 2040204930, "username": "mehran.sharifi983", "full_name": "Mehran Sharifi", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/cd4668e5e7ca8efc42bbf915079ef41c/5D7B8907/t51.2885-19/s150x150/47689970_376736066210269_3098521468988817408_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1948187917461873554_2040204930", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 3066454435, "username": "saeid.omidy", "full_name": "mohamad reza", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/9879f576f8b972ca728c6cd2293ed275/5D8DBD68/t51.2885-19/s150x150/918123_183434025373870_1173704079_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 8017008337, "username": "mmss234859", "full_name": "mohammad", "is_private": false, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}, {"pk": 5832094889, "username": "mons220", "full_name": "mons2", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/09c8b18a4e287a6d24bec39734fc9895/5D9ECB2A/t51.2885-19/s150x150/20582419_507115192958233_6430623845312364544_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1574252377942175029_5832094889", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5441795096, "username": "mrs_kara17", "full_name": "mrs,_,kara", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/f733d7eb94fab6e4970c7f8ec3edf588/5D832E64/t51.2885-19/s150x150/39601917_1374493082682900_8284520300805095424_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1511197772135803080_5441795096", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5830065117, "username": "nstrn_hzrtii", "full_name": "nastrn_hzrtii", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/c5e0e6315af0f8eeae0fae45cb32b204/5D9BF465/t51.2885-19/s150x150/60139286_413838076012576_7599235694380187648_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2054471848946625905_5830065117", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 6230175515, "username": "samira_tehrani7260", "full_name": "samira_tehrani", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/30dcce69661ba5989662cbec0852268a/5D7EBBA4/t51.2885-19/s150x150/57488397_645891872522427_6989983313448402944_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2037860939524644542_6230175515", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7690260664, "username": "samyar954", "full_name": "Samyar Agriculture Services", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/d3589d739c6bfe23d113e477ddb2ee91/5D96E290/t51.2885-19/s150x150/31218750_206085693333761_496908101031559168_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1774008943555718444_7690260664", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 6985024984, "username": "seyd_ali0079", "full_name": "seyd ali 0079", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/4e932976353b4ab8050b8491c4763bc6/5D8FD8A4/t51.2885-19/s150x150/31490823_1536068873183159_8252017985468760064_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1779687926987028559_6985024984", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 9006260210, "username": "shahab_mhb1", "full_name": "shahab_mhb1", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/5b6fb6c2d4913abbfe42ee7b49df0bb2/5D8930E4/t51.2885-19/s150x150/41318640_240365106643099_7064445316077977600_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1876280977623251253_9006260210", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 4581335033, "username": "shahabiii123", "full_name": "shahabii", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/8a5c0d05cbc59b701887c4d13eed4b05/5D95078D/t51.2885-19/s150x150/16583421_228921764182781_2011242470712541184_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1445154854971720242_4581335033", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 4479422676, "username": "siavash7348", "full_name": "siavash", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/da648b2cfa090ce94dfcd64af9a92915/5D7A1E42/t51.2885-19/s150x150/16110554_1941849752724096_5777845419694358528_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1432028205463725633_4479422676", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 7178322722, "username": "yos6187", "full_name": "yo3i", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/48fbe7d721b7ecd60a8699c95e29ef19/5D7E9704/t51.2885-19/s150x150/60501218_2433810810188328_325888219906310144_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2055697396441821213_7178322722", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 4413344953, "username": "yonesi683019", "full_name": "yonesi68", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/41dc68d798ac4736bc3b64ae1be9c7f7/5D864E76/t51.2885-19/s150x150/15803773_1728634507452753_948113274677755904_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1426357023397788837_4413344953", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 3146999538, "username": "upweb_hosting_domain", "full_name": "\u062e\u0631\u06cc\u062f \u0647\u0627\u0633\u062a", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/0b713826809b967facdb9b02a204002b/5D97BA6A/t51.2885-19/s150x150/12383671_213340509048484_742137404_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5747782090, "username": "s.japani", "full_name": "\u0633\u0639\u06cc\u062f", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/4f1851669149f822b83f37a6bce87332/5D7F999C/t51.2885-19/s150x150/56764788_586696528516620_424430201494044672_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2025006512167220658_5747782090", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 6903357243, "username": "source_yab", "full_name": "\u0633\u0648\u0631\u0633 \u06a9\u062f\u0647\u0627\u06cc \u0622\u0645\u0627\u062f\u0647 \u0622\u0646\u062f\u0631\u0648\u06cc\u062f", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/d8ede3f84af2aef70cd66a445a5069ac/5D7EF1DF/t51.2885-19/s150x150/26293698_457612047988474_6982079757579976704_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1704315497916445384_6903357243", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5878346533, "username": "digidev", "full_name": "\u0641\u0631\u0648\u0634\u06af\u0627\u0647 \u0648\u0631\u062f\u067e\u0631\u0633 DigiDev", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/914f5cd8ed824f752493de012b100125/5D8B442C/t51.2885-19/s150x150/50332535_1047114228797073_1984172863803883520_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1961596425255217201_5878346533", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5888850403, "username": "09.1783", "full_name": "\u06f0\u06f9\u06f1\u06f7\u06f8\u06f3", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/6718f82b1a40ed1b5109f12bebce7b19/5D893A87/t51.2885-19/s150x150/20839029_166484650586385_2581467218264981504_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1582602527487131260_5888850403", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 6298046349, "username": "x_mehdi_sultan_x", "full_name": "\u1d0d\u1d07\u029c\u1d05\u026a s\u029c\u1d00\u029c\u0299\u1d00\u1d22\u026a", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/4ee00a9ae819d0833f1e8b76ea20402b/5D9A7661/t51.2885-19/s150x150/23824442_169348966987795_6540030543675785216_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1655066658542429649_6298046349", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 5321778569, "username": "mohammad.r6081", "full_name": "\u2764/\\/\\\u25cb|-|/-\\ /\\/\\ /-\\ |)\u2764\u2664tk\u2664", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/ed481eba770423226fbd8589ba0a84a4/5D7BAD9C/t51.2885-19/s150x150/35575762_2038237983097372_4795148522888364032_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1810960512832217947_5321778569", "is_verified": false, "has_anonymous_profile_picture": false}, {"pk": 8102255176, "username": "k.82717", "full_name": "\ud83d\ude08", "is_private": true, "profile_pic_url": "https://instagram.fyvr4-1.fna.fbcdn.net/vp/635b97d35d913a3661ff89061dcf3bb1/5D7C0DF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=instagram.fyvr4-1.fna.fbcdn.net", "is_verified": false, "has_anonymous_profile_picture": true}], "big_list": false, "next_max_id": null, "page_size": 200, "status": "ok"}
    */
    public void getUserFollowers(final String maxID, final ResultConnection resultConnection) {
        getUserFollowers(IgUser.currentIgUser.userID, maxID, resultConnection);
    }

    public void getUserFollowers(String userID, final String maxID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/friendships/%s/followers/?search_surface=follow_list_page&max_id=%s", userID, maxID), resultConnection);
    }

    /*
      {"comment_likes_enabled": true, "comments": [{"pk": 17855175712321167, "user_id": 8471211875, "text": "\u067e\u0633\u062a \u0639\u0627\u0644\u06cc", "type": 0, "created_at": 1547267438, "created_at_utc": 1547267438, "content_type": "comment", "status": "Active", "bit_flags": 0, "user": {"pk": 8471211875, "username": "hoobareh_ad", "full_name": "\u0647\u0648\u0628\u0631\u0647 - \u06af\u0631\u0648\u0647 \u062a\u062e\u0635\u0635\u06cc IT", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/c2a7e0d53702fb2d48e6d547024b6440/5DA05A8F/t51.2885-19/s150x150/49745805_420191145384280_7366157994003791872_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1965219972034434029_8471211875", "is_verified": false}, "did_report_as_spam": false, "share_enabled": false, "has_liked_comment": false, "comment_like_count": 0, "inline_composer_display_condition": "never", "restricted_status": 0}], "comment_count": 1, "caption": {"pk": 17925275698015671, "user_id": 4191271519, "text": "\u0622\u0645\u0648\u0632\u0634 \u0628\u0631\u0631\u0633\u06cc \u0627\u062a\u0635\u0627\u0644 \u06af\u0648\u0634\u06cc \u0628\u0647 \u0633\u0631\u0648\u0631 \u062f\u0631 \u0628\u0631\u0646\u0627\u0645\u0647\n#\u0622\u0645\u0648\u0632\u0634_\u0627\u0646\u062f\u0631\u0648\u06cc\u062f\n\n\u0627\u0637\u0644\u0627\u0639\u0627\u062a \u0648 \u0622\u06af\u0627\u0647\u06cc \u0628\u06cc\u0634\u062a\u0631 :\nhttp://farasource.ir/check-server\n\n\u0633\u0627\u06cc\u062a \u0641\u0631\u0627 \u0633\u0648\u0631\u0633:\nfarasource.ir\n\n#Farasource", "type": 1, "created_at": 1519247419, "created_at_utc": 1519247419, "content_type": "comment", "status": "Active", "bit_flags": 0, "user": {"pk": 4191271519, "username": "farasource", "full_name": "\u0641\u0631\u0627 \u0633\u0648\u0631\u0633 | Fara Source", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/d9007097e7c1f94d28f905d09fcbbb85/5DB9A6C7/t51.2885-19/s150x150/61338376_587064731782813_5790911062396108800_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2057367026155302349_4191271519", "is_verified": false}, "did_report_as_spam": false, "share_enabled": false, "has_translation": true}, "caption_is_edited": false, "has_more_comments": false, "has_more_headload_comments": false, "media_header_display": "none", "display_realtime_typing_indicator": true, "preview_comments": [], "can_view_more_preview_comments": false, "status": "ok"}
     */
    public void getComments(final String mediaID, final String maxID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/media/%s/comments/?max_id=%s", mediaID, mediaID), resultConnection);
    }

    /*
        {"users": [{"pk": 3567615618, "username": "milad.moradi.nasab", "full_name": "\u0b9c\u06e9\u06e9\u0b9c \u1e42\u0197\u2c62\u0104\u0189 \u0b9c\u06e9\u06e9\u0b9c", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/9dd090a00160dcd7d720a9ec28cd1efc/5DC65744/t51.2885-19/s150x150/54513529_357463944854774_5258914968341839872_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2008127838905812853_3567615618", "is_verified": false}, {"pk": 6902514566, "username": "ciafus", "full_name": "CIAFUS", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/26109d9821023b049a15238c7f38cdef/5DA7338E/t51.2885-19/s150x150/26066994_1537731393003867_5124877177996705792_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1689804512493429250_6902514566", "is_verified": false}, {"pk": 5321778569, "username": "mohammad.r6081", "full_name": "\u2764/\\/\\\u25cb|-|/-\\ /\\/\\ /-\\ |)\u2764\u2664tk\u2664", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/78f1a99174b0d25b77b316568b432c83/5DA33A9C/t51.2885-19/s150x150/35575762_2038237983097372_4795148522888364032_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1810960512832217947_5321778569", "is_verified": false}, {"pk": 3025035149, "username": "m.arash_2019a", "full_name": "mr:mir.ARASH{ASKARI}", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/d6a098691590740a71a0faaae910efd5/5DA8DDF4/t51.2885-19/s150x150/54731656_1958861750891693_8915583410429231104_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2014880031507323913_3025035149", "is_verified": false}, {"pk": 8890006033, "username": "mohammadmoradpoor.shahmarvan", "full_name": "mohammadmoradpoor.shahmarvand", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/1d8bb682bd9e6249a11ac35c5d3720c4/5DAE87BD/t51.2885-19/46795613_504394240081801_7924297244751167488_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1929311938892653115_8890006033", "is_verified": false}, {"pk": 4565221523, "username": "mahdi_bahrami026", "full_name": "\u0648\u0642\u062a\u06cc \u062d\u0631\u0641 \u062f\u0644\u0645\u0648 \u0632\u062f\u0645 \u062f\u0644\u0634\u0648 \u0632\u062f\u064521", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/9c91588bfb38507966016bc94eb8ef4c/5DAE2704/t51.2885-19/s150x150/51726631_2219909041603168_645573373165830144_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1989921537070984647_4565221523", "is_verified": false}, {"pk": 6262970905, "username": "cesarpietrionline", "full_name": "Cesar Pietri", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/7ada7127328c65a1e7890fa7fe8caf57/5DB6A702/t51.2885-19/s150x150/22802001_296638100819728_8059682082869215232_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1631832168904711914_6262970905", "is_verified": false}, {"pk": 1726548897, "username": "shajahanali.bd", "full_name": "Shajahan Ali", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/db18ef13549e8136304125b6976bbf65/5DBA03BB/t51.2885-19/s150x150/13408837_1759457894275789_199824314_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1271769715009448349_1726548897", "is_verified": false, "latest_reel_media": 0}, {"pk": 8662993058, "username": "taha._.hmt", "full_name": "xl-TAHA", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/b3d04d69d3f349ee63f9526a193703c5/5DBB33FA/t51.2885-19/s150x150/61803520_1066652683525861_4781255729614946304_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2064866605369121533_8662993058", "is_verified": false, "latest_reel_media": 1562021311}, {"pk": 8398278436, "username": "mohsen_ciplex", "full_name": "ciplex\u200c", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/80c5e6252f970d38e4e89e68bd351bf2/5DA0F682/t51.2885-19/s150x150/60303648_650544122073545_6085643464983183360_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2044896270398319280_8398278436", "is_verified": false}, {"pk": 5725786021, "username": "volleyball_iran_2", "full_name": "erfan", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/2c1ce2a5e93dc4b3ea2f2daae71943a5/5DA380B2/t51.2885-19/s150x150/43708293_2238499309809171_3134255940488921088_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1887703289475209422_5725786021", "is_verified": false, "latest_reel_media": 0}, {"pk": 6285616806, "username": "reza1376mt", "full_name": "(reza-madahy) (\ub808\uc790)", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/de7e6458c27e07062fb3a7126823bc01/5DB4D499/t51.2885-19/s150x150/61034540_392663731459239_6302601925464424448_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2058659892595581504_6285616806", "is_verified": false}, {"pk": 7618738987, "username": "basiri1605", "full_name": "amirali basiri", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/d846fdfdd1b2cb7267cd7f4af469e443/5DA7CBD5/t51.2885-19/s150x150/44781089_258199865053863_4324054880407781376_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1771740263597996561_7618738987", "is_verified": false}, {"pk": 3617204662, "username": "naeimeh_javahri", "full_name": "Naeimeh\ud83d\udc8e\ud83c\udf19", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/152c9767aa7d679782a9f667c9cb018c/5DA2FA57/t51.2885-19/s150x150/49913276_468166990379782_1407416553325985792_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1803250906706165165_3617204662", "is_verified": false}, {"pk": 6130217658, "username": "wppolandcom", "full_name": "WPPoland WordPress development", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/89c4f81c935a32d0014c0d2b1eac1716/5DAEF019/t51.2885-19/s150x150/22280269_284886342005538_548530360934203392_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1621944163190719838_6130217658", "is_verified": false}, {"pk": 8702380352, "username": "asaljoon8511", "full_name": "\u0639\u0633\u0644 \u0645\u0631\u0627\u062f\u06cc", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/98d32542905b3722dd8e849b1e81d29e/5DB06F7D/t51.2885-19/s150x150/42810372_282906705893376_642545833539010560_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1889419833429007035_8702380352", "is_verified": false, "latest_reel_media": 0}, {"pk": 8523330497, "username": "omid_darvishpour_2", "full_name": "\u0634\u0646\u0648 \u0634\u0627\u0631\u0647 \u0633\u0631\u0628\u0647 \u0631\u0632\u06a9\u0645", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/a1b37e94e6e27eed451322bbfc0fc98a/5DBE6E6A/t51.2885-19/s150x150/64910541_1606672896135492_3167177010841649152_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2075259039421271205_8523330497", "is_verified": false}, {"pk": 8361600586, "username": "binamoriginal", "full_name": "", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/24e109b6c21eb4b81031ef45b84c498a/5DBD789E/t51.2885-19/s150x150/37701703_2178248622457586_2575246666806329344_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1838497569517459562_8361600586", "is_verified": false, "latest_reel_media": 0}, {"pk": 5669501643, "username": "mhdiii_asghary", "full_name": "M.A10\u26a1\ud83d\udc8e\ud83d\udca5", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/dbf038be5ba2fee6722739433b67bef7/5DC4CFEB/t51.2885-19/s150x150/61743237_369017143971944_7568406105596887040_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2059147795571696483_5669501643", "is_verified": false}, {"pk": 6187746506, "username": "webdoktoru", "full_name": "Web Doktoru", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/985bee7d593555ec502a400bc739d620/5DA5D294/t51.2885-19/s150x150/24838752_893503250827979_6427394515237076992_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1621248307101952522_6187746506", "is_verified": false, "latest_reel_media": 0}, {"pk": 7614425935, "username": "mahdiyeh_k17", "full_name": "mahdiyeh_ka", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/a7539f45cd4820d770f57fb5d012e53e/5DC535D4/t51.2885-19/s150x150/64783593_2324746627792853_2933126088055848960_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2077792342102180372_7614425935", "is_verified": false, "latest_reel_media": 1561997186}, {"pk": 14111445844, "username": "abbasghasemi_tajic", "full_name": "abbas qasemi", "is_private": false, "profile_pic_url": "https://scontent.cdninstagram.com/vp/e0f6c6cd37871d159bec36453412c0e7/5DA39AF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=scontent.cdninstagram.com", "is_verified": false}, {"pk": 7968315738, "username": "sinderela_a78", "full_name": "A.sh.m", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/722b74925ab1504a6c8ca059a8cff085/5DC01EAA/t51.2885-19/s150x150/65291656_634247057052852_5711887306281975808_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2076278963224658755_7968315738", "is_verified": false}, {"pk": 9023061467, "username": "shopdroid.ir", "full_name": "shopdroid", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/6ec68cb552f0587515057b49acbd95cd/5DA71AC2/t51.2885-19/s150x150/44535177_2166452063394295_125063597914062848_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1915175568721046067_9023061467", "is_verified": false}, {"pk": 7905116000, "username": "mahdi_seilzadeh", "full_name": "mahdi_seilzadeh", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/d7b3d1496d296365dd6093dacc0b7c9f/5DB7C8A6/t51.2885-19/s150x150/49933395_341169686486812_7701483425290518528_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1964031135992219931_7905116000", "is_verified": false, "latest_reel_media": 0}, {"pk": 5502397920, "username": "comparethehosts", "full_name": "Comparethehosts.com", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/1f04a06a7b1684120a8778e91315fc0e/5DBC9752/t51.2885-19/s150x150/23099299_1673106129413787_4828414655208620032_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1641457203884139546_5502397920", "is_verified": false}, {"pk": 8897685994, "username": "8980aag", "full_name": "", "is_private": true, "profile_pic_url": "https://scontent.cdninstagram.com/vp/e0f6c6cd37871d159bec36453412c0e7/5DA39AF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=scontent.cdninstagram.com", "is_verified": false, "latest_reel_media": 0}, {"pk": 7646819566, "username": "arezoo_borumand", "full_name": "\u043d\u0310\u0337\u0337 \u0454\u0310\u0337\u0337 \u2113\u0310\u0337\u0337 \u026a\u0307\u0310\u0337\u0337 \u043f\u0335\u0310\u0337\u0337", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/47f884b564028845017561e9867a6bcb/5DC4D18F/t51.2885-19/s150x150/41449775_1191407820998304_4397720239618392064_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1851546441564359713_7646819566", "is_verified": false}, {"pk": 5850274288, "username": "linux.sysadmin", "full_name": "Saman", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/e18b3ec6945f728a829fa5b4cff7ee87/5DABCB49/t51.2885-19/s150x150/46233547_360788121377109_6687300986525974528_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1929353144564585097_5850274288", "is_verified": false}, {"pk": 5599325299, "username": "h.a.m.e.d.83", "full_name": "Hamed", "is_private": false, "profile_pic_url": "https://scontent.cdninstagram.com/vp/e0f6c6cd37871d159bec36453412c0e7/5DA39AF1/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=scontent.cdninstagram.com", "is_verified": false, "latest_reel_media": 0}, {"pk": 4771871608, "username": "matin_rostamian", "full_name": "Matin Rostamian", "is_private": true, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/7412b8d9a20dbcde95f117c0822b8d57/5DA5288A/t51.2885-19/s150x150/52639041_364684274132940_2768391706884702208_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1999404481641392006_4771871608", "is_verified": false, "latest_reel_media": 0}, {"pk": 8681026553, "username": "yasin_.rahimi._", "full_name": "\u2764\u10e7\uff91\u24e2\u0268\u014b\u2764", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/2e01706a02222d2b5748a212e3f506e5/5DA4F22B/t51.2885-19/s150x150/64407473_359276018123669_149691365591089152_n.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "2076753213202542613_8681026553", "is_verified": false}, {"pk": 4076045818, "username": "ali.h733", "full_name": "ali\ud83d\udc99\ud83d\udc99\ud83d\udc99\ud83d\udc99", "is_private": false, "profile_pic_url": "https://instagram.fdoh5-1.fna.fbcdn.net/vp/835f8818a879c79f64f77a5a5a826b9b/5DA44B08/t51.2885-19/s150x150/14719718_1824580497810617_8816975853280821248_a.jpg?_nc_ht=instagram.fdoh5-1.fna.fbcdn.net", "profile_pic_id": "1368150201932194767_4076045818", "is_verified": false}], "user_count": 33, "status": "ok"}
     */
    public void getLikers(final String mediaID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/media/%s/likers/", mediaID), resultConnection);
    }

    public void staticUserThread(final String userID, final ResultConnection resultConnection) {
        getThread(String.format("https://i.instagram.com/api/v1/friendships/show/%s/", userID), (status, object) -> {
            if (status == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //
                }
                getThread(String.format("https://i.instagram.com/api/v1/users/%s/info/", userID), (status1, object1) -> {
                    try {
                        resultConnection.result(null, new JSONObject().put("status", object1 == null ? "2" : "1"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                try {
                    resultConnection.result(null, new JSONObject().put("status", "3"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void mediaSearch(String code, final ResultConnection resultConnection) {
        if (code.length() > 11) {
            code = code.substring(0, 11);
        }
        long mediaid = 0;
        try {
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
            for (int i = 0; i < code.toCharArray().length; i++) {
                int index = alphabet.indexOf(code.toCharArray()[i]);
                if (index < 0) {
                    resultConnection.result(IgResponse.responseServerError(-1), null);
                    return;
                }
                mediaid = mediaid * 64 + index;
            }
        } catch (Exception e) {
            resultConnection.result(IgResponse.responseServerError(-1), null);
            return;
        }
        get(String.format("https://i.instagram.com/api/v1/media/%s/info/", mediaid), resultConnection);
    }

    public void userSearch(String user, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/users/search/?search_surface=user_search_page&timezone_offset=%s&q=%s&count=30", timezone_offset, user), resultConnection);
    }

    public void getStories(String userID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/feed/user/%s/story/?supported_capabilities_new=[{\"value\":\"119.0,120.0,121.0,122.0,123.0,124.0,125.0,126.0,127.0,128.0,129.0,130.0,131.0,132.0,133.0,134.0,135.0,136.0,137.0,138.0,139.0,140.0,141.0,142.0\",\"name\":\"SUPPORTED_SDK_VERSIONS\"},{\"value\":\"14\",\"name\":\"FACE_TRACKER_VERSION\"},{\"value\":\"ETC2_COMPRESSION\",\"name\":\"COMPRESSION\"},{\"value\":\"gyroscope_enabled\",\"name\":\"gyroscope\"}]",
                        userID),
                resultConnection
        );
    }

    public void getHighlight(String userID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/highlights/%s/highlights_tray/?supported_capabilities_new=[{\"name\":\"SUPPORTED_SDK_VERSIONS\",\"value\":\"73.0,74.0,75.0,76.0,77.0,78.0,79.0,80.0,81.0,82.0,83.0,84.0,85.0,86.0,87.0,88.0,89.0,90.0,91.0,92.0,93.0,94.0,95.0,96.0,97.0,98.0,99.0,100.0,101.0,102.0,103.0,104.0,105.0,106.0,107.0,108.0,109.0\"},{\"name\":\"FACE_TRACKER_VERSION\",\"value\":\"14\"},{\"name\":\"segmentation\",\"value\":\"segmentation_enabled\"},{\"name\":\"COMPRESSION\",\"value\":\"ETC2_COMPRESSION\"},{\"name\":\"world_tracker\",\"value\":\"world_tracker_enabled\"},{\"name\":\"gyroscope\",\"value\":\"gyroscope_enabled\"}]&phone_id=%s&battery_level=100&is_charging=1&is_dark_mode=0&will_sound_on=0",
                        userID, IgUser.currentIgUser.phoneID),
                resultConnection
        );
    }

    public void getHighlightMedias(String userID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/feed/reels_media/?reel_ids=%s", userID), resultConnection);
    }

    public void getFriendships(String userID, final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/friendships/show/%s/", userID), resultConnection);
    }

    public void remove(final String userID, final ResultConnection resultConnection) {
        post(String.format("https://i.instagram.com/api/v1/friendships/remove_follower/%s/", userID),
                new JsonBuilder()
                        .addBody("_uid", IgUser.currentIgUser.userID)
                        .addBody("_uuid", IgUser.currentIgUser.deviceID)
                        .addBody("device_id", IgUser.currentIgUser.androidID)
                        .addBody("radio_type", "wifi-none")
                        .addBody("user_id", userID)
                        .toString(),
                resultConnection);
    }

    public void follow(final String userID, final ResultConnection resultConnection) {
        post(String.format("https://i.instagram.com/api/v1/friendships/create/%s/", userID),
                new JsonBuilder()
                        .addBody("_uid", IgUser.currentIgUser.userID)
                        .addBody("_uuid", IgUser.currentIgUser.deviceID)
                        .addBody("device_id", IgUser.currentIgUser.androidID)
                        .addBody("radio_type", "wifi-none")
                        .addBody("user_id", userID)
                        .toString(),
                resultConnection);
    }

    public void unFollow(final String userID, final ResultConnection resultConnection) {
        post(String.format("https://i.instagram.com/api/v1/friendships/destroy/%s/", userID),
                new JsonBuilder()
                        .addBody("_uid", IgUser.currentIgUser.userID)
                        .addBody("_uuid", IgUser.currentIgUser.deviceID)
                        .addBody("device_id", IgUser.currentIgUser.androidID)
                        .addBody("radio_type", "wifi-none")
                        .addBody("user_id", userID)
                        .toString(),
                resultConnection);
    }


    // plus

    public void zrToken2(final ResultConnection resultConnection) {
        get(String.format("https://b.i.instagram.com/api/v1/zr/token/result/?device_id=%s&token_hash=&custom_device_id=%s&fetch_reason=token_expired",
                        IgUser.currentIgUser.androidID, IgUser.currentIgUser.deviceID)
                , resultConnection);
    }

    public void bGetAccountFamily(final ResultConnection resultConnection) {
        get("https://b.i.instagram.com/api/v1/multiple_accounts/get_account_family/", resultConnection);
    }

    public void bLauncherSync3(final ResultConnection resultConnection) {
        post("https://b.i.instagram.com/api/v1/launcher/sync/", new JsonBuilder()
                .addBody("_uuid", IgUser.currentIgUser.deviceID)
                .addBody("server_config_retrieval", "1")
                .addBody("id", IgUser.currentIgUser.deviceID)
                .addBody("_uid", IgUser.currentIgUser.userID)
                .addBody("server_config_retrieval", "1")
                .addBody("experiments", "ig_android_video_upload_transform_matrix_fix_universe,ig_android_test_not_signing_address_book_unlink_endpoint,ig_android_whitehat_options_universe,ig_android_camera_reduce_file_exif_reads,ig_android_render_thread_memory_leak_holdout,ig_android_xposting_feed_to_stories_reshares_universe,ig_android_realtime_mqtt_logging,ig_android_video_exoplayer_2,ig_android_igtv_whitelisted_for_web,ig_explore_reel_ring_universe,ig_android_video_raven_bitrate_ladder_universe,ig_camera_android_effect_metadata_cache_refresh_universe,ig_android_vc_background_call_toast_universe,ig_android_stories_sundial_creation_universe,ig_camera_android_gallery_search_universe,ig_android_business_attribute_sync,ig_android_image_exif_metadata_ar_effect_id_universe,ig_android_fb_link_ui_polish_universe,ig_android_location_integrity_universe,ig_android_camera_gyro_universe,ig_android_appstate_logger,ig_payment_checkout_info,ig_android_business_promote_tooltip,mi_viewpoint_viewability_universe,ig_android_multi_thread_sends,ig_stories_allow_camera_actions_while_recording,ig_android_new_follower_removal_universe,ig_android_shopping_bag_optimization_universe,ig_android_disable_manual_retries,ig_android_show_create_content_pages_universe,ig_android_optic_new_architecture,ig_android_direct_leave_from_group_message_requests,ig_android_fbc_upsell_on_dp_first_load,ig_android_profile_thumbnail_impression,ig_branded_content_tagging_approval_request_flow_brand_side_v2,ig_android_feed_post_warning_universe,ig_android_branded_content_appeal_states,ig_android_zero_rating_carrier_signal,ig_android_apr_lazy_build_request_infra,ig_android_igtv_autoplay_on_prepare,ig_android_business_transaction_in_stories_creator,ig_ei_option_setting_universe,ig_android_music_browser_redesign,ig_android_stories_quick_react_gif_universe,ig_android_vc_explicit_intent_for_notification,ig_shopping_insights_wc_copy_update_android,ig_android_audience_control,ig_android_recognition_tracking_thread_prority_universe,ig_android_business_transaction_in_stories_consumer,ig_android_claim_location_page,ig_android_vc_cpu_overuse_universe,ig_android_purx_native_checkout_universe,ig_android_direct_inbox_cache_universe,ig_android_place_signature_universe,ig_android_analytics_background_uploader_schedule,ig_android_frx_highlight_cover_reporting_qe,ig_android_emoji_util_universe_3,ig_android_user_url_deeplink_fbpage_endpoint,ig_android_video_visual_quality_score_based_abr,ig_android_ads_data_preferences_universe,ig_android_ads_rendering_logging,ig_android_image_pdq_calculation,ig_android_camera_upsell_dialog,ig_android_wishlist_reconsideration_universe,ig_android_photo_creation_large_width,ig_android_interest_follows_universe,ig_android_country_code_fix_universe,ig_cameracore_android_new_optic_camera2,ig_android_invite_list_button_redesign_universe,ig_rti_inapp_notifications_universe,ig_stories_ads_media_based_insertion,ig_android_logged_in_delta_migration,ig_direct_android_bubble_system,ig_android_explore_discover_people_entry_point_universe,ig_android_payments_growth_promote_payments_in_payments,ig_android_enable_zero_rating,ig_android_stories_vpvd_container_module_fix,ig_android_shopping_product_metadata_on_product_tiles_universe,instagram_android_profile_follow_cta_context_feed,ig_explore_2019_h1_destination_cover,ig_android_fs_new_gallery_hashtag_prompts,ig_android_ig_personal_account_to_fb_page_linkage_backfill,ig_android_rainbow_hashtags,ig_android_stories_music_lyrics,ig_android_music_story_fb_crosspost_universe,ig_emoji_render_counter_logging_universe,ig_android_stories_video_prefetch_kb,ig_android_live_subscribe_user_level_universe,ig_camera_android_feed_effect_attribution_universe,ig_android_create_mode_tap_to_cycle,ig_android_fb_sync_options_universe,ig_threads_sanity_check_thread_viewkeys,ig_explore_2019_h1_video_autoplay_resume,ig_shopping_checkout_improvements_universe,ig_android_sidecar_segmented_streaming_universe,ig_android_persistent_nux,ig_payments_billing_address,ig_android_logging_metric_universe_v2,ig_stories_rainbow_ring,ig_mprotect_code_universe,ig_android_direct_add_member_dialog_universe,ig_android_separate_empty_feed_su_universe,ig_android_vc_capture_universe,ig_business_new_value_prop_universe,ig_android_wellbeing_support_frx_hashtags_reporting,ig_android_qr_code_nametag,ig_android_account_insights_shopping_content_universe,ig_xposting_biz_feed_to_story_reshare,ig_android_suggested_users_background,ig_android_whats_app_contact_invite_universe,ig_android_video_abr_universe,ig_android_create_mode_memories_see_all,ig_android_on_notification_cleared_async_universe,ig_android_network_perf_qpl_ppr,ig_sim_api_analytics_reporting,ig_android_video_call_finish_universe,ig_android_fbpage_on_profile_side_tray,ig_android_xposting_dual_destination_shortcut_fix,ig_android_viewpoint_occlusion,ig_android_stories_music_search_typeahead,ig_android_stories_boomerang_v2_universe,ig_android_vc_cowatch_universe,ig_android_self_story_button_non_fbc_accounts,ig_shopping_checkout_mvp_experiment,ig_android_inline_editing_local_prefill,ig_camera_android_paris_filter_universe,ig_android_ttcp_improvements,ig_android_share_publish_page_universe,ig_android_stories_project_eclipse,instagram_ns_qp_prefetch_universe,ig_cameracore_android_new_optic_camera2_galaxy,ig_android_optic_photo_cropping_fixes,ig_android_stories_context_sheets_universe,ig_android_search_nearby_places_universe,ig_android_create_mode_templates,ig_android_push_reliability_universe,ig_inventory_connections,ig_android_edit_location_page_info,ig_android_fb_profile_integration_universe,ig_biz_growth_insights_universe,ig_android_direct_multi_upload_universe,ig_android_shopping_pdp_post_purchase_sharing,ig_android_stories_weblink_creation,ig_promote_interactive_poll_sticker_igid_universe,ig_android_product_breakdown_post_insights,ig_android_ad_stories_scroll_perf_universe,aymt_instagram_promote_flow_abandonment_ig_universe,ig_android_biz_story_to_fb_page_improvement,ig_android_dead_code_detection,ig_android_direct_view_more_qe,ig_android_direct_mutation_manager_media_3,ig_android_camera_formats_ranking_universe,ig_android_li_session_chaining,ig_commerce_platform_ptx_bloks_universe,ig_android_smplt_universe,ig_android_memory_use_logging_universe,ig_android_igtv_player_follow_button,ig_direct_max_participants,ig_camera_android_facetracker_v12_universe,ig_android_direct_new_gallery,ig_android_viewpoint_stories_public_testing,ig_android_stories_video_seeking_audio_bug_fix,ig_android_reel_raven_video_segmented_upload_universe,ig_android_stories_music_overlay,ig_disable_fsync_universe,ig_android_sso_use_trustedapp_universe,ig_search_hashtag_content_advisory_remove_snooze,ig_biz_post_approval_nux_universe,ig_android_recipient_picker,ig_android_video_streaming_upload_universe,ig_android_save_all,ig_android_direct_block_from_group_message_requests,ig_camera_android_subtle_filter_universe,ig_prefetch_scheduler_backtest,ig_android_save_to_collections_flow,ig_android_shopping_bag_null_state_v1,ig_traffic_routing_universe,ig_android_contact_point_upload_rate_limit_killswitch,ig_challenge_general_v2,ig_android_create_page_on_top_universe,ig_android_video_upload_quality_qe1,ig_android_stories_music_awareness_universe,ig_android_insights_post_dismiss_button,ig_android_publisher_stories_migration,ig_android_xposting_newly_fbc_people,ig_camera_android_share_effect_link_universe,ig_carousel_bumped_organic_impression_client_universe,ig_android_unfollow_from_main_feed_v2,ig_android_disk_usage_logging_universe,ig_android_external_gallery_import_affordance,ig_shopping_size_selector_redesign,ig_android_personal_user_xposting_destination_fix,ig_android_igtv_browse_long_press,ig_android_sso_kototoro_app_universe,ig_android_video_raven_streaming_upload_universe,ig_android_business_cross_post_with_biz_id_infra,ig_android_stories_gallery_video_segmentation,ig_android_stories_question_sticker_music_format,ig_android_live_egl10_compat,ig_android_vc_face_effects_universe,ig_android_xposting_reel_memory_share_universe,ig_camera_android_gyro_senser_sampling_period_universe,ig_android_fs_creation_flow_tweaks,ig_android_view_info_universe,ig_payment_checkout_cvv,ig_android_skip_button_content_on_connect_fb_universe,ig_android_stories_blacklist,ig_android_tango_cpu_overuse_universe,ig_explore_2018_post_chaining_account_recs_dedupe_universe,ig_android_igtv_stories_preview,ig_android_camera_leak,ig_android_page_claim_deeplink_qe,ig_android_live_webrtc_livewith_params,ig_android_comment_warning_non_english_universe,ig_android_direct_mark_as_read_notif_action,instagram_shopping_hero_carousel_visual_variant_consolidation,ig_threads_clear_notifications_on_has_seen,ig_android_partial_share_sheet,ig_android_video_upload_hevc_encoding_universe,ig_android_vc_migrate_to_bluetooth_v2_universe,ig_android_qp_kill_switch,ig_android_recommend_accounts_destination_routing_fix,ig_android_igtv_refresh_tv_guide_interval,ig_camera_android_bg_processor,ig_android_react_native_email_sms_settings_universe,ig_android_qr_code_scanner,ig_android_video_qp_logger_universe,ig_android_vc_shareable_moments_universe,ig_android_direct_segmented_video,ig_android_fb_url_universe,ig_android_feed_ads_ppr_universe,ig_android_direct_aggregated_media_and_reshares,ig_rn_branded_content_settings_approval_on_select_save,ig_android_video_fit_scale_type_igtv,ig_android_optic_face_detection,ig_android_recyclerview_binder_group_enabled_universe,ig_android_igtv_pip,ig_android_arengine_remote_scripting_universe,ig_android_enable_automated_instruction_text_ar,ig_android_video_product_specific_abr,ig_video_experimental_encoding_consumption_universe,ig_android_direct_wellbeing_message_reachability_settings,ig_android_stories_share_extension_video_segmentation,ig_android_feed_cache_update,ig_android_image_upload_quality_universe,igqe_pending_tagged_posts,ig_android_stories_layout_universe,ig_android_feed_post_sticker,ig_android_explore_recyclerview_universe,ig_branded_content_settings_unsaved_changes_dialog,ig_android_graphql_survey_new_proxy_universe,ig_android_igtv_explore2x2_viewer,ig_android_direct_message_follow_button,ig_android_stories_gallery_sticker_universe,android_ig_cameracore_aspect_ratio_fix,ig_stories_ads_delivery_rules,ig_android_reel_tray_item_impression_logging_viewpoint,ig_android_frx_creation_question_responses_reporting,ig_android_canvas_cookie_universe,ig_xposting_mention_reshare_stories,ig_pacing_overriding_universe,ig_android_wellbeing_timeinapp_v1_universe,ig_android_media_remodel,ig_android_video_raven_passthrough,ig_shopping_checkout_improvements_v2_universe,ig_android_vc_cowatch_media_share_universe")
                .toString(), resultConnection);
    }

    public void bSync2(final ResultConnection resultConnection) {
        post("https://b.i.instagram.com/api/v1/qe/sync/", new JsonBuilder()
                .addBody("experiments", "ig_android_video_raven_streaming_upload_universe,ig_android_vc_explicit_intent_for_notification,ig_stories_ads_delivery_rules,ig_shopping_checkout_improvements_universe,ig_business_new_value_prop_universe,ig_android_suggested_users_background,ig_android_stories_music_search_typeahead,ig_android_direct_mutation_manager_media_3,ig_android_shopping_bag_null_state_v1,ig_camera_android_feed_effect_attribution_universe,ig_android_test_not_signing_address_book_unlink_endpoint,ig_android_stories_share_extension_video_segmentation,ig_android_search_nearby_places_universe,ig_android_vc_migrate_to_bluetooth_v2_universe,ig_ei_option_setting_universe,instagram_ns_qp_prefetch_universe,ig_android_camera_leak,ig_android_separate_empty_feed_su_universe,ig_stories_rainbow_ring,ig_android_zero_rating_carrier_signal,ig_explore_2019_h1_destination_cover,ig_android_explore_recyclerview_universe,ig_android_image_pdq_calculation,ig_camera_android_subtle_filter_universe,ig_android_whats_app_contact_invite_universe,ig_android_direct_add_member_dialog_universe,ig_android_xposting_reel_memory_share_universe,ig_android_viewpoint_stories_public_testing,ig_android_photo_creation_large_width,ig_android_save_all,ig_android_video_upload_hevc_encoding_universe,instagram_shopping_hero_carousel_visual_variant_consolidation,ig_android_vc_face_effects_universe,ig_android_fbpage_on_profile_side_tray,ig_android_ttcp_improvements,ig_android_igtv_refresh_tv_guide_interval,ig_android_recyclerview_binder_group_enabled_universe,ig_android_video_exoplayer_2,ig_rn_branded_content_settings_approval_on_select_save,ig_android_account_insights_shopping_content_universe,ig_branded_content_tagging_approval_request_flow_brand_side_v2,ig_android_render_thread_memory_leak_holdout,ig_threads_clear_notifications_on_has_seen,ig_android_xposting_dual_destination_shortcut_fix,ig_android_show_create_content_pages_universe,ig_android_camera_reduce_file_exif_reads,ig_android_disk_usage_logging_universe,ig_android_stories_blacklist,ig_payments_billing_address,ig_android_fs_new_gallery_hashtag_prompts,ig_android_video_product_specific_abr,ig_android_sidecar_segmented_streaming_universe,ig_camera_android_gyro_senser_sampling_period_universe,ig_android_xposting_feed_to_stories_reshares_universe,ig_android_stories_layout_universe,ig_emoji_render_counter_logging_universe,ig_android_vc_cpu_overuse_universe,ig_android_image_upload_quality_universe,ig_android_invite_list_button_redesign_universe,ig_android_react_native_email_sms_settings_universe,ig_android_enable_zero_rating,ig_android_direct_leave_from_group_message_requests,ig_android_publisher_stories_migration,aymt_instagram_promote_flow_abandonment_ig_universe,ig_android_whitehat_options_universe,ig_android_stories_context_sheets_universe,ig_android_stories_vpvd_container_module_fix,instagram_android_profile_follow_cta_context_feed,ig_android_personal_user_xposting_destination_fix,ig_android_stories_boomerang_v2_universe,ig_android_direct_message_follow_button,ig_android_video_raven_passthrough,ig_android_vc_cowatch_universe,ig_shopping_insights_wc_copy_update_android,ig_stories_ads_media_based_insertion,ig_android_analytics_background_uploader_schedule,ig_android_wellbeing_timeinapp_v1_universe,ig_android_feed_ads_ppr_universe,ig_android_igtv_browse_long_press,ig_xposting_mention_reshare_stories,ig_threads_sanity_check_thread_viewkeys,ig_android_vc_shareable_moments_universe,ig_android_igtv_stories_preview,ig_android_shopping_product_metadata_on_product_tiles_universe,ig_android_stories_quick_react_gif_universe,ig_android_video_qp_logger_universe,ig_android_stories_weblink_creation,ig_android_frx_highlight_cover_reporting_qe,ig_android_vc_capture_universe,ig_android_optic_face_detection,ig_android_save_to_collections_flow,ig_android_direct_segmented_video,ig_android_stories_video_prefetch_kb,ig_android_direct_mark_as_read_notif_action,ig_android_product_breakdown_post_insights,ig_inventory_connections,ig_android_canvas_cookie_universe,ig_android_video_streaming_upload_universe,ig_android_smplt_universe,ig_cameracore_android_new_optic_camera2,ig_android_partial_share_sheet,ig_android_fbc_upsell_on_dp_first_load,ig_android_stories_sundial_creation_universe,ig_android_music_story_fb_crosspost_universe,ig_android_payments_growth_promote_payments_in_payments,ig_carousel_bumped_organic_impression_client_universe,ig_android_business_attribute_sync,ig_biz_post_approval_nux_universe,ig_camera_android_bg_processor,ig_android_ig_personal_account_to_fb_page_linkage_backfill,ig_android_ad_stories_scroll_perf_universe,ig_android_persistent_nux,ig_android_tango_cpu_overuse_universe,ig_android_direct_wellbeing_message_reachability_settings,ig_android_edit_location_page_info,ig_android_unfollow_from_main_feed_v2,ig_android_stories_project_eclipse,ig_direct_android_bubble_system,ig_android_frx_creation_question_responses_reporting,ig_android_li_session_chaining,ig_android_create_mode_memories_see_all,ig_android_feed_post_warning_universe,ig_mprotect_code_universe,ig_android_video_visual_quality_score_based_abr,ig_explore_2018_post_chaining_account_recs_dedupe_universe,ig_android_view_info_universe,ig_android_camera_upsell_dialog,ig_android_business_transaction_in_stories_consumer,ig_android_dead_code_detection,ig_android_stories_video_seeking_audio_bug_fix,ig_android_qp_kill_switch,ig_android_new_follower_removal_universe,ig_android_feed_post_sticker,ig_android_business_cross_post_with_biz_id_infra,ig_android_inline_editing_local_prefill,ig_android_reel_tray_item_impression_logging_viewpoint,ig_android_video_abr_universe,ig_android_vc_cowatch_media_share_universe,ig_challenge_general_v2,ig_android_place_signature_universe,ig_android_direct_inbox_cache_universe,ig_android_business_promote_tooltip,ig_android_wellbeing_support_frx_hashtags_reporting,ig_android_direct_aggregated_media_and_reshares,ig_camera_android_facetracker_v12_universe,igqe_pending_tagged_posts,ig_sim_api_analytics_reporting,ig_android_interest_follows_universe,ig_android_direct_view_more_qe,ig_android_audience_control,ig_android_memory_use_logging_universe,ig_camera_android_paris_filter_universe,ig_android_igtv_whitelisted_for_web,ig_rti_inapp_notifications_universe,ig_android_share_publish_page_universe,ig_direct_max_participants,ig_commerce_platform_ptx_bloks_universe,ig_android_video_raven_bitrate_ladder_universe,ig_android_recipient_picker,ig_android_graphql_survey_new_proxy_universe,ig_android_music_browser_redesign,ig_android_disable_manual_retries,ig_android_qr_code_nametag,ig_android_purx_native_checkout_universe,ig_android_fs_creation_flow_tweaks,ig_android_apr_lazy_build_request_infra,ig_android_business_transaction_in_stories_creator,ig_cameracore_android_new_optic_camera2_galaxy,ig_android_branded_content_appeal_states,ig_android_claim_location_page,ig_android_location_integrity_universe,ig_video_experimental_encoding_consumption_universe,ig_android_biz_story_to_fb_page_improvement,ig_shopping_checkout_improvements_v2_universe,ig_android_create_mode_tap_to_cycle,ig_android_fb_profile_integration_universe,ig_android_shopping_bag_optimization_universe,ig_android_create_page_on_top_universe,android_ig_cameracore_aspect_ratio_fix,ig_android_skip_button_content_on_connect_fb_universe,ig_android_igtv_explore2x2_viewer,ig_android_network_perf_qpl_ppr,ig_android_insights_post_dismiss_button,ig_xposting_biz_feed_to_story_reshare,ig_android_user_url_deeplink_fbpage_endpoint,ig_android_comment_warning_non_english_universe,ig_android_stories_question_sticker_music_format,ig_promote_interactive_poll_sticker_igid_universe,ig_android_feed_cache_update,ig_pacing_overriding_universe,ig_explore_reel_ring_universe,ig_android_igtv_pip,ig_android_wishlist_reconsideration_universe,ig_android_sso_use_trustedapp_universe,ig_android_stories_music_lyrics,ig_android_camera_formats_ranking_universe,ig_android_direct_multi_upload_universe,ig_android_stories_music_awareness_universe,ig_explore_2019_h1_video_autoplay_resume,ig_android_video_upload_quality_qe1,ig_android_country_code_fix_universe,ig_android_stories_music_overlay,ig_android_multi_thread_sends,ig_android_emoji_util_universe_3,ig_android_shopping_pdp_post_purchase_sharing,ig_branded_content_settings_unsaved_changes_dialog,ig_android_realtime_mqtt_logging,ig_android_rainbow_hashtags,ig_android_create_mode_templates,ig_android_direct_block_from_group_message_requests,ig_android_live_subscribe_user_level_universe,ig_android_video_call_finish_universe,ig_android_viewpoint_occlusion,ig_biz_growth_insights_universe,ig_android_logged_in_delta_migration,ig_android_push_reliability_universe,ig_android_self_story_button_non_fbc_accounts,ig_android_stories_gallery_video_segmentation,ig_android_explore_discover_people_entry_point_universe,ig_android_live_webrtc_livewith_params,ig_camera_android_effect_metadata_cache_refresh_universe,ig_android_appstate_logger,ig_prefetch_scheduler_backtest,ig_android_ads_data_preferences_universe,ig_payment_checkout_cvv,ig_android_vc_background_call_toast_universe,ig_android_fb_link_ui_polish_universe,ig_android_qr_code_scanner,ig_disable_fsync_universe,mi_viewpoint_viewability_universe,ig_android_live_egl10_compat,ig_android_camera_gyro_universe,ig_android_video_upload_transform_matrix_fix_universe,ig_android_fb_url_universe,ig_android_reel_raven_video_segmented_upload_universe,ig_android_fb_sync_options_universe,ig_android_stories_gallery_sticker_universe,ig_android_recommend_accounts_destination_routing_fix,ig_android_enable_automated_instruction_text_ar,ig_traffic_routing_universe,ig_stories_allow_camera_actions_while_recording,ig_shopping_checkout_mvp_experiment,ig_android_video_fit_scale_type_igtv,ig_android_igtv_player_follow_button,ig_android_arengine_remote_scripting_universe,ig_android_page_claim_deeplink_qe,ig_android_logging_metric_universe_v2,ig_android_xposting_newly_fbc_people,ig_android_recognition_tracking_thread_prority_universe,ig_android_contact_point_upload_rate_limit_killswitch,ig_android_optic_photo_cropping_fixes,ig_camera_android_gallery_search_universe,ig_android_sso_kototoro_app_universe,ig_android_profile_thumbnail_impression,ig_android_media_remodel,ig_camera_android_share_effect_link_universe,ig_android_igtv_autoplay_on_prepare,ig_android_ads_rendering_logging,ig_shopping_size_selector_redesign,ig_android_image_exif_metadata_ar_effect_id_universe,ig_android_optic_new_architecture,ig_android_external_gallery_import_affordance,ig_search_hashtag_content_advisory_remove_snooze,ig_android_on_notification_cleared_async_universe,ig_android_direct_new_gallery,ig_payment_checkout_info")
                .addBody("_uid", IgUser.currentIgUser.userID)
                .addBody("id", IgUser.currentIgUser.deviceID)
                .addBody("_uuid", IgUser.currentIgUser.deviceID)
                .addBody("server_config_retrieval", "1").toString(), resultConnection);
    }

    public void bBanyan(final ResultConnection resultConnection) {
        get("https://b.i.instagram.com/api/v1/banyan/banyan/?views=[\"direct_user_search_keypressed\",\"group_stories_share_sheet\",\"call_recipients\",\"reshare_share_sheet\",\"direct_inbox_active_now\",\"story_share_sheet\",\"forwarding_recipient_sheet\",\"direct_user_search_nullstate\",\"threads_people_picker\"]", resultConnection);
    }

    public void bReelsTray(boolean init, final ResultConnection resultConnection) {
        String reason = "cold_start";
        if (!init) {
            reason = "pull_to_refresh";
        }
        post("https://b.i.instagram.com/api/v1/feed/reels_tray/", signed_body + "supported_capabilities_new=[{\"name\":\"SUPPORTED_SDK_VERSIONS\",\"value\":\"73.0,74.0,75.0,76.0,77.0,78.0,79.0,80.0,81.0,82.0,83.0,84.0,85.0,86.0,87.0,88.0,89.0,90.0,91.0,92.0,93.0,94.0,95.0,96.0,97.0,98.0,99.0,100.0,101.0,102.0,103.0,104.0,105.0,106.0,107.0,108.0,109.0\"},{\"name\":\"FACE_TRACKER_VERSION\",\"value\":\"14\"},{\"name\":\"segmentation\",\"value\":\"segmentation_enabled\"},{\"name\":\"COMPRESSION\",\"value\":\"ETC2_COMPRESSION\"},{\"name\":\"world_tracker\",\"value\":\"world_tracker_enabled\"},{\"name\":\"gyroscope\",\"value\":\"gyroscope_enabled\"}]&" +
                "reason=" + reason + "&timezone_offset=" + timezone_offset + "&" +
                "tray_session_id=" + nextUUID(IgUser.currentIgUser.sessionID) +
                "&request_id=" + nextUUID(IgUser.currentIgUser.adID) +
                "_uuid=" + IgUser.currentIgUser.deviceID, resultConnection);
    }

    public void notifications(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/notifications/badge/", "phone_id=" + IgUser.currentIgUser.phoneID +
                "&user_ids=" + IgUser.currentIgUser.userID + "&_uuid=" + IgUser.currentIgUser.deviceID
                + "&device_id=" + IgUser.currentIgUser.androidID, resultConnection);
    }

    public void news(final ResultConnection resultConnection) {
        get(String.format("https://i.instagram.com/api/v1/news/inbox/?mark_as_seen=false&timezone_offset=%s", String.valueOf(timezone_offset)), resultConnection);
    }

    public void fetchConfig(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/loom/fetch_config/", resultConnection);
    }

    public void bootstrap(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/scores/bootstrap/users/?surfaces=[\"coefficient_direct_recipients_ranking_variant_2\",\"coefficient_rank_recipient_user_suggestion\",\"coefficient_besties_list_ranking\",\"coefficient_ios_section_test_bootstrap_ranking\",\"autocomplete_user_list\"]", resultConnection);
    }

    public void arlinkDownloadInfo(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/users/arlink_download_info/?version_override=2.2.1", resultConnection);
    }

    public void blocked(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/media/blocked/", resultConnection);
    }

    public void getCooldowns(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/qp/get_cooldowns/?signed_body=SIGNATURE.{}", resultConnection);
    }

    public void inbox(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/direct_v2/inbox/?visual_message_return_type=unseen&persistentBadging=true&limit=0", resultConnection);
    }

    public void getPresenceDisabled(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/accounts/get_presence_disabled/?signed_body=SIGNATURE.{}", resultConnection);
    }

    public void batchFetch(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/qp/batch_fetch/", new JsonBuilder()
                .addBody("surfaces_to_triggers", "{\"5734\":[\"instagram_shopping_enable_auto_highlight_interstitial\",\"instagram_feed_prompt\"],\"5858\":[\"instagram_navigation_tooltip\",\"instagram_featured_product_media_tooltip\",\"instagram_feed_promote_cta_tooltip\",\"instagram_feed_tool_tip\"],\"4715\":[\"instagram_feed_header\"]}")
                .addBody("surfaces_to_queries", "{\"5734\":\"Query+QuickPromotionSurfaceQuery:+Viewer{viewer(){eligible_promotions.trigger_context_v2(<trigger_context_v2>).ig_parameters(<ig_parameters>).trigger_name(<trigger_name>).surface_nux_id(<surface>).external_gating_permitted_qps(<external_gating_permitted_qps>).supports_client_filters(true).include_holdouts(true){edges{client_ttl_seconds,log_eligibility_waterfall,is_holdout,priority,time_range{start,end},node{id,promotion_id,logging_data,max_impressions,triggers,contextual_filters{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}}}}}},is_uncancelable,template{name,parameters{name,required,bool_value,string_value,color_value}},creatives{title{text},content{text},footer{text},social_context{text},social_context_images,primary_action{title{text},url,limit,dismiss_promotion},secondary_action{title{text},url,limit,dismiss_promotion},dismiss_action{title{text},url,limit,dismiss_promotion},image.scale(<scale>){uri,width,height},dark_mode_image.scale(<scale>){uri,width,height}}}}}}}\",\"5858\":\"Query+QuickPromotionSurfaceQuery:+Viewer{viewer(){eligible_promotions.trigger_context_v2(<trigger_context_v2>).ig_parameters(<ig_parameters>).trigger_name(<trigger_name>).surface_nux_id(<surface>).external_gating_permitted_qps(<external_gating_permitted_qps>).supports_client_filters(true).include_holdouts(true){edges{client_ttl_seconds,log_eligibility_waterfall,is_holdout,priority,time_range{start,end},node{id,promotion_id,logging_data,max_impressions,triggers,contextual_filters{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}}}}}},is_uncancelable,template{name,parameters{name,required,bool_value,string_value,color_value}},creatives{title{text},content{text},footer{text},social_context{text},social_context_images,primary_action{title{text},url,limit,dismiss_promotion},secondary_action{title{text},url,limit,dismiss_promotion},dismiss_action{title{text},url,limit,dismiss_promotion},image.scale(<scale>){uri,width,height}}}}}}}\",\"4715\":\"Query+QuickPromotionSurfaceQuery:+Viewer{viewer(){eligible_promotions.trigger_context_v2(<trigger_context_v2>).ig_parameters(<ig_parameters>).trigger_name(<trigger_name>).surface_nux_id(<surface>).external_gating_permitted_qps(<external_gating_permitted_qps>).supports_client_filters(true).include_holdouts(true){edges{client_ttl_seconds,log_eligibility_waterfall,is_holdout,priority,time_range{start,end},node{id,promotion_id,logging_data,max_impressions,triggers,contextual_filters{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}},clauses{clause_type,filters{filter_type,unknown_action,value{name,required,bool_value,int_value,string_value},extra_datas{name,required,bool_value,int_value,string_value}}}}}},is_uncancelable,template{name,parameters{name,required,bool_value,string_value,color_value}},creatives{title{text},content{text},footer{text},social_context{text},social_context_images,primary_action{title{text},url,limit,dismiss_promotion},secondary_action{title{text},url,limit,dismiss_promotion},dismiss_action{title{text},url,limit,dismiss_promotion},image.scale(<scale>){uri,width,height},dark_mode_image.scale(<scale>){uri,width,height}}}}}}}\"}")
                .addBody("vc_policy", "default")
                .addBody("_uid", IgUser.currentIgUser.userID)
                .addBody("_uuid", IgUser.currentIgUser.deviceID)
                .addBody("scale", "2")
                .addBody("version", "1")
                .toString(), resultConnection);
    }

    public void sync2(ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/qe/sync/", new JsonBuilder().addBody("experiments", "ig_android_reg_nux_headers_cleanup_universe,ig_android_device_detection_info_upload,ig_android_gmail_oauth_in_reg,ig_android_device_info_foreground_reporting,ig_android_device_verification_fb_signup,ig_android_passwordless_account_password_creation_universe,ig_android_direct_add_direct_to_android_native_photo_share_sheet,ig_growth_android_profile_pic_prefill_with_fb_pic_2,ig_account_identity_logged_out_signals_global_holdout_universe,ig_android_quickcapture_keep_screen_on,ig_android_device_based_country_verification,ig_android_login_identifier_fuzzy_match,ig_android_reg_modularization_universe,ig_android_security_intent_switchoff,ig_android_device_verification_separate_endpoint,ig_android_suma_landing_page,ig_android_sim_info_upload,ig_android_fb_account_linking_sampling_freq_universe,ig_android_retry_create_account_universe,ig_android_caption_typeahead_fix_on_o_universe")
                .addBody("id", IgUser.currentIgUser.deviceID)
                .addBody("server_config_retrieval", "1").toString(), resultConnection);
    }

    public void hasInteropUpgraded(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/direct_v2/has_interop_upgraded/", resultConnection);
    }

    public void getPresence(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/direct_v2/get_presence", resultConnection);
    }

    public void processContactPointSignals(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/accounts/process_contact_point_signals/", new JsonBuilder()
                .addBody("_uid", IgUser.currentIgUser.userID)
                .addBody("phone_id", IgUser.currentIgUser.phoneID).addBody("supported_capabilities_new", "")
                .addBody("device_id", IgUser.currentIgUser.deviceID)
                .addBody("_uuid", IgUser.currentIgUser.deviceID)
                .addBody("google_tokens", "[]").toString(), resultConnection);
    }

    public void storeClientPushPermissions(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/notifications/store_client_push_permissions/", "enabled=true" +
                "&_uuid=" + IgUser.currentIgUser.deviceID + "&device_id=" +
                IgUser.currentIgUser.androidID, resultConnection);
    }


    public void getLinkageStatus(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/linked_accounts/get_linkage_status/", resultConnection);
    }

    public void writeSupportedCapabilities(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/creatives/write_supported_capabilities/", new JsonBuilder()
                .addBody("_uid", IgUser.currentIgUser.userID)
                .addBody("_uuid", IgUser.currentIgUser.deviceID).
                addBody("supported_capabilities_new", "[{\"name\":\"SUPPORTED_SDK_VERSIONS\",\"value\":\"73.0,74.0,75.0,76.0,77.0,78.0,79.0,80.0,81.0,82.0,83.0,84.0,85.0,86.0,87.0,88.0,89.0,90.0,91.0,92.0,93.0,94.0,95.0,96.0,97.0,98.0,99.0,100.0,101.0,102.0,103.0,104.0,105.0,106.0,107.0,108.0,109.0\"},{\"name\":\"FACE_TRACKER_VERSION\",\"value\":\"14\"},{\"name\":\"segmentation\",\"value\":\"segmentation_enabled\"},{\"name\":\"COMPRESSION\",\"value\":\"ETC2_COMPRESSION\"},{\"name\":\"world_tracker\",\"value\":\"world_tracker_enabled\"},{\"name\":\"gyroscope\",\"value\":\"gyroscope_enabled\"}]").toString(), resultConnection);
    }

    public void logResurrectAttribution(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/attribution/log_resurrect_attribution/", new JsonBuilder()
                .addBody("_uuid", IgUser.currentIgUser.deviceID)
                .addBody("adid", IgUser.currentIgUser.adID)
                .addBody("_uid", IgUser.currentIgUser.userID).toString(), resultConnection);
    }

    public void banyan(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/banyan/banyan/?views=[\"direct_user_search_keypressed\",\"group_stories_share_sheet\",\"call_recipients\",\"reshare_share_sheet\",\"direct_inbox_active_now\",\"story_share_sheet\",\"forwarding_recipient_sheet\",\"direct_user_search_nullstate\",\"threads_people_picker\"]", resultConnection);
    }


    public void userXpostingDestination(final ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/ig_fb_xposting/account_linking/user_xposting_destination/?signed_body=SIGNATURE.{}SIGNATURE.{}", resultConnection);
    }

    public void wwwGraphQL2(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/wwwgraphql/ig/query/", signed_body + "doc_id=3074914985892821&locale=en_US&vc_policy=default&strip_nulls=true&strip_defaults=true&query_params={\"payment_type\":\"ig_payment_settings\"}", resultConnection);
    }

    public void profileArchiveBadge(final ResultConnection resultConnection) {
        post("https://i.instagram.com/api/v1/archive/reel/profile_archive_badge/", "timezone_offset=" + timezone_offset + "&_uuid="
                + IgUser.currentIgUser.deviceID, resultConnection);
    }

    public void getViewableStatuses(ResultConnection resultConnection) {
        get("https://i.instagram.com/api/v1/status/get_viewable_statuses/", resultConnection);
    }


    // plus


    private void get(String url, ResultConnection resultConnection) {
        get(url, appHeaders(IgUser.currentIgUser), IgUser.currentIgUser.headerListener, resultConnection, true);
    }

    private void getThread(String url, ResultConnection resultConnection) {
        get(url, appHeaders(IgUser.currentIgUser), IgUser.currentIgUser.headerListener, resultConnection, false);
    }

    private void get(String url, Map<String, String> headers, IgHttp.HeaderListener headerListener, ResultConnection resultConnection, boolean useThread) {
        connectionThread(IgHttp.GET, url, null, headers, headerListener, resultConnection, useThread);
    }

    private void post(String url, String body, ResultConnection resultConnection) {
        post(url, body, appHeaders(IgUser.currentIgUser), IgUser.currentIgUser.headerListener, resultConnection, true);
    }

    private void post(String url, String body, Map<String, String> headers, IgHttp.HeaderListener headerListener, ResultConnection resultConnection, boolean useThread) {
        connectionThread(IgHttp.POST, url, body, headers, headerListener, resultConnection, useThread);
    }

    private void connectionThread(String method, String url, String body, Map<String, String> headers, IgHttp.HeaderListener headerListener, ResultConnection resultConnection, boolean useThread) {
        IgUser currentUser = IgUser.currentIgUser;
        Runnable runnable = () -> {
            String[] response = new IgHttp().request(method, url, body, headers, headerListener);
            netConnectionResponse(currentUser, response, resultConnection, useThread);
        };
        if (useThread) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    private void netConnectionResponse(IgUser currentUser, String[] response, ResultConnection resultConnection, boolean useThread) {
        Runnable runnable = () -> {
            if (response != null) {
                try {
                    try {
                        JSONObject jsonObject = new JSONObject(response[1]);
                        if (jsonObject.getString("status").equals("ok")) {
                            resultConnection.result(null, jsonObject);
                        } else {
                            resultConnection.result(IgResponse.responseStatus(response[1]), jsonObject);
                        }
                    } catch (Exception e) {
                        if (TextUtils.isEmpty(response[1]) && currentUser.loginRequired && CheckNetworkState.isOnline()) {
                            resultConnection.result(IgResponse.responseServerError(-2), null);
                        } else {
                            resultConnection.result(IgResponse.responseServerError(Integer.parseInt(response[0])), null);
                        }
                    }
                    return;
                } catch (Exception e) {
                    //
                }
            }
            if (currentUser.loginRequired && CheckNetworkState.isOnline()) {
                resultConnection.result(IgResponse.responseServerError(-2), null);
            } else {
                resultConnection.result(IgResponse.responseServerError(-1), null);
            }
        };
        if (MainActivity.isRunning) {
            if (useThread) {
                BuildApp.runOnUIThread(runnable);
            } else {
                runnable.run();
            }
        }
    }


    private JSONObject genSeen(long timeEnd, String... media_ids) {
        JSONObject result = new JSONObject();
        for (String media_id : media_ids) {
            String[] split_media_id = media_id.split("_");
            String media_pk = split_media_id[0];
            String user_id = split_media_id[1];
            if (new Random().nextBoolean()) {
                timeEnd++;
            }
            long begin = timeEnd - ThreadLocalRandom.current().nextInt(100, 3000);
            JSONArray time_range = new JSONArray();
            time_range.put(begin + "_" + timeEnd);
            try {
                result.put(media_pk + "_" + user_id + "_" + user_id, time_range);
            } catch (JSONException e) {
                //
            }
        }
        return result;

    }

    private String getLatency(String phoneID) {
        int amount = 0;
        for (int i = 0; i < phoneID.length(); i++) {
            amount += (int) phoneID.charAt(i);
        }
        return String.valueOf(amount % 5 + 1);
    }

    private static String nextUUID(String uuid) {
        uuid = uuid.replace("-", "");
        String end = uuid.substring(uuid.length() - 2);
        uuid = end + uuid.substring(0, uuid.length() - 2);
        return String.format("%s-%s-%s-%s-%s",
                uuid.substring(0, 8),
                uuid.substring(8, 12),
                uuid.substring(12, 16),
                uuid.substring(16, 20),
                uuid.substring(20, 32));
    }

    private static String genUserBreadcrumb(int size) {
        try {
            String key = "iN4$aGr0m";
            long dt = System.currentTimeMillis();
            int time_elapsed = (new Random().nextInt(1001) + 500) + size * (new Random().nextInt(1001) + 500);
            int text_change_event_count = Math.max(1, size / (new Random().nextInt(3) + 3));
            String data = String.format(Locale.US, "%d %d %d %d", size, time_elapsed, text_change_event_count, dt);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacResult = mac.doFinal(data.getBytes());
            String hmacEncoded = Base64.encodeToString(hmacResult, Base64.NO_WRAP);
            String dataEncoded = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
            return String.format("%s\n%s\n", hmacEncoded, dataEncoded);
        } catch (Exception e) {
            //
        }
        return "";
    }

    private static class JsonBuilder {
        private final JSONObject jsonObject = new JSONObject();

        public JsonBuilder addBody(String key, Object object) {
            try {
                jsonObject.put(key, object);
            } catch (JSONException e) {
                //
            }
            return this;
        }

        @NonNull
        @Override
        public String toString() {
            String json = jsonObject.toString();
            if (jsonObject.length() == 0) {
                json = "\" \"";
            }
            try {
                return "signed_body=SIGNATURE." + URLEncoder.encode(json, Charsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                return "signed_body=SIGNATURE." + Uri.encode(json);
            }
        }
    }

    public interface ResultConnection {
        void result(IgResponse.Status status, JSONObject object);
    }

}