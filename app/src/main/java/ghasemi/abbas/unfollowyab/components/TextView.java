package ghasemi.abbas.unfollowyab.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import ghasemi.abbas.unfollowyab.R;


public class TextView extends AppCompatTextView {
    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyleAttr, 0);
        boolean b = a.getBoolean(R.styleable.TextView_bold, false);
        int color = a.getColor(R.styleable.TextView_color, 0xFF797979);
        a.recycle();
        if (b) {
            setTypeface(ResourcesCompat.getFont(context, R.font.sans_bold));
        }
        setTextColor(color);
    }
}