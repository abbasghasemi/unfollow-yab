package ghasemi.abbas.unfollowyab.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.LoginActivity;
import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.WebLoginActivity;
import ghasemi.abbas.unfollowyab.api.IgUser;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.api.Utilities;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;

public class Account extends BaseFragment {

    @Override
    public int onCreateView() {
        return R.layout.perview;
    }

    @Override
    protected int buttonRightIconRes() {
        return R.drawable.ic_round_add_24;
    }

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> {
            if (BuildApp.webViewEnabled()) {
                if (MainActivity.canUseItem((MainActivity) getActivity()))
                    startActivity(new Intent(getActivity(), WebLoginActivity.class));
            } else {
                BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.sys_webview)
                        .setTitle("No WebView installed!")
                        .setMessage("کاربر گرامی برای ورود به برنامه نیاز است، اپلیکیشن Android System WebView را بروزرسانی و فعال نمایید.\n\n در صورتی که تلفن شما برند هواوی و تحریم می باشد، نیاز است برنامه Huawei WebView را نصب نمایید.")
                        .setNegativeButton("تنظیمات", (dialogInterface, i) -> {
                            if (BuildApp.appInstalledOrNot("com.google.android.webview")) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", "com.google.android.webview", null);
                                intent.setData(uri);
                                startActivity(intent);
                            } else if (BuildApp.appInstalledOrNot("com.huawei.webview")) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", "com.huawei.webview", null);
                                intent.setData(uri);
                                startActivity(intent);
                            } else if (BuildApp.appInstalledOrNot("com.android.vending")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setPackage("com.android.vending");
                                intent.setData(Uri.parse("market://details?id=com.google.android.webview"));
                                startActivity(intent);
                            } else if (BuildApp.appInstalledOrNot("com.huawei.appmarket")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setPackage("com.huawei.appmarket");
                                intent.setData(Uri.parse("appmarket://details?id=com.huawei.webview"));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.webview"));
                                try {
                                    startActivity(intent);
                                } catch (Exception exception) {
                                    //
                                }
                            }
                        })
                        .show());
            }
        };
    }

    private ArrayList<Bundle> bundles;
    private RecyclerView.Adapter adapter;
    private boolean needReState;
    private int pos = -1;

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("مدیریت حساب ها");

        EditText filter = findViewById(R.id.filter);
        filter.setVisibility(View.GONE);

        bundles = SQL.getSql().getAccounts();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setPadding(0, BuildApp.dp(5), 0, 0);
        recyclerView.setClipToPadding(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(adapter = new AccountAdapter());
    }

    public void accountExit(int position, boolean isCurrentUser) {
        final boolean[] delete = new boolean[]{false};
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMultiChoiceItems(new String[]{"حذف تمامی داده ها و اطلاعات اکانت از آنفالویاب"}, new boolean[]{false}, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        delete[0] = isChecked;
                    }
                })
                .setTitle("خروج از حساب " + bundles.get(position).getString("username"))
                .setPositiveButton("بستن", null)
                .setNegativeButton("خروج", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQL.getSql().removeAccount(delete[0], bundles.get(position).getString("user_id"), false);
                        bundles.remove(position);
                        adapter.notifyItemRemoved(position);
                        if (bundles.isEmpty()) {
                            needReState = false;
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        } else {
                            if (isCurrentUser) {
                                needReState = true;
                                Utilities.setUserID(bundles.get(0).getString("user_id"));
                                IgUser.initial();
                                adapter.notifyItemRangeChanged(position, bundles.size());
                            }
                        }
                    }
                })
                .show();
        BuildApp.setCustomFontDialog(alertDialog);
        final AlertDialog finalAlertDialog = alertDialog;
        Glide.with(getActivity()).load(bundles.get(position).getString("profile_pic_url"))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (finalAlertDialog.isShowing()) {
                            finalAlertDialog.setIcon(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        if (needReState) {
            ((MainActivity) getActivity()).reState();
        }
        super.onDestroy();
    }

    public class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.item_account_home, null)) {

            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RoundedImageView profile = holder.itemView.findViewById(R.id.profile);
            TextView full_name = holder.itemView.findViewById(R.id.full_name);
            TextView username = holder.itemView.findViewById(R.id.username);
            View login = holder.itemView.findViewById(R.id.login);
            View delete = holder.itemView.findViewById(R.id.delete);
            Bundle bundle = bundles.get(position);
            Glide.with(holder.itemView.getContext()).load(bundle.getString("profile_pic_url")).into(profile);
            username.setText(bundle.getString("username"));
            if (bundle.getString("full_name").isEmpty()) {
                full_name.setVisibility(View.GONE);
            } else {
                full_name.setVisibility(View.VISIBLE);
                full_name.setText(bundle.getString("full_name"));
            }
            boolean isCurrentUser = Utilities.userID().equals(bundle.getString("user_id"));
            if (isCurrentUser) {
                login.setVisibility(View.GONE);
            } else {
                login.setVisibility(View.VISIBLE);
                login.setOnClickListener(view -> {
                    Utilities.setUserID(bundle.getString("user_id"));
                    IgUser.initial();
                    ((MainActivity) getActivity()).reState();
                    finish();
                });
            }
            delete.setOnClickListener(view -> accountExit(holder.getLayoutPosition(), isCurrentUser));

            if (position > pos) {
                pos++;
                holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_bottom));
            }
        }

        @Override
        public int getItemCount() {
            return bundles.size();
        }
    }
}