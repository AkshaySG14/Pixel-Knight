package com.inoculates.knight.platforms;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.inoculates.knight.screens.GameScreen;

public class Platform2 extends Platform {

    Rectangle leftBounds, rightBounds;

    public Platform2(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, float x, float y, Rectangle l, Rectangle r) {
        super(screen, atlas, layer, x, y);
        setX(x);
        setY(y);
        leftBounds = l;
        rightBounds = r;
        vel.x = 1;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //Moves right or left.
    protected void move() {
        if (getX() > rightBounds.getX())
            vel.x = -1;
        if (getX() < leftBounds.getX())
            vel.x = 1;
    }

}
