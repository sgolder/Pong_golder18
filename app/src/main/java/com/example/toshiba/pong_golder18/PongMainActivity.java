package com.example.toshiba.pong_golder18;

import android.os.Bundle;
import android.app.Activity;

/**
 * PongMainActivity
 *
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 *
 * The pong game consists of a human player competing against a computer
 * player. Each time a player does not successfully return the ball,
 * their opponent gets a point. To play at different difficulties,
 * the user must make their selection after the conclusion of playing
 * round.
 *
 * Enhancements include:
 * Waiting for user input to restart the game (a)
 * Allow user to change size of the paddle (a)
 * Keep a running score of the gameplay (b)
 * Original pong: playing vs. computer (b)
 *
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @author Sarah Golder
 * @version July 2013
 *
 */
public class PongMainActivity extends Activity {

    /**
     * creates an AnimationSurface containing a TestAnimator.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_main);

        // Connect the animation surface with the animator
        AnimationSurface mySurface = (AnimationSurface) this
                .findViewById(R.id.animationSurface);

        // Set surface to be pong animator
        PongAnimator myAnimator = new PongAnimator();
        mySurface.setAnimator(myAnimator);
    }
}
