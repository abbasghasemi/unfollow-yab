package ghasemi.abbas.unfollowyab.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.components.TextView;

public class RESItem extends RecyclerView.Adapter<RESItem.Holder> {

    private String[] title;
    private int[] images;

    private OnClickHolder onClickHolder;
    private int layoutID;

    public void setLayoutID(int layoutID) {
        this.layoutID = layoutID;
    }

    public void setOnClickHolder(OnClickHolder onClickHolder) {
        this.onClickHolder = onClickHolder;
    }

    public void setData(String[] title, int[] images) {
        this.title = title;
        this.images = images;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(layoutID, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int i) {
        holder.title.setText(title[i]);
        holder.img.setImageResource(images[i]);
        holder.onClick.setOnClickListener(v -> onClickHolder.onClick(i));
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    static class Holder extends RecyclerView.ViewHolder {

        AppCompatImageView img;
        TextView title;
        View onClick;

        Holder(@NonNull View itemView) {
            super(itemView);
            onClick = itemView;
            img = onClick.findViewById(R.id.img);
            title = onClick.findViewById(R.id.title);
        }
    }

}