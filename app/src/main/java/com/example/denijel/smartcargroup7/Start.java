package com.example.denijel.smartcargroup7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Joseph on 4/20/2016.
 */
public class Start extends Activity implements GestureDetector.OnGestureListener {

    GestureDetector detector;
    String lastClass = "";

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        final TextView tv = (TextView) findViewById(R.id.tvMenu);
        final Button start = (Button) findViewById(R.id.btStart);
        final Button options = (Button) findViewById(R.id.btOptions);
        final Button exit = (Button) findViewById(R.id.btExit);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fall);

        detector = new GestureDetector(this, this);

        tv.startAnimation(an);

        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickList(v);
                //Intent i = new Intent(Start.this, MainActivity.class);
                //depth += 1;
                //startActivityForResult(i, /*depth*/1);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Start.this, Options.class);
                startActivityForResult(i, /*depth*/1);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                System.exit(0);
            }
        });
    }
    public void onClickList(View target){
        Intent listing = new Intent(this, com.example.denijel.smartcargroup7.BluetoothScan.class);
        startActivityForResult(listing, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  /*depth*/1) {
            if(resultCode == RESULT_OK){
                lastClass = data.getStringExtra("the current class");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX()<e2.getX() && e2.getX()-e1.getX() > Math.abs(e2.getY()-e1.getY())){

            if(!this.getClass().equals(Start.class)) {
                Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("the current class", this.getClass().getSimpleName());
                setResult(RESULT_OK, intent);
                finish();
            }else {
                Toast.makeText(getApplicationContext(), "Can't back further", Toast.LENGTH_SHORT).show();
            }
        }
        if(e1.getX()>e2.getX() && e1.getX()-e2.getX() > Math.abs(e2.getY()-e1.getY())){
            Toast.makeText(getApplicationContext(),"Undo",Toast.LENGTH_SHORT).show();

            switch (lastClass){
                case "Start":
                    startActivityForResult(new Intent(this, Start.class), 1);
                    break;
                case "MainActivity":
                    startActivityForResult(new Intent(this, MainActivity.class), 1);
                    break;
                case "Options":
                    startActivityForResult(new Intent(this, Options.class), 1);
                    break;
//                case "Help":
//                  startActivityForResult(new Intent('Help.this, Help.class), 1);
//                  break;
                default:
                    break;
            }
        }
        if(e1.getY()>e2.getY() && e1.getY()-e2.getY() > Math.abs(e2.getX()-e1.getX())){
            //swipe up action possibility
        }
        if(e1.getY()<e2.getY() && e2.getY()-e1.getY() > Math.abs(e2.getX()-e1.getX())){
            //swipe down action possibility
        }

        return true;
    }
}
