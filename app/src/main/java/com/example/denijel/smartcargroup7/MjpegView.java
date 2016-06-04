package com.example.denijel.smartcargroup7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import java.io.IOException;

public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {

    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD   = 1;
    public final static int SIZE_BEST_FIT   = 4;
    public final static int SIZE_FULLSCREEN = 8;

    MjpegViewThread thread;
    private MjpegInputStream mIn = null;
    private boolean showFps = false;
    private boolean mRun = false;
    private boolean surfaceDone = false;
    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;


    public class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private long start;
        private Bitmap ovl;

        public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) { mSurfaceHolder = surfaceHolder; }
        /*
            Simply makes the rectangle screen of the camera streaming according to what you chose on line 220 in sendinput. If you chose standard it alter it,
            best fit it alters it aswell, fullscreen it makes it fullscreen.
         */
        private Rect destRect(int bmw, int bmh) {
            int tempx;
            int tempy;
            if (displayMode == MjpegView.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_BEST_FIT) {
                float bmasp = (float) bmw / (float) bmh;
                bmw = dispWidth;
                bmh = (int) (dispWidth / bmasp);
                if (bmh > dispHeight) {
                    bmh = dispHeight;
                    bmw = (int) (dispHeight * bmasp);
                }
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegView.SIZE_FULLSCREEN) return new Rect(0, 0, dispWidth, dispHeight);
            return null;
        }
        /*
        Sets surface size
         */
        public void setSurfaceSize(int width, int height) {
            synchronized(mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }
        /*
        Method that shows the fps overlay and draws it on the screen, only used for testing our FPS
        never showed in the demo since it made the UI look uglier
         */
        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth  = b.width()+2;
            int bheight = b.height()+2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);
            c.drawText(text, -b.left+1, (bheight/2)-((p.ascent()+p.descent())/2)+1, p);
            return bm;
        }

        public void run() {
            start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
            Bitmap bm;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps = "";
            while (mRun) {
                    if (surfaceDone) {
                        try {
                            c = mSurfaceHolder.lockCanvas();
                            synchronized (mSurfaceHolder) {
                                try {
                                    bm = mIn.readMjpegFrame();
                                    destRect = destRect(bm.getWidth(), bm.getHeight()); //makes the rectangle for the screen stream input
                                    c.drawColor(Color.BLACK);
                                    c.drawBitmap(bm, null, destRect, p); // draws  the map
                                    showFps = false; //disable the showfps to not make red markers on stream
                                    if (showFps) {
                                        /*
                                        p.setXfermode(mode);
                                        if (ovl != null) {
                                            height = ((ovlPos & 1) == 1) ? destRect.top : destRect.bottom - ovl.getHeight();
                                            width = ((ovlPos & 8) == 8) ? destRect.left : destRect.right - ovl.getWidth();
                                            c.drawBitmap(ovl, width, height, null);
                                        }
                                        p.setXfermode(null);
                                        frameCounter++;
                                        if ((System.currentTimeMillis() - start) >= 1000) {
                                            fps = String.valueOf(frameCounter) + "fps";
                                            frameCounter = 0;
                                            start = System.currentTimeMillis();
                                            ovl = makeFpsOverlay(overlayPaint, fps);

                                        }*/
                                    }
                                } catch (IOException e) {
                                }
                            }
                        } finally {
                            if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
                        }
                }
            }
        }
    }

    private void init(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new MjpegViewThread(holder, context); //new thread
        setFocusable(true);
        overlayPaint = new Paint(); //makes paint for fps
        overlayPaint.setTextAlign(Paint.Align.LEFT); //sets alignment for the text on the overlay
        overlayPaint.setTextSize(12); //fps overlay textsize
        overlayPaint.setTypeface(Typeface.DEFAULT);
        overlayTextColor = Color.WHITE; //color
        overlayBackgroundColor = Color.BLACK; //backround
        ovlPos = MjpegView.POSITION_LOWER_RIGHT;
        displayMode = MjpegView.SIZE_STANDARD; //size standard
        dispWidth = getWidth(); //display width
        dispHeight = getHeight(); //display height
    }
    /*
    Sets mRun to true which starts the while loop in the run function and aswell starts the thread.
     */
    public void startPlayback() {
        if(mIn != null) {
            mRun = true;
            thread.start();
        }
    }
    /*
    Stops everything.
     */
    public void stopPlayback() {
        mRun = false;
        boolean retry = true;
        while(retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    public MjpegView(Context context, AttributeSet attrs) {
        super(context, attrs); init(context);
    }

    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        thread.setSurfaceSize(w, h);
    }

    /*
    Used when the surface gets destroyed, sets mrun to false and stops the while loop, and sets surfacedone to false
    which also denies the while loop. Uses the method stopplayback aswell
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        mRun = false;
        stopPlayback();

    }

    public MjpegView(Context context) {
        super(context);
        init(context);
    }
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceDone = true;
    }
    /*
    method below just used to
     */
    public void showFps(boolean b) {
        showFps = b;
    }
    /*
    Sets the MjpegInputstream as source. and starts playback
     */

    public void setSource(MjpegInputStream source) {
        mIn = source;
        startPlayback();
    }
    /*
    the 4 first method are just for the fps overlay which are never used.
     */
    public void setOverlayPaint(Paint p) {
        overlayPaint = p;
    }
    public void setOverlayTextColor(int c) {
        overlayTextColor = c;
    }
    public void setOverlayBackgroundColor(int c) {
        overlayBackgroundColor = c;
    }
    public void setOverlayPosition(int p) {
        ovlPos = p;
    }
    /*
    Sets the display mode, 3 alternatives
    best_Fit, fullscreen or standard.
     */
    public void setDisplayMode(int s) {

        displayMode = s;
    }
}