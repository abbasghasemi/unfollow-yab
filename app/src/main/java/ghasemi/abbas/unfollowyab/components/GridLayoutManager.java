package ghasemi.abbas.unfollowyab.components;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutManager extends androidx.recyclerview.widget.GridLayoutManager {

    public GridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Log.e("onLayoutChildren", e.toString());
        }
    }
}