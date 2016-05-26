package com.example.denijel.smartcargroup7;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.view.View;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Created by Denijel and Olle
 */
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    MySurfaceThread thread;
    String tag = "debugging";
    Paint paint1;
    float x,y,dx,dy, c, angle;
    boolean run = true;
    Bitmap ball, background, cachedBitmap;
    float zeroX, zeroY, radius;
    private static final UUID MY_UUID =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;


    public CustomSurfaceView(Context context){
        super(context);
        init(context);
        this.context = context;
        setZOrderOnTop(true);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs, defStyle);
        init(context);
    }
    //Creates the 3 Custom Surface Views for the background, the camera and the joystick.

    private void init(Context context) {
        thread = new MySurfaceThread(getHolder(), this);
        SurfaceHolder ourHolder = getHolder();
        getHolder().addCallback(this);
        paint1 = new Paint();
        radius = 200;
        paint1.setTextSize(40);
        paint1.setColor(Color.rgb(255, 0, 0));
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.joystick);
        background = Bitmap.createScaledBitmap(background, 600, 600, true);
        ball = Bitmap.createScaledBitmap(ball, 200, 200, true);
        this.context = context;
        //Initializing variables with set values.
    }



    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Surface surface = arg0.getSurface();


        if (surface != null){
            System.out.println("Surface holder is: " + surface);
            thread.execute((Void[])null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {

        thread.cancel(true);
        //If surface is destroyed the thread stops.
    }

    protected void onDraw(Canvas canvas, float x, float y, float zx, float zy){

        dx = x-zx;
        dy = y-zy;

        super.onDraw(canvas);

        canvas.drawBitmap(background, (canvas.getWidth() - canvas.getWidth() / 7) - background.getWidth() / 2, (canvas.getHeight() - canvas.getHeight() / 4) - background.getHeight() / 2, null);
        canvas.drawText(Float.toString(x), 60, 60, paint1);
        canvas.drawText(Float.toString(y), 60, 120, paint1);
        canvas.drawText(Float.toString(angle), 60, 180, paint1);
        if (SendInput.active) {
            passToMain(angle, x, y);
        }
        //Draws the background of the joystick.


        if (x == 0 && y == 0) {
            canvas.drawBitmap(ball, (canvas.getWidth()-canvas.getWidth() / 7) - ball.getWidth() / 2, (canvas.getHeight()-canvas.getHeight() / 4) - ball.getHeight() / 2, null);
        //Draws the ball on the joystick if it is in the middle.
        //If x and y == 0 the ball is in th middle.
        }
        else {
            canvas.drawBitmap(ball, x - ball.getWidth() / 2, y - ball.getHeight() / 2, null);
        //Draws the ball at the current location
        //determined on the x and y values.
        }
    }

    private void passToMain(float value, float x, float y){
        if (SendInput.active) {
            ((SendInput) context).callMe(value);
            ((SendInput) context).callMeX(x);
            ((SendInput) context).callMeY(y);
        }
        //Sends input of current position.



    }


    public class MySurfaceThread extends AsyncTask<Void, Void, Void>{

        SurfaceHolder mSurfaceHolder;
        CustomSurfaceView cSurfaceView;


        public MySurfaceThread(SurfaceHolder sh, CustomSurfaceView csv){
            mSurfaceHolder = sh;
            cSurfaceView = csv;
            x = y = 0;

            cSurfaceView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    x = event.getX();
                    y = event.getY();

                    calculateValues(x,y);

                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            x = y = 0;
                            dx = dy = 0;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                    }
                    return true;
                }
                // Listener for the joystick. When the joystick is touched and moved
                // the values are registered to be calculated in calculateValues.

                public float calculateValues(float xx, float yy){
                    dx = xx-zeroX;
                    dy = yy-zeroY;
                    angle = (float)Math.atan(Math.abs(dy / dx));
                    c = (float)Math.sqrt(dx * dx + dy * dy);

                    if(c > radius){
                        if (dx > 0 && dy > 0) { //lower right corner
                            xx = (float) (zeroX + radius * Math.cos(angle));
                            yy = (float) (zeroY + radius * Math.sin(angle));
                        }
                        else if(dx > 0 && dy < 0){ //top right corner
                            xx = (float) (zeroX + radius * Math.cos(angle));
                            yy = (float) (zeroY - radius * Math.sin(angle));
                        }
                        else if(dx < 0 && dy < 0){ //top left corner
                            xx = (float) (zeroX - radius * Math.cos(angle));
                            yy = (float) (zeroY - radius * Math.sin(angle));
                        }
                        else if(dx < 0 && dy > 0){ //lower left corner
                            xx = (float) (zeroX - radius * Math.cos(angle));
                            yy = (float) (zeroY + radius * Math.sin(angle));
                        }
                    }
                    //Calculating the position of the joystick from X and Y.
                    else {
                        xx = zeroX + dx;
                        yy = zeroY + dy;
                    }

                    x = xx;
                    y = yy;

                    return angle;
                }
            });
        }


        //@Override
        protected Void doInBackground(Void... params) {

            while(run) {

                Canvas canvas = new Canvas();

                try {

                    canvas = mSurfaceHolder.lockCanvas(null);

                    synchronized (mSurfaceHolder) {

                        zeroX = (canvas.getWidth()-canvas.getWidth()/8);
                        zeroY = (canvas.getHeight()-canvas.getHeight()/4);
                        canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                        cSurfaceView.onDraw(canvas, x, y, zeroX, zeroY);

                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                //Thread that paints the canvas continuously
            }
            return null;
        }
    }
}