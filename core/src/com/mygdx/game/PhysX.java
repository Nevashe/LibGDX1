package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.List;


public class PhysX {
    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    public final static float PPM = 100;

    public PhysX(){
        world = new World(new Vector2(0, -9.81f), true);
        world.setContactListener(new MyContList());
        debugRenderer = new Box2DDebugRenderer();

    }

    public Body addObject(RectangleMapObject object){
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();
        Rectangle r = object.getRectangle();
        float x = r.getWidth()/2/PPM;
        float y = r.getHeight()/2/PPM;
        def.type = BodyDef.BodyType.valueOf(String.valueOf(object.getProperties().get("BodyType", Integer.class)));
        def.position.set(r.x/PPM + x, r.y/PPM + y);
        def.gravityScale = object.getProperties().get("gravityScale", Float.class);

        polygonShape.setAsBox(x, y);

        fdef.shape = polygonShape;
        fdef.friction = 0;

        fdef.density = 1;
        fdef.restitution = object.getProperties().get("restitution", Float.class);

        Body body;
        String name = object.getName();
        body = world.createBody(def);
        body.setUserData(new PhysCharacteristics(name,new Vector2(r.x, r.y), new Vector2(r.width, r.height)));

        body.createFixture(fdef).setUserData(new PhysCharacteristics(name, new Vector2(r.x, r.y), new Vector2(r.width, r.height)));
        if(name!= null){
            if(name.equals("hero")){
                body.setFixedRotation(true);
                polygonShape.setAsBox(x/2, y/60, new Vector2(0, -y), 0);
                body.createFixture(fdef).setUserData(new PhysCharacteristics("foot", new Vector2(r.x, r.y), new Vector2(r.width, r.height)));
                body.getFixtureList().get(body.getFixtureList().size-1).setSensor(true);
            }
        }
        polygonShape.dispose();

        return body;
    }

    public Body addObject(PolygonMapObject object){
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        ChainShape cs = new ChainShape();
        float x = object.getPolygon().getX()/PPM;
        float y = object.getPolygon().getY()/PPM;
        object.getPolygon().setOrigin(0,0);
        object.getPolygon().setPosition(x,y);
        float scaleX = object.getPolygon().getScaleX()/PPM;
        float scaleY = object.getPolygon().getScaleY()/PPM;
        object.getPolygon().setScale(scaleX,scaleY);
        cs.createLoop(object.getPolygon().getTransformedVertices());

        def.type = BodyDef.BodyType.valueOf(String.valueOf(object.getProperties().get("BodyType", Integer.class)));
        def.gravityScale = object.getProperties().get("gravityScale", Float.class);

        fdef.shape = cs;
        fdef.friction = 0;
        fdef.density = 1;
        fdef.restitution = object.getProperties().get("restitution", Float.class);
        Body body;
        String name = object.getName();
        body = world.createBody(def);
        body.isBullet();
        body.createFixture(fdef).setUserData(new PhysCharacteristics(name, new Vector2(x, y), new Vector2(0, 0)));

        cs.dispose();

        return body;
    }

    public void setGravity(Vector2 gravity){
        world.setGravity(gravity);
    }

    public void step(){
        world.step(1/60f, 3, 3);
    }

    public void debugDraw(OrthographicCamera camera){
        debugRenderer.render(world,camera.combined);
    }

    public void deleteBody(Body body){
        world.destroyBody(body);
    }

    public void dispose(){
        world.dispose();
        debugRenderer.dispose();
    }

    public Array<Body> getBodies(String name){
        Array<Body> ab = new Array<>();
        world.getBodies(ab);
        Iterator<Body> it = ab.iterator();
        while (it.hasNext()){
            PhysCharacteristics pc = (PhysCharacteristics) it.next().getUserData();
            if(pc == null){
                it.remove();
            } else {
                String text = pc.name;
                if (!text.equals(name)) {
                    it.remove();
                }
            }
        }
        return ab;
    }

    public void deleteBodies(List<Body> bodies) {
        for (Body value : bodies) {
            deleteBody(value);
        }
    }
}
