package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyAnimation {
    private Texture img;
    private Animation<TextureRegion> animation;
    private float time;

    public MyAnimation(String nameImg, int col, int row, Animation.PlayMode playMode) {
        img = new Texture(nameImg);

        TextureRegion region0 = new TextureRegion(img);

        int xScr = img.getWidth() / col;
        int ySrc = img.getHeight() / row;

        TextureRegion[][] regions0 = region0.split(xScr, ySrc);

        TextureRegion[] regions1 = new TextureRegion[regions0.length * regions0[0].length];
        int tmp = 0;
        for (int i = 0; i < regions0.length; i++) {
            for (int j = 0; j < regions0[i].length; j++) {
                regions1[tmp++] = regions0[i][j];

            }
        }

        animation = new Animation<TextureRegion>(1/20f,regions1);
        animation.setPlayMode(playMode);

        time += Gdx.graphics.getDeltaTime();
    }

    public TextureRegion getFrame(){
        return animation.getKeyFrame(time);
    }

    public void setTime(float time){
        this.time += time;
    }

    public void zeroTime(){
        time = 0;
    }

    public boolean isAnimationOver(){
        return animation.isAnimationFinished(time);
    }

    public void setPlayMode(Animation.PlayMode playMode){
        this.animation.setPlayMode(playMode);
    }

    public void dispose(){
        img.dispose();
    }

}
