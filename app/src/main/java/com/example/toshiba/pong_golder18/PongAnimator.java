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
    private int countX = 150; // counts the number of logical clock ticks
    private int countY = 150;

    // features of the paddle
    private float paddleLeft = 500;
    private float paddleRight = 700;
    private float paddleSize = 100;

    // features of the ball
    private boolean reverseX = false;
    private boolean reverseY = false;
    private float ballX = 65;
    private float ballY = 65;
    private int speedX = 30; // speed of the ball
    private int speedY = 30; // speed of the ball

    private int width = 1536; // width of our surface
    private int height = 2000; // height of our surface

    private boolean betweenPlay = false;


    /**
     * Interval between animation frames: .03 seconds (i.e., about 33 times
     * per second).
     *
     * @return the time interval between frames, in milliseconds.
     */
    @Override
    public int interval() {
        return 30;
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
        if( betweenPlay ) {
            return true;
        }
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
        ballX = (countX*speedX)%width;
        ballY = (countY*speedY)%height;
        if ( ballX+50 >= 1500 || ballX-50 <= 50 ) {
            reverseX = !reverseX;
            if( ballX > 1555 || ballX < 50) {
                newBall();
            }
        }
        if ( ballY-50 <= 50 ) {
            reverseY = !reverseY;
            if( ballY < 50) {
                newBall();
            }
        }

        // Draw walls
        Paint wall = new Paint();
        wall.setColor(Color.BLACK);
        wall.setStrokeWidth(10.0f);
        g.drawLine(50, 50, 50, 1850, wall);         // left
        g.drawLine(50, 50, 1500, 50, wall);         // top
        g.drawLine(1500, 50, 1500, 1850, wall);     // right
        g.drawLine(50, 1850, 1500, 1850, wall);         // bottom

        // Draw the ball in the correct position.
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        g.drawCircle(ballX, ballY, 50, redPaint);
        redPaint.setColor(0xff0000ff);

        // Draw paddle in correct position
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.BLACK);
        g.drawRect(paddleLeft, 1800, paddleRight, 1825, paddlePaint);
        paddlePaint.setColor(0xff0000ff);



        if( ballY+50 > 1800 ) {
            // Hits the paddle
            if ( ballX+50 > paddleLeft && ballX-50 < paddleRight ) {
                reverseY = !reverseY;
                Log.i("PongAnimator", "Bouncy bounce");
            }
            // Misses the paddle
            else if ( ballY < 2000 ) {
                Log.i("PongAnimator", "Misses the paddle");
                Paint restart = new Paint();
                restart.setColor(Color.BLACK);
                //External Reference
                //https://developer.android.com/reference/android/graphics/Paint.html
                restart.setTextSize(40.0f);
                g.drawText("Tap screen to begin with new ball", 500, 700, restart);
                betweenPlay = true;

                // Draw play mode buttons
                Paint easyMode = new Paint();
                easyMode.setColor(Color.GREEN);
                g.drawRect(125, 100, 525, 200, easyMode);
                Paint mediumMode = new Paint();
                mediumMode.setColor(Color.YELLOW);
                g.drawRect(575, 100, 975, 200, mediumMode);
                Paint difficultMode = new Paint();
                difficultMode.setColor(Color.RED);
                g.drawRect(1025, 100, 1425, 200, difficultMode);
            }
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        // TODO: Create new method for changing difficulty?
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if( betweenPlay ) {
                // In the top area with the buttons
                if( eventY > 100 && eventY < 200 ) {
                    if( eventX > 125 && eventX < 525 ) {      // easy
                        paddleSize = 200;
                    }
                    else if( eventX > 575 && eventX < 975 ) { // med
                        paddleSize = 100;
                    }
                    else if( eventX > 1025 && eventX < 1425) { // dif
                        paddleSize = 25;
                    }
                    else { // not on a button
                        newBall();
                        betweenPlay = false;
                    }
                }
                // Somewhere else on the screen
                else {
                    newBall();
                    betweenPlay = false;
                }

            }
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            paddleLeft = event.getX()-paddleSize;
            paddleRight = event.getX()+paddleSize;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            paddleLeft = event.getX()-paddleSize;
            paddleRight = event.getX()+paddleSize;
        }
    }

    public void newBall() {
        Random rand = new Random();
        //countX = rand.nextInt(width-50)+50;
        countX = 150;
        countY = 150;
        reverseX = rand.nextBoolean();
        reverseY = true;
        speedX = rand.nextInt(50)+5;
        speedY = rand.nextInt(50)+5;
    }
}

