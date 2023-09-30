package ghasemi.abbas.unfollowyab.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESPeople2;
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;
import ghasemi.abbas.unfollowyab.components.LinearLayoutManager;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;

public class DontCommentPost extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RecyclerView recyclerView;
    private AlertDialog progress;
    private RESPeople2 resPeople2;

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> createDialog("افرادی که شما را فالو کرده اند، اما برای این پست کامنتی قرار نداده اند.");
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

    private void createDialogRequestLogin() {
        createDialog("ورود قبلی به حساب اینستاگرام شما باطل شده است،لطفا برای این اکانت دوباره وارد شوید.", (dialog, which) -> ((MainActivity) getActivity()).reOrderAccount());
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

        setPageTitle("کیا کامنت نگذاشته اند");

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
                if (resPeople2 != null) if (resPeople2 != null)
                    resPeople2.getFilter().filter(charSequence, i3 -> {
                        recyclerView.scrollToPosition(0);
                        setPageTitle(bundles.size() + " نفر کامنت نگذاشته");
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
            bundles = SQL.getSql().getUsersDontCommentPost(getArguments().getString("post_pk"));
            runOnUIThread(() -> {
                if (getActivity() == null || isRemoving()) return;
                dismissProgressDialog();
                if (bundles.isEmpty()) {
                    notFound();
                } else {
                    setPageTitle(bundles.size() + " نفر کامنت نگذاشته");
                    resPeople2 = new RESPeople2();
                    resPeople2.setData(bundles);
                    resPeople2.setMainActivity(getActivity());
                    resPeople2.setOnClickHolder(p -> removeDialog(p));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(resPeople2);
                }
            });
        }).start();
    }

    private void remove(final int position) {
        if (bundles == null) {
            return;
        }
        showProgressDialog();
        IgApi.instance().remove(bundles.get(position).getString("pk"), (status, object) -> {
            if (getActivity() == null || isRemoving()) return;
            dismissProgressDialog();
            if (status == null) {
                if (bundles.get(position).getString("type").equals("0")) {
                    SQL.getSql().delete(bundles.get(position).getString("pk"));
                } else {
                    SQL.getSql().setType(bundles.get(position).getString("pk"), "2");
                }
                bundles.remove(position);
                recyclerView.getAdapter().notifyItemRemoved(position);
                recyclerView.getAdapter().notifyItemRangeChanged(position, bundles.size());
                int count = Integer.parseInt(Store.data().getString("follower_count")) + -1;
                Store.data().putString("follower_count", String.valueOf(count));
                BuildApp.Toast("انجام شد");
            } else if (!status.serverError) {
                if (!IgResponse.showDialogMessage(getActivity(), status)) {
                    BuildApp.Toast("خطا در ارتباط با سرور.");
                }
            } else {
                BuildApp.Toast("خطا در ارتباط با سرور.");
            }
        });
    }

    private void removeDialog(final int p) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.alert_remove);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.findViewById(R.id.remove).setOnClickListener(v -> {
            remove(p);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.back_dialog);
        dialog.getWindow().setWindowAnimations(R.style.anim_dialog);
        TextView name = dialog.findViewById(R.id.help);
        name.setText(String.format("آیا می خواهید %s را اخراج کنید؟", bundles.get(p).getString("username")));
        Glide.with(getActivity())
                .load(bundles.get(p).getString("profile_pic_url"))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        try {
                            if (dialog.isShowing()) {
                                RoundedImageView imageView = dialog.findViewById(R.id.remove_img);
                                imageView.setImageDrawable(resource);
                            }
                        } catch (Exception e) {
                            //
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

}