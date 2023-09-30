package ghasemi.abbas.unfollowyab.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESPeople2;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;

public class MyFollowers extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RecyclerView recyclerView;
    private RESPeople2 resPeople2;

    @Override
    public int onCreateView() {
        return R.layout.perview;
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("دنبال کنندگان");

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
                bk = charSequence.toString();
                if (resPeople2 != null)
                    resPeople2.getFilter().filter(charSequence, i3 -> {
                        recyclerView.scrollToPosition(0);
                        setPageTitle(bundles.size() + " دنبال کننده");
                    });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        showProgressDialog();
        new Thread(() -> {
            bundles = SQL.getSql().getFollower();
            runOnUIThread(() -> {
                if (getActivity() == null || isRemoving()) return;
//                        dismissProgressDialog();
                if (bundles.isEmpty()) {
//                            if (TinyData.getTinyData().getString("l_t_u_h_" + Utilities.userID()).isEmpty()) {
//                                view.findViewById(R.id.last_check).performClick();
//                                return;
//                            }
                    findViewById(R.id.not_found).setVisibility(View.VISIBLE);
                } else {
                    setPageTitle(bundles.size() + " دنبال کننده");
                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    resPeople2 = new RESPeople2();
                    resPeople2.setData(bundles);
                    resPeople2.setMainActivity(getActivity());
                    resPeople2.setOnClickHolder(null);
                    recyclerView.setAdapter(resPeople2);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }
}
