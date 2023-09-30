package ghasemi.abbas.unfollowyab.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.components.TextView;

public class RESPost extends RecyclerView.Adapter<RESPost.Holder> {

    private ArrayList<Bundle> bundles;

    private OnClickHolder onClickHolder;
    private boolean isComment;
    private boolean isView;
    private boolean isPost;
    private boolean hide;

    public void setComment() {
        isComment = true;
    }

    public void setPost() {
        isPost = true;
    }

    public void setOnClickHolder(OnClickHolder onClickHolder) {
        this.onClickHolder = onClickHolder;
    }

    public void setData(ArrayList<Bundle> bundles) {
        this.bundles = bundles;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, null));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") final int i) {
        if (!hide) {
            if (isView) {
                holder.video.setVisibility(View.VISIBLE);
                holder.title.setVisibility(View.GONE);
                holder.title3.setVisibility(View.VISIBLE);
                holder.title3.setText(String.format("%s بازدید", bundles.get(i).getString("view_count")));
            } else {
                if (isComment) {
                    holder.title.setText(String.format("%s کامنت", bundles.get(i).getString("comment_count")));
                } else {
                    holder.title.setText(String.format("%s لایک", bundles.get(i).getString("like_count")));
                    if (isPost) {
                        holder.title2.setText(String.format("%s کامنت", bundles.get(i).getString("comment_count")));
                    }
                }
                if (bundles.get(i).getBoolean("is_video")) {
                    holder.video.setVisibility(View.VISIBLE);
                    if (isPost) {
                        holder.title3.setVisibility(View.VISIBLE);
                        holder.title3.setText(String.format("%s بازدید", bundles.get(i).getString("view_count")));
                    }
                } else {
                    holder.title3.setVisibility(View.GONE);
                    holder.video.setVisibility(View.GONE);
                }
            }
        } else {
            if (bundles.get(i).getBoolean("is_video")) {
                holder.video.setImageResource(R.drawable.ic_video);
                holder.video.setVisibility(View.VISIBLE);
            } else if (bundles.get(i).getBoolean("is_slider")) {
                holder.video.setImageResource(R.drawable.ic_round_collections_24);
                holder.video.setVisibility(View.VISIBLE);
            } else {
                holder.video.setVisibility(View.GONE);
            }
            holder.title.setVisibility(View.GONE);
            holder.title2.setVisibility(View.GONE);
            holder.title3.setVisibility(View.GONE);
        }
        Glide.with(holder.img.getContext())
                .load(bundles.get(i).getString("pic_url"))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.img);
        if (onClickHolder == null) {
            holder.onClick.setClickable(false);
        } else {
            holder.onClick.setClickable(true);
            holder.onClick.setOnClickListener(v -> onClickHolder.onClick(i));
        }
    }

    @Override
    public int getItemCount() {
        return bundles.size();
    }

    public void setView() {
        isView = true;
    }

    public void hideInfo() {
        hide = true;
    }

    class Holder extends RecyclerView.ViewHolder {

        AppCompatImageView img, video;
        TextView title, title2, title3;
        View onClick;

        Holder(@NonNull View itemView) {
            super(itemView);
            onClick = itemView.findViewById(R.id.onClick);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            video = itemView.findViewById(R.id.video);
            title2 = itemView.findViewById(R.id.title2);
            title3 = itemView.findViewById(R.id.title3);
            if (isPost) {
                title2.setVisibility(View.VISIBLE);
            }
        }
    }

    public void applyAndAnimateMovedItems(ArrayList<Bundle> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Bundle model = newModels.get(toPosition);
            final int fromPosition = bundles.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


    public void moveItem(int fromPosition, int toPosition) {
        final Bundle model = bundles.remove(fromPosition);
        bundles.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}