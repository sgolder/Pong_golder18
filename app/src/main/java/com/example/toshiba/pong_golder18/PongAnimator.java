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

    // Properties of the player's paddle
    private float playerPaddleLeft = 500;
    private float playerPaddleRight = 700;
    private float paddleSize = 100;

    // Properties for computer player
    private float compPaddleLeft = 500;
    private float compPaddleRight = 700;
    private int chanceOfMissing = 6;

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

    private int playerScore = 0;
    private int computerScore = 0;

    private boolean betweenGames = false;

    //TODO: create an initialization method?

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
        duringGameView(g);

        // If ball reaches the player paddle height
        if( ballY+50 > 1800 ) { // Player paddle
            // Hits the paddle, reverse as if hitting a wall
            if ( ballX+50 > playerPaddleLeft && ballX-50 < playerPaddleRight ) {
                playerScore++;
                reverseY = !reverseY;
            }
            // Misses player's paddle
            else if ( ballY < 2000 ) {
                playerScore =- 5;
                betweenGameView(g);
            }
        }
        else if( ballY-50 < 200 ) { // Computer paddle
            // Hits computer paddle, reverse as if hitting a wall
            if ( ballX+50 > compPaddleLeft && ballX-50 < compPaddleRight ) {
                computerScore++;
                reverseY = !reverseY;
            }
            // Misses computer paddle
            else if ( ballY < 100 ) {
                computerScore =- 5;
                betweenGameView(g);
            }
        }

        // Computer moving paddle
        Random rand = new Random();
        int willMiss = rand.nextInt(chanceOfMissing);

        compPaddleLeft = ballX-paddleSize;
        compPaddleRight = ballX+paddleSize;
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
            if(betweenGames) {
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
                        betweenGames = false;
                    }
                }
                // Somewhere else on the screen
                else {
                    newBall();
                    betweenGames = false;
                }
            }
        }
        // User is moving the paddle
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            playerPaddleLeft = event.getX()-paddleSize;
            playerPaddleRight = event.getX()+paddleSize;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            playerPaddleLeft = event.getX()-paddleSize;
            playerPaddleRight = event.getX()+paddleSize;
        }
    }

    public void duringGameView(Canvas g) {
        // Draw walls
        Paint wall = new Paint();
        wall.setColor(Color.WHITE);
        wall.setStrokeWidth(10.0f);
        g.drawLine(50, 50, 50, 1850, wall);         // left
        g.drawLine(50, 50, 1500, 50, wall);         // top
        g.drawLine(1500, 50, 1500, 1850, wall);     // right
        g.drawLine(50, 1850, 1500, 1850, wall);     // bottom

        // Draw the ball in the correct position.
        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);
        g.drawCircle(ballX, ballY, 50, ballPaint);
        ballPaint.setColor(0xff0000ff);

        // Draw player's paddle
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.WHITE);
        g.drawRect(playerPaddleLeft, 1800, playerPaddleRight, 1825, paddlePaint);
        paddlePaint.setColor(0xff0000ff);

        // Draw computer's paddle
        paddlePaint.setColor(Color.WHITE);
        g.drawRect(compPaddleLeft, 75, compPaddleRight, 100, paddlePaint);
        paddlePaint.setColor(0xff0000ff);

        // Draw score card
        Paint scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(50.0f);
        /*
        g.drawText("Your", 100, 900, scorePaint );
        g.drawText("Score: "+playerScore, 100, 950, scorePaint );
        g.drawText("Computer's", 1200, 900, scorePaint );
        g.drawText("Score: "+playerScore, 1200, 950, scorePaint );
        */
    }

    public void betweenGameView(Canvas g) {
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
        //g.drawText("Select difficulty and tap screen to restart",
        //        400, 400, restart);
        betweenGames = true;

        // Draw difficulty mode buttons
        Paint modeText = new Paint(); // Text for buttons
        modeText.setColor(Color.WHITE);
        modeText.setTextSize(70.0f);

        Paint easyMode = new Paint(); // Easy
        easyMode.setColor(Color.rgb(229, 183, 139));
        g.drawRect(500, 600, 1050, 750, easyMode);
        g.drawText("E A S Y", 670, 700, modeText);

        Paint mediumMode = new Paint(); // Medium
        mediumMode.setColor(Color.rgb(221, 141, 117));
        g.drawRect(500, 850, 1050, 1000, mediumMode);
        g.drawText("M E D I U M", 600, 950, modeText);

        Paint hardMode = new Paint(); // Hard
        hardMode.setColor(Color.rgb( 138, 113, 106));
        g.drawRect(500, 1100, 1050, 1250, hardMode);
        g.drawText("H A R D", 665, 1200, modeText);
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
        return Color.rgb(208, 206, 186);
    }

    @Override
    public boolean doPause() {
        if(betweenGames) {
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
}

