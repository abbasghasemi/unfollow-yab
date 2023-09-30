package ghasemi.abbas.unfollowyab.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import ghasemi.abbas.unfollowyab.R;
import ghasemi.abbas.unfollowyab.components.TextView;

public class PrivacyPolicy extends BaseFragment {

    private final String FA = "<h4>آخرین ویرایش: نسخه ۳.۰</h4>\n" +
            "<b>ذخیره داده ها:</b>\n" +
            "<p>\n" +
            "    ما داده های اینستاگرام شما را، تنها در دیتابیس اپلیکیشن ذخیره کرده و آنها را محفوظ نگه می داریم، داده هایی که توسط\n" +
            "    برنامه جمع آوری و ذخیره می شوند، توسط شما به صورت کامل از دیتابیس برنامه حذف می شوند.\n" +
            "    ما تصدیق می کنیم داده های ذخیره شده به هیج وجه به سرور ها، اپلیکیشن ها و خارج از برنامه، ارسال نگردند، بنابراین\n" +
            "    برنامه ما مستقل از هر چیزی تنها با اینستاگرام فعالیت دارد و کلیه داده های حساس شما را در برنامه محفوظ نگه می دارد.\n" +
            "    همچنین باید بدانید اطلاعات محرمانه شما همانند گذرواژه ها به هیچ عنوان ذخیره نمی شوند.\n" +
            "</p>\n" +
            "<b>حذف داده ها:</b>\n" +
            "<p>\n" +
            "    با حذف اپلیکیشن کلیه داده های شما برا همیشه حذف خواهند شد؛ همچنین از قسمت مدیریت حساب ها می توانید حساب خاصی را به\n" +
            "    طور کامل حذف نمایید.\n" +
            "</p>\n" +
            "<b>فعالیت برنامه:</b>\n" +
            "<p>\n" +
            "    ما این برنامه را برای مدیریت دقیق تر و کمک به رونق دادن به کسب و کار شما ایجاد کرده ایم. ما داده های حساب شما را\n" +
            "    تجزیه و تحلیل می کنیم و اطلاعات آماری را در اختیار شما قرار می دهیم تا به کمک آنها نقاط قوت و ضعف کاری خود را متوجه\n" +
            "    شوید.\n" +
            "    ما تصدیق می کنیم فعالیت برنامه در چارچوب قوانین اینستاگرام است.\n" +
            "</p>\n" +
            "<b>داده هایی که در برنامه ذخیره می شوند:</b>\n" +
            "<p>\n" +
            "    ما ممکن است داده های زیر را جمع آوری کنیم، این داده ها فقط در برنامه ذخیره می شوند و به هیچ وجه با دیگران، سرور،\n" +
            "    سرویس و اشخاص ثالث و غیره به اشتراک گذاشته نمی شوند.\n" +
            "    <br>\n" +
            "    - اکانت پروفایل شما\n" +
            "    <br>\n" +
            "    - افرادی که دنبال می کنید\n" +
            "    <br>\n" +
            "    - افرادی که دنبال می شوند\n" +
            "    <br>\n" +
            "    - پست های حساب شما\n" +
            "</p>\n" +
            "<b>استفاده از داده ها:</b>\n" +
            "<p>\n" +
            "    ما داده های شما را در اپلیکیشن تجزیه و تحلیل می کنیم و برای شما گزارش تهیه می کنیم که با کمک این تحلیل و بررسی ها می\n" +
            "    توانید نقاط قوت و ضعف خود را پیدا کرده و روی آن ها کار کنید.\n" +
            "    ما تصدیق می کنیم جز تحلیل داده ها فرآیند های دیگری روی حساب شما اعمال نگردند و تغییری در حساب های کاربری شما جز با\n" +
            "    اختیار شما ایجاد نگردد.\n" +
            "</p>\n" +
            "<b>خط مشی و حریم خصوصی ما به شما این امکان را می دهد که هر زمان که نیاز دارید این کار را انجام دهید:</b>\n" +
            "<p>\n" +
            "    - تمام اطلاعات خود را از برنامه حذف کنید.\n" +
            "    <br>\n" +
            "    - تمام فعالیت های خود را ببندید و حساب خود را با داده های آن برای همیشه حذف کنید.\n" +
            "    <br>\n" +
            "    - با حذف اپلیکیشن، عملا تمامی اطلاعات شما برای همیشه از بین می رود.\n" +
            "</p>\n" +
            "<b>شرایط استفاده:</b>\n" +
            "<p>\n" +
            "    اپلیکیشن ممکن است بر روی برخی دستگاه ها و یا به دلیل اختلالات اینستاگرام یا شبکه ی شما و هرگونه خطای پیش بینی نشده دیگر کارکرد مطلوب خود را نداشته و یا رضایت شما را ایجاد نکرده باشد لذا هیچ گونه ضمانت اجرایی در قبال آن نخواهیم داد و همواره در تلاشیم در هر ابدیت مشکلات گزارشی شما را برطرف کنیم.\n" +
            "</p>\n" +
            "<b>شرایط سنی:</b>\n" +
            "<p>\n" +
            "    مناسب برای گروه سنی نوجوان و بالاتر\n" +
            "</p>\n" +
            "<b>خدمات شخص ثالث:</b>\n" +
            "<p>\n" +
            "    ما از خدمات شخص ثالث در برنامه خود استفاده می کنیم، تصدیق می کنیم که به اطلاعات حساب شما دسترسی ندارند و تنها به\n" +
            "    داده های خود متکی است، بنابراین خط مشی آن را بخوانید:\n" +
            "    <br>\n" +
            "    - سرویس اطلاع رسانی (Firebase)\n" +
            "</p>";

