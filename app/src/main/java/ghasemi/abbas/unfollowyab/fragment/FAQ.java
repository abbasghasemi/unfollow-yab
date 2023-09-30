package ghasemi.abbas.unfollowyab.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.components.TextView;

public class FAQ extends BaseFragment {
    @Override
    public int onCreateView() {
        return R.layout.privacy_policy;
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("سوالات متداول");
        findViewById(R.id.actionBarLayout).setVisibility(View.GONE);
        TextView privacy = findViewById(R.id.privacy);
        privacy.setText(HtmlCompat.fromHtml(
                "سرعت بروزرسانی برای اکانت من کند است، چکار کنم؟" + "<br>" + "برنامه با نهایت سرعت ممکن، فعالیت خود را انجام می دهد اما گاهی اوقات ممکن است به دلیل اختلالات شبکه اینترنت یا سرور اینستاگرام بروزرسانی کند گردد و برنامه نیاز دارد کلیه داده ها را از اینستاگرام دریافت کند تا بتواند به درستی کار کند بنابراین هرچه تعداد فالوور ها و پست هاتون بیشتر باشد، زمان بروزرسانی نیز بیشتر می گردد." + "<br><br>" + "نکته مهم هنگام بروزرسانی بخش پست ها:<br>" + "توجه نمایید اینستاگرام تنها 1000 لایک یا کامنت آخر هر پست شما را مشخص می کند، توسط چه کسی انجام شده است، بنابراین به دلیل کامل نبودن داده ها، ممکن است اشتباهات آماری به وجود آورد که به هیچ عنوان این مورد قابل رفع شدن نیست." + "<br><br>" + "آیا برنامه به صورت نامحدود می تواند آنفالو یا فالو کند؟" + "<br>" + "آنفالو یا فالو توسط اینستاگرام انجام می شود و درصورت مسدود شدن موقت نیاز است مدت زمانی صبر کنید تا از مسدودیت خارج شوید و این موضوع قابل دور زدن نیست و به اینستاگرام مربوط است." + "<br><br>" + "اکانت من محدود شده است،چکار کنم؟" + "<br>" + "تنها راه حل صبر کردن است، تا از محدودیت ها رهایی پیدا کنید، اما پیشنهاد می شود مطمئن شوید بعد هر لاگین در بخش تنظیمات->امنیت-> فعالیت های ورود به سیستم، لاگین های اضافی حذف شده و ورود فعلی را تائید کرده باشید (دکمه ی 'من هستم' یا 'This Was Me')."
                , HtmlCompat.FROM_HTML_MODE_LEGACY));
    }
}
