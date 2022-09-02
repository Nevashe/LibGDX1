package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class PhysX {
    private final World world;
    private final Box2DDebugRenderer debugRenderer;

    public PhysX(){
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
    }

    public Body addObject(RectangleMapObject object){
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();
        Rectangle r = object.getRectangle();
        def.type = BodyDef.BodyType.valueOf(String.valueOf(object.getProperties().get("BodyType", Integer.class)));
        def.position.set(r.x + r.getWidth()/2, r.y + r.getHeight()/2);
        def.gravityScale = object.getProperties().get("gravityScale", Float.class);

        polygonShape.setAsBox(r.getWidth()/2, r.getHeight()/2);

        fdef.shape = polygonShape;
        fdef.friction = 0;
        fdef.density = 1;
        fdef.restitution = object.getProperties().get("restitution", Float.class);

        Body body;
        body = world.createBody(def);
        body.createFixture(fdef).setUserData("s");

        polygonShape.dispose();

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

    public void dispose(){
        world.dispose();
        debugRenderer.dispose();
    }
}
