package com.example.toshiba.pong_golder18;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Random;
/**
 * Class that animates the ball to bounce within a field
 * If the balls gets to the bottom or top edge of the field without
 * being bounced back by the user moving the paddle or the computer,
 * the game restarts. Points are gained when the opponent misses.
 *
 * Playing difficulty is determined by 3 sizes of the paddle
 * and the chances of the computer opponent missing the ball.
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
    private int chanceOfMissing = 2;
    private float trackingX;
    private boolean willMiss = false;

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

    /**
     * Move ball across screen according to logical ticks
     * and moves computer paddle to block and occasionally miss
     * the ball
     *
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

        // If ball reaches the player paddle height
        if( ballY+50 > 1800 ) { // Player paddle
            // Hits the paddle
            if ( ballX+50 > playerPaddleLeft &&
                    ballX-50 < playerPaddleRight ) {
                reverseY = !reverseY;
            }
            // Misses player's paddle
            else if ( ballY < 2000 ) {
                computerScore++;
                betweenGameView(g);
            }
        }
        else if( ballY-50 < 100 ) { // Computer paddle
            // Hits computer paddle
            if ( ballX+50 > compPaddleLeft &&
                    ballX-50 < compPaddleRight ) {
                reverseY = !reverseY;
                // Randomly choose if computer will miss next turn
                // based on the difficulty level chosen by user
                Random rand = new Random();
                int toMiss = rand.nextInt(chanceOfMissing);
                if( toMiss == 1 ) {
                    willMiss = true;
                }
            }
            // Misses computer paddle
            else if ( ballY < 100 ) {
                playerScore++;
                betweenGameView(g);
            }
        }

        // Make computer paddle track or lock
        if( !willMiss && ballY < 800) {
            trackingX = ballX;
        }
        else if ( ballY < 300 ) {
            trackingX = 400;
        }
        duringGameView(g);
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
            if(eventX > 500 && eventX < 1050 && betweenGames) {
                //
                if( eventY > 600 && eventY < 750 ) {        // EASY
                    paddleSize = 200;
                    chanceOfMissing = 4;
                }
                else if( eventY > 850 && eventY < 1000 ) {  // MEDIUM
                    paddleSize = 100;
                    chanceOfMissing = 6;
                }
                else if( eventY > 1100 && eventY < 1250 ) { // HARD
                    paddleSize = 25;
                    chanceOfMissing = 10;
                }
            }
            // Restart the game
            if(betweenGames) {
                newBall();
                betweenGames = false;
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

    /**
     * Paints objects to animator that are relevant to gameplay
     *
     */
    public void duringGameView(Canvas g) {
        /**
         External Citation
         Date: 3/26/2018
         Problem: Didn't know how to add shadows to all the objects
         Resource:
         https: https://developer.android.com/reference/android/
         graphics/Paint.html
         Solution: I used the setShadowLayer method
         */
        // Draw walls
        Paint wall = new Paint();
        wall.setColor(Color.WHITE);
        wall.setStrokeWidth(10.0f);
        wall.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawLine(50, 50, 50, 1850, wall);         // left
        g.drawLine(50, 50, 1500, 50, wall);         // top
        g.drawLine(1500, 50, 1500, 1850, wall);     // right
        g.drawLine(50, 1850, 1500, 1850, wall);     // bottom

        // Draw the ball in the correct position.
        Paint ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);
        ballPaint.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawCircle(ballX, ballY, 50, ballPaint);
        ballPaint.setColor(0xff0000ff);

        // Draw player's paddle
        Paint paddlePaint = new Paint();
        paddlePaint.setColor(Color.WHITE);
        paddlePaint.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawRect(playerPaddleLeft, 1800, playerPaddleRight, 1825, paddlePaint);

        // Draw computer's paddle
        compPaddleLeft = trackingX-paddleSize;
        compPaddleRight = trackingX+paddleSize;
        g.drawRect(compPaddleLeft, 75, compPaddleRight, 100, paddlePaint);
        paddlePaint.setColor(0xff0000ff);
    }

    /**
     * Paints buttons and text to animator for between game
     * interface
     *
     */
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

        // Tell user to select mode and restart
        Paint restart = new Paint();
        restart.setColor(Color.WHITE);
        restart.setTextSize(30.0f);
        restart.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawText("C H O O S E  D I F F I C U L T Y /",
                560, 450, restart);
        g.drawText("T A P  S C R E E N  T O  R E S T A R T",
                535, 500, restart);

        // Draw difficulty mode buttons
        Paint modeText = new Paint(); // Text for buttons
        modeText.setColor(Color.WHITE);
        modeText.setTextSize(70.0f);
        modeText.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));

        Paint easyMode = new Paint(); // Easy
        easyMode.setColor(Color.rgb(229, 183, 139));
        easyMode.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawRect(500, 600, 1050, 750, easyMode);
        g.drawText("E A S Y", 670, 700, modeText);

        Paint mediumMode = new Paint(); // Medium
        mediumMode.setColor(Color.rgb(221, 141, 117));
        mediumMode.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawRect(500, 850, 1050, 1000, mediumMode);
        g.drawText("M E D I U M", 600, 950, modeText);

        Paint hardMode = new Paint(); // Hard
        hardMode.setColor(Color.rgb( 138, 113, 106));
        hardMode.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawRect(500, 1100, 1050, 1250, hardMode);
        g.drawText("H A R D", 665, 1200, modeText);

        // Draw score card
        Paint scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(30.0f);
        scorePaint.setShadowLayer(2, 2, 2, Color.rgb(185, 183, 153));
        g.drawText("C O M P U T E R ' S  S C O R E: " +
                computerScore, 570, 1380, scorePaint );
        g.drawText("Y O U R  S C O R E: " + playerScore, 645, 1430,
                scorePaint );
    }

    /**
     * Resets ball properties to random speed and position for a new
     * game
     */
    public void newBall() {
        Random rand = new Random();
        countX = 150;
        countY = 150;
        reverseX = rand.nextBoolean();
        reverseY = true;
        speedX = rand.nextInt(50)+5;
        speedY = rand.nextInt(50)+5;
        willMiss = false;
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
     * The background color: slate.
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

}

