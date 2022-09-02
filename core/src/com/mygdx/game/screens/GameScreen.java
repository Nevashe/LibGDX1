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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Animation.Enum.MoveAnimation;
import com.mygdx.game.Animation.MyAnimationAtlas;
import com.mygdx.game.Main;
import com.mygdx.game.PhysX;

public class GameScreen implements Screen {
    private Main main;
    private SpriteBatch batch;

    private  Rectangle rectClose;

    private MyAnimationAtlas animationAtlas;


    private OrthographicCamera camera;

    private final int cameraStep;
    private TiledMap map;

    private OrthogonalTiledMapRenderer mapRenderer;

    private final int[] bg;
    private final int[] l1;
    private PhysX physX;
    private Body body;
    private final Rectangle heroRect;
    //переменные для движения
    private boolean dir = true;
    private int xDir = 0;
    private int yDir = 0;

    private boolean idle = true;

    private TextureAtlas.AtlasRegion close;
    public GameScreen(Main main) {
        this.main = main;
        batch = new SpriteBatch();

        animationAtlas = new MyAnimationAtlas("atlas/gameAtlas.atlas");
        animationAtlas.updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);

        rectClose = new Rectangle();
        close = animationAtlas.getAtlas().findRegion("RedButton-Active");
        rectClose = animationAtlas.getAtlas().createSprite("RedButton-Active").getBoundingRectangle();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        map = new TmxMapLoader().load("map/карта1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        cameraStep = 20;

        bg = new int[1];
        bg[0] = map.getLayers().getIndex("fon");
        l1 = new int[1];
        l1[0] = map.getLayers().getIndex("pole");

        Array<RectangleMapObject> objects =  map.getLayers().get("objects").getObjects().getByType(RectangleMapObject.class);

        physX = new PhysX();
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }

        RectangleMapObject rmo = (RectangleMapObject) map.getLayers().get("setting").getObjects().get("hero");
//        map.getLayers().get("objects").getObjects().getByType(RectangleMapObject.class); - выбор по типу
        body = physX.addObject(rmo);
        body.isFixedRotation();
        heroRect = rmo.getRectangle();
        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;
        camera.update();
        animationAtlas.setTime(delta);

        int xClose = Gdx.graphics.getWidth()/2-close.originalWidth+(int)camera.position.x;
        int yClose = Gdx.graphics.getHeight()/2-close.originalHeight+(int)camera.position.y;

        rectClose.setPosition(xClose,yClose);
        mapRenderer.setView(camera);
        mapRenderer.render(bg);
        checkMove();

        xDir = (int) (body.getPosition().x - heroRect.width / 2);
        yDir = (int) (body.getPosition().y - heroRect.height / 2);

        batch.begin();
        batch.draw(animationAtlas.getFrame(), xDir , yDir, heroRect.width, heroRect.height);
        batch.draw(close, xClose, yClose,close.originalWidth,close.originalHeight);
        batch.end();
        batch.setProjectionMatrix(camera.combined);
        mapRenderer.render(l1);
        checkNewScreen();
        checkZoom();

        physX.step();
        physX.debugDraw(camera);
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
        physX.dispose();
    }

    private void checkMove() {
        checkLeftOrRight();
    }

    private void checkLeftOrRight(){
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(idle){
                animationAtlas.updateAnimation(MoveAnimation.Walk,Animation.PlayMode.LOOP);
                body.applyForceToCenter(new Vector2(-1000000000,0),true);
                idle = !idle;
            }
            dir = false;
        }else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if(idle){
                animationAtlas.updateAnimation(MoveAnimation.Walk,Animation.PlayMode.LOOP);
                body.applyForceToCenter(new Vector2(1000000000,0),true);
                idle = !idle;
            }
            dir = true;
        } else {
            if(!idle){
                idle();
            }
        }
        if (!animationAtlas.getFrame().isFlipX() && !dir || animationAtlas.getFrame().isFlipX() && dir) {
            animationAtlas.getFrame().flip(true, false);
        }

    }

    private void idle() {
        idle = !idle;
        animationAtlas.updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);
        body.setLinearVelocity(0,0);
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
        if(Gdx.input.isButtonJustPressed(Input.Buttons.BACK)){
            camera.zoom  += 0.1f;
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.FORWARD) && camera.zoom >0){
            camera.zoom -= 0.1f;
        }
    }

}
