package com.example.toshiba.pong_golder18;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.Random;
/**
 * Class that animates the ball to bounce within a field
 * If the balls gets to the bottom edge of the field without
 * being bounced back by the user moving the paddle, the game
 * restarts.
 *
 * Playing difficulty is determined by 3 sizes of the paddle
 *
 * @author Sarah Golder
 * @version March 2018
 */

public class PongAnimator implements Animator {

    // Instance variables
    private int countX = 150; // counts the number of logical clock ticks
    private int countY = 150;

    // Properties of the paddle
    private float paddleLeft = 500;
    private float paddleRight = 700;
    private float paddleSize = 100;

    // Properties of the ball
    private boolean reverseX = false;
    private boolean reverseY = false;
    private float ballX = 65;
    private float ballY = 65;
    private int speedX = 30; // speed of the ball
    private int speedY = 30; // speed of the ball

    // Properties of screen
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
        return Color.rgb(203, 247, 237);
    }

    @Override
    public boolean doPause() {
        if( betweenPlay ) {
            return true;
        }
        return false;
    }

    @Override
    public boolean doQuit() {
        return false;
    }

    /**
     * Paints ball, walls and paddle on clock tick
     * Keeps ball from going outside the walls
     *
     * @param g the graphics object on which to draw
     */
    @Override
    public void tick(Canvas g) {
        // bump our x and y counts either up or down by one,
        // depending on whether we are in "reverse mode".
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
        // Determine the pixel position of our ball.
        ballX = (countX*speedX)%width;
        ballY = (countY*speedY)%height;
        // Reverse the x and y if the ball reaches a wall
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
        g.drawLine(50, 1850, 1500, 1850, wall);     // bottom

        // Draw the ball in the correct position.
        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.rgb(22, 25, 37));
        g.drawCircle(ballX, ballY, 50, ballPaint);
        ballPaint.setColor(0xff0000ff);

        // Draw paddle in correct position
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.BLACK);
        g.drawRect(paddleLeft, 1800, paddleRight, 1825, paddlePaint);
        paddlePaint.setColor(0xff0000ff);

        if( ballY+50 > 1800 ) { // If ball reaches the paddle height
            // Hits the paddle, reverse as if hitting a wall
            if ( ballX+50 > paddleLeft && ballX-50 < paddleRight ) {
                reverseY = !reverseY;
            }
            // Misses the paddle
            else if ( ballY < 2000 ) {
                /**
                 External Citation
                 Date: 3/20/2018
                 Problem: Couldn't remember how to change font size
                 Resource:
                 https://developer.android.com/reference/android/
                 graphics/Paint.html
                 Solution: I used the setTextSize method
                 */

                // Tell user to tap screen to continue playing
                Paint restart = new Paint();
                restart.setColor(Color.BLACK);
                restart.setTextSize(50.0f);
                g.drawText("Tap screen to begin with new ball",
                        400, 700, restart);
                betweenPlay = true;

                // Draw difficulty mode buttons
                Paint easyMode = new Paint(); // Easy
                easyMode.setColor(Color.rgb(142, 168, 195));
                g.drawRect(125, 100, 525, 200, easyMode);
                Paint mediumMode = new Paint(); // Medium
                mediumMode.setColor(Color.rgb( 64, 110, 142));
                g.drawRect(575, 100, 975, 200, mediumMode);
                Paint hardMode = new Paint(); // Hard
                hardMode.setColor(Color.rgb( 35, 57, 91));
                g.drawRect(1025, 100, 1425, 200, hardMode);
                // Write text to go on buttons
                Paint modeText = new Paint();
                modeText.setColor(Color.WHITE);
                modeText.setTextSize(50.0f);
                g.drawText("Easy", 270, 170, modeText);
                g.drawText("Medium", 690, 170, modeText);
                g.drawText("Hard", 1175, 170, modeText);
            }
        }
    }

    /**
     * Adjusts game based on user choice for difficulty, restarting
     * game play, or moving the paddle
     *
     */
    @Override
    public void onTouch(MotionEvent event) {
        // Where the user touched
        float eventX = event.getX();
        float eventY = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if( betweenPlay ) {
                if( eventY > 100 && eventY < 200 ) {
                    // Set paddle size based on difficulty choice
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
        // User is moving the paddle
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            paddleLeft = event.getX()-paddleSize;
            paddleRight = event.getX()+paddleSize;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            paddleLeft = event.getX()-paddleSize;
            paddleRight = event.getX()+paddleSize;
        }
    }

    /**
     * Resets ball properties to random speed and position for a new
     * game
     */
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

