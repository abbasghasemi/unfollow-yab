package ghasemi.abbas.unfollowyab.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESPeople2;
import ghasemi.abbas.unfollowyab.api.CheckNetworkState;
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.BottomSheetDialog;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;
import ghasemi.abbas.unfollowyab.components.ProgressDialog;

public class DontFollow extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RecyclerView recyclerView;
    private AlertDialog progress;
    private BottomSheetDialog sheetDialog;
    private RESPeople2 resPeople2;

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> createDialog("فالو نکنندگان به افرادی می گویند که فالو شده اند اما فالو نکرده اند." +
                "\n" +
                "جهت جلوگیری از آنفالو شدن در آنفالوی خودکار با لمس هر کاربر آن را به لیست سفید انتقال دهید.");
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

    private ProgressDialog progressDialog;

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("فالو نکنندگان");

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
                        setPageTitle((bundles.size() - 1) + " نفر فالو نکرده");
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
            bundles = SQL.getSql().getDontFollow();
            runOnUIThread(() -> {
                if (getActivity() == null || isRemoving()) return;
                dismissProgressDialog();
                if (bundles.size() == 1) {
                    notFound();
                } else {
                    setPageTitle(bundles.size() + " نفر فالو نکرده");
                    resPeople2 = new RESPeople2();
                    resPeople2.setUnFollow();
                    resPeople2.setData(bundles);
                    resPeople2.setMainActivity(getActivity());
                    resPeople2.setOnClickHolder(p -> {
                        if (p == 0) {
                            if (bundles.size() == 1) {
                                BuildApp.Toast("موردی وجود ندارد.");
                            } else {
                                if (MainActivity.canUseItem((MainActivity) getActivity())) {
                                    BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                                            .setMessage("آیا می خواهید حالت خودکار فعال گردد؟")
                                            .setTitle(getString(R.string.app_name))
                                            .setPositiveButton("بستن", null)
                                            .setNegativeButton("شروع حالت خودکار", (dialogInterface, i) -> unFollow())
                                            .show());
                                }
                            }
                        } else {
                            if (sheetDialog != null && sheetDialog.isShowing()) {
                                return;
                            }
                            sheetDialog = new BottomSheetDialog.Builder(getActivity())
                                    .setFullName(bundles.get(p).getString("username"))
                                    .setIcon(bundles.get(p).getString("profile_pic_url"))
                                    .setWhiteListBotton(bundles.get(p).getBoolean("no_white_list"), v -> {
                                        if (p >= bundles.size() || bundles.size() <= 1) {
                                            return;
                                        }
                                        if (bundles.get(p).getBoolean("no_white_list")) {
                                            bundles.get(p).putBoolean("no_white_list", false);
                                            SQL.getSql().addDataWhiteList(bundles.get(p).getString("pk"));
                                            BuildApp.Toast("اضافه شد.");
                                        } else {
                                            bundles.get(p).putBoolean("no_white_list", true);
                                            SQL.getSql().removeWhiteList(bundles.get(p).getString("pk"));
                                            BuildApp.Toast("حذف شد.");
                                        }
                                    })
                                    .setBotton(v -> {
                                        if (p >= bundles.size() || bundles.size() <= 1) {
                                            return;
                                        }
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://instagram.com/" + bundles.get(p).getString("username")));
                                        startActivity(intent);
                                    })
                                    .setBotton("آنفالو", v -> {
                                        if (p >= bundles.size() || bundles.size() <= 1) {
                                            return;
                                        }
                                        showProgressDialog();
                                        IgApi.instance().unFollow(bundles.get(p).getString("pk"), (status, object) -> {
                                            if (getActivity() == null || isRemoving()) return;
                                            dismissProgressDialog();
                                            if (status == null) {
                                                SQL.getSql().setType(bundles.get(p).getString("pk"), "-1");
                                                if (!TextUtils.isEmpty(Store.data().getString("following_count", "0"))) {
                                                    int count = Integer.parseInt(Store.data().getString("following_count", "0")) - 1;
                                                    Store.data().putString("following_count", String.valueOf(count));
                                                }
                                                resPeople2.removeByIndex(p);
                                                resPeople2.notifyItemRemoved(p);
                                                resPeople2.notifyItemRangeChanged(p, bundles.size());
                                                BuildApp.Toast("انجام شد");
                                                if (bundles.size() == 1) {
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
                        }
                    });
                    recyclerView.setAdapter(resPeople2);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    private void exit() {
        if (!MainActivity.autoFollow) {
            return;
        }
        MainActivity.autoFollow = false;
        resPeople2.notifyItemRangeChanged(0, bundles.size());
        progressDialog.dismiss();
        progressDialog = null;
    }

    private void next() {
        next(Store.data().getInt("intervalPerFollow", 1));
    }

    private void next(int intervalPerFollow) {
        countDownTimer(intervalPerFollow);
    }

    private void countDownTimer(int intervalPerFollow) {
        if (!MainActivity.autoFollow || !CheckNetworkState.isOnline() ||
                bundles.size() <= position || bundles.size() <= 1) {
            exit();
            return;
        }
        if (intervalPerFollow < 1) {
            autoUnFollow();
            return;
        }
        new Handler().postDelayed(() -> countDownTimer(intervalPerFollow - 1), 1000L);
    }

    private int listCount, position;

    private void unFollow() {
        for (int i = 1; i < bundles.size(); i++) {
            if (!bundles.get(i).getBoolean("no_white_list")) {
                resPeople2.removeByIndex(i--);
            }
        }
        resPeople2.notifyItemRangeChanged(0, bundles.size());
        if (bundles.size() == 1) {
            BuildApp.Toast("موردی وجود ندارد.");
            return;
        }
        MainActivity.autoFollow = true;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("درحال آنفالو کردن ...");
        progressDialog.setButton(v -> exit());
        progressDialog.setProgress(0);
        progressDialog.show();
        listCount = bundles.size() - 1;
        position = 1;
        autoUnFollow();
    }

    private void autoUnFollow() {
        String pk = bundles.get(position).getString("pk");
        IgApi.instance().unFollow(pk, (status, object) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                SQL.getSql().setType(pk, "-1");
                resPeople2.removeByIndex(position);
                resPeople2.notifyItemRemoved(position);
                resPeople2.notifyItemRangeChanged(position, bundles.size());
                if (progressDialog != null)
                    progressDialog.setProgress((int) ((float) (listCount - (bundles.size() - 1)) / listCount * 100));
                if (!TextUtils.isEmpty(Store.data().getString("following_count", "0"))) {
                    int count = Integer.parseInt(Store.data().getString("following_count", "0")) - 1;
                    Store.data().putString("following_count", String.valueOf(count));
                }
                next();
            } else if (!status.serverError) {
                if (IgResponse.showDialogMessage(getActivity(), status)) {
                    exit();
                } else {
                    position++;
                    next(2);
                }
            } else {
                position++;
                next(2);
            }
        });
    }

}