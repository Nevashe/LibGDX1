package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Animation.Enum.MoveAnimation;
import com.mygdx.game.Animation.MyAnimationAtlas;
import com.mygdx.game.Main;

public class GameScreen implements Screen {
    private Main main;
    private SpriteBatch batch;

    private  Rectangle rectClose;

    private MyAnimationAtlas animationAtlas;


    private OrthographicCamera camera;

    private final int cameraStep;
    private TiledMap map;

    private OrthogonalTiledMapRenderer mapRenderer;

    private Rectangle mapSize;
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

        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        map = new TmxMapLoader().load("map/карта1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);


        RectangleMapObject rmo = (RectangleMapObject) map.getLayers().get("objects").getObjects().get("camera");
//        map.getLayers().get("objects").getObjects().getByType(RectangleMapObject.class); - выбор по типу
        camera.position.x = rmo.getRectangle().x;
        camera.position.y = rmo.getRectangle().y;
        cameraStep = 20;
        mapSize =  ((RectangleMapObject) map.getLayers().get("objects").getObjects().get("zone")).getRectangle();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();
        ScreenUtils.clear(1, 1, 1, 1);
        animationAtlas.setTime(delta);

        int xNewSize = animationAtlas.getFrame().getRegionWidth()/2;
        int yNewSize = animationAtlas.getFrame().getRegionHeight()/2;

        int xClose = Gdx.graphics.getWidth()/2-close.originalWidth+(int)camera.position.x;
        int yClose = Gdx.graphics.getHeight()/2-close.originalHeight+(int)camera.position.y;
        rectClose.setPosition(xClose,yClose);
        mapRenderer.setView(camera);
        mapRenderer.render();
        checkMove(xNewSize,yNewSize);

        batch.begin();
        batch.draw(animationAtlas.getFrame(), xDir, yDir, xNewSize, yNewSize);
        batch.draw(close, xClose, yClose,close.originalWidth,close.originalHeight);
        batch.end();
        batch.setProjectionMatrix(camera.combined);

        checkNewScreen();
        checkMoveCamera();
        checkZoom();

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;

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
        mapRenderer.dispose();
    }

    private void checkMove(int xNewSize, int yNewSize) {
        checkLeftOrRight(xNewSize);
        checkRun();
        checkJump(yNewSize);
    }

    private void checkLeftOrRight(int xNewSize){
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(idle){
                animationAtlas.updateAnimation(MoveAnimation.Walk, Animation.PlayMode.LOOP);
                idle = !idle;
            }
            dir = false;
            xDir -= xDirValue;
            if (xDir <= 0) {
                xDir = 0;
            }
        }else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
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
        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && !jump){
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
    private void checkZoom(){
        if(Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)){
            camera.zoom  += 0.01f;
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.FORWARD) && camera.zoom >0){
            camera.zoom -= 0.01f;
        }
    }

    private void checkMoveCamera(){
        //if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && camera.position.x>Gdx.graphics.getWidth()/2){
            camera.position.x -= cameraStep;
        }
        //if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && camera.position.x<mapSize.getWidth() - Gdx.graphics.getWidth()/2){
            camera.position.x += cameraStep;
        }
        //if(Gdx.input.isKeyPressed(Input.Keys.UP)){
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && camera.position.y<mapSize.getHeight() - Gdx.graphics.getHeight()/2){
            camera.position.y += cameraStep;
        }
        //if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && camera.position.y>Gdx.graphics.getHeight()/2){
            camera.position.y -= cameraStep;
        }
    }

}
