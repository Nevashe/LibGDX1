package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Animation.MyAnimationAtlas;
import com.mygdx.game.Main;

public class MainScreen implements Screen {
    private Main main;
    private SpriteBatch batch;
    private MyAnimationAtlas animationAtlas;
    private Rectangle rectStart;
    private TextureAtlas.AtlasRegion startButton;
    private TextureAtlas.AtlasRegion icon;
    public MainScreen(Main main) {
        this.main = main;
        batch = new SpriteBatch();
        animationAtlas = new MyAnimationAtlas("atlas/mainAtlas.atlas");
        startButton = animationAtlas.getAtlas().findRegion("start");
        icon = animationAtlas.getAtlas().findRegion("icon");
        rectStart = animationAtlas.getAtlas().createSprite("start").getBoundingRectangle();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 1, 1, 1);

        int xStartButton = Gdx.graphics.getWidth()/2-startButton.getRegionWidth()/2;

        int xIcon = Gdx.graphics.getWidth()/2-icon.getRegionHeight()/2;
        int yIcon = Gdx.graphics.getHeight()/2-icon.getRegionHeight()/2;
        rectStart.setPosition(xStartButton,0);
        batch.begin();
        batch.draw(startButton, xStartButton, 0, startButton.getRegionWidth(), startButton.getRegionHeight());
        batch.draw(icon, xIcon, yIcon,icon.getRegionWidth(),icon.getRegionHeight());
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

    private void checkNewScreen(){
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if(rectStart.contains(x,y)){
                dispose();
                main.setScreen(new GameScreen(main));
            }
        }
    }
}
