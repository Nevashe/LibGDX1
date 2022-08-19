package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;

	Texture img;
	int clk;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("p.png");
		Gdx.graphics.setResizable(false);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1);

		float x = Gdx.input.getX() - 50;
		float y = Gdx.graphics.getHeight() - Gdx.input.getY() - 50;

		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
			clk++;
		}

		Gdx.graphics.setTitle("Clicked " + clk + " times");

		batch.begin();
		batch.draw(img, 0, 0,100,100);
		batch.draw(img, x, y,100,100);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
