package ghasemi.abbas.unfollowyab.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import ghasemi.abbas.unfollowyab.builder.BuildApp;

public class LinearView extends View {

    private int count;
    private int position;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int colorBack = 0xccDADADA;
    private int colorOn = 0xffffffff;
    private final float paddingH;

    public LinearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(BuildApp.getDensity() * 1.7f);

        paddingH = BuildApp.getDensity() * 3.5f;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPosition(int position) {
        this.position = position;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int split = (int) (paddingH * (count + 1));
        width -= split;
        float w = (float) width / count;
        float nextWidth = paddingH;
        paint.setColor(colorOn);
        int h = getHeight() / 2;
        int i = 0;
        for (; i <= position; i++) {
            canvas.drawLine(nextWidth, h, nextWidth + w, h, paint);
            nextWidth += w + paddingH;
        }
        paint.setColor(colorBack);
        for (; i < count; i++) {
            canvas.drawLine(nextWidth, h, nextWidth + w, h, paint);
            nextWidth += w + paddingH;
        }
    }
}
