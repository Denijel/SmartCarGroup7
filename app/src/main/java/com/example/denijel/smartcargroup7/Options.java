package com.example.denijel.smartcargroup7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by Joseph on 4/20/2016.
 */
public class Options extends Activity implements GestureDetector.OnGestureListener {

    GestureDetector detector;
    String lastClass = "";

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        final Button help = (Button) findViewById(R.id.btHelp);
        final Button rpi = (Button) findViewById(R.id.btRPi);

        detector = new GestureDetector(this, this);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Options.this, Help.class);
                startActivityForResult(i,1);
            }
        });
        /*
        Button below used for shutting down the RPi through the terminal
         */
        rpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Shutting Down", Toast.LENGTH_SHORT).show();

                new ShutDownPi().execute();

            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
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
                case "Help":
                  startActivityForResult(new Intent(this, Help.class), 1);
                  break;
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
