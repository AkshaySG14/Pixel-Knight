package com.inoculates.knight.pixelknight;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.inoculates.knight.screens.GameScreen;

public class DmgText extends DmgNotif {
    float time = 0;
    GameScreen screen;
    TextureAtlas atlas;
    TiledMapTileLayer layer;
    Vector2 vel = new Vector2();

    public DmgText(TextureAtlas.AtlasRegion region, TiledMapTileLayer layer, GameScreen screen, float x, float y)
    {
        super(region);
        this.layer = layer;
        this.screen = screen;
        setSize(17, 17);
        setX(x);
        setY(y);
        vel.y = 0.5f;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //Moves the text upwards.
    protected void move(float deltaTime) {
        setX(getX() + vel.x);
        setY(getY() + vel.y);
        if (time > 1)
            screen.dmg.removeValue(this, false);
        time += deltaTime;
    }
}
