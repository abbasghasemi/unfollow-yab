package ghasemi.abbas.unfollowyab.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESPeople3;
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;

public class UserFollowers extends BaseFragment {
    ArrayList<Bundle> bundles = new ArrayList<>();
    private RESPeople3 resPeople3;
    private boolean isLoading = true;
    private String max_id;

    @Override
    public int onCreateView() {
        return R.layout.search;
    }

    private void createDialog(String msg) {
        BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle(getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("باشه", (dialogInterface, i) -> finish())
                .show());
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);

        setPageTitle("دنبال کنندگان");

        EditText filter = findViewById(R.id.filter);
        filter.setVisibility(View.GONE);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        resPeople3 = new RESPeople3();
        resPeople3.setData(bundles);
        resPeople3.setOnClickHolder(p -> {
            User user = new User();
            Bundle b = resPeople3.getBundles().get(p);
            Bundle bundle = MainActivity.foundCashed(b.getString("pk"));
            if (bundle == null) {
                bundle = b;
                MainActivity.addCashed(bundle);
            } else {
                bundle.putString("profile_pic_url", b.getString("profile_pic_url"));
                bundle.putBoolean("is_private", b.getBoolean("is_private"));
                bundle.putBoolean("is_verified", b.getBoolean("is_verified"));
                bundle.putInt("latest_reel_media", b.getInt("latest_reel_media"));
            }
            user.setArguments(bundle);
            startFragment(user);
        });
        recyclerView.setAdapter(resPeople3);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !max_id.equals("false")
                        && !isLoading && !max_id.isEmpty()) {
                    load();
                }
            }
        });
        load();
    }

    private void load() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        isLoading = true;
        IgApi.instance().getUserFollowers(getArguments().getString("pk"), max_id, (status, jsonObject) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    if (jsonObject.has("big_list") && jsonObject.getBoolean("big_list")) {
                        max_id = jsonObject.getString("next_max_id");
                    } else {
                        max_id = "false";
                    }
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("pk", object.getString("pk"));
                        bundle.putString("username", object.getString("username"));
                        bundle.putString("full_name", object.getString("full_name"));
                        bundle.putString("profile_pic_url", object.getString("profile_pic_url"));
                        bundle.putBoolean("is_private", object.getBoolean("is_private"));
                        bundle.putBoolean("is_verified", object.getBoolean("is_verified"));
                        bundle.putInt("latest_reel_media", object.getInt("latest_reel_media"));
                        bundle.putBoolean("friendship_status", false);
                        bundles.add(bundle);
                    }
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    resPeople3.notifyItemRangeChanged(bundles.size() - jsonArray.length(), bundles.size());
                } catch (JSONException e) {
                    //
                }
                isLoading = false;
            } else if (!status.serverError) {
                if (!IgResponse.showDialogMessage(getActivity(), status)) {

                }
                findViewById(R.id.progress).setVisibility(View.GONE);
                isLoading = false;
            } else {
                findViewById(R.id.progress).setVisibility(View.GONE);
                isLoading = false;
            }
        });
    }
}
