package com.ikea.aceh.unicef.sip;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(SampleSlide.newInstance(R.layout.intro_custom_layout1));

        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
//        SliderPage sliderPage = new SliderPage();
//        sliderPage.setTitle(title);
//        sliderPage.setDescription(description);
//        sliderPage.setImageDrawable(image);
//        sliderPage.setBgColor(backgroundColor);
//        addSlide(AppIntroFragment.newInstance(sliderPage));

        addSlide(AppIntroFragment.newInstance("Hello Food!", "The easiest way to order your favourite food!", R.drawable.ic_arrow_back_white, Color.parseColor("#f64c73")));
        addSlide(AppIntroFragment.newInstance("Great Discounts", "Great Discounts and low rates on every Single service we offer!", R.drawable.ic_done_white, Color.parseColor("#20d2bb")));
//        addSlide(AppIntroFragment.newInstance("Movie Tickets!", "Book the movie tickets for your friends and family!", R.drawable.ic_navigate_next_white, Color.parseColor("#3395ff")));
//        addSlide(AppIntroFragment.newInstance("Travel", "Book the tickets of bus, trains and airlines from here!", R.drawable.ic_navigate_before_white, Color.parseColor("#c873f4")));

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); // OR

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);


//        setContentView(R.layout.activity_intro);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        // Ask for CAMERA permission on the second slide
//        askForPermissions(new String[]{Manifest.permission.CAMERA}, 1); // OR

        // This will ask for the camera permission AND the contacts permission on the same slide.
        // Ensure your slide talks about both so as not to confuse the user.
//        askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS}, 2);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
