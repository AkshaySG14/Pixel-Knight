package com.inoculates.knight.effects;

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

public abstract class Effect extends Sprite {
    float time = 0, animationTime = 0;
    float spawnX, spawnY;
    static final float GRAVITY = 0.098f;
    static final int ACTING = 0;
    static final int DECAYING = 1;
    static final int DESTROYED = 2;
    static final int LEFT = -1;
    static final int RIGHT = 1;
    int state, dir;
    boolean onSlopeRight, onSlopeLeft;

    public Vector2 vel = new Vector2();
    TiledMapTileLayer layer;
    TextureAtlas atlas;
    GameScreen screen;
    Array<RectangleMapObject> rSlopes = new Array<RectangleMapObject>();
    Array<RectangleMapObject> lSlopes = new Array<RectangleMapObject>();
    PixelKnight pK;

    public Effect(TiledMapTileLayer layer, TextureAtlas atlas, GameScreen screen, float spawnX, float spawnY, float width, float height, int state) {
        if (layer != null)
        this.layer = layer;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.atlas = atlas;
        this.state = state;
        setSize(width, height);
        if (screen != null) {
            this.screen = screen;
            rSlopes = screen.pKnight.getSlope(true);
            lSlopes = screen.pKnight.getSlope(false);
            pK = screen.pKnight;
        }
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update(float deltaTime) {
        updateTime(deltaTime);
        chooseSprite();
        flipSprite();
        period();
    }

    protected void period() {
        tryMove();
    }

    protected boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
    }

    protected boolean isCellPlatform(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("platform")
                && onTop(cell.getTile().getOffsetY());
    }

    protected boolean isCellSpike(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    protected boolean isCellSwimmable(float x, float y) {
        TiledMapTileLayer background = (TiledMapTileLayer) screen.map.getLayers().get(1);
        TiledMapTileLayer.Cell cell = background.getCell(((int) (x / layer.getTileWidth())), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("water");
    }

    protected boolean collidesRight() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 2)
            if (isCellBlocked(getX() + getWidth() - 4f, getY() + step))
                return true;
        return false;
    }

    protected boolean collidesLeft() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 2)
            if (isCellBlocked(getX() + 4f, getY() + step))
                return true;
        return false;
    }

    protected boolean collidesTop() {
        for (float step = 5f; step < getWidth() - 5f; step += layer.getTileWidth() / 2)
            if (isCellBlocked(getX() + step, getY() + getHeight()))
                return true;
        return false;

    }

    protected boolean collidesBottom() {
        for (float step = 5f; step < getWidth() - 5f; step += layer.getTileWidth() / 16)
            if (isCellBlocked(getX() + step, getY()) || isCellPlatform(getX() + step, getY())) {
                return true;
            }
        return false;
    }

    private boolean onTop(float h)
    {
        float c = (getY() / layer.getTileHeight() - h / layer.getTileHeight());
        for (int i = 0; i < 50; i ++)
            if (i - c < 0.2 && i - c > 0)
                return true;
        return false;
    }

    public boolean collidesSlopeRight() {
        if (rSlopes == null) return false;
        for (RectangleMapObject rectMapObject : rSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            int tWidth = (int) (rect.getX() + rect.getWidth() / layer.getTileWidth());
            for (float i = tWidth * layer.getTileWidth(); i > 0; i -= layer.getTileWidth() / 100) {
                if (getX() > rect.getX() + i - 14 && getY() < rect.getY() + i && getX() < rect.getX() + rect.getWidth() - 14) {
                    onSlopeRight = true;
                    setY(rect.getY() + i);
                    return true;
                }
                if (onSlopeRight && getY() > rect.getY() + layer.getTileHeight() && getX() > rect.getX() + rect.getWidth() - 18)
                    setY((int) ((rect.getY() + rect.getHeight()) / layer.getTileHeight()) * layer.getTileHeight());
            }
        }
        if (onSlopeRight)
        onSlopeRight = false;
        return false;
    }

    public boolean collidesSlopeLeft() {
        if (lSlopes == null)
            return false;
        for (RectangleMapObject rectMapObject : lSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            int tWidth = (int) (rect.getX() + rect.getWidth() / layer.getTileWidth());
            for (float i = 0; i < tWidth * layer.getTileWidth(); i += layer.getTileWidth() / 100) {
                if (getX() < rect.getX() + i - 10 && getY() < rect.getY() + rect.getHeight() - i + 4 && getX() > rect.getX() - 5) {
                    onSlopeLeft = true;
                    setY(rect.getY() + rect.getHeight() - i + 4);
                    return true;
                }
                if (onSlopeLeft && getY() > rect.getY() + layer.getTileHeight() && getX() < rect.getX())
                    setY((int) ((rect.getY() + rect.getHeight()) / layer.getTileHeight()) * layer.getTileHeight());
            }
        }
        if (onSlopeLeft)
            onSlopeLeft = false;
        return false;
    }

    protected boolean detectPlatforms() {
        for (Platform platform : screen.platforms) {
            if (platform.detectCollision(this) && vel.y < 0) {
                setY(platform.getY() + platform.getHeight());
                return true;
            }
        }
        return false;
    }

    protected boolean detectSwimming(float x, float y) {
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellSwimmable(x + step, y))
                return true;
        return false;
    }

    protected void setState(int state) {
        if (priorities(state)) {
            if (this.state != state)
                animationTime = 0;
            this.state = state;
        }
    }

    private void updateTime(float deltaTime) {
        time += deltaTime;
        animationTime += deltaTime;
    }

    public int getState() {
        return state;
    }

    abstract boolean priorities(int cState);

    abstract void destroy();

    abstract void decaying();

    abstract void chooseSprite();

    abstract void tryMove();

    abstract void flipSprite();
}
