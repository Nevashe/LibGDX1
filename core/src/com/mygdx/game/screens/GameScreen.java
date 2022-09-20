package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Animation.Enum.MoveAnimation;
import com.mygdx.game.Animation.MyAnimationAtlas;
import com.mygdx.game.Animation.MyFont;
import com.mygdx.game.Main;
import com.mygdx.game.Person;
import com.mygdx.game.PhysCharacteristics;
import com.mygdx.game.PhysX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mygdx.game.PhysX.PPM;

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
    private final int[] dan;
    private PhysX physX;
    public static List<Body> bodies;

    private TextureAtlas.AtlasRegion close;

    private final Person person;

    private Map<String, TextureRegion> regions;

    private MyFont font;

    public static int keys;

    public static int maxKeys;
    public static boolean win;
    public static boolean lose;
    private final Texture img;

    public GameScreen(Main main, String mapPath) {
        this.main = main;
        batch = new SpriteBatch();

        animationAtlas = new MyAnimationAtlas("atlas/pers2.atlas");
        person = new Person(animationAtlas);
        regions = new HashMap<>();
        img = new Texture("map/temple-tileset.png");
        regions.put("key", getTextureRegion(10, 8, 7, 4));
        regions.put("box", getTextureRegion(10, 8, 6, 4));

        rectClose = new Rectangle();
        close = animationAtlas.getAtlas().findRegion("RedButton-Active");
        rectClose = animationAtlas.getAtlas().createSprite("RedButton-Active").getBoundingRectangle();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        camera.zoom = 0.3f;
        bg = new int[1];
        bg[0] = map.getLayers().getIndex("back");
        l1 = new int[1];
        l1[0] = map.getLayers().getIndex("board");
        dan = new int[1];
        dan[0] = map.getLayers().getIndex("danger");

        Array<RectangleMapObject> objects = map.getLayers().get("objectBoard").getObjects().getByType(RectangleMapObject.class);

        physX = new PhysX();
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }

        objects = map.getLayers().get("reward").getObjects().getByType(RectangleMapObject.class);

        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }

        Array<PolygonMapObject> objects2 = map.getLayers().get("objectDanger").getObjects().getByType(PolygonMapObject.class);

        for (int i = 0; i < objects2.size; i++) {
            physX.addObject(objects2.get(i));
        }

        RectangleMapObject rmo = (RectangleMapObject) map.getLayers().get("setting").getObjects().get("hero");
//        map.getLayers().get("objects").getObjects().getByType(RectangleMapObject.class); - выбор по типу
        person.body = physX.addObject(rmo);

        person.rectangle = rmo.getRectangle();
        camera.position.x = person.body.getPosition().x * PPM;
        camera.position.y = person.body.getPosition().y * PPM;

        bodies = new ArrayList<>();
        font = new MyFont(30);
        keys = 0;
        maxKeys = physX.getBodies("key").size;
        win = false;
        lose = false;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        camera.position.x = person.body.getPosition().x * PPM;
        camera.position.y = person.body.getPosition().y * PPM;
        camera.update();
        animationAtlas.setTime(delta);

        int xClose = Gdx.graphics.getWidth() - close.originalWidth;
        int yClose = Gdx.graphics.getHeight() - close.originalHeight;

        rectClose.setPosition(xClose, yClose);
        mapRenderer.setView(camera);
        mapRenderer.render(bg);
        mapRenderer.render(l1);
        mapRenderer.render(dan);
        batch.begin();

        drawRegion("key");
        drawRegion("box");

        person.render(batch, camera.zoom);
        batch.draw(close, xClose, yClose);

        font.render(batch, "Ключи : " + keys + " / " + maxKeys, 0, Gdx.graphics.getHeight());
        batch.end();
        checkZoom();

        physX.step();
        physX.debugDraw(camera);

        physX.deleteBodies(bodies);
        bodies.clear();

        checkNewScreen();
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
        img.dispose();
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
        if (win) {
            dispose();
            main.setScreen(new GameScreen(main, "map/map2.tmx"));
        }
        if (lose) {
            dispose();
            main.setScreen(new GameScreen(main, "map/map2.tmx"));
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

    private TextureRegion getTextureRegion(int colImg, int rowImg, int col, int row) {

        TextureRegion region0 = new TextureRegion(img);
        TextureRegion[][] regions = region0.split(img.getWidth() / colImg, img.getHeight() / rowImg);
        return regions[row][col];
    }

    private void drawRegion(String name) {
        Array<Body> keys = physX.getBodies(name);
        for (Body b : keys) {
            float x = Gdx.graphics.getWidth() / 2 +
                    (b.getPosition().x * PPM - ((PhysCharacteristics) b.getUserData()).size.x / 2 - camera.position.x) / camera.zoom;
            float y = Gdx.graphics.getHeight() / 2 +
                    (b.getPosition().y * PPM - ((PhysCharacteristics) b.getUserData()).size.y / 2 - camera.position.y) / camera.zoom;
            batch.draw(regions.get(name), x, y,
                    ((PhysCharacteristics) b.getUserData()).size.x / camera.zoom,
                    ((PhysCharacteristics) b.getUserData()).size.y / camera.zoom);
        }
    }
}
