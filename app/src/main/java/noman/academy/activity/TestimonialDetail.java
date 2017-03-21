package noman.academy.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import noman.Ads.AdIntegration;

/**
 * Created by Administrator on 3/15/2017.
 */

public class TestimonialDetail extends AdIntegration {

    String[] testimonial = {
            "Learning Quran means to stay in touch with Islam. It’s hard to find reliable Quran tutors in U.S. I was worried enough about the Quran education of my two daughters. Then, I explored QuranReading.com as the best option, due to quality of their services. I always found their CSRs supportive and ready to help in any issue regarding change of timing, or learning sessions. Thank you QuranReading Team! \n" +
                    "\n" +
                    "Sohail Anwar \n" +
                    "Virginia, USA", "My son Mehdi Walji, who is just 4 years, is having his classes from QuranReading.com. I really didn’t imagine that it could work. Now, I am 100% satisfied with his fast progress, and his interest in Quran learning. It’s all because of the tutors at QuranReading.com, who are teaching in a friendly environment. Hats off to you guys! \n" +
            "\n" +
            "Asif Walji \n" +
            "Texas, USA", "Anybody having a computer and access to internet can learn with us. We will walk you through how to download simple Gotomeeting program for interactive Live Quran Reading session with live Quran teacher. \n" +
            "\n" +
            "Allah Ma'ki \n" +
            "New Jersey, USA", "Living in the middle of Arkansas and having no tutor of Qur'an, a desperate condition for me. My friend told me about QuranReading.com, where there is no age limit for students. I have started taking Quran classes three days a week. Now, my two sons are also students of their tutors. I will highly recommend you all to try their Quran Learning program for once. \n" +
            "\n" +
            "Sister Aqsa \n" +
            "Arkansas, USA", "www.QuranReading.com is an amazing program. My two kids are studying since one year. We sometimes go on long vacations to another country and their Online Quran classes go with us. I am happy with their Tajweed and Tafseer services. Being a computer professional, I am glad to see that they have number of computer engineers to resolve any computer issue. All these efforts make the learning process smooth. Believe me; it's awesome! \n" +
            "\n" +
            "Asif Jafery \n" +
            "Virginia, USA", "Since my kids started to grow, I started searching for a Quran tutor who could teach my kids about Quran and Islam. After a long search I could find any tutor who could come to my home and teach Quran to my children. Luckily when doing online search, if found about QuranReading.com and tried their trial service. During the trial period I supervised the classes and found that the method of teaching was quite interesting and the Quran tutor had through knowledge about the rules of pronunciation. Thus, my first two kids learned Quran from the same teacher without me worrying about dropping them to a Quran academy or having to deal with the problems of academies. Now my third child is also a student at QuranReading.com and highly recommend it to all those Muslim parents residing in countries of West to have your kids enrolled with QuranReading to make them learn Quran and about Islam as well. \n" +
            "\n" +
            "Mudasser Khalid \n" +
            "New Jersey, USA"
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonial_detail);
        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linear_ad));
        }
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayout ln = (LinearLayout) findViewById(R.id.ln_testimonial);
        for (int i = 0; i < testimonial.length; i++) {
            testimonialContainer(ln, testimonial[i]);
        }


    }

    void testimonialContainer(LinearLayout ln, String textViewText) {

        View layout2 = LayoutInflater.from(this).inflate(R.layout.dynamic_testimonial_block, ln, false);

        TextView textView = (TextView) layout2.findViewById(R.id.txt_testimonial_dynamic);
        textView.setText(textViewText);

        ln.addView(layout2);

    }
}
