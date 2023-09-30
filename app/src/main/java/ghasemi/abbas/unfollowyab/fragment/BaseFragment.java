package ghasemi.abbas.unfollowyab.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import ghasemi.abbas.unfollowyab.MainActivity;
import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.builder.BuildApp;
import ghasemi.abbas.unfollowyab.components.TextView;

public abstract class BaseFragment extends Fragment {

    protected TextView pageTitle;
    private CharSequence _pageTitle;

    @LayoutRes
    public abstract int onCreateView();

    public void setPageTitle(CharSequence pageTitle) {
        if (!TextUtils.isEmpty(pageTitle)) {
            this._pageTitle = pageTitle;
            if (this.pageTitle != null) {
                this.pageTitle.setText(pageTitle);
            }
        }
    }

    public void setPageTitle(TextView pageTitle) {
        this.pageTitle = pageTitle;
        setPageTitle(_pageTitle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(onCreateView(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setFocusable(true);
        view.setClickable(true);
        onCreateFragment(savedInstanceState);
    }

    @CallSuper
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {

    }

    @Nullable
    protected View.OnClickListener buttonRightClick() {
        return null;
    }

    protected int buttonRightIconRes() {
        return R.drawable.ic_round_help_outline_24;
    }

    public void onCreateButtonRight(AppCompatImageView imageView) {
        if (buttonRightClick() != null) {
            imageView.setOnClickListener(buttonRightClick());
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
        imageView.setImageResource(buttonRightIconRes());
    }

    public <T extends View> T findViewById(@IdRes int id) {
        if (getView() == null) return null;
        return getView().findViewById(id);
    }

    @Override
    public void onStart() {
        super.onStart();
        hide();
    }


    public void onResumeFragment() {

    }

    @Override
    public void onPause() {
        hide();
        super.onPause();
    }

    private void hide() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            //
        }
    }

    public void startFragment(BaseFragment baseFragment) {
        if (getActivity() == null || isRemoving()) return;
        ((MainActivity) getActivity()).startFragment(baseFragment);
    }


    public void finish() {
        if (getActivity() == null || isRemoving()) return;
        getActivity().onBackPressed();
    }

    public void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public void runOnUIThread(Runnable runnable, long delay) {
        if (getActivity() == null || isRemoving()) return;
        BuildApp.runOnUIThread(runnable, delay);
    }

    public void cancelRunOnUIThread(Runnable runnable) {
        BuildApp.cancelRunOnUIThread(runnable);
    }

}
