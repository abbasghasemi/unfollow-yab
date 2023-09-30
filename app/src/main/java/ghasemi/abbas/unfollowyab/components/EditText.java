package ghasemi.abbas.unfollowyab.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class EditText extends AppCompatEditText {

    {
        setTextColor(Color.BLACK);
    }

    public EditText(Context context) {
        super(context);

    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
