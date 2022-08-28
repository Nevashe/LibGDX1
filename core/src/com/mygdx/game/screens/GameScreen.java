package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Animation.Enum.MoveAnimation;
import com.mygdx.game.Animation.MyAnimationAtlas;
import com.mygdx.game.Main;

public class GameScreen implements Screen {
    private Main main;
    private SpriteBatch batch;

    private  Rectangle rectClose;

    private MyAnimationAtlas animationAtlas;

    //переменные для движения
    private boolean dir = true;
    private int xDir = 0;
    private int yDir = 0;
    private int xDirValue;
    private int xDirValueStandart;
    private boolean shift = false;
    private boolean jump = false;
    private boolean tmpJump = false;
    private boolean idle = true;
    private MoveAnimation back;

    private TextureAtlas.AtlasRegion close;
    public GameScreen(Main main) {
        this.main = main;
        batch = new SpriteBatch();

        animationAtlas = new MyAnimationAtlas("atlas/gameAtlas.atlas");
        animationAtlas.updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);

        xDirValueStandart = animationAtlas.getFrame().getRegionWidth() / 80;
        xDirValue = xDirValueStandart;

        rectClose = new Rectangle();
        close = animationAtlas.getAtlas().findRegion("RedButton-Active");
        rectClose = animationAtlas.getAtlas().createSprite("RedButton-Active").getBoundingRectangle();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        animationAtlas.setTime(delta);

        int xNewSize = animationAtlas.getFrame().getRegionWidth()/4;
        int yNewSize = animationAtlas.getFrame().getRegionHeight()/4;

        int xClose = Gdx.graphics.getWidth()-close.originalWidth;
        int yClose = Gdx.graphics.getHeight()-close.originalHeight;
        rectClose.setPosition(xClose,yClose);

        checkMove(xNewSize,yNewSize);

        batch.begin();
        batch.draw(animationAtlas.getFrame(), xDir, yDir, xNewSize, yNewSize);
        batch.draw(close, xClose, yClose,close.originalWidth,close.originalHeight);
        batch.end();

        checkNewScreen();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        animationAtlas.dispose();
    }

    private void checkMove(int xNewSize, int yNewSize) {
        checkLeftOrRight(xNewSize);
        checkRun();
        checkJump(yNewSize);
    }

    private void checkLeftOrRight(int xNewSize){
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(idle){
                animationAtlas.updateAnimation(MoveAnimation.Walk, Animation.PlayMode.LOOP);
                idle = !idle;
            }
            dir = false;
            xDir -= xDirValue;
            if (xDir <= 0) {
                xDir = 0;
            }
        }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(idle){
                animationAtlas.updateAnimation(MoveAnimation.Walk, Animation.PlayMode.LOOP);
                idle = !idle;
            }
            dir = true;
            xDir += xDirValue;
            if (xDir >= Gdx.graphics.getWidth() - xNewSize) {
                xDir = Gdx.graphics.getWidth() - xNewSize;
            }
        } else {
            if(!idle){
                idle();
            }
        }
        if (!animationAtlas.getFrame().isFlipX() && !dir || animationAtlas.getFrame().isFlipX() && dir) {
            animationAtlas.getFrame().flip(true, false);
        }
    }

    private void checkRun() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && !idle){
            if(shift){
                animationAtlas.updateAnimation(MoveAnimation.Walk, Animation.PlayMode.LOOP);
                xDirValue = xDirValue / 2;
            }else {
                animationAtlas.updateAnimation(MoveAnimation.Run, Animation.PlayMode.LOOP);
                xDirValue = xDirValue * 2;
            }
            shift = !shift;
        }
    }

    private void checkJump(int yNewSize){
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && !jump){
            jump = true;
            back = animationAtlas.getMove();
            animationAtlas.updateAnimation(MoveAnimation.Jump, Animation.PlayMode.LOOP);

        }
        if(jump){
            if(!tmpJump){
                yDir += yNewSize/10;
            } else {
                yDir -= yNewSize/10;
            }
            if(yDir>=yNewSize){
                tmpJump = !tmpJump;
            }
            if(yDir<= 0){
                tmpJump = !tmpJump;
                jump = false;
                yDir = 0;
                animationAtlas.updateAnimation(back, Animation.PlayMode.LOOP);
            }
        }
    }

    private void idle() {
        idle = !idle;
        animationAtlas.updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);
        xDirValue = xDirValueStandart;
        shift = false;
    }

    private void checkNewScreen(){
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if(rectClose.contains(x,y)){
                dispose();
                main.setScreen(new MainScreen(main));
            }
        }
    }
}
