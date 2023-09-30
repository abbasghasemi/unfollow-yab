package ghasemi.abbas.unfollowyab.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.components.TextView;
import ghasemi.abbas.unfollowyab.components.roundedimageview.RoundedImageView;

public class RESPeople3 extends RecyclerView.Adapter<RESPeople3.Holder> {

    private ArrayList<Bundle> bundles;
    private OnClickHolder onClickHolder;

    public void setOnClickHolder(OnClickHolder onClickHolder) {
        this.onClickHolder = onClickHolder;
    }

    public void setData(ArrayList<Bundle> bundles) {
        this.bundles = bundles;
    }


    public ArrayList<Bundle> getBundles() {
        return bundles;
    }

    @NonNull
    @Override
    public RESPeople3.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RESPeople3.Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_people_live, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RESPeople3.Holder holder, @SuppressLint("RecyclerView") final int ii) {
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

    class Holder extends RecyclerView.ViewHolder {

        RoundedImageView img;
        TextView name, username;
        View onClick, live, is_verified, is_following, is_private;

        Holder(@NonNull View itemView) {
            super(itemView);
            onClick = itemView;
            img = onClick.findViewById(R.id.pic);
            live = onClick.findViewById(R.id.live);
            username = onClick.findViewById(R.id.username);
            name = onClick.findViewById(R.id.name);
            is_following = onClick.findViewById(R.id.is_following);
            is_private = onClick.findViewById(R.id.is_private);
            is_verified = onClick.findViewById(R.id.is_verified);
        }

        void bind() {
            Bundle bundle = bundles.get(getLayoutPosition());
            username.setText(bundle.getString("username"));
            if (bundle.getString("full_name").isEmpty()) {
                name.setVisibility(View.GONE);
            } else {
                name.setVisibility(View.VISIBLE);
                name.setText(bundle.getString("full_name"));
            }

            if (bundle.getBoolean("is_verified")) {
                is_verified.setVisibility(View.VISIBLE);
            } else {
                is_verified.setVisibility(View.GONE);
            }

            if (bundle.getBoolean("is_private")) {
                is_private.setVisibility(View.VISIBLE);
            } else {
                is_private.setVisibility(View.GONE);
            }

            if (bundle.getBoolean("is_following")) {
                is_following.setVisibility(View.VISIBLE);
            } else {
                is_following.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext()).load(bundle.getString("profile_pic_url")).into(img);
            if (bundle.getInt("latest_reel_media") == 0) {
                live.setBackground(null);
                img.setBorderColor(0xff000000);
            } else {
                live.setBackgroundResource(R.drawable.live);
                img.setBorderColor(0xffffffff);
            }
            if (onClickHolder == null) {
                onClick.setClickable(false);
            } else {
                onClick.setClickable(true);
                onClick.setOnClickListener(v -> {
                    if (getLayoutPosition() != RecyclerView.NO_POSITION)
                        onClickHolder.onClick(getLayoutPosition());
                });
            }


        }
    }

    public void animateTo(ArrayList<Bundle> models) {
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

    private void removeItem(int position) {
        bundles.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, Bundle model) {
        bundles.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final Bundle model = bundles.remove(fromPosition);
        bundles.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}