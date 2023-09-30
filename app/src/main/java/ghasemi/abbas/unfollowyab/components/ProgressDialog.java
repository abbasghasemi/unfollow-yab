package ghasemi.abbas.unfollowyab.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import ghasemi.abbas.unfollowyab.R;

public class ProgressDialog extends Dialog {
    private final ProgressBar progressBar;
    private final TextView help;
    private final TextView number;

    public ProgressDialog(Context context) {
        super(context);

        setContentView(LayoutInflater.from(context).inflate(R.layout.progress_data, null));
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressBar = findViewById(R.id.progressBar);
        help = findViewById(R.id.help);
        number = findViewById(R.id.number);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
        number.setText(String.format("%s%s", progress, "%"));
    }

    public void setMessage(String message) {
        help.setText(message);
    }

    public void setButton(final View.OnClickListener onClickListener) {
        findViewById(R.id.cancel).setVisibility(View.VISIBLE);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onClickListener.onClick(v);
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
