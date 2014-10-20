package com.inoculates.knight.objects;

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

public abstract class Destructible extends Sprite {
    float time = 0, animationTime = 0;
    float spawnX, spawnY;
    static final float GRAVITY = 0.098f;
    static final int IDLE = 0;
    static final int EXPLODE = 1;
    static final int DECAYING = 2;
    static final int DESTROYED = 3;
    int state;
    boolean onSlopeRight = false, onSlopeLeft = false;
    boolean swimming = false;
    boolean gravity, tangible;
    public Vector2 vel = new Vector2();

    TiledMapTileLayer layer;
    TextureAtlas atlas;
    GameScreen screen;
    Array<RectangleMapObject> rSlopes = new Array<RectangleMapObject>();
    Array<RectangleMapObject> lSlopes = new Array<RectangleMapObject>();
    Array<Destructible> objects = new Array<Destructible>();
    PixelKnight pK;

    public Destructible(TiledMapTileLayer layer, TextureAtlas atlas, GameScreen screen, float spawnX, float spawnY, boolean gravity, boolean tangible) {
        this.layer = layer;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.atlas = atlas;
        this.screen = screen;
        this.gravity = gravity;
        this.tangible = tangible;
        rSlopes = screen.pKnight.getSlope(true);
        lSlopes = screen.pKnight.getSlope(false);
        objects = screen.dManager;
        pK = screen.pKnight;
    }

    public void draw(Batch batch) {
        super.draw(batch);

    }

    public void update(float deltaTime) {
        updateTime(deltaTime);
        chooseSprite();
        period();
        tryMove();
    }

    protected void period() {
        if (detectSwimming(getX(), getY()) && !swimming) {
            vel.x = vel.x / 2;
            vel.y = vel.y / 2;
            swimming = true;
        }
        else if (!detectSwimming(getX(), getY()) && swimming) {
            vel.x = vel.x * 2;
            vel.y = vel.y * 2;
            swimming = false;
        }

        if (gravity)
            vel.y -= GRAVITY;
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

    protected boolean isCellSwimmable(float x, float y) {
        TiledMapTileLayer background = (TiledMapTileLayer) screen.map.getLayers().get(1);
        TiledMapTileLayer.Cell cell = background.getCell(((int) (x / layer.getTileWidth())), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("water");
    }

    private boolean isCellLog(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("log") && onTop(cell.getTile().getOffsetY() - 2)) {
            setY((int) (getY() / layer.getTileHeight() + 1) * layer.getTileHeight() - 2);
            return true;
        }
        return false;
    }

    private boolean isCellSpike(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    public boolean collidesRight() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 2)
            for (Destructible destructible : objects)
                if (isCellBlocked(getX() + getWidth(), getY() + step) || getX() + getWidth() > destructible.getX() && getX() + getWidth() < destructible.getX() + destructible.getWidth() &&
                        getY() + step > destructible.getY() && getY() + step < destructible.getY() + destructible.getHeight() && destructible.isTangible())
                    return true;
        return false;
    }

    public boolean collidesLeft() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 2)
            for (Destructible destructible : objects)
                if (isCellBlocked(getX(), getY() + step) || getX() > destructible.getX() && getX() < destructible.getX() + destructible.getWidth() &&
                        getY() + step > destructible.getY() && getY() + step < destructible.getY() + destructible.getHeight() && destructible.isTangible())
                    return true;
        return false;
    }

    public boolean collidesTop() {
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 2)
            for (Destructible destructible : objects)
                if (isCellBlocked(getX() + step, getY() + getHeight()) || getX() + step > destructible.getX() && getX() + step < destructible.getX() + destructible.getWidth() &&
                        getY() + getHeight() > destructible.getY() && getY() + getHeight() < destructible.getY() + destructible.getHeight() && destructible.isTangible())
                    return true;
        return false;
    }

    public boolean collidesBottom() {
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 16) {
            if (isCellBlocked(getX() + step, getY()))
                return true;
            if (isCellLog(getX() + step, getY() + 3))
                return true;
            if (isCellSwimmable(getX() + step, getY()) && this instanceof Raft)
                return true;
            for (Destructible destructible : objects)
                if (getX() + step > destructible.getX() && getX() + step < destructible.getX() + destructible.getWidth() &&
                        getY() > destructible.getY() && getY() < destructible.getY() + destructible.getHeight() && destructible.isTangible())
                    return true;
            if (isCellSpike(getX() + step, getY() + 3))
                explode(this);
        }
        return false;
    }

    public boolean collidesPlatform() {
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellPlatform(getX() + step, getY())) {
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

    public boolean isHit(int pDir, float displacement) {
        for (float i = 0; i <= displacement; i += 0.1f) {
            float distanceX = Math.abs(pK.getX() + i * pDir - getX());
            float distanceY = Math.abs(pK.getY() - getY());
            if (distanceX < getWidth() && distanceY < getHeight())
                return true;
        }
        return false;
    }

    private void updateTime(float deltaTime) {
        time += deltaTime;
        animationTime += deltaTime;
    }

    public boolean isTangible() {
        return tangible;
    }

    public int getState() {
        return state;
    }

    abstract boolean priorities(int cState);

    abstract void destroy();

    abstract void decaying();

    abstract void chooseSprite();

    abstract void tryMove();

    public abstract void explode(Sprite sprite);
}
