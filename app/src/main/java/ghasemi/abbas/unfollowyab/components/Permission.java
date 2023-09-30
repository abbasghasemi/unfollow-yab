package ghasemi.abbas.unfollowyab.components;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import com.farasource.component.button.MaterialButton;

import ghasemi.abbas.unfollowyab.R;

public class Permission {

    public Permission(Activity activity, View.OnClickListener click, String title, String content, int logo) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.anim_dialog);
        dialog.setContentView(R.layout.dialog_permission);
        dialog.show();
        MaterialButton settings = dialog.findViewById(R.id.settings);
        settings.setTypeface(ResourcesCompat.getFont(activity, R.font.sans_bold));
        settings.setOnClickListener(v -> {
            dialog.dismiss();
            click.onClick(v);
        });
        MaterialButton close = dialog.findViewById(R.id.close);
        close.setTypeface(ResourcesCompat.getFont(activity, R.font.sans_bold));
        close.setOnClickListener(view -> dialog.dismiss());

        TextView _title = dialog.findViewById(R.id.title);
        TextView _content = dialog.findViewById(R.id.content);
        AppCompatImageView _logo = dialog.findViewById(R.id.logo);

        if (title != null) {
            _title.setText(title);
        }
        if (content != null) {
            _content.setText(content);
        }
        if (logo != View.NO_ID) {
            _logo.setImageResource(logo);
        }
    }
}
