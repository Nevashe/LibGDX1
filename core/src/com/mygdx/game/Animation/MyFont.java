package com.mygdx.game.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class MyFont {
        private BitmapFont cyrillicFont;

        public MyFont(int size){
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.otf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = size;
            parameter.characters = "ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮйцукенгшщзхъфывапролджэячсмитьбю" +
                    "-+=0123456789.!/";
            cyrillicFont = generator.generateFont(parameter);
            generator.dispose();
        }

        public void render (SpriteBatch batch, String str, int x, int y) {cyrillicFont.draw(batch, str, x, y);}

        public int getHeight() {return (int) cyrillicFont.getCapHeight()+1;}
        public void setColor(Color color) {cyrillicFont.setColor(color);}

        public void dispose() {
            cyrillicFont.dispose();
        }

}
