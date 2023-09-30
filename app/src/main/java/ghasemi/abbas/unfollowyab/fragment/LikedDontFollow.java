package ghasemi.abbas.unfollowyab.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESPeople2;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;
import ghasemi.abbas.unfollowyab.components.LinearLayoutManager;

public class LikedDontFollow extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RecyclerView recyclerView;
    private AlertDialog progress;
    private RESPeople2 resPeople2;

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> createDialog("افرادی که شما را فالو نکرده اند، اما پست های شما را لایک کرده اند.");
    }


    private void dismissProgressDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progress != null && !isDetached() && getActivity() != null && !getActivity().isFinishing()) {
            progress.show();
        }
    }

    private void createDialog(String msg) {
        createDialog(msg, null);
    }

    private void createDialog(String msg, DialogInterface.OnClickListener onClickListener) {
        BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle(getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("باشه", onClickListener)
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
        setPageTitle("لایک کرده اند فالو نه");

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
                if (resPeople2 != null) resPeople2.getFilter().filter(charSequence, i3 -> {
                    recyclerView.scrollToPosition(0);
                    if (bundles.size() == 1) {
                        setPageTitle(bundles.size() + " فالو نکرده لایک کرده");
                    } else {
                        setPageTitle(bundles.size() + " فالو نکرده لایک کرده اند");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        progress = new AlertDialog.Builder(getActivity()).setView(LayoutInflater.from(getActivity()).inflate(R.layout.progress, null))
                .setCancelable(false).create();
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showProgressDialog();
        load();
    }

    private void load() {
        new Thread(() -> {
            bundles = SQL.getSql().getUserDontFollowAndLike();
            runOnUIThread(() -> {
                if (getActivity() == null || isRemoving()) return;
                dismissProgressDialog();
                if (bundles.isEmpty()) {
                    notFound();
                } else {
                    if (bundles.size() == 1) {
                        setPageTitle(bundles.size() + " فالو نکرده لایک کرده");
                    } else {
                        setPageTitle(bundles.size() + " فالو نکرده لایک کرده اند");
                    }
                    resPeople2 = new RESPeople2();
                    resPeople2.setData(bundles);
                    resPeople2.setMainActivity(getActivity());
                    resPeople2.setOnClickHolder(null);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(resPeople2);
                }
            });
        }).start();

    }
}