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


    final ImageView iv = (ImageView) findViewById(R.id.logo);
    final TextView tv = (TextView) findViewById(R.id.wlcm);
    final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fall);
    final Animation aan = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fall);
    final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//<<<<<<< HEAD
        setContentView(R.layout.splash);

        /*final ImageView iv = (ImageView) findViewById(R.id.logo);
        final TextView tv = (TextView) findViewById(R.id.wlcm);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fall);
        final Animation aan = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fall);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);*/

        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setVisibility(View.VISIBLE);
                tv.startAnimation(aan);
                aan.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tv.setVisibility(View.VISIBLE);
                        iv.startAnimation(an2);
                        tv.startAnimation(an2);
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
//=======
//>>>>>>> ea77fbcaf7a5e072f83a253d499508d2fdf821b6

    }
}
