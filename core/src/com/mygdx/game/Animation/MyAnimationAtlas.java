package com.mygdx.game.Animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Animation.Enum.MoveAnimation;

public class MyAnimationAtlas {

    private Animation<TextureRegion> animation;
    private float time;
    private TextureAtlas atlas;
    private MoveAnimation move;

    public MyAnimationAtlas(String nameImg) {
        atlas = new TextureAtlas(nameImg);
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

    public void updateAnimation(MoveAnimation move, Animation.PlayMode playMode){
        this.move = move;
        animation = new Animation<TextureRegion>(1/20f,atlas.findRegions(move.name()));
        animation.setPlayMode(playMode);
    }

    public MoveAnimation getMove(){
        return move;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void dispose(){
        atlas.dispose();
    }

}
