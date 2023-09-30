package ghasemi.abbas.unfollowyab.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;

import ghasemi.abbas.unfollowyab.BuildConfig;
import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.RESItem;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.api.Utilities;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.IgTask;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;
import ghasemi.abbas.unfollowyab.components.TextView;

public class MainPosts extends Fragment {
    private AlertDialog alertDialog;
    private IgTask igTask;
    TextView last_check;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (igTask != null) {
            igTask.close();
            igTask = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        last_check = view.findViewById(R.id.last_check);
        last_check.setOnClickListener(view1 -> {
            if (alertDialog != null && alertDialog.isShowing()) {
                return;
            }
            alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("بروزرسانی پست ها")
                    .setMessage("از تنظیمات می توانید محدودیت تعداد پست ایجاد کنید،دریافت و بررسی اطلاعات بسته به تعداد پست، لایک ها و کامنت ها متفاوت است،بدیهی است تعداد بالا نیاز به زمان بیشتر برای بروزرسانی دارد.")
                    .setNegativeButton("انجام", (dialog, which) -> {
                        dismissDialog();
                        igTask = new IgTask(getActivity(), new IgTask.OnUpdateAccount() {
                            @Override
                            public boolean isHomePage() {
                                return false;
                            }

                            @Override
                            public void ok() {
                                Store.data().putString("l_t_u_s_" + Utilities.userID(), String.valueOf(System.currentTimeMillis()));
                                igTask = null;
                                last_check.setText(String.format("آخرین بروزرسانی: %s", getLastTime()));
                            }

                            @Override
                            public void error(final String error) {
                                BuildApp.setCustomFontDialog(alertDialog = new AlertDialog.Builder(getActivity())
                                        .setMessage("با عرض پوزش درحین دریافت اطلاعات با خطای ناشناخته روبرو شدیم. \n با گزارش آن به ما باعث بهبود اپلیکیشن شوید.")
                                        .setTitle("Bug Fix")
                                        .setIcon(R.drawable.ic_bug_report)
                                        .setCancelable(false)
                                        .setPositiveButton("لفو", null)
                                        .setNegativeButton("گزارش", (dialog1, which1) -> {
                                            try {
                                                File outputDir = getContext().getCacheDir();
                                                File outputFile = File.createTempFile("log", ".txt", outputDir);
                                                FileOutputStream stream = new FileOutputStream(outputFile);
                                                stream.write(error.getBytes());
                                                stream.close();
                                                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", outputFile);
                                                Intent intent = new ShareCompat.IntentBuilder(getActivity())
                                                        .setStream(uri)
                                                        .setType("*/txt")
                                                        .getIntent()
                                                        .setAction(Intent.ACTION_SEND)
                                                        .putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی برای آنفالویاب اینستاگرام " + BuildConfig.VERSION_NAME)
                                                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                try {
                                                    BuildApp.addToClipboard("پشتیبانی برای آنفالویاب اینستاگرام " + BuildConfig.VERSION_NAME + "\n\n" + "باسلام، شرح اشکال به شرح زیر است: \n" + error);
                                                } catch (Exception e1) {
                                                    //
                                                }
                                                BuildApp.Toast("متن خطا در دستگاه شما کپی شد لطفا برای رفع مشکل آنرا به پشتیبانی تلگرام ارسال کنید.");
                                            }
                                        })
                                        .show());
                                igTask = null;
                            }
                        });
                    })
                    .setPositiveButton("الان نه", null)
                    .show();
            BuildApp.setCustomFontDialog(alertDialog);
        });
        last_check.setText(String.format("آخرین بروزرسانی: %s", getLastTime()));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        RESItem resItem = new RESItem();
        resItem.setLayoutID(R.layout.item_statistics);

        resItem.setOnClickHolder(p -> {
            if (Store.data().getString("l_t_u_s_" + Utilities.userID()).isEmpty()) {
                last_check.performClick();
                return;
            }
            if (Store.data().getString("l_t_u_h_" + Utilities.userID()).isEmpty()) {
                BuildApp.Toast("لطفا بخش فالوور ها را بروزرسانی نمایید.");
                return;
            }
            switch (p) {
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    if (!MainActivity.canUseItem((MainActivity) getActivity())) {
                        return;
                    }
            }
            switch (p) {
                case 0:
                    ((MainActivity) getActivity()).startFragment(new TopLikePost());
                    break;
                case 1:
                    ((MainActivity) getActivity()).startFragment(new TopCommentPost());
                    break;
                case 2:
                    ((MainActivity) getActivity()).startFragment(new TopViewPost());
                    break;
                case 3:
                    ((MainActivity) getActivity()).startFragment(new TopLikersPost());
                    break;
                case 4:
                    ((MainActivity) getActivity()).startFragment(new TopCommenterPost());
                    break;
                case 5: {
                    ((MainActivity) getActivity()).startFragment(new UserDontLike());
                    break;
                }
                case 6: {
                    ((MainActivity) getActivity()).startFragment(new UserDontComment());
                    break;
                }
                case 7: {
                    ((MainActivity) getActivity()).startFragment(new MutualDontLike());
                    break;
                }
                case 8: {
                    ((MainActivity) getActivity()).startFragment(new MutualDontComment());
                    break;
                }
                case 9: {
                    ((MainActivity) getActivity()).startFragment(new SoulFollowers());
                    break;
                }
                case 10: {
                    ((MainActivity) getActivity()).startFragment(new LikedDontFollow());
                    break;
                }
                case 11: {
                    ((MainActivity) getActivity()).startFragment(new CommentedDontFollow());
                    break;
                }
                case 12: {
                    Bundle bundle = SQL.getSql().getStatusDetails();
                    if (alertDialog != null && alertDialog.isShowing()) {
                        return;
                    }
                    alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("وضعیت")
                            .setMessage(String.format("تعداد تک تصویر ها: %s%sتعداد تک ویدئو ها: %s%sتعداد چند تصویری یا چند ویدئویی ها: %s%sمیانگین لایک به ازای هر پست: %s%sمیانگین کامنت به ازای هر پست: %s%sتعداد بلاک های من: %s%sتعداد بلاک کنندگان: %s%s",
                                    bundle.getString("count_graph_img"), "\n\n", bundle.getString("count_graph_video"), "\n\n",
                                    bundle.getString("count_graph_sidecar"), "\n\n", bundle.getString("sum_likes"), "\n\n",
                                    bundle.getString("sum_comments"), "\n\n", bundle.getString("count_block"), "\n\n",
                                    bundle.getString("count_mblock"), ""))
                            .setPositiveButton("باشه", null)
                            .show();
                    BuildApp.setCustomFontDialog(alertDialog);
                    break;
                }
                default:

            }
        });
        String[] title = new String[]{
                "پر لایک ترین پست ها", "پر کامنت ترین پست ها", "پر بیننده ترین پست ها", "کیا زیاد لایک کرده اند", "کیا زیاد کامنت گذاشته اند",
                "کیا لایک نکرده اند", "کیا کامنت نگذاشته اند", "کدام فالوور متقابل لایک نکرده", "کدام فالوور متقابل کامنت نگذاشته",
                "فالوورهای روح", "لایک کرده اند فالو نه", "کامنت گذاشته اند فالو نه", "وضعیت"
        };
        resItem.setData(title, new int[]{
                R.drawable.ic_like, R.drawable.ic_comment, R.drawable.ic_round_remove_red_eye_24, R.drawable.ic_like, R.drawable.ic_comment, R.drawable.ic_like,
                R.drawable.ic_comment, R.drawable.ic_like, R.drawable.ic_comment, R.drawable.soul, R.drawable.ic_like,
                R.drawable.ic_comment, R.drawable.ic_status
        });
        recyclerView.setAdapter(resItem);
        return view;
    }

    private String getLastTime() {
        String l = Store.data().getString("l_t_u_s_" + Utilities.userID());
        if (l.isEmpty()) {
            return "لمس برای بروزرسانی";
        }
        long time = (System.currentTimeMillis() - Long.parseLong(l)) / 1000;
        if (time >= 604800) {
            long t = time / 604800;
            return String.format("%s هفته پیش", t);
        } else if (time >= 86400) {
            long t = time / 86400;
            return String.format("%s روز پیش", t);
        } else if (time >= 3600) {
            long t = time / 3600;
            return String.format("%s ساعت پیش", t);
        } else if (time >= 120) {
            long t = time / 60;
            return String.format("%s دقیقه پیش", t);
        } else {
            return "به تازگی";
        }
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
