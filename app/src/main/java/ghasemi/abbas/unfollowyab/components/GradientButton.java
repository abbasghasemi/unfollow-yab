package ghasemi.abbas.unfollowyab.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import ghasemi.abbas.unfollowyab.R;

public class GradientButton extends RelativeLayout {
    private CardView card_button;
    private TextView text_button;

    public GradientButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.gradient_button, this);

        card_button = findViewById(R.id.card_button);
        text_button = findViewById(R.id.text_button);

        attrsSet(context, attrs);
    }

    private void attrsSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GradientButton, 0, 0);
        setText(typedArray.getString(R.styleable.GradientButton_gb_text));
        text_button.setBackgroundResource(typedArray.getResourceId(R.styleable.GradientButton_gb_drawable, 0));
        card_button.setRadius(typedArray.getDimension(R.styleable.GradientButton_gb_radius, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics()
        )));
        card_button.getLayoutParams().height = (int) typedArray.getDimension(R.styleable.GradientButton_gb_height, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()
        ));
        typedArray.recycle();
    }

    public void setText(CharSequence text) {
        text_button.setText(text);
    }
}
