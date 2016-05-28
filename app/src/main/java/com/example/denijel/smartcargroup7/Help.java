package com.example.denijel.smartcargroup7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Joseph on 5/27/2016.
 */
public class Help extends Activity implements GestureDetector.OnGestureListener {
    GestureDetector detector;
    String lastClass = "";
    int next = 0;
    ImageView start;
    ImageView scan;
    ImageView select;
    ImageView options;
    ImageView help;
    ImageView back;
    ImageView undo;
    ImageView shutrpi;
    ImageView exit;
    ProgressBar bar;
    RelativeLayout bg;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        start = (ImageView)findViewById(R.id.start);
        scan = (ImageView)findViewById(R.id.scan);
        select = (ImageView)findViewById(R.id.select);
        options = (ImageView)findViewById(R.id.options);
        help = (ImageView)findViewById(R.id.help);
        back = (ImageView)findViewById(R.id.back);
        undo = (ImageView)findViewById(R.id.undo);
        shutrpi = (ImageView)findViewById(R.id.shutrpi);
        exit = (ImageView)findViewById(R.id.exit);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        bg = (RelativeLayout) findViewById(R.id.bg);

        detector = new GestureDetector(this, this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                lastClass = data.getStringExtra("the current class");
            }
        }
    }

    public void updateProgress(){
        final int progress = bar.getMax() * next / 9;
        bar.setProgress(progress);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        switch (next){
            case 0:
                bg.setBackgroundColor(0xff000000);
                start.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 1:
                scan.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 2:
                select.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 3:
                options.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 4:
                help.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 5:
                back.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 6:
                undo.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 7:
                shutrpi.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 8:
                exit.setVisibility(View.VISIBLE);
                next+=1;
                updateProgress();
                break;
            case 9:
                Intent intent = new Intent();
                intent.putExtra("the current class", this.getClass().getSimpleName());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("the current class", this.getClass().getSimpleName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}














