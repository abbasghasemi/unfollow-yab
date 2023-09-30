package ghasemi.abbas.unfollowyab.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.adapter.OnClickHolder;
import ghasemi.abbas.unfollowyab.adapter.RESPost;
import ghasemi.abbas.unfollowyab.api.DownloadTask;
import ghasemi.abbas.unfollowyab.api.IgApi;
import ghasemi.abbas.unfollowyab.api.IgResponse;
import ghasemi.abbas.unfollowyab.api.SQL;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.GradientButton;
import ghasemi.abbas.unfollowyab.components.GridLayoutManager;
import ghasemi.abbas.unfollowyab.components.LinearLayoutManager;
import ghasemi.abbas.unfollowyab.components.LinearView;
import ghasemi.abbas.unfollowyab.components.PostsView;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.ViewPager;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class User extends BaseFragment {

    @Override
    public int onCreateView() {
        return R.layout.user;
    }

    private TextView posts;
    private TextView followers;
    private TextView followings;
    private TextView profile_context;
    private TextView bio;
    private TextView btn;

    private View progressPosts;
    private View pageNeedRefresh, progress, followingsLayout, followersLayout;
    private RoundedImageView pic;
    private final ArrayList<Bundle> bundles = new ArrayList<>();
    private String max_id = "";
    private boolean isLoading = true;
    private String ProfileUrl = null;
    private boolean infoLoaded, highlightLoaded, storyLoaded, postLoaded, statusLoaded = true;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Bundle> stories = null;
    private final ArrayList<Bundle> highlights = new ArrayList<>();
    private final Map<String, ArrayList<Bundle>> highlightsMap = new HashMap<>();
    private boolean isLoadingStories = false;
    private RecyclerView highlight;
    private boolean checkAll;

    private void reload() {
        if (!isLoading) {
            if (!statusLoaded) {
                progress.setVisibility(View.VISIBLE);
                Friendships();
                runOnUIThread(() -> refreshLayout.setRefreshing(false), 1500);
            } else if (!infoLoaded) {
                progress.setVisibility(View.VISIBLE);
                loadUserInfo();
                runOnUIThread(() -> refreshLayout.setRefreshing(false), 1500);
            } else if (!highlightLoaded) {
                progress.setVisibility(View.VISIBLE);
                loadHighlights();
                runOnUIThread(() -> refreshLayout.setRefreshing(false), 1500);
            } else if (!storyLoaded) {
                progress.setVisibility(View.VISIBLE);
                loadStories(true);
                runOnUIThread(() -> refreshLayout.setRefreshing(false), 1500);
            } else if (!postLoaded) {
                progressPosts.setVisibility(View.VISIBLE);
                loadPostInfo();
                runOnUIThread(() -> refreshLayout.setRefreshing(false), 1500);
            } else {
                refreshLayout.setRefreshing(false);
            }
        } else {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);

        setPageTitle(getArguments().getString("username"));

        pic = findViewById(R.id.pic);

        if (getArguments().getInt("latest_reel_media") == -1) {
            checkAll = true;
        } else {
            storyLoaded = true;
            if (getArguments().getInt("latest_reel_media") == 0) {
                findViewById(R.id.live).setBackground(null);
                pic.setBorderColor(0xff000000);
            } else {
                pic.setOnClickListener(view -> {
                    if (stories == null) {
                        if (isLoadingStories) {
                            return;
                        }
                        BuildApp.Toast("لطفا چند لحظه منتظر باشید...");
                        loadStories(false);
                    } else {
                        showStories();
                    }
                });
            }
        }

        progress = findViewById(R.id.progress);
        followingsLayout = findViewById(R.id.followingsLayout);
        followersLayout = findViewById(R.id.followersLayout);
        pageNeedRefresh = findViewById(R.id.pageNeedRefresh);
        pageNeedRefresh.setOnClickListener(view -> {
            pageNeedRefresh.setVisibility(View.GONE);
            reload();
        });
        posts = findViewById(R.id.posts);
        followers = findViewById(R.id.followers);
        btn = findViewById(R.id.btn);
        followings = findViewById(R.id.followings);
        profile_context = findViewById(R.id.profile_context);
        TextView fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        highlight = findViewById(R.id.highlight);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        highlight.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        progressPosts = findViewById(R.id.progressPosts);

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this::reload);

        RESPost resPost = new RESPost();
        resPost.setPost();
        resPost.hideInfo();
        resPost.setData(bundles);
        resPost.setOnClickHolder(new OnClickHolder() {
            AlertDialog alertDialog;

            @Override
            public void onClick(int p) {
                try {
                    final JSONObject jsonObject = new JSONObject(bundles.get(p).getString("json"));
                    final View view = getLayoutInflater().inflate(R.layout.post_download, null);
                    final GradientButton download = view.findViewById(R.id.download);
                    ViewPager viewPager = view.findViewById(R.id.viewPager);
                    ScrollingPagerIndicator indicator = view.findViewById(R.id.indicator);
                    final PostsView postsView = new PostsView(viewPager, indicator, download, jsonObject);
                    TextView textView = view.findViewById(R.id.text);
                    textView.setText(jsonObject.getString("text"));
                    view.findViewById(R.id.cancel).setOnClickListener(v -> alertDialog.dismiss());
                    alertDialog = new AlertDialog.Builder(getActivity())
                            .setView(view)
                            .show();
                    BuildApp.setCustomFontDialog(alertDialog);
                    download.setOnClickListener(v -> new DownloadTask(getActivity()).execute(postsView.dl(), postsView.type()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(resPost);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !max_id.equals("false")
                        && !isLoading && !max_id.isEmpty()) {
                    progressPosts.setVisibility(View.VISIBLE);
                    loadPostInfo();
                }
            }
        });

        Glide.with(this).load(getArguments().getString("profile_pic_url")).into(pic);

        if (getArguments().getString("full_name").isEmpty()) {
            fullname.setText(getArguments().getString("full_name"));
            fullname.setVisibility(View.GONE);
        }
        findViewById(R.id.openInWindow).setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://instagram.com/_u/" + getArguments().getString("username")));
                intent.setPackage("com.instagram.android");
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://instagram.com/" + getArguments().getString("username")));
                startActivity(intent);
            }
        });

        if (getArguments().getString("full_name").equals("⛔ حساب کاربر در دسترس نیست ⛔")) {
            progress.setVisibility(View.GONE);
            findViewById(R.id.pageNotFound).setVisibility(View.VISIBLE);
            refreshLayout.setEnabled(false);
        } else {
            loadUserInfo();
        }
    }

    private AlertDialog alertDialog;
    private int storyPosition, storyPosition2, highlightPosition;
    private boolean startFromStory, startFromHighlight;
    private ArrayList<String> actions;

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        if (startFromStory) {
            startFromStory = false;
            showStories();
        } else if (startFromHighlight) {
            startFromHighlight = false;
            showHighlight(highlightPosition);
        }
    }

    private void createDialog(String msg) {
        BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle(getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("باشه", (dialogInterface, i) -> finish())
                .show());
    }

    private void createHighlights() {
        highlight.setVisibility(View.VISIBLE);
        highlight.setOnTouchListener((view, motionEvent) -> {
            if (highlight.getParent() != null && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                highlight.getParent().requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });
        highlight.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.item_highlight, null)) {

                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
                Bundle bundle = highlights.get(position);
                RoundedImageView pic = holder.itemView.findViewById(R.id.pic);
                Glide.with(getContext()).load(bundle.getString("cover_media")).into(pic);
                TextView title = holder.itemView.findViewById(R.id.title);
                title.setText(bundle.getString("title"));
                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    private boolean started;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                if (!started) {
                                    return false;
                                }
                                started = false;
                                pic.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                                long duration = motionEvent.getEventTime() - motionEvent.getDownTime();
                                if (motionEvent.getAction() == MotionEvent.ACTION_UP && duration < 500) {
                                    if (bundle.getBoolean("is_loaded")) {
                                        showHighlight(position);
                                    } else {
                                        if (!bundle.getBoolean("isLoading")) {
                                            loadHighlightMedia(position);
                                        }
                                    }
                                }
                                return false;
                            case MotionEvent.ACTION_BUTTON_PRESS:
                            case MotionEvent.ACTION_DOWN:
                            case MotionEvent.ACTION_MOVE:
                                if (started) {
                                    return true;
                                }
                                started = true;
                                pic.animate().scaleX(0.9f).scaleY(0.9f).setDuration(250).start();
                                return true;
                        }
                        return false;
                    }
                });
            }

            @Override
            public int getItemCount() {
                return highlights.size();
            }
        });
    }

    private void createMoreButton(View view, Bundle bundle, int model) {
        view.setVisibility(View.GONE);
        ArrayList<String> strings = new ArrayList<>();
        actions = new ArrayList<>();
        if (bundle.getBoolean("story_feed_media")) {
            view.setVisibility(View.VISIBLE);
            actions.addAll(bundle.getStringArrayList("story_feed_media_list"));
            for (String ignored : actions) {
                strings.add("مشاهده پست");
            }
        }
        if (bundle.getBoolean("reel_mentions")) {
            view.setVisibility(View.VISIBLE);
            ArrayList<String> arrayList = bundle.getStringArrayList("reel_mentions_list");
            for (String str : arrayList) {
                try {
                    JSONObject object = new JSONObject(str);
                    strings.add("@" + object.getString("username"));
                    actions.add(str);
                } catch (JSONException e) {
                    //
                }
            }
        }
        view.setOnClickListener(view1 -> {
            ListAdapter listAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_list, strings);
            BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                    .setAdapter(listAdapter, (dialogInterface, i) -> {
                        String str = actions.get(i);
                        if (str.startsWith("https://")) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str
                                        .replace("/p/", "/_p/").replace("/reel/", "/_reel/")
                                        .replace("/tv/", "/_tv/")
                                )));
                            } catch (Exception e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
                            }
                        } else {
                            try {
                                JSONObject object = new JSONObject(str);
                                Bundle b = MainActivity.foundCashed(object.getString("pk"));
                                if (b == null) {
                                    b = new Bundle();
                                    b.putString("pk", object.getString("pk"));
                                    b.putString("username", object.getString("username"));
                                    b.putString("full_name", object.getString("full_name"));
                                    b.putBoolean("friendship_status", false);
                                    MainActivity.addCashed(b);
                                }
                                b.putString("profile_pic_url", object.getString("profile_pic_url"));
                                b.putBoolean("is_private", object.getBoolean("is_private"));
                                b.putBoolean("is_verified", object.getBoolean("is_verified"));
                                b.putInt("latest_reel_media", -1);
                                User user = new User();
                                user.setArguments(b);
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                    alertDialog = null;
                                    if (model == 0) {
                                        startFromStory = true;
                                    } else {
                                        startFromHighlight = true;
                                    }
                                }
                                startFragment(user);
                            } catch (JSONException e) {
                                //
                            }
                        }
                    })
                    .show());
        });
    }

    private void showStories() {
        if (alertDialog != null && alertDialog.isShowing()) return;
        if (getActivity() == null || isRemoving()) return;
        final View view = getLayoutInflater().inflate(R.layout.story, null);
        LinearView linearView = view.findViewById(R.id.linearView);
        linearView.setCount(stories.size());
        linearView.setPosition(storyPosition);
        final TextView download = view.findViewById(R.id.download);
        final TextView username = view.findViewById(R.id.username);
        final View more = view.findViewById(R.id.more);
        createMoreButton(more, stories.get(storyPosition), 0);
        username.setText(getArguments().getString("username"));
        AppCompatImageView picture = view.findViewById(R.id.picture);
        AppCompatImageView imagePreview = view.findViewById(R.id.imagePreview);
        Glide.with(getContext()).load(getArguments().getString("profile_pic_url")).into(picture);
        Glide.with(getContext()).load(stories.get(storyPosition).getString("display_url")).into(imagePreview);
        if (stories.size() > 1) {
            view.findViewById(R.id.leftClick).setOnClickListener(view12 -> {
                if (storyPosition > 0) {
                    storyPosition--;
                } else {
                    storyPosition = stories.size() - 1;
                }
                Glide.with(getContext()).load(stories.get(storyPosition).getString("display_url")).into(imagePreview);
                linearView.setPosition(storyPosition);
                createMoreButton(more, stories.get(storyPosition), 0);
            });
            view.findViewById(R.id.rightClick).setOnClickListener(view1 -> {
                if (stories.size() - 1 > storyPosition) {
                    storyPosition++;
                } else {
                    storyPosition = 0;
                }
                Glide.with(getContext()).load(stories.get(storyPosition).getString("display_url")).into(imagePreview);
                linearView.setPosition(storyPosition);
                createMoreButton(more, stories.get(storyPosition), 0);
            });
        } else {
            view.findViewById(R.id.linearViewLayout).setVisibility(View.GONE);
        }
        download.setOnClickListener(v -> {
            boolean video_url = stories.get(storyPosition).getBoolean("is_video");
            new DownloadTask(getActivity()).execute(
                    video_url ? stories.get(storyPosition).getString("video_url") :
                            stories.get(storyPosition).getString("display_url")
                    , video_url ? "true" : "false");
        });
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        alertDialog.getWindow().setBackgroundDrawable(null);
        alertDialog.getWindow().setWindowAnimations(R.style.anim_dialog);
    }

    private void showHighlight(int position) {
        if (alertDialog != null && alertDialog.isShowing()) return;
        if (getActivity() == null || isRemoving()) return;
        highlightPosition = position;
        ArrayList<Bundle> stories = highlightsMap.get(highlights.get(position).getString("id"));
        storyPosition2 = highlights.get(position).getInt("story_position");
        final View view = getLayoutInflater().inflate(R.layout.story, null);
        LinearView linearView = view.findViewById(R.id.linearView);
        linearView.setCount(stories.size());
        linearView.setPosition(storyPosition2);
        final TextView download = view.findViewById(R.id.download);
        final TextView username = view.findViewById(R.id.username);
        final View more = view.findViewById(R.id.more);
        createMoreButton(more, stories.get(storyPosition2), 1);
        username.setText(getArguments().getString("username"));
        AppCompatImageView picture = view.findViewById(R.id.picture);
        AppCompatImageView imagePreview = view.findViewById(R.id.imagePreview);
        Glide.with(getContext()).load(getArguments().getString("profile_pic_url")).into(picture);
        Glide.with(getContext()).load(stories.get(storyPosition2).getString("display_url")).into(imagePreview);
        if (stories.size() > 1) {
            view.findViewById(R.id.leftClick).setOnClickListener(view12 -> {
                if (storyPosition2 > 0) {
                    storyPosition2--;
                } else {
                    storyPosition2 = stories.size() - 1;
                }
                Glide.with(getContext()).load(stories.get(storyPosition2).getString("display_url")).into(imagePreview);
                linearView.setPosition(storyPosition2);
                createMoreButton(more, stories.get(storyPosition2), 1);
            });
            view.findViewById(R.id.rightClick).setOnClickListener(view1 -> {
                if (stories.size() - 1 > storyPosition2) {
                    storyPosition2++;
                } else {
                    storyPosition2 = 0;
                }
                Glide.with(getContext()).load(stories.get(storyPosition2).getString("display_url")).into(imagePreview);
                linearView.setPosition(storyPosition2);
                createMoreButton(more, stories.get(storyPosition2), 1);
            });
        } else {
            view.findViewById(R.id.linearViewLayout).setVisibility(View.GONE);
        }
        download.setOnClickListener(v -> {
            boolean video_url = stories.get(storyPosition2).getBoolean("is_video");
            new DownloadTask(getActivity()).execute(
                    video_url ? stories.get(storyPosition2).getString("video_url") :
                            stories.get(storyPosition2).getString("display_url")
                    , video_url ? "true" : "false");
        });
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setOnDismissListener(dialogInterface -> highlights.get(position).putInt("story_position", storyPosition2))
                .show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        alertDialog.getWindow().setBackgroundDrawable(null);
        alertDialog.getWindow().setWindowAnimations(R.style.anim_dialog);
    }

    private void nextButton() {
        if (getArguments().getBoolean("is_private") && !getArguments().getBoolean("is_following")) {
            canLoadPosts = false;
            postLoaded = true;
            TextView is_private = findViewById(R.id.is_private);
            is_private.setVisibility(View.VISIBLE);
            followersLayout.setOnClickListener(null);
            followersLayout.setEnabled(false);
            followingsLayout.setOnClickListener(null);
            followingsLayout.setEnabled(false);
        } else {
            followersLayout.setEnabled(true);
            followersLayout.setOnClickListener(view -> {
                if (followers.getText().toString().equals("0")) {
                    return;
                }
                UserFollowers userFollowers = new UserFollowers();
                userFollowers.setArguments(getArguments());
                startFragment(userFollowers);
            });
            followingsLayout.setEnabled(true);
            followingsLayout.setOnClickListener(view -> {
                if (followings.getText().toString().equals("0")) {
                    return;
                }
                UserFollowings userFollowings = new UserFollowings();
                userFollowings.setArguments(getArguments());
                startFragment(userFollowings);
            });
            if (posts.getText().toString().equals("0")) {
                canLoadPosts = false;
                postLoaded = true;
                TextView is_private = findViewById(R.id.is_private);
                is_private.setVisibility(View.VISIBLE);
                is_private.setText("پستی برای نمایش وجود ندارد.");
            }
        }

        launchButton();
    }

    private void launchButton() {
        if (getArguments().getBoolean("is_following")) {
            btn.setText("دنبال شده");
            btn.setTextColor(0xff000000);
            btn.setBackgroundResource(R.drawable.button_demo);
        } else if (getArguments().getBoolean("outgoing_request")) {
            btn.setText("درخواست شده");
            btn.setTextColor(0xff000000);
            btn.setBackgroundResource(R.drawable.button_demo);
        } else {
            btn.setText("دنبال کردن");
            btn.setTextColor(0xffffffff);
            btn.setBackgroundResource(R.drawable.follow_demo);
        }
        btn.setOnClickListener(view -> {
            if (getArguments().getBoolean("outgoing_request") || getArguments().getBoolean("is_following")) {
                BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                        .setMessage("آیا می خواهید " + getArguments().getString("username") + " را آنفالو کنید؟")
                        .setTitle(getString(R.string.app_name))
                        .setPositiveButton("خیر", null)
                        .setNegativeButton("بله", (dialogInterface, i) -> unfollow())
                        .show());
            } else {
                BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                        .setMessage("آیا می خواهید " + getArguments().getString("username") + " را دنبال کنید؟")
                        .setTitle(getString(R.string.app_name))
                        .setPositiveButton("خیر", null)
                        .setNegativeButton("بله", (dialogInterface, i) -> follow())
                        .show());
            }
        });
    }

    private void follow() {
        btn.setEnabled(false);
        IgApi.instance().follow(getArguments().getString("pk"), (status, object) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                try {
                    JSONObject jsonObject = object.getJSONObject("friendship_status");
                    boolean outgoing_request = getArguments().getBoolean("outgoing_request");
                    boolean is_following = getArguments().getBoolean("is_following");
                    if (jsonObject.has("outgoing_request") && jsonObject.has("following")) {
                        outgoing_request = jsonObject.getBoolean("outgoing_request");
                        is_following = jsonObject.getBoolean("following");
                    } else {
                        if (getArguments().getBoolean("is_private")) {
                            outgoing_request = true;
                        } else {
                            is_following = true;
                        }
                    }
                    SQL.getSql().setFollowing(getArguments().getString("pk"), getArguments().getString("profile_pic_url"),
                            getArguments().getString("username"), getArguments().getString("full_name"));
                    int count = Integer.parseInt(Store.data().getString("following_count")) + 1;
                    Store.data().putString("following_count", String.valueOf(count));
                    getArguments().putBoolean("outgoing_request", outgoing_request);
                    getArguments().putBoolean("is_following", is_following);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                btn.setEnabled(true);
                nextButton();
            } else if (!status.serverError) {
                btn.setEnabled(true);
                if (!IgResponse.showDialogMessage(getActivity(), status)) {
                    BuildApp.Toast("درخواست دنبال کردن شکست خورد.");
                }
            } else {
                btn.setEnabled(true);
                BuildApp.Toast("درخواست دنبال کردن شکست خورد.");
            }
        });
    }

    private void unfollow() {
        btn.setEnabled(false);
        IgApi.instance().unFollow(getArguments().getString("pk"), (status, object) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                SQL.getSql().setType(getArguments().getString("pk"), "-1");
                int count = Integer.parseInt(Store.data().getString("following_count")) - 1;
                Store.data().putString("following_count", String.valueOf(count));
                getArguments().putBoolean("outgoing_request", false);
                getArguments().putBoolean("is_following", false);
                btn.setEnabled(true);
                nextButton();
            } else if (!status.serverError) {
                btn.setEnabled(true);
                if (!IgResponse.showDialogMessage(getActivity(), status)) {
                    BuildApp.Toast("درخواست آنفالو کردن شکست خورد.");
                }

            } else {
                btn.setEnabled(true);
                BuildApp.Toast("درخواست آنفالو کردن شکست خورد.");
            }
        });
    }

    boolean canLoadPosts = true;

    private void loadUserInfo() {
        isLoading = true;
        IgApi.instance().getUserInfo(getArguments().getString("pk"), (status, object) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                infoLoaded = true;
                runOnUIThread(() -> {
                    try {
                        JSONObject jsonObject = object.getJSONObject("user");
                        if (!jsonObject.getString("biography").isEmpty()) {
                            bio.setText(jsonObject.getString("biography"));
                            bio.setVisibility(View.VISIBLE);
                        }
                        if (jsonObject.has("profile_context") && !jsonObject.getString("profile_context").isEmpty()) {
                            SpannableString spannable = new SpannableString(jsonObject.getString("profile_context"));
                            profile_context.setVisibility(View.VISIBLE);
                            JSONArray array = jsonObject.getJSONArray("profile_context_links_with_user_ids");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject name = array.getJSONObject(i);
                                try {
                                    final StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
                                    spannable.setSpan(bold, name.getInt("start"), name.getInt("end"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                } catch (Exception e) {
                                    //
                                }
                            }
                            profile_context.setText(spannable);
                        }
                        posts.setText(jsonObject.getString("media_count"));
                        followers.setText(jsonObject.getString("follower_count"));
                        followings.setText(jsonObject.getString("following_count"));
                        setPageTitle(jsonObject.getString("username"));
                        getArguments().putString("username", jsonObject.getString("username"));
                        getArguments().putString("profile_pic_url", jsonObject.getString("profile_pic_url"));
                        getArguments().putString("full_name", jsonObject.getString("full_name"));
                        getArguments().putBoolean("is_private", jsonObject.getBoolean("is_private"));
                        getArguments().putBoolean("is_verified", jsonObject.getBoolean("is_verified"));
                        Glide.with(getContext()).load(jsonObject.getString("profile_pic_url")).into(pic);
                        if (jsonObject.has("hd_profile_pic_url_info")) {
                            ProfileUrl = jsonObject.getJSONObject("hd_profile_pic_url_info").getString("url");
                        } else if (jsonObject.has("hd_profile_pic_versions")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("hd_profile_pic_versions");
                            ProfileUrl = jsonArray.getJSONObject(jsonArray.length() - 1).getString("url");
                        }
                        if (ProfileUrl != null) {
                            pic.setOnLongClickListener(view -> {
                                BuildApp.setCustomFontDialog(new AlertDialog.Builder(getActivity())
                                        .setMessage("آیا می خواهید عکس پروفایل کاربر دانلود گردد؟")
                                        .setTitle(getString(R.string.app_name))
                                        .setPositiveButton("بستن", null)
                                        .setNegativeButton("ذخیره با بیشترین کیفیت", (dialogInterface, i) -> new DownloadTask(getActivity()).execute(ProfileUrl, "false"))
                                        .show());
                                return true;
                            });
                        }
                        if (getArguments().getBoolean("friendship_status")) {
                            nextButton();
                            loadHighlights();
                        } else {
                            Friendships();
                        }
                    } catch (JSONException e) {
                        IgResponse.loginRequired(getActivity());
                    }
                }, 200);
            } else if (!status.serverError) {
                progress.setVisibility(View.GONE);
                if (status.notFound) {
                    findViewById(R.id.pageNotFound).setVisibility(View.VISIBLE);
                    refreshLayout.setEnabled(false);
                } else if (checkAll) {
                    if (!IgResponse.showDialogMessage(getActivity(), status)) {
                        pageNeedRefresh.setVisibility(View.VISIBLE);
                    }
                } else {
                    pageNeedRefresh.setVisibility(View.VISIBLE);
                }
                isLoading = false;
            } else {
                progress.setVisibility(View.GONE);
                pageNeedRefresh.setVisibility(View.VISIBLE);
                isLoading = false;
            }
        });
    }

    private void Friendships() {
        statusLoaded = false;
        isLoading = true;
        IgApi.instance().getFriendships(getArguments().getString("pk"), (status, object) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                statusLoaded = true;
                runOnUIThread(() -> {
                    try {
                        getArguments().putBoolean("is_following", object.getBoolean("following"));
                        getArguments().putBoolean("outgoing_request", object.getBoolean("outgoing_request"));
                        getArguments().putBoolean("friendship_status", true);
                        nextButton();
                        loadHighlights();
                    } catch (JSONException e) {
                        //
                    }
                }, 200);
            } else if (!status.serverError) {
                progress.setVisibility(View.GONE);
                pageNeedRefresh.setVisibility(View.VISIBLE);
                isLoading = false;
                IgResponse.showDialogMessage(getActivity(), status);
            } else {
                progress.setVisibility(View.GONE);
                pageNeedRefresh.setVisibility(View.VISIBLE);
                isLoading = false;
            }
        });
    }

    private void loadHighlights() {
        isLoading = true;
        IgApi.instance().getHighlight(getArguments().getString("pk"), (status, jsonObject) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                highlightLoaded = true;
                try {
                    JSONArray array = jsonObject.getJSONArray("tray");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", object.getString("id"));
                        bundle.putString("title", object.getString("title").replace("\n", " ").trim());
                        bundle.putBoolean("isLoading", false);
                        bundle.putBoolean("is_loaded", false);
                        bundle.putInt("story_position", 0);
                        bundle.putString("cover_media", object.getJSONObject("cover_media").getJSONObject("cropped_image_version").getString("url"));
                        highlights.add(bundle);
                    }
                } catch (Exception e) {
                    //
                }
                if (!highlights.isEmpty()) {
                    createHighlights();
                }
                if (storyLoaded) {
                    progress.setVisibility(View.GONE);
                    if (canLoadPosts) {
                        progressPosts.setVisibility(View.VISIBLE);
                    } else {
                        isLoading = false;
                    }
                }

                if (storyLoaded) {
                    if (canLoadPosts) {
                        runOnUIThread(this::loadPostInfo, 200);
                    }
                } else {
                    runOnUIThread(() -> loadStories(true), 200);
                }
            } else if (!status.serverError) {
                progress.setVisibility(View.GONE);
                pageNeedRefresh.setVisibility(View.VISIBLE);
                isLoading = false;
            } else {
                progress.setVisibility(View.GONE);
                pageNeedRefresh.setVisibility(View.VISIBLE);
                isLoading = false;
            }
        });
    }

    private void loadStories(boolean autoLoad) {
        isLoadingStories = true;
        isLoading = true;
        IgApi.instance().getStories(getArguments().getString("pk"), (status, jsonObject) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                isLoadingStories = false;
                stories = new ArrayList<>();
                storyLoaded = true;
                try {
                    JSONArray jsonArray = jsonObject.getJSONObject("reel").getJSONArray("items");
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        JSONArray jSONArray2 = object.getJSONObject("image_versions2").getJSONArray("candidates");
                        bundle.putString("display_url", jSONArray2.getJSONObject(0).getString("url"));
                        if (object.has("video_versions")) {
                            String video = object.getJSONArray("video_versions").getJSONObject(0).getString("url");
                            bundle.putString("video_url", video);
                            bundle.putBoolean("is_video", true);
                        } else {
                            bundle.putBoolean("is_video", false);
                        }
                        try {
                            JSONArray story_feed_media = object.getJSONArray("story_feed_media");
                            if (story_feed_media.length() == 0) {
                                bundle.putBoolean("story_feed_media", false);
                            } else {
                                bundle.putBoolean("story_feed_media", true);
                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int j = 0; j < story_feed_media.length(); j++) {
                                    JSONObject object1 = story_feed_media.getJSONObject(j);
                                    String p = "p";
                                    if (object1.getString("product_type").equals("clips")) {
                                        p = "reel";
                                    } else if (object1.getString("product_type").equals("igtv")) {
                                        p = "tv";
                                    }
                                    arrayList.add("https://instagram.com/" + p + "/" + object1.getString("media_code"));
                                }
                                bundle.putStringArrayList("story_feed_media_list", arrayList);
                            }
                        } catch (Exception e) {
                            bundle.putBoolean("story_feed_media", false);
                        }
                        try {
                            JSONArray reel_mentions = object.getJSONArray("reel_mentions");
                            if (reel_mentions.length() == 0) {
                                bundle.putBoolean("reel_mentions", false);
                            } else {
                                bundle.putBoolean("reel_mentions", true);
                                ArrayList<String> arrayList = new ArrayList<>();
                                for (int j = 0; j < reel_mentions.length(); j++) {
                                    arrayList.add(reel_mentions.getJSONObject(j).getString("user"));
                                }
                                bundle.putStringArrayList("reel_mentions_list", arrayList);
                            }
                        } catch (Exception e) {
                            bundle.putBoolean("reel_mentions", false);
                        }
                        stories.add(bundle);
                    }
                } catch (Exception e) {
                    //
                }
                if (autoLoad) {
                    if (stories.isEmpty()) {
                        findViewById(R.id.live).setBackground(null);
                        pic.setBorderColor(0xff000000);
                    } else {
                        pic.setOnClickListener(view -> showStories());
                    }
                    progress.setVisibility(View.GONE);
                    if (canLoadPosts) {
                        progressPosts.setVisibility(View.VISIBLE);
                    } else {
                        isLoading = false;
                    }
                } else {
                    showStories();
                }
                if (autoLoad && canLoadPosts) {
                    runOnUIThread(() -> loadPostInfo(), 100);
                }
            } else if (!status.serverError) {
                isLoadingStories = false;
                if (autoLoad) {
                    progress.setVisibility(View.GONE);
                    pageNeedRefresh.setVisibility(View.VISIBLE);
                    isLoading = false;
                } else {
                    BuildApp.Toast("امکان مشاهده استوری های کاربر برای شما وجود ندارد.");
                }
            } else {
                isLoadingStories = false;
                if (autoLoad) {
                    progress.setVisibility(View.GONE);
                    pageNeedRefresh.setVisibility(View.VISIBLE);
                    isLoading = false;
                } else {
                    BuildApp.Toast("مشکلی در باز کردن استوری کاربر وجود دارد.");
                }
            }
        });
    }

    private void loadPostInfo() {
        isLoading = true;
        IgApi.instance().getUserPosts(getArguments().getString("pk"), max_id, (status, jsonObject) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                postLoaded = true;
                try {
                    if (jsonObject.has("more_available") && jsonObject.getBoolean("more_available")) {
                        max_id = jsonObject.getString("next_max_id");
                    } else {
                        max_id = "false";
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0, l = jsonArray.length(); i < l; i++) {
                        Bundle bundle = new Bundle();
                        JSONObject object = jsonArray.getJSONObject(i);
                        String caption = "";
                        if (object.has("caption") && !object.getString("caption").equals("null")) {
                            caption = object.getJSONObject("caption").getString("text");
                        }
                        bundle.putString("caption", caption);
                        String like_count = "0";
                        if (object.has("like_count")) {
                            like_count = object.getString("like_count");
                        }
                        bundle.putString("like_count", like_count);
                        String view_count = "0";
                        if (object.has("view_count")) {
                            view_count = object.getString("view_count");
                        }
                        bundle.putString("view_count", view_count);
                        String comment_count = "0";
                        if (object.has("comment_count")) {
                            comment_count = object.getString("comment_count");
                        }
                        bundle.putString("comment_count", comment_count);
                        String image_url = "";
                        if (object.has("image_versions2")) {
                            JSONArray jSONArray2 = new JSONObject(object.getString("image_versions2")).getJSONArray("candidates");
                            image_url = jSONArray2.getJSONObject(jSONArray2.length() - 1).getString("url");
                        } else if (object.has("carousel_media")) {
                            JSONArray jSONArray2 = new JSONObject(object.getJSONArray("carousel_media").getJSONObject(0).getString("image_versions2")).getJSONArray("candidates");
                            image_url = jSONArray2.getJSONObject(jSONArray2.length() - 1).getString("url");
                        }
                        bundle.putBoolean("is_video", object.getString("media_type").equals("2"));
                        bundle.putBoolean("is_slider", object.getString("media_type").equals("8"));
                        bundle.putString("pic_url", image_url);

                        JSONObject resultForDownload = new JSONObject();
                        resultForDownload.put("text", caption);
                        if (object.has("image_versions2")) {
                            resultForDownload.put("is_slider", false);
                            JSONArray jSONArray2 = object.getJSONObject("image_versions2").getJSONArray("candidates");
                            resultForDownload.put("display_url", jSONArray2.getJSONObject(0).getString("url"));
                            if (object.has("video_versions")) {
                                String video = object.getJSONArray("video_versions").getJSONObject(0).getString("url");
                                resultForDownload.put("video_url", video);
                                resultForDownload.put("is_video", true);
                            } else {
                                resultForDownload.put("is_video", false);
                            }
                        } else if (object.has("carousel_media")) {
                            resultForDownload.put("is_slider", true);
                            JSONArray jSONArray2 = object.getJSONArray("carousel_media");
                            JSONArray array1 = new JSONArray();
                            for (int j = 0; j < jSONArray2.length(); j++) {
                                JSONObject result1 = new JSONObject();
                                JSONObject object2 = jSONArray2.getJSONObject(j);
                                JSONArray jsonArray3 = object2.getJSONObject("image_versions2").getJSONArray("candidates");
                                result1.put("display_url", jsonArray3.getJSONObject(0).getString("url"));
                                if (object2.has("video_versions")) {
                                    String video = object2.getJSONArray("video_versions").getJSONObject(0).getString("url");
                                    result1.put("video_url", video);
                                    result1.put("is_video", true);
                                } else {
                                    result1.put("is_video", false);
                                }
                                array1.put(result1);
                            }
                            resultForDownload.put("edges", array1);
                        }
                        bundle.putString("json", resultForDownload.toString());
                        bundles.add(bundle);
                    }
                } catch (JSONException e) {
                    //
                }
            } else if (!status.serverError) {

            } else {

            }
            isLoading = false;
            progressPosts.setVisibility(View.GONE);
        });
    }

    private void loadHighlightMedia(int position) {
        highlights.get(position).putBoolean("isLoading", true);
        BuildApp.Toast("لطفا چند لحظه منتظر باشید...");
        IgApi.instance().getHighlightMedias(highlights.get(position).getString("id"), (status, jsonObject) -> {
            if (getActivity() == null || isRemoving()) return;
            if (status == null) {
                try {
                    if (jsonObject.getString("status").equals("ok")) {
                        try {
                            String key = jsonObject.getJSONObject("reels").keys().next();
                            JSONArray jsonArray = jsonObject.getJSONObject("reels").getJSONObject(key).getJSONArray("items");
                            ArrayList<Bundle> bundles = new ArrayList<>();
                            for (int i = 0, l = jsonArray.length(); i < l; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Bundle bundle = new Bundle();
                                JSONArray jSONArray2 = object.getJSONObject("image_versions2").getJSONArray("candidates");
                                bundle.putString("display_url", jSONArray2.getJSONObject(0).getString("url"));
                                if (object.has("video_versions")) {
                                    String video = object.getJSONArray("video_versions").getJSONObject(0).getString("url");
                                    bundle.putString("video_url", video);
                                    bundle.putBoolean("is_video", true);
                                } else {
                                    bundle.putBoolean("is_video", false);
                                }
                                try {
                                    JSONArray story_feed_media = object.getJSONArray("story_feed_media");
                                    if (story_feed_media.length() == 0) {
                                        bundle.putBoolean("story_feed_media", false);
                                    } else {
                                        bundle.putBoolean("story_feed_media", true);
                                        ArrayList<String> arrayList = new ArrayList<>();
                                        for (int j = 0; j < story_feed_media.length(); j++) {
                                            JSONObject object1 = story_feed_media.getJSONObject(j);
                                            String p = "p";
                                            if (object1.getString("product_type").equals("clips")) {
                                                p = "reel";
                                            } else if (object1.getString("product_type").equals("igtv")) {
                                                p = "tv";
                                            }
                                            arrayList.add("https://instagram.com/" + p + "/" + object1.getString("media_code"));
                                        }
                                        bundle.putStringArrayList("story_feed_media_list", arrayList);
                                    }
                                } catch (Exception e) {
                                    bundle.putBoolean("story_feed_media", false);
                                }
                                try {
                                    JSONArray reel_mentions = object.getJSONArray("reel_mentions");
                                    if (reel_mentions.length() == 0) {
                                        bundle.putBoolean("reel_mentions", false);
                                    } else {
                                        bundle.putBoolean("reel_mentions", true);
                                        ArrayList<String> arrayList = new ArrayList<>();
                                        for (int j = 0; j < reel_mentions.length(); j++) {
                                            arrayList.add(reel_mentions.getJSONObject(j).getString("user"));
                                        }
                                        bundle.putStringArrayList("reel_mentions_list", arrayList);
                                    }
                                } catch (Exception e) {
                                    bundle.putBoolean("reel_mentions", false);
                                }
                                bundles.add(bundle);
                            }
                            highlightsMap.put(highlights.get(position).getString("id"), bundles);
                            highlights.get(position).putBoolean("is_loaded", true);
                            showHighlight(position);
                        } catch (Exception e) {
                            //
                        }
                    } else {
                        highlights.get(position).putBoolean("isLoading", false);
                        BuildApp.Toast("امکان مشاهده هایلایت های کاربر برای شما وجود ندارد.");
                    }
                } catch (JSONException e) {
                    highlights.get(position).putBoolean("isLoading", false);
                }
            } else if (!status.serverError) {
                highlights.get(position).putBoolean("isLoading", false);
                BuildApp.Toast("مشکلی در باز کردن هایلایت کاربر وجود دارد.");
            } else {
                highlights.get(position).putBoolean("isLoading", false);
                BuildApp.Toast("مشکلی در باز کردن هایلایت کاربر وجود دارد.");
            }
        });
    }
}
