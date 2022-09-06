package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.screens.GameScreen;

public class MyContList implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if (a.getUserData() != null && b.getUserData() != null) {
            String stringA = (String) a.getUserData();
            String stringB = (String) b.getUserData();
            if (stringA.equals("hero") && stringB.equals("antihero")) {
                GameScreen.bodies.add(b.getBody());
            }
            if (stringA.equals("antihero") && stringB.equals("hero")) {
                GameScreen.bodies.add(a.getBody());
            }

            if (stringA.equals("foot") && stringB.equals("obj")) {
                GameScreen.setJump(false);
            }
            if (stringA.equals("obj") && stringB.equals("foot")) {
                GameScreen.setJump(false);
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
//        Fixture a = contact.getFixtureA();
//        Fixture b = contact.getFixtureB();
//        if (a.getUserData() != null && b.getUserData() != null) {
//            String stringA = (String) a.getUserData();
//            String stringB = (String) b.getUserData();
//            if (stringA.equals("foot") && stringB.equals("obj")) {
//                GameScreen.setJump(true);
//            }
//            if (stringA.equals("obj") && stringB.equals("foot")) {
//                GameScreen.setJump(true);
//            }
//        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
