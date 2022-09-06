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
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Animation.Enum.MoveAnimation;
import com.mygdx.game.Animation.MyAnimationAtlas;
import com.mygdx.game.Main;
import com.mygdx.game.PhysX;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private Main main;
    private SpriteBatch batch;

    private Rectangle rectClose;

    private MyAnimationAtlas animationAtlas;


    private OrthographicCamera camera;

    private TiledMap map;

    private OrthogonalTiledMapRenderer mapRenderer;

    private final int[] bg;
    private final int[] l1;
    private PhysX physX;
    private Body body;
    private final Rectangle heroRect;

    public static List<Body> bodies;
    //переменные для движения
    private boolean dir = true;
    private Vector2 move;
    private float xDir = 0;
    private float yDir = 0;

    private boolean idle = true;
    private static boolean jump = false;
    private TextureAtlas.AtlasRegion close;
    private int doubleJump = 0;

    public GameScreen(Main main) {
        this.main = main;
        batch = new SpriteBatch();

        animationAtlas = new MyAnimationAtlas("atlas/pers2.atlas");
        animationAtlas.updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);

        rectClose = new Rectangle();
        close = animationAtlas.getAtlas().findRegion("RedButton-Active");
        rectClose = animationAtlas.getAtlas().createSprite("RedButton-Active").getBoundingRectangle();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        map = new TmxMapLoader().load("map/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        camera.zoom = 0.5f;
        bg = new int[1];
        bg[0] = map.getLayers().getIndex("back");
        l1 = new int[1];
        l1[0] = map.getLayers().getIndex("board");

        Array<RectangleMapObject> objects = map.getLayers().get("objectBoard").getObjects().getByType(RectangleMapObject.class);

        physX = new PhysX();
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }

        RectangleMapObject rmo = (RectangleMapObject) map.getLayers().get("setting").getObjects().get("hero");
//        map.getLayers().get("objects").getObjects().getByType(RectangleMapObject.class); - выбор по типу
        body = physX.addObject(rmo);

        heroRect = rmo.getRectangle();
        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;

        bodies = new ArrayList<>();

        move = new Vector2();
    }

    public static void setJump(boolean b) {
        jump = b;
        System.out.println(jump);

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

        int xClose = Gdx.graphics.getWidth() / 2 - close.originalWidth + (int) camera.position.x;
        int yClose = Gdx.graphics.getHeight() / 2 - close.originalHeight + (int) camera.position.y;

        rectClose.setPosition(xClose, yClose);
        mapRenderer.setView(camera);
        mapRenderer.render(bg);
        checkMove(delta);

        float widthHero = heroRect.height * (((float) animationAtlas.getFrame().getRegionWidth() / (float) animationAtlas.getFrame().getRegionHeight()));

        xDir = body.getPosition().x - widthHero / 2;
        yDir = body.getPosition().y - heroRect.height / 2;
        batch.begin();
        batch.draw(animationAtlas.getFrame(), xDir, yDir, widthHero, heroRect.height);
        batch.draw(close, xClose, yClose, close.originalWidth, close.originalHeight);
        batch.end();
        batch.setProjectionMatrix(camera.combined);
        mapRenderer.render(l1);
        checkNewScreen();
        checkZoom();

        physX.step();
        physX.debugDraw(camera);

        for (Body value : bodies) {
            physX.deleteBody(value);
        }
        bodies.clear();
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

    private void checkMove(float delta) {
        move.set(checkLeftOrRight(),checkJump());
        if (move.x != 0 || move.y != 0) {
            body.applyForceToCenter(move, true);
        }

    }

    private float checkJump() {
        float y;

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)  && doubleJump != 2) {
            y = 100000000;
            animationAtlas.updateAnimation(MoveAnimation.Jump, Animation.PlayMode.LOOP);
            idle = false;
            jump = true;
            doubleJump++;
        } else{
            y = 0;
        }
        if(!jump){
            doubleJump = 0;
        }
        return y;
    }

    private float checkLeftOrRight() {
        float x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if(!jump){
                animationAtlas.updateAnimation(MoveAnimation.Run, Animation.PlayMode.LOOP);
            }
            x = -100000;
            idle = false;
            dir = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if(!jump) {
                animationAtlas.updateAnimation(MoveAnimation.Run, Animation.PlayMode.LOOP);
            }
            x = 100000;
            idle = false;
            dir = true;
        } else {
            if (!idle && !jump) {
                x = 0;
                idle();
            }
        }
        if (!animationAtlas.getFrame().isFlipX() && !dir || animationAtlas.getFrame().isFlipX() && dir) {
            animationAtlas.getFrame().flip(true, false);
        }
        return x;
    }

    private void idle() {
        idle = !idle;
        animationAtlas.updateAnimation(MoveAnimation.Idle, Animation.PlayMode.LOOP);
        body.setLinearVelocity(0, 0);
    }

    private void checkNewScreen() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (rectClose.contains(x, y)) {
                dispose();
                main.setScreen(new MainScreen(main));
            }
        }
    }

    private void checkZoom() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.BACK)) {
            camera.zoom += 0.1f;
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.FORWARD) && camera.zoom > 0) {
            camera.zoom -= 0.1f;
        }
    }

}
