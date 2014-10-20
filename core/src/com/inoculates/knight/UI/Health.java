package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.knight.effects.Effect;
import com.inoculates.knight.screens.GameScreen;

//Health icon.
public class Health extends UI {
    TextureAtlas.AtlasRegion health1 = atlas.findRegion("health1");
    TextureAtlas.AtlasRegion health2 = atlas.findRegion("health2");
    TextureAtlas.AtlasRegion health3 = atlas.findRegion("health3");
    TextureAtlas.AtlasRegion health4 = atlas.findRegion("health4");
    TextureAtlas.AtlasRegion health5 = atlas.findRegion("health5");
    TextureAtlas.AtlasRegion health6 = atlas.findRegion("health6");
    TextureAtlas.AtlasRegion health7 = atlas.findRegion("health7");
    TextureAtlas.AtlasRegion health8 = atlas.findRegion("health8");
    TextureAtlas.AtlasRegion health9 = atlas.findRegion("health9");
    TextureAtlas.AtlasRegion health10 = atlas.findRegion("health10");
    TextureAtlas.AtlasRegion health11 = atlas.findRegion("health11");


    public Health(float sX, float sY, TextureAtlas atlas, GameScreen screen) {
        super(null, atlas, screen, sX, sY, 59.435f, 13.6f, 0);
        setX(sX);
        setY(sY);
        setRegion(health1);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //Sets health based on the pixel knight's health.
    public void update(int health) {
        switch (health) {
            case 0:
                setRegion(health11);
                break;
            case 1:
                setRegion(health10);
                break;
            case 2:
                setRegion(health9);
                break;
            case 3:
                setRegion(health8);
                break;
            case 4:
                setRegion(health7);
                break;
            case 5:
                setRegion(health6);
                break;
            case 6:
                setRegion(health5);
                break;
            case 7:
                setRegion(health4);
                break;
            case 8:
                setRegion(health3);
                break;
            case 9:
                setRegion(health2);
                break;
            case 10:
                setRegion(health1);
                break;
        }
    }

    protected void tryMove() {

    }

    protected void chooseSprite() {

    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
