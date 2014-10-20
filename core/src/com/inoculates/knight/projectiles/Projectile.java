package com.inoculates.knight.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.inoculates.knight.objects.Destructible;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.platforms.Platform;
import com.inoculates.knight.screens.GameScreen;

public abstract class Projectile extends Sprite {
    float time = 0, animationTime = 0;
    float spawnX, spawnY;
    static final float GRAVITY = 0.098f;
    static final int LAUNCHING = 0;
    static final int FLYING = 1;
    static final int DECAYING = 2;
    static final int DESTROYED = 3;
    static final int LEFT = -1;
    static final int RIGHT = 1;
    int state;
    boolean onSlopeRight = false;
    boolean onSlopeLeft = false;
    boolean swimming = false;
    public boolean enemy;
    public boolean lethal;

    public Vector2 vel = new Vector2();

    TiledMapTileLayer layer;
    TextureAtlas atlas;
    GameScreen screen;
    Array<RectangleMapObject> rSlopes = new Array<RectangleMapObject>();
    Array<RectangleMapObject> lSlopes = new Array<RectangleMapObject>();
    PixelKnight pK;

    public Projectile(TiledMapTileLayer layer, TextureAtlas atlas, GameScreen screen, float spawnX, float spawnY, int state) {
        this.layer = layer;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.atlas = atlas;
        this.screen = screen;
        this.state = state;
        rSlopes = screen.pKnight.getSlope(true);
        lSlopes = screen.pKnight.getSlope(false);
        pK = screen.pKnight;
    }

    public void draw(Batch batch) {
        super.draw(batch);

    }

    public void update(float deltaTime) {
        updateTime(deltaTime);
        chooseSprite();
        period();
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
        for (float y = getY(); y < getY() + getHeight(); y ++)
            for (float x = getX(); x < getX() + getWidth(); x ++) {
                for (Destructible destructible : screen.destructibles) {
                    if (isCellBlocked(x, y))
                        return true;
                    if (x > destructible.getX() && x < destructible.getX() + destructible.getWidth() &&
                            y > destructible.getY() && y < destructible.getY() + destructible.getHeight() && destructible.isTangible()) {
                        destructible.explode(this);
                        return true;
                    }
                }
            }
        return false;
    }

    public boolean collidesLeft() {
        for (float y = getY(); y < getY() + getHeight(); y ++)
            for (float x = getX(); x < getX() + getWidth(); x ++) {
                for (Destructible destructible : screen.destructibles) {
                    if (isCellBlocked(x, y))
                        return true;
                    if (x > destructible.getX() && x < destructible.getX() + destructible.getWidth() &&
                            y > destructible.getY() && y < destructible.getY() + destructible.getHeight() && destructible.isTangible()) {
                        destructible.explode(this);
                        return true;
                    }
                }
            }
        return false;
    }

    public boolean collidesTop() {
        for (float y = getY(); y < getY() + getHeight(); y ++)
            for (float x = getX(); x < getX() + getWidth(); x ++) {
                for (Destructible destructible : screen.destructibles) {
                    if (isCellBlocked(x, y))
                        return true;
                    if (x > destructible.getX() && x < destructible.getX() + destructible.getWidth() &&
                            y > destructible.getY() && y < destructible.getY() + destructible.getHeight() && destructible.isTangible()) {
                        destructible.explode(this);
                        return true;
                    }
                }
            }
        return false;
    }

    public boolean collidesBottom() {
        for (float y = getY(); y < getY() + getHeight(); y++)
            for (float x = getX(); x < getX() + getWidth(); x++) {
                if (isCellBlocked(x, y))
                    return true;
                if (isCellLog(x, getY() + 3))
                    return true;
                for (Destructible destructible : screen.destructibles)
                    if (x > destructible.getX() && x < destructible.getX() + destructible.getWidth() &&
                            y > destructible.getY() && y < destructible.getY() + destructible.getHeight() && destructible.isTangible()) {
                        destructible.explode(this);
                        return true;
                    }
                if (isCellSpike(x, getY() + 3))
                    return true;
            }
        return false;
    }
    public boolean collidesPlatform() {
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellPlatform(getX() + step, getY()) && vel.y < 0) {
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
        float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
        float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + getWidth() / 2;
        float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
        for (float i = rWidth; i > 0; i -= layer.getTileWidth() / 10000) {
            if (getX() + getWidth() > rX + i && getY() < rY + i && getX() + getWidth() < rX + rWidth) {
                onSlopeRight = true;
                setY(rY + i);
                return true;
            }
        }
    }

    onSlopeRight = false;
    return false;
}

    public boolean collidesSlopeLeft() {
        if (lSlopes == null)
            return false;
        for (RectangleMapObject rectMapObject : lSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
            float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
            float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() - getWidth() / 2;
            float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
            for (float i = 0; i < rWidth; i += layer.getTileWidth() / 10000) {
                if (getX() < rX + i && getY() < rY + rHeight - i && getX() > rX) {
                    onSlopeLeft = true;
                    setY(rY + rHeight - i);
                    return true;
                }
            }
        }
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

    protected void updateTime(float deltaTime) {
        time += deltaTime;
        animationTime += deltaTime;
    }

    public int getState() {
        return state;
    }

    abstract boolean priorities(int cState);

    abstract void destroy();

    abstract void decaying();

    public abstract void effects(Object object);

    abstract void chooseSprite();

    abstract void tryMove();
}
