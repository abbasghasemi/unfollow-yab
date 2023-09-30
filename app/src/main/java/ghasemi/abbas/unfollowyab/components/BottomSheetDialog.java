package ghasemi.abbas.unfollowyab.components;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;

import ghasemi.abbas.unfollowyab.R;

public class BottomSheetDialog extends com.google.android.material.bottomsheet.BottomSheetDialog {
    private AppCompatImageView icon;
    private TextView white_list, full_name, open, unfollow, delete;

    public BottomSheetDialog(@NonNull Context context) {
        super(context, R.style.ThemeBottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_layout, null);
        icon = view.findViewById(R.id.pic);
        full_name = view.findViewById(R.id.full_name);
        white_list = view.findViewById(R.id.white_list);
        open = view.findViewById(R.id.open);
        unfollow = view.findViewById(R.id.unfollow);
        delete = view.findViewById(R.id.delete);
        setContentView(view);
        getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);
    }

    public static class Builder {
        BottomSheetDialog bottomSheetDialog;

        public Builder(Context context) {
            bottomSheetDialog = new BottomSheetDialog(context);
        }

        public Builder setFullName(String fullName) {
            bottomSheetDialog.full_name.setText(fullName);
            return this;
        }

        public Builder setIcon(String profile_pic_url) {
            Glide.with(bottomSheetDialog.getContext()).load(profile_pic_url).into(bottomSheetDialog.icon);
            return this;
        }

        public Builder setDelete(final View.OnClickListener onClickListener) {
            bottomSheetDialog.delete.setVisibility(View.VISIBLE);
            bottomSheetDialog.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                    bottomSheetDialog.dismiss();
                }
            });
            return this;
        }

        public Builder setBotton(final View.OnClickListener onClickListener) {
            bottomSheetDialog.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                    bottomSheetDialog.dismiss();
                }
            });
            return this;
        }

        public Builder setBotton(String name, final View.OnClickListener onClickListener) {
            if (name == null) {
                bottomSheetDialog.unfollow.setVisibility(View.GONE);
                return this;
            }
            bottomSheetDialog.unfollow.setText(name);
            bottomSheetDialog.unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                    bottomSheetDialog.dismiss();
                }
            });
            return this;
        }

        public Builder setWhiteListBotton(boolean no_white_list, final View.OnClickListener onClickListener) {
            if (!no_white_list) {
                bottomSheetDialog.white_list.setText("حذف از لیست سفید");
                bottomSheetDialog.unfollow.setVisibility(View.GONE);
            }
            bottomSheetDialog.white_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.white_list.setVisibility(View.VISIBLE);
            return this;
        }

        public BottomSheetDialog create() {
            return bottomSheetDialog;
        }

        public BottomSheetDialog show() {
            if (!bottomSheetDialog.getOwnerActivity().isFinishing()) {
                bottomSheetDialog.show();
            }
            return bottomSheetDialog;
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
