package com.inoculates.knight.gems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.screens.GameScreen;

public abstract class Gem extends Sprite {
    float time, animationTime;
    float spawnX, spawnY;
    static final float GRAVITY = 0.098f;
    static final int SPARKLING = 0;
    static final int DESTROYED = 1;
    public int state;

    public Vector2 vel = new Vector2();
    TiledMapTileLayer layer;
    TextureAtlas atlas;
    GameScreen screen;
    Array<RectangleMapObject> rSlopes = new Array<RectangleMapObject>();
    Array<RectangleMapObject> lSlopes = new Array<RectangleMapObject>();
    PixelKnight pK;

    public Gem(TiledMapTileLayer layer, TextureAtlas atlas, GameScreen screen, float spawnX, float spawnY) {
        if (layer != null)
        this.layer = layer;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.atlas = atlas;
        this.screen = screen;
        rSlopes = screen.pKnight.getSlope(true);
        lSlopes = screen.pKnight.getSlope(false);
        pK = screen.pKnight;
        setState(SPARKLING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update(float deltaTime) {
        chooseSprite();
        period(deltaTime);
    }

    protected void period(float deltaTime) {
        time += deltaTime;
        animationTime += deltaTime;
        tryMove();
    }

    public void setState(int state) {
        if (priorities(state)) {
            if (this.state != state)
                animationTime = 0;
            this.state = state;
        }
    }

    public boolean detectCollision() {
        float distanceX = Math.abs(screen.pKnight.getX() - getX());
        float distanceY = Math.abs(screen.pKnight.getY() - getY());
        return distanceX < getWidth() && distanceY < getHeight();
    }

    public int getState() {
        return state;
    }

    abstract boolean priorities(int cState);

    abstract void destroy();

    abstract void decaying();

    abstract void chooseSprite();

    abstract void tryMove();
}
