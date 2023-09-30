package ghasemi.abbas.unfollowyab.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.OnClickHolder;
import ghasemi.abbas.unfollowyab.adapter.RESPost;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;

public class TopLikePost extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> createDialog("پست های که بیشترین لایک را داشته اند." +
                "\n" +
                "\n- برای نمایش افرادی که پست شما را لایک نکرده اند،روی عکس مورد نظر کلیک نمائید.");
    }


    private void createDialog(String msg) {
        BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle(getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("باشه", null)
                .show());
    }

    @Override
    public int onCreateView() {
        return R.layout.perview;
    }

    private void notFound() {
        findViewById(R.id.not_found).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("پر لایک ترین پست ها");

        EditText filter = findViewById(R.id.filter);
        filter.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setPadding(BuildApp.dp(-2), BuildApp.dp(-2), BuildApp.dp(-2), BuildApp.dp(-2));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        AlertDialog progress = new AlertDialog.Builder(getActivity()).setView(LayoutInflater.from(getActivity()).inflate(R.layout.progress, null))
                .setCancelable(false).create();
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        load();
    }

    private void load() {
        bundles = SQL.getSql().getLikePosts();
        if (bundles.isEmpty()) {
            notFound();
        } else {
            setPageTitle(bundles.size() + " پست با بیشترین لایک");
            RESPost resPost = new RESPost();
            resPost.setData(bundles);
            resPost.setOnClickHolder(new OnClickHolder() {
                @Override
                public void onClick(final int p) {
                    DontLikePost likePost = new DontLikePost();
                    Bundle bundle = new Bundle();
                    bundle.putString("post_pk", bundles.get(p).getString("pk"));
                    likePost.setArguments(bundle);
                    startFragment(likePost);
                }
            });
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerView.setAdapter(resPost);
        }
    }

}