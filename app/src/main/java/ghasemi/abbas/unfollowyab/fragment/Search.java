package ghasemi.abbas.unfollowyab.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESPeople3;
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.api.Utilities;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;

public class Search extends BaseFragment {

    private RecyclerView recyclerView;
    private RESPeople3 resPeople3;
    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final Handler handler = new Handler();
    private Runnable runnable = null;
    private boolean allow = true;
    private View progress;

    @Override
    public int onCreateView() {
        return R.layout.search;
    }

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> createDialog("ویژگی های سرچ پلاس", "- مشاهده تمامی استوری و هایلایت های کاربران به صورت پنهان\n" +
                "- دانلود استوری، هایلایت، عکس پروفایل و پست های پیج های عمومی یا فالو شده با بیشترین کیفیت\n" +
                "- امکان کپی کردن بیوگرافی پیج ها\n" +
                "\n" +
                "نکته:\n" +
                "- برای دانلود عکس پروفایل، پس از ورود به هر پیج، بر روی عکس پروفایل کاربر لمس طولانی نمایید.\n" +
                "- در سرچ پلاس، کاربران متوجه بازدید های شما نمی شوند.");
    }

    private void createDialog(String title, String msg) {
        BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle(title == null ? getString(R.string.app_name) : title)
                .setCancelable(false)
                .setPositiveButton("باشه", null)
                .show());
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);

        setPageTitle("سرچ پلاس");

        progress = findViewById(R.id.progress);
        EditText filter = findViewById(R.id.filter);
        filter.addTextChangedListener(new TextWatcher() {
            private String bk = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (bk.equals(charSequence.toString().trim())) {
                    return;
                }
                bk = charSequence.toString().trim();
                allow = bk.isEmpty();
                if (allow) {
                    progress.setVisibility(View.GONE);
                } else {
                    progress.setVisibility(View.VISIBLE);
                }
                synchronized (this) {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                        runnable = null;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (bk.isEmpty()) {
                    return;
                }
                handler.postDelayed(runnable = () -> startSearchUsers(bk, atomicInteger.incrementAndGet()), 3000);
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1) {
            @Override
            public boolean canScrollVertically() {
                return allow;
            }
        });
        resPeople3 = new RESPeople3();
        resPeople3.setData(new ArrayList<>());
        resPeople3.setOnClickHolder(p -> {
            User user = new User();
            user.setArguments(resPeople3.getBundles().get(p));
            startFragment(user);
        });
        recyclerView.setAdapter(resPeople3);
    }

    private void startSearchUsers(String username, int nonce) {
        IgApi.instance().userSearch(username, (status, jsonObject) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                if (nonce != atomicInteger.get() || isRemoving()) {
                    return;
                }
                final ArrayList<Bundle> bundles = new ArrayList<>();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String pk = object.getString("pk");
                        if (pk.equals(Utilities.userID())) continue;
                        boolean No = true;
                        for (Bundle b : resPeople3.getBundles()) {
                            if (b.getString("pk").equals(pk)) {
                                bundles.add(b);
                                No = false;
                                break;
                            }
                        }
                        if (No) {
                            Bundle bundle = MainActivity.foundCashed(pk);
                            if (bundle == null) {
                                bundle = new Bundle();
                                bundle.putString("pk", pk);
                                bundle.putString("username", object.getString("username"));
                                bundle.putString("full_name", object.getString("full_name"));
                                MainActivity.addCashed(bundle);
                            }
                            bundle.putString("profile_pic_url", object.getString("profile_pic_url"));
                            bundle.putBoolean("is_private", object.getBoolean("is_private"));
                            bundle.putBoolean("is_verified", object.getBoolean("is_verified"));
                            bundle.putInt("latest_reel_media", object.getInt("latest_reel_media"));
                            bundle.putBoolean("friendship_status", true);
                            bundle.putBoolean("is_following", object.getJSONObject("friendship_status").getBoolean("following"));
                            bundle.putBoolean("outgoing_request", object.getJSONObject("friendship_status").getBoolean("outgoing_request"));
                            bundles.add(bundle);
                        }
                    }
                } catch (JSONException e) {
                    //
                }
                allow = true;
                progress.setVisibility(View.GONE);
                resPeople3.animateTo(bundles);
                recyclerView.scrollToPosition(0);
            } else if (!status.serverError) {
                if (nonce != atomicInteger.get()) {
                    return;
                }
                IgResponse.showDialogMessage(getActivity(), status);
                allow = true;
                progress.setVisibility(View.GONE);
            } else {
                if (nonce != atomicInteger.get()) {
                    return;
                }
                allow = true;
                progress.setVisibility(View.GONE);
            }
        });
    }
}
