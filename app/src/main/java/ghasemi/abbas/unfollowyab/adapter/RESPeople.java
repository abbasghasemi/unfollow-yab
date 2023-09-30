package ghasemi.abbas.unfollowyab.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;
import ghasemi.abbas.unfollowyab.fragment.User;

public class RESPeople extends RecyclerView.Adapter<RESPeople.Holder> implements Filterable {

    private final ArrayList<Bundle> FULL = new ArrayList<>();
    private ArrayList<Bundle> bundles;
    private OnClickHolder onClickHolder;
    private boolean isComment;
    private MainActivity mainActivity;

    public void setOnClickHolder(OnClickHolder onClickHolder) {
        this.onClickHolder = onClickHolder;
    }

    private int pos = -1;

    public void setComment() {
        isComment = true;
    }

    public void setData(ArrayList<Bundle> bundles) {
        this.bundles = bundles;
        FULL.addAll(bundles);
    }


    @NonNull
    @Override
    public RESPeople.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RESPeople.Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_people, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RESPeople.Holder holder, @SuppressLint("RecyclerView") final int ii) {
        holder.bind();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Holder holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }

    public void setMainActivity(FragmentActivity fragmentActivity) {
        this.mainActivity = (MainActivity) fragmentActivity;
    }

    @Override
    public int getItemCount() {
        return bundles.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        RoundedImageView img;
        TextView name, count;
        View onClick;
        AppCompatImageView openInWindow;

        Holder(@NonNull View itemView) {
            super(itemView);
            onClick = itemView;
            img = onClick.findViewById(R.id.pic);
            count = onClick.findViewById(R.id.count);
            name = onClick.findViewById(R.id.name);
            openInWindow = onClick.findViewById(R.id.openInWindow);
        }

        void bind() {
            Bundle bundle = bundles.get(getLayoutPosition());
            if (isComment) {
                count.setText(String.format("%s کامنت", bundle.getString("comment_count")));
            } else {
                count.setText(String.format("%s لایک", bundle.getString("like_count")));
            }
            if (bundle.getString("full_name").isEmpty()) {
                name.setText(bundle.getString("username"));
            } else {
                name.setText(String.format("%s | %s", bundle.getString("username"), bundle.getString("full_name")));
            }
            Glide.with(itemView.getContext()).load(bundle.getString("profile_pic_url")).into(img);
            if (onClickHolder == null) {
                onClick.setClickable(false);
            } else {
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
            results.values = RESPeople.this.filter(FULL, constraint.toString());
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((ArrayList<Bundle>) results.values);
        }
    };

    private ArrayList<Bundle> filter(ArrayList<Bundle> src, CharSequence query) {
        final ArrayList<Bundle> filteredModelList = new ArrayList<>();
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
}