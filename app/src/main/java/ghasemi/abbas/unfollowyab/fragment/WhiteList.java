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
import android.widget.Filter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.OnClickHolder;
import ghasemi.abbas.unfollowyab.adapter.RESPeople2;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.BottomSheetDialog;
import ghasemi.abbas.unfollowyab.components.EditText;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;

public class WhiteList extends BaseFragment {

    private ArrayList<Bundle> bundles;
    private RecyclerView recyclerView;
    private AlertDialog progress;
    private BottomSheetDialog sheetDialog;

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> createDialog("لیست سفید ، افرادی هستند انتخابی از طرف شما،که در بخش های فالو نکنندگان نمایش داده نمی شوند تا از آنفالو شدن آن ها جلوگیری گردد.");
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

    private RESPeople2 resPeople2;

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("لیست سفید");

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
                    resPeople2.getFilter().filter(charSequence, new Filter.FilterListener() {
                        @Override
                        public void onFilterComplete(int i) {
                            recyclerView.scrollToPosition(0);
                            setPageTitle(bundles.size() + " نفر در لیست سفید");
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

        load();
    }

    private void load() {
        bundles = SQL.getSql().getWhiteList();
        if (bundles.isEmpty()) {
            notFound();
        } else {
            setPageTitle(bundles.size() + " نفر در لیست سفید");
            resPeople2 = new RESPeople2();
            resPeople2.setData(bundles);
            resPeople2.setMainActivity(getActivity());
            resPeople2.setOnClickHolder(new OnClickHolder() {
                @Override
                public void onClick(final int p) {
                    if (sheetDialog != null && sheetDialog.isShowing()) {
                        return;
                    }
                    sheetDialog = new BottomSheetDialog.Builder(getActivity())
                            .setFullName(bundles.get(p).getString("username"))
                            .setIcon(bundles.get(p).getString("profile_pic_url"))
                            .setBotton(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("https://instagram.com/" + bundles.get(p).getString("username")));
                                    startActivity(intent);
                                }
                            }).setBotton(bundles.get(p).getBoolean("white_list", true) ? "خروج از لیست سفید" : "بازگشت به لیست سفید", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bundles.get(p).getBoolean("white_list", true)) {
                                        bundles.get(p).putBoolean("white_list", false);
                                        SQL.getSql().removeWhiteList(bundles.get(p).getString("pk"));
                                    } else {
                                        bundles.get(p).putBoolean("white_list", true);
                                        SQL.getSql().addDataWhiteList(bundles.get(p).getString("pk"));
                                    }
                                }
                            }).create();
                    sheetDialog.show();
                }
            });
            recyclerView.setAdapter(resPeople2);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}