package com.example.toshiba.pong_golder18;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Random;

/**
 * class that animates the ball to play the game of pong
 *
 * @author Sarah Golder
 * @version March 2018
 */

public class PongAnimator implements Animator {

    //instance variables
    private int countX = 50; // counts the number of logical clock ticks
    private int countY = 50;
    private boolean goBackwards = false; // whether clock is ticking backwards
    private boolean reverseX = false;
    private boolean reverseY = false;
    // current position of paddle
    private float paddleLeft = 500;
    private float paddleRight = 700;
    // current position of ball
    private float ballX = 65;
    private float ballY = 65;

    private int width = 1536; // width of our surface
    private int height = 2000; // height of our surface
    private int speed = 15; // speed of the ball

    /**
     * Interval between animation frames: .03 seconds (i.e., about 33 times
     * per second).
     *
     * @return the time interval between frames, in milliseconds.
     */
    @Override
    public int interval() {
        return speed;
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
        if (reverseX) {
            countX--;
        }
        else {
            countX++;
        }
        if(reverseY) {
            countY--;
        }
        else {
            countY++;
        }

        // Determine the pixel position of our ball.  Multiplying by 15
        // has the effect of moving 15 pixel per frame.  Modding by 600
        // (with the appropriate correction if the value was negative)
        // has the effect of "wrapping around" when we get to either end
        // (since our canvas size is 600 in each dimension)

        // attempt ball gravity
        ballX = (countX*15)%width;
        ballY = (countY*15)%height;
        Log.i("PongAnimator", "BallX: "+ballX);
        if ( ballX >= (width-10) || ballX <= 10 ) {
            reverseX = !reverseX;
        }
        if ( ballY >= (height-10) || ballY <= 10 ) {
            reverseY = !reverseY;
        }
        if( ballY > 1750 ) {
            // Hits the paddle
            if ( ballX > paddleLeft && ballX < paddleRight ) {
                reverseY = !reverseY;
            }
            // Misses the paddle
            else {
                Random rand = new Random();
                ballX = rand.nextInt(width+10);
                ballY = 1800;
                reverseX = rand.nextBoolean();
                reverseY = true;
            }
        }

        // Draw the ball in the correct position.
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        g.drawCircle(ballX, ballY, 60, redPaint);
        redPaint.setColor(0xff0000ff);

        //TODO: Draw paddle to tick
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.BLACK);
        g.drawRect(paddleLeft, 1800, paddleRight, 1810, paddlePaint);
        paddlePaint.setColor(0xff0000ff);
    }

    @Override
    public void onTouch(MotionEvent event) {

        event.getEdgeFlags();//??
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            paddleLeft = event.getX()-100;
            paddleRight = event.getX()+100;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            paddleLeft = event.getX()-100;
            paddleRight = event.getX()+100;
        }
    }

    /**
     * Tells the animation whether to go backwards.
     *
     * @param b true if animation is to go backwards.
     */
    public void goBackwards(boolean b) {
        // set our instance variable
        goBackwards = b;
    }

    public void reset() {
        Random rand = new Random();
        ballX = rand.nextInt(width+10);
        ballY = 1800;
        reverseX = rand.nextBoolean();
        reverseY = true;
    }
}

