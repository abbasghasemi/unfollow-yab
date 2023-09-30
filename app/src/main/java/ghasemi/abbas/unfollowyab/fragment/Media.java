package ghasemi.abbas.unfollowyab.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESPost;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;

public class Media extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RESPost resPost;
    private RecyclerView recyclerView;

    @Override
    public int onCreateView() {
        return R.layout.perview;
    }

    @Override
    protected int buttonRightIconRes() {
        return R.drawable.ic_round_filter_list_24;
    }

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildApp.setCustomFontDialog(
                        new AlertDialog.Builder(getActivity())
                                .setTitle("ترتیب نمایش پست ها بر حسب:")
                                .setAdapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list, new String[]{
                                                "بیشترین بازدید", "کمترین بازدید",
                                                "بیشترین لایک", "کمترین لایک",
                                                "بیشترین کامنت", "کمترین کامنت",
                                                "بیشترین لایک و کامنت",
                                                "کمترین لایک و کامنت",
                                        }),
                                        (dialogInterface, i) -> {
                                            final ArrayList<Bundle> _bundles = new ArrayList<>(bundles);
                                            switch (i) {
                                                case 0:
                                                case 1:
                                                case 2:
                                                case 3:
                                                case 4:
                                                case 5:
                                                    String key;
                                                    if (i < 2) {
                                                        key = "view_count";
                                                    } else if (i < 4) {
                                                        key = "like_count";
                                                    } else {
                                                        key = "comment_count";
                                                    }
                                                    String finalKey = key;
                                                    Collections.sort(_bundles, (b1, b2) -> {
                                                        int c = Integer.compare(Integer.parseInt(b1.getString(finalKey)), Integer.parseInt(b2.getString(finalKey)));
                                                        if (i % 2 == 0) c *= -1;
                                                        return c;
                                                    });
                                                    break;
                                                case 6:
                                                case 7:
                                                    Collections.sort(_bundles, (b1, b2) -> {
                                                        final int l1 = Integer.parseInt(b1.getString("like_count")),
                                                                l2 = Integer.parseInt(b2.getString("like_count"));
                                                        final int c1 = Integer.parseInt(b1.getString("comment_count")),
                                                                c2 = Integer.parseInt(b2.getString("comment_count"));
                                                        int c = Integer.compare(l1 + c1, l2 + c2);
                                                        if (i % 2 == 0) c *= -1;
                                                        return c;
                                                    });
                                                    break;
                                            }
                                            resPost.applyAndAnimateMovedItems(_bundles);
                                            recyclerView.scrollToPosition(0);
                                        })
                                .setPositiveButton("بستن", null)
                                .show()
                );
            }
        };
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("پست های من");

        EditText filter = findViewById(R.id.filter);
        filter.setVisibility(View.GONE);

        bundles = SQL.getSql().getPosts();
        if (bundles.isEmpty()) {
            findViewById(R.id.not_found).setVisibility(View.VISIBLE);
        } else {
            setPageTitle(bundles.size() + " پست");
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setPadding(BuildApp.dp(-2), BuildApp.dp(-2), BuildApp.dp(-2), BuildApp.dp(-2));
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            resPost = new RESPost();
            resPost.setData(bundles);
            resPost.setPost();
            resPost.setOnClickHolder(null);
            recyclerView.setAdapter(resPost);
        }
    }
}
