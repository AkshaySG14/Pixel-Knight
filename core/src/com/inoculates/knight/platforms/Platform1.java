package com.inoculates.knight.platforms;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.inoculates.knight.screens.GameScreen;

public class Platform1 extends Platform {

    Rectangle upperBounds, lowerBounds;
    public Platform1(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, float x, float y, Rectangle u, Rectangle d) {
        super(screen, atlas, layer, x, y);
        setX(x);
        setY(y);
        vel.y = 1;
        upperBounds = u;
        lowerBounds = d;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //Moves up or down.
    protected void move() {
        if (getY() > upperBounds.getY())
            vel.y = -1;
        if (getY() < lowerBounds.getY())
            vel.y = 1;
    }
}
