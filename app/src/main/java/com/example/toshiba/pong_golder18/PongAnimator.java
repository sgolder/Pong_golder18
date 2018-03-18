package com.example.toshiba.pong_golder18;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

/**
 * class that animates the ball to play the game of pong
 *
 * @author Sarah Golder
 * @version March 2018
 */

public class PongAnimator implements Animator {

    //instance variables
    private int count = 0; // counts the number of logical clock ticks
    private boolean goBackwards = false; // whether clock is ticking backwards
    private boolean reverseX = false;
    private boolean reverseY = false;
    // current position of paddle
    private float paddleX = 65;
    private float paddleY = 65;
    // current position of ball
    private float ballX = 65;
    private float ballY = 65;

    private int width = 1536; // width of our surface
    private int height = 2048; // height of our surface
    /**
     * Interval between animation frames: .03 seconds (i.e., about 33 times
     * per second).
     *
     * @return the time interval between frames, in milliseconds.
     */
    @Override
    public int interval() {
        return 10;
    }

    /**
     * The background color: a light blue.
     *
     * @return the background color onto which we will draw the image.
     */
    @Override
    public int backgroundColor() {
        return Color.rgb(180, 200, 255);
    }

    @Override
    public boolean doPause() {
        return false;
    }

    //TODO: Setup a quit button on the interface
    @Override
    public boolean doQuit() {
        return false;
    }

    /**
     * Action to perform on clock tick
     *
     * @param g the graphics object on which to draw
     */
    @Override
    public void tick(Canvas g) {
// bump our count either up or down by one, depending on whether
        // we are in "backwards mode".
        if (goBackwards) {
            count--;
        }
        else {
            count++;
        }

        // Determine the pixel position of our ball.  Multiplying by 15
        // has the effect of moving 15 pixel per frame.  Modding by 600
        // (with the appropriate correction if the value was negative)
        // has the effect of "wrapping around" when we get to either end
        // (since our canvas size is 600 in each dimension)

        // attempt ball gravity
        ballX = (count*15)%width;
        ballY = (count*15)%height;
        Log.i("PongAnimator", "BallX: "+ballX);
        if ( ballX >= (width-10) || ballX <= 10 ) {
            goBackwards = !goBackwards;
        }
        if ( ballY >= (height-10) || ballY <= 10 ) {
            goBackwards = !goBackwards;
        }

        // Draw the ball in the correct position.
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        g.drawCircle(ballX, ballY, 60, redPaint);
        redPaint.setColor(0xff0000ff);

        //TODO: Draw paddle to tick
        /*
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.BLACK);
        g.drawRect(paddleX, paddleY, 100.0f, 50.0f, paddlePaint);
        paddlePaint.setColor(0xff0000ff);
        */
    }

    @Override
    public void onTouch(MotionEvent event) {

        event.getEdgeFlags();//??
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            goBackwards = !goBackwards;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            paddleX = event.getX();
            paddleY = event.getY();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            paddleX = event.getX();
            paddleY = event.getY();
        }

        if( paddleX == ballX && paddleY == ballY ) {
            reverseX = !reverseX;
            reverseY = !reverseY;
        }
    }

    /**
     * Tells the animation whether to go backwards.
     *
     * @param b true iff animation is to go backwards.
     */
    public void goBackwards(boolean b) {
        // set our instance variable
        goBackwards = b;
    }

    //Setters
    public void setWidth ( int initwidth ) { width = initwidth; }
    public void setHeight ( int initheight ) { height = initheight; }
}

