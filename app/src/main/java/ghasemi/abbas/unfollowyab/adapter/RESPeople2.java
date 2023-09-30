package ghasemi.abbas.unfollowyab.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.GradientButton;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;
import ghasemi.abbas.unfollowyab.fragment.User;

public class RESPeople2 extends RecyclerView.Adapter<RESPeople2.Holder> implements Filterable {
    private int pos = -1;
    private final ArrayList<Bundle> FULL = new ArrayList<>();
    private ArrayList<Bundle> bundles;
    private OnClickHolder onClickHolder;
    private boolean unFollow, Follow;
    private MainActivity mainActivity;

    public void setOnClickHolder(OnClickHolder onClickHolder) {
        this.onClickHolder = onClickHolder;
    }

    public void setUnFollow() {
        unFollow = true;
    }

    public void setFollow() {
        setUnFollow();
        Follow = true;
    }

    public void setData(ArrayList<Bundle> bundles) {
        this.bundles = bundles;
        FULL.addAll(bundles);
        if (unFollow) {
            FULL.remove(0);
        }
    }

    public void setMainActivity(FragmentActivity fragmentActivity) {
        this.mainActivity = (MainActivity) fragmentActivity;
    }

    @NonNull
    @Override
    public RESPeople2.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new RESPeople2.Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_auto_un_follow, null), i);
        }
        return new RESPeople2.Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_people, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RESPeople2.Holder holder, @SuppressLint("RecyclerView") final int ii) {
        holder.bind();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Holder holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return bundles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (unFollow && position == 0) {
            return 1;
        }
        return super.getItemViewType(position);
    }

    class Holder extends RecyclerView.ViewHolder {

        RoundedImageView img;
        TextView username, name;
        GradientButton start;
        View onClick;
        AppCompatImageView openInWindow;

        Holder(@NonNull View itemView) {
            super(itemView);
            onClick = itemView;
            img = onClick.findViewById(R.id.pic);
            name = onClick.findViewById(R.id.name);
            openInWindow = onClick.findViewById(R.id.openInWindow);
            name.setGravity(Gravity.LEFT);
            username = onClick.findViewById(R.id.count);
            username.setGravity(Gravity.LEFT);
        }

        Holder(@NonNull View itemView, int x) {
            super(itemView);
            start = itemView.findViewById(R.id.start);
            if (Follow) {
                start.setText("فالو خودکار");
            }
            start.setOnClickListener(v -> onClickHolder.onClick(0));
        }

        public void bind() {
            if (start == null) {
                Bundle bundle = bundles.get(getLayoutPosition());
                username.setText(bundle.getString("username"));
                if (bundle.getString("full_name").isEmpty()) {
                    name.setVisibility(View.GONE);
                } else {
                    name.setVisibility(View.VISIBLE);
                    name.setText(bundle.getString("full_name"));
                }
                Glide.with(itemView.getContext()).load(bundle.getString("profile_pic_url")).into(img);
                if (onClickHolder == null) {
                    onClick.setClickable(false);
                    openInWindow.setColorFilter(openInWindow.getContext().getResources().getColor(R.color.colorPrimary));
                } else {
                    openInWindow.setColorFilter(openInWindow.getContext().getResources().getColor(R.color.colorAccent));
                    onClick.setClickable(true);
                    onClick.setOnClickListener(v -> {
                        if (getLayoutPosition() != RecyclerView.NO_POSITION)
                            onClickHolder.onClick(getLayoutPosition());
                    });
                }
                openInWindow.setOnClickListener(v -> {
                    if (Store.data().getBool("openAccountInApp")) {
                        Bundle b = MainActivity.foundCashed(bundle.getString("pk"));
                        if (b == null) {
                            b = bundle;
                            b.putBoolean("friendship_status", false);
                            MainActivity.addCashed(b);
                        }
                        b.putInt("latest_reel_media", -1);
                        User user = new User();
                        user.setArguments(b);
                        mainActivity.startFragment(user);
                    } else {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://instagram.com/_u/" + bundle.getString("username")));
                            intent.setPackage("com.instagram.android");
                            v.getContext().startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://instagram.com/" + bundle.getString("username")));
                            v.getContext().startActivity(intent);
                        }
                    }
                });
            }
            if (getLayoutPosition() > pos) {
                pos++;
                itemView.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.slide_in_bottom));
            }
        }
    }

    @Override
    public Filter getFilter() {
        return Searched;
    }

    private final Filter Searched = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            results.values = RESPeople2.this.filter(FULL, constraint.toString());
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((ArrayList<Bundle>) results.values);
        }
    };

    private ArrayList<Bundle> filter(ArrayList<Bundle> src, CharSequence query) {
        final ArrayList<Bundle> filteredModelList = new ArrayList<>();
        if (unFollow) {
            filteredModelList.add(null);
        }
        if (TextUtils.isEmpty(query)) {
            filteredModelList.addAll(src);
        } else {
            final String lowerCaseQuery = query.toString().toLowerCase();
            for (Bundle bundle : src) {
                final String username = bundle.getString("username");
                final String full_name = bundle.getString("full_name");
                if (username.contains(lowerCaseQuery) || full_name.contains(lowerCaseQuery)) {
                    filteredModelList.add(bundle);
                }
            }
        }
        return filteredModelList;
    }

    private void animateTo(ArrayList<Bundle> models) {
        try {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        } catch (Exception e) {
            //
        }
    }

    private void applyAndAnimateRemovals(ArrayList<Bundle> newModels) {
        for (int i = bundles.size() - 1; i >= 0; i--) {
            final Bundle model = bundles.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Bundle> newModels) {

        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Bundle model = newModels.get(i);
            if (!bundles.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Bundle> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Bundle model = newModels.get(toPosition);
            final int fromPosition = bundles.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void removeItem(int position) {
        bundles.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(int position, Bundle model) {
        bundles.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Bundle model = bundles.remove(fromPosition);
        bundles.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


    public void removeByIndex(int index) {
        removeFromFull(bundles.remove(index));
    }

    private void removeFromFull(Bundle bundle) {
        FULL.remove(bundle);
    }
}