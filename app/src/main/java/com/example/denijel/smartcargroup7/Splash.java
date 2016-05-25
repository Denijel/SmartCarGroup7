package com.example.denijel.smartcargroup7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Joseph on 4/8/2016.
 */
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        final ImageView light = (ImageView) findViewById(R.id.light);
        final TextView welcome = (TextView) findViewById(R.id.wlcm);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fall);
        final Animation aan = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fall);

        light.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                light.setVisibility(View.VISIBLE);
                welcome.startAnimation(aan);
                aan.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        welcome.setVisibility(View.VISIBLE);
                        finish();
                        Intent i = new Intent(Splash.this, Start.class);
                        startActivity(i);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
