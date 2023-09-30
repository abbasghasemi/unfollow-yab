package ghasemi.abbas.unfollowyab.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.BottomSheetDialog;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;

public class MutualFollowers extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RecyclerView recyclerView;
    private AlertDialog progress;
    private BottomSheetDialog sheetDialog;
    private RESPeople2 resPeople2;

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> createDialog("فالوکنندگان متقابل به افرادی می گویند که هم شما آنها را و هم آنها شما را فالو کرده اند.");
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
        setPageTitle("فالو کنندگان متقابل");

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
                        setPageTitle(bundles.size() + " فالو کننده ی متقابل");
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
            bundles = SQL.getSql().getRecFollow();
            runOnUIThread(() -> {
                if (getActivity() == null || isRemoving()) return;
                dismissProgressDialog();
                if (bundles.isEmpty()) {
                    notFound();
                } else {
                    setPageTitle(bundles.size() + " فالو کننده ی متقابل");
                    resPeople2 = new RESPeople2();
                    resPeople2.setData(bundles);
                    resPeople2.setMainActivity(getActivity());
                    resPeople2.setOnClickHolder(p -> {
                        if (sheetDialog != null && sheetDialog.isShowing()) {
                            return;
                        }
                        sheetDialog = new BottomSheetDialog.Builder(getActivity())
                                .setFullName(bundles.get(p).getString("username"))
                                .setIcon(bundles.get(p).getString("profile_pic_url"))
                                .setBotton(v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("https://instagram.com/" + bundles.get(p).getString("username")));
                                    startActivity(intent);
                                })
                                .setBotton("آنفالو", v -> {

                                    showProgressDialog();
                                    IgApi.instance().unFollow(bundles.get(p).getString("pk"), (status, object) -> {
                                        if (getActivity() == null || isRemoving()) return;
                                        dismissProgressDialog();
                                        if (status == null) {
                                            SQL.getSql().setType(bundles.get(p).getString("pk"), "0");
                                            int count = Integer.parseInt(Store.data().getString("following_count")) - 1;
                                            Store.data().putString("following_count", String.valueOf(count));
                                            bundles.remove(p);
                                            resPeople2.notifyItemRemoved(p);
                                            resPeople2.notifyItemRangeChanged(p, bundles.size());
                                            BuildApp.Toast("انجام شد");
                                            if (bundles.isEmpty()) {
                                                notFound();
                                            }
                                        } else if (!status.serverError) {
                                            if (!IgResponse.showDialogMessage(getActivity(), status)) {
                                                BuildApp.Toast("در حال حاضر امکان آنفالو کردن این شخص وجود ندارد،لطفا دقایقی بعد تلاش کنید.");
                                            }
                                        } else {
                                            BuildApp.Toast("در حال حاضر امکان آنفالو کردن این شخص وجود ندارد،لطفا دقایقی بعد تلاش کنید.");
                                        }
                                    });
                                })
                                .create();
                        sheetDialog.show();
                    });
                    recyclerView.setAdapter(resPeople2);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }
}