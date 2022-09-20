package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.screens.GameScreen;

public class MyContList implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        String nameA = ((PhysCharacteristics) a.getUserData()).name;
        String nameB = ((PhysCharacteristics) b.getUserData()).name;
        if (nameA.equals("hero") && nameB.equals("antihero")) {
            GameScreen.bodies.add(b.getBody());
        }
        if (nameA.equals("antihero") && nameB.equals("hero")) {
            GameScreen.bodies.add(a.getBody());
        }

        if (nameA.equals("foot") && nameB.equals("obj")) {
            Person.setJump(false);
        }
        if (nameA.equals("obj") && nameB.equals("foot")) {
            Person.setJump(false);
        }

        if (nameA.equals("hero") && nameB.equals("key")) {
            GameScreen.bodies.add(b.getBody());
            GameScreen.keys++;
        }
        if (nameA.equals("key") && nameB.equals("hero")) {
            GameScreen.bodies.add(a.getBody());
            GameScreen.keys++;
        }
        if (nameA.equals("hero") && nameB.equals("box")) {
            if(GameScreen.keys == GameScreen.maxKeys ){
                GameScreen.win = true;
            }
        }
        if (nameA.equals("box") && nameB.equals("hero")) {
            if(GameScreen.keys == GameScreen.maxKeys ){
                GameScreen.win = true;
            }
        }
        if (nameA.equals("hero") && nameB.equals("d")) {
                GameScreen.lose = true;
        }
        if (nameA.equals("d") && nameB.equals("hero")) {
                GameScreen.lose = true;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
