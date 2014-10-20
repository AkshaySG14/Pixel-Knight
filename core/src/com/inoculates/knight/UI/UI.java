package com.inoculates.knight.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.platforms.Platform;
import com.inoculates.knight.screens.GameScreen;

public abstract class UI extends Sprite {
    float time, animationTime;
    float spawnX, spawnY, width, height;
    static final int ACTING = 0, DESTROYED = 1;
    int state;

    public Vector2 vel = new Vector2();
    TiledMapTileLayer layer;
    TextureAtlas atlas;
    GameScreen screen;
    PixelKnight pK;

    public UI(TiledMapTileLayer layer, TextureAtlas atlas, GameScreen screen, float spawnX, float spawnY, float width, float height, int state) {
        if (layer != null)
        this.layer = layer;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.atlas = atlas;
        this.state = state;
        this.width = width;
        this.height = height;
        setSize(width, height);
        if (screen != null) {
            this.screen = screen;
            pK = screen.pKnight;
        }
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update(float deltaTime) {
        period(deltaTime);
    }

    protected void period(float deltaTime) {
        chooseSprite();
        tryMove();
        updateTime(deltaTime);
    }

    protected void setState(int state) {
        if (priorities(state)) {
            if (this.state != state)
                animationTime = 0;
            this.state = state;
        }
    }

    protected void setSizeBoth(float x, float y) {
        setSize(x, y);
        width = x;
        height = y;
    }

    private void updateTime(float deltaTime) {
        time += deltaTime;
        animationTime += deltaTime;
    }

    public float getSizeWidth() {
        return width;
    }

    public float getSizeHeight() {
        return height;
    }

    public int getState() {
        return state;
    }

    abstract boolean priorities(int cState);

    abstract void destroy();

    abstract void chooseSprite();

    abstract void tryMove();
}
