package com.constantlab.wpconnect.views.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.ui.contracts.SplashScreenContract;
import com.constantlab.wpconnect.core.ui.presenters.SplashScreenPresenter;

public class SplashScreenActivity extends BaseActivity implements SplashScreenContract.SplashScreenView {

    SplashScreenPresenter mPresenter;
    ImageView mLogo;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        mLogo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progress_bar);
        mPresenter = new SplashScreenPresenter(DataManager.getInstance(this));
        mPresenter.attachView(this);
        setAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUserLoggedIn()) {
                    mPresenter.onValidateUser(getUserToken());
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 3000);
    }

    private void setAnimation() {
        animation1();
    }


    private String getUserToken() {
        String bearer = null;
        if (applicationMain.token != null && !applicationMain.token.isEmpty()) {
            bearer = "Bearer " + applicationMain.token;
            return bearer;
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPreferences.getString(Constants.KEY_FOR_TOKEN, null);
            if (token != null) {
                bearer = "Bearer " + token;
            }
            return bearer;
        }
    }


    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onValidationResponse(boolean status) {
        if (status) {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.session_expired, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();
        }
    }
}