    private final String EN = "<h4>Last edit: version 3.0</h4>\n" +
            "<b>Data Storage:</b>\n" +
            "<p>\n" +
            "    We store your Instagram data only in the program database and keep them protected , data collected and stored by the program\n" +
            "    are completely removed from the program database .\n" +
            "    We recognize that the stored data are not sent to servers , applications and out of the program , so our program is\n" +
            "    independent of everything only with Instagram and keeps all sensitive data stored in the program .\n" +
            "    You also need to know that your confidential information is not stored as passwords.\n" +
            "</p>\n" +
            "<b>Delete data:</b>\n" +
            "<p>\n" +
            "    By removing the application of all your data will be permanently deleted , as well as from the management of the\n" +
            "    accounts you can completely eliminate a specific account .\n" +
            "</p>\n" +
            "<b>Program Activity:</b>\n" +
            "<p>\n" +
            "    We have created this program for more detailed management and helping to stimulate your business . We analyze your\n" +
            "    account data and provide you with statistical information so that you can understand your strengths and weaknesses .\n" +
            "    We admit that the programme 's activity comes within the framework of Instagram laws .\n" +
            "</p>\n" +
            "<b>Data stored in the program:</b>\n" +
            "<p>\n" +
            "    We may collect the following data , data are stored only in the program and are not shared with others , servers ,\n" +
            "    services , and third parties and so on .\n" +
            "    <br>\n" +
            "    - Your profile account\n" +
            "    <br>\n" +
            "    - people you are following\n" +
            "    <br>\n" +
            "    - People who are followed by you\n" +
            "    <br>\n" +
            "    - Your Account posts\n" +
            "</p>\n" +
            "<b>Use data:</b>\n" +
            "<p>\n" +
            "    We analyze your data on the tool and report to you that with the help of this analysis you can find your strengths and weaknesses and work on them .\n" +
            "    We acknowledge that the analysis of the data will not apply to your account , and that there is no change in your accounts save at your disposal .\n" +
            "</p>\n" +
            "<b>Our policy and privacy allow you to do so whenever you need:</b>\n" +
            "<p>\n" +
            "    - Remove all your information from the program.\n" +
            "    <br>\n" +
            "    - Close all your activities and delete your account with the data for all the time.\n" +
            "    <br>\n" +
            "    - By removing the application, virtually all your information is lost forever.\n" +
            "</p>\n" +
            "<b>Age condition:</b>\n" +
            "<p>\n" +
            "    Suitable for adolescent and older age group\n" +
            "</p>\n" +
            "<b>Third - party services:</b>\n" +
            "<p>\n" +
            "    We use third - party services in our program , admitting that they do not have access to your account data and only rely on your data , so read the policy:\n" +
            "    <br>\n" +
            "    - Notification (Firebase)\n" +
            "</p>";

    private TextView privacy;
    private boolean language;

    @Override
    public int onCreateView() {
        return R.layout.privacy_policy;
    }

    @Override
    protected int buttonRightIconRes() {
        return R.drawable.ic_round_language_24;
    }

    @Nullable
    @Override
    protected View.OnClickListener buttonRightClick() {
        return view -> {
            privacy.setText(HtmlCompat.fromHtml(language ? EN : FA, HtmlCompat.FROM_HTML_MODE_LEGACY));
            language = !language;
        };
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        super.onCreateFragment(savedInstanceState);
        setPageTitle("Privacy Policy");
        findViewById(R.id.buttonBack).setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStack());

        findViewById(R.id.buttonChangeLanguage).setOnClickListener(buttonRightClick());

        if (getArguments() == null) findViewById(R.id.actionBarLayout).setVisibility(View.GONE);

        privacy = findViewById(R.id.privacy);

        privacy.setText(HtmlCompat.fromHtml(EN, HtmlCompat.FROM_HTML_MODE_LEGACY));
    }
}