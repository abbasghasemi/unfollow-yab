package ghasemi.abbas.unfollowyab.fragment;

import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;

import com.shawnlin.numberpicker.NumberPicker;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.Store;
import ghasemi.abbas.unfollowyab.components.TextView;

public class Settings extends BaseFragment {

    @Override
    public int onCreateView() {
        return R.layout.settings;
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("تنظیمات");
        String[] listAdapter = {"همه ی پست ها", "18 پست آخر", "36 پست آخر", "54 پست آخر", "72 پست آخر",
                "90 پست آخر", "108 پست آخر", "126 پست آخر", "144 پست آخر", "162 پست آخر", "180 پست آخر"};
        NumberPicker numberPicker = findViewById(R.id.picker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(listAdapter.length - 1);
        numberPicker.setDisplayedValues(listAdapter);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setValue(Store.data().getInt("limitCheckPosts", 1));
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setTypeface(ResourcesCompat.getFont(getContext(), R.font.sans));
        numberPicker.setSelectedTypeface(ResourcesCompat.getFont(getContext(), R.font.sans));
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> Store.data().putInt("limitCheckPosts", newVal));

        TextView time = findViewById(R.id.time);
        time.setText(String.valueOf(Store.data().getInt("intervalPerFollow", 1)));
        AppCompatSeekBar seekbar = findViewById(R.id.seekbar);
        seekbar.setProgress(Store.data().getInt("intervalPerFollow", 1) - 1);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Store.data().putInt("intervalPerFollow", ++i);
                time.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SwitchCompat switchBtn = findViewById(R.id.switchBtn);
        switchBtn.setChecked(Store.data().getBool("openAccountInApp"));
        switchBtn.setOnCheckedChangeListener((compoundButton, b) -> Store.data().putBool("openAccountInApp", b));
        findViewById(R.id.switchLayout).setOnClickListener(view -> {
            if (MainActivity.canUseItem((MainActivity) getActivity())) {
                switchBtn.setChecked(!switchBtn.isChecked());
            }
        });
    }
}
