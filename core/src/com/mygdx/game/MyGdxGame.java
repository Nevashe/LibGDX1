package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Animation.MyAnimation;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;

	private int clk;
	private MyAnimation animation;
	private boolean dir = true;
	private int xDir = 0;
	private int xDirValue;
	@Override
	public void create () {
		batch = new SpriteBatch();
		animation = new MyAnimation("fly.png",5,4, Animation.PlayMode.LOOP);
		xDirValue = animation.getFrame().getRegionWidth()/80;
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1);

		animation.setTime(Gdx.graphics.getDeltaTime());

		float x = Gdx.input.getX() - animation.getFrame().getRegionWidth()/2;
		float y = Gdx.graphics.getHeight() - Gdx.input.getY() - animation.getFrame().getRegionHeight()/2;

		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
			clk++;
		}

		Gdx.graphics.setTitle("Clicked " + clk + " times");

		getXDir();
		batch.begin();
		batch.draw(animation.getFrame(), xDir, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		animation.dispose();
	}

	private void getXDir(){
		if(dir){
			if(!animation.getFrame().isFlipX()) {
				animation.getFrame().flip(true, false);
			}
			xDir += xDirValue;
			if(xDir >= Gdx.graphics.getWidth() - animation.getFrame().getRegionWidth()){
				dir = !dir;
			}
		} else {
			if(animation.getFrame().isFlipX()) {
				animation.getFrame().flip(true, false);
			}
			xDir -= xDirValue;
			if(xDir <= 0){
				dir = !dir;
			}
		}
	}

	private void checkPressButton(){
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
			dir = false;
		};
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT )){
			dir = true;
		};

		if(animation.getFrame().isFlipX() && !dir){
			animation.getFrame().flip(true, false);
		}
		if(!animation.getFrame().isFlipX() && dir){
			animation.getFrame().flip(true, false);
		}
	}

	private void flyPressButton(){
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			animation.setTime(Gdx.graphics.getDeltaTime());
			dir = false;
			xDir -= xDirValue;
			if(xDir <= 0){
				xDir = 0;
			}
		};
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT )){
			animation.setTime(Gdx.graphics.getDeltaTime());
			dir = true;
			xDir += xDirValue;
			if(xDir >= Gdx.graphics.getWidth() - animation.getFrame().getRegionWidth()){
				xDir = Gdx.graphics.getWidth() - animation.getFrame().getRegionWidth();
			}
		};

		if(animation.getFrame().isFlipX() && !dir){
			animation.getFrame().flip(true, false);
		}
		if(!animation.getFrame().isFlipX() && dir){
			animation.getFrame().flip(true, false);
		}
	}
}
