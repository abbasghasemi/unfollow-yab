package ghasemi.abbas.unfollowyab.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class PostsView {

    private ViewPager viewPager;
    private JSONObject jsonObject;
    private GradientButton download;
    private boolean isSlider;
    private int len = 1;


    public PostsView(ViewPager viewPager, ScrollingPagerIndicator indicator, GradientButton download, JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.download = download;
        try {
            isSlider = jsonObject.getBoolean("is_slider");
            if (isSlider) {
                len = jsonObject.getJSONArray("edges").length();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.viewPager = viewPager;
        this.viewPager.setOffscreenPageLimit(1);
        this.viewPager.setAdapter(new VPagerAdapter());
        if (isSlider) {
            indicator.attachToPager(this.viewPager);
        } else {
            indicator.setVisibility(View.GONE);
        }
    }

    private class VPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return len;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view1 = LayoutInflater.from(container.getContext()).inflate(R.layout.post_view_pager, null);
            AppCompatImageView imageView = view1.findViewById(R.id.pic);
            try {
                if (jsonObject.getBoolean("is_slider")) {
                    JSONArray array = jsonObject.getJSONArray("edges");
                    JSONObject object = array.getJSONObject(position);
                    Glide.with(container).load(object.getString("display_url"))
                            .into(imageView);
                    if (object.getBoolean("is_video")) {
                        view1.findViewById(R.id.vedio).setVisibility(View.VISIBLE);
                    }
                } else {
                    Glide.with(container).load(jsonObject.getString("display_url"))
                            .into(imageView);
                    if (jsonObject.getBoolean("is_video")) {
                        view1.findViewById(R.id.vedio).setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                //
            }
            container.addView(view1);
            return view1;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (isSlider) {
                if (download != null) {
                    download.setText("دانلود قسمت " + (position + 1));
                }
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    public String dl() {
        String dl = "";
        try {
            if (isSlider) {
                JSONArray array = jsonObject.getJSONArray("edges");
                JSONObject object = array.getJSONObject(viewPager.getCurrentItem());
                dl = object.getBoolean("is_video") ? object.getString("video_url") : object.getString("display_url");
            } else {
                dl = jsonObject.getBoolean("is_video") ? jsonObject.getString("video_url") : jsonObject.getString("display_url");
            }
        } catch (JSONException e) {
            //
        }
        return dl;
    }

    public String type() {
        boolean type = false;
        try {
            if (isSlider) {
                JSONArray array = jsonObject.getJSONArray("edges");
                JSONObject object = array.getJSONObject(viewPager.getCurrentItem());
                type = object.getBoolean("is_video");
                if (download == null) {
                    BuildApp.Toast("دانلود قسمت " + (viewPager.getCurrentItem() + 1));
                }
            } else {
                type = jsonObject.getBoolean("is_video");
            }
        } catch (JSONException e) {
            //
        }
        return type ? "true" : "false";
    }
}
