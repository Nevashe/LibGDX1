package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import com.mygdx.game.Animation.Enum.MoveAnimation;
import com.mygdx.game.Animation.MyAnimationAtlas;


public class Person {
    private final MyAnimationAtlas atlas;
    public Body body;
    private boolean dir = true;
    private Vector2 move;
    private float xDir = 0;
    private float yDir = 0;
    private static final float X_FORCE = 0.05f ;
    private static final float Y_FORCE = 2f;
    private boolean idle ;
    private static boolean jump;
    private int doubleJump = 0;


    public Rectangle rectangle;

    public Person(MyAnimationAtlas atlas) {
        this.atlas = atlas;
        move = new Vector2(xDir, yDir);
        updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);
        idle = true;
        jump = false;
    }

    public void render(Batch batch, float zoom) {
        checkMove();
        float widthHero = rectangle.height * (((float) atlas.getFrame().getRegionWidth() / (float) atlas.getFrame().getRegionHeight()));
        xDir = Gdx.graphics.getWidth() / 2 - widthHero / 2 / zoom;
        yDir = Gdx.graphics.getHeight() / 2 - rectangle.height / 2 / zoom;
        batch.draw(atlas.getFrame(), xDir, yDir, widthHero / zoom, rectangle.height / zoom);
    }

    public void updateAnimation(MoveAnimation move, Animation.PlayMode mode) {
        atlas.updateAnimation(move, mode);
    }

    private void checkMove() {
        move.set(checkLeftOrRight(), checkJump());
        if (move.x != 0 || move.y != 0) {
            body.applyForceToCenter(move, true);
        }

    }

    private float checkJump() {
        float y;

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && doubleJump != 2) {
            y = Y_FORCE;
            updateAnimation(MoveAnimation.Jump, Animation.PlayMode.LOOP);
            idle = false;
            jump = true;
            doubleJump++;
        } else {
            y = 0;
        }
        if (!jump) {
            doubleJump = 0;
        }
        return y;
    }

    private float checkLeftOrRight() {
        float x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (!jump) {
                updateAnimation(MoveAnimation.Run, Animation.PlayMode.LOOP);
            }
            x = -X_FORCE;
            idle = false;
            dir = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (!jump) {
                updateAnimation(MoveAnimation.Run, Animation.PlayMode.LOOP);
            }
            x = X_FORCE;
            idle = false;
            dir = true;
        } else {
            if (!idle && !jump) {
                x = 0;
                idle();
            }
        }
        if (!atlas.getFrame().isFlipX() && !dir || atlas.getFrame().isFlipX() && dir) {
            atlas.getFrame().flip(true, false);
        }
        return x;
    }

    private void idle() {
        idle = !idle;
        updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);
        body.setLinearVelocity(0, 0);
    }

    public static void setJump(boolean b) {
        jump = b;
    }
}
