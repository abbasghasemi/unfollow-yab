package ghasemi.abbas.unfollowyab.fragment;


import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import ghasemi.abbas.unfollowyab.BuildConfig;
import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.OnClickHolder;
import ghasemi.abbas.unfollowyab.adapter.RESItem;
import ghasemi.abbas.unfollowyab.api.CheckNetworkState;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.api.Utilities;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.IgTask;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;
import im.dacer.androidcharts.LineView;

public class MainFollowers extends Fragment {
    private View view;
    private TextView following_count;
    static TextView follower_count;
    private AlertDialog alertDialog;
    private IgTask igTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home, container, false);
        RecyclerView recyclerViewItem = view.findViewById(R.id.recyclerView_item);
        recyclerViewItem.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewItem.setNestedScrollingEnabled(false);
        final RESItem resItem = new RESItem();
        String[] title = new String[]{
                "فالو کنندگان متقابل", "فالو نکنندگان", "فالوور های از دست رفته", "فالوور های جدید", "طرفدار ها", "لیست سفید"
                , "فالوئینگ های از دست رفته", "بلاک کننده ها"
        };
        resItem.setData(title, new int[]{
                R.drawable.rec_follow, R.drawable.no_follow, R.drawable.unfollow,
                R.drawable.new_followers, R.drawable.followers, R.drawable.white_list,
                R.drawable.no_following, R.drawable.bloked
        });
        resItem.setLayoutID(R.layout.item_home);
        resItem.setOnClickHolder(new OnClickHolder() {
            @Override
            public void onClick(int p) {
                if (Store.data().getString("l_t_u_h_" + Utilities.userID()).isEmpty()) {
                    view.findViewById(R.id.last_check).performClick();
                    return;
                }
                switch (p) {
                    case 0:
                        ((MainActivity) getActivity()).startFragment(new MutualFollowers());
                        break;
                    case 1:
                        ((MainActivity) getActivity()).startFragment(new DontFollow());
                        break;
                    case 2:
                        ((MainActivity) getActivity()).startFragment(new LostFollowers());
                        break;
                    case 3:
                        ((MainActivity) getActivity()).startFragment(new NewFollowers());
                        break;
                    case 4:
                        ((MainActivity) getActivity()).startFragment(new Followers());
                        break;
                    case 5:
                        ((MainActivity) getActivity()).startFragment(new WhiteList());
                        break;
                    case 6:
                        ((MainActivity) getActivity()).startFragment(new LostFollowing());
                        break;
                    default:
                        ((MainActivity) getActivity()).startFragment(new BlockedMe());
                }
            }
        });
        recyclerViewItem.setAdapter(resItem);
        init();
        view.findViewById(R.id.last_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    return;
                }
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("بروزرسانی فالوور ها")
                        .setMessage("دریافت و بررسی اطلاعات بسته به تعداد فالوور و فالوئینگ های شما متفاوت است،بدیهی است تعداد بالا نیاز به زمان بیشتر برای بروزرسانی دارد.")
                        .setNegativeButton("انجام", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog();
                                startTask();
                            }
                        })
                        .setPositiveButton("الان نه", null)
                        .show();
                BuildApp.setCustomFontDialog(alertDialog);
            }
        });
        view.findViewById(R.id.media_count).setOnClickListener(v -> {
            if (Store.data().getString("l_t_u_s_" + Utilities.userID()).isEmpty()) {
                BuildApp.Toast("ابتدا بخش پست ها را بروزرسانی نمایید.");
                return;
            }
            ((MainActivity) getActivity()).startFragment(new Media());
        });
        view.findViewById(R.id.follower_count).setOnClickListener(v -> {
            if (Store.data().getString("l_t_u_h_" + Utilities.userID()).isEmpty()) {
                view.findViewById(R.id.last_check).performClick();
                return;
            }
            ((MainActivity) getActivity()).startFragment(new MyFollowers());
        });
        view.findViewById(R.id.following_count).setOnClickListener(v -> {
            if (Store.data().getString("l_t_u_h_" + Utilities.userID()).isEmpty()) {
                view.findViewById(R.id.last_check).performClick();
                return;
            }
            ((MainActivity) getActivity()).startFragment(new MyFollowing());
        });
        initChart();
        return view;
    }

    private String getLastTime() {
        long time = (System.currentTimeMillis() - Long.parseLong(Store.data().getString("l_t_u_h_" + Utilities.userID()))) / 1000;
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


    private void startTask() {
        if (CheckNetworkState.isOnline()) {
            igTask = new IgTask(getActivity(), new IgTask.OnUpdateAccount() {
                @Override
                public boolean isHomePage() {
                    return true;
                }

                @Override
                public void ok() {
                    Store.data().putString("l_t_u_h_" + Utilities.userID(), String.valueOf(System.currentTimeMillis()));
                    SQL.getSql().setDetails();
                    init();
                    initChart();
                    igTask = new IgTask(getActivity());
                }

                @Override
                public void error(final String error) {
                    BuildApp.setCustomFontDialog(alertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage("با عرض پوزش درحین دریافت اطلاعات با خطای ناشناخته روبرو شدیم. \n با گزارش آن به ما باعث بهبود اپلیکیشن شوید.")
                            .setTitle("Bug Fix")
                            .setIcon(R.drawable.ic_bug_report)
                            .setCancelable(false)
                            .setPositiveButton("لفو", null)
                            .setNegativeButton("گزارش", (dialog, which) -> {
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
        } else {
            BuildApp.Toast("شبکه در دسترس نیست!");
        }
    }

    private void init() {
        RoundedImageView imageView = view.findViewById(R.id.pic);
        Glide.with(getActivity())
                .load(Store.data().getString("profile_pic_url"))
                .into(imageView);

        TextView username = view.findViewById(R.id.username);
        username.setText(Store.data().getString("username"));

        TextView full_name = view.findViewById(R.id.full_name);
        full_name.setText(Store.data().getString("full_name"));

        TextView media_count = view.findViewById(R.id.media_count);
        media_count.setText(String.format("%s پست", Store.data().getString("media_count")));

        follower_count = view.findViewById(R.id.follower_count);
        follower_count.setText(String.format("%s فالوور", Store.data().getString("follower_count")));

        following_count = view.findViewById(R.id.following_count);
        following_count.setText(String.format("%s فالوئینگ", Store.data().getString("following_count")));

        TextView last_check = view.findViewById(R.id.last_check);
        if (Store.data().getString("l_t_u_h_" + Utilities.userID()).isEmpty()) {
            last_check.setText("آخرین بروزرسانی: لمس برای بروزرسانی");
        } else {
            last_check.setText(String.format("آخرین بروزرسانی: %s", getLastTime()));
        }
    }

    private void initChart() {
        LineView lineView = view.findViewById(R.id.chart);
        lineView.setColorArray(new int[]{0xff2196F3, 0xffE91E63});
        try {
            Field field = LineView.class.getField("BACKGROUND_LINE_COLOR");
            field.setAccessible(true);
            field.setInt(lineView, 0xFFE0E0E0);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        SQL.getSql().Details(lineView, view.findViewById(R.id.no_data));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (igTask != null) {
            igTask.close();
            igTask = null;
        }
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}