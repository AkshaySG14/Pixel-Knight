package com.inoculates.knight.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.enemies.Creature;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.screens.GameScreen;

public class Spiker extends Destructible {
    float xDivider1, xDivider2, yDivider1, yDivider2;

    TextureAtlas.AtlasRegion idle = atlas.findRegion("spike_flyer");

    public Spiker(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen) {
        super(layer, atlas, screen, sX, sY, false, false);
        setX(sX);
        setY(sY);
        setState(IDLE);
        setRegion(idle);
        setSize(48, 48);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft();
        else if (vel.x > 0)
            collisionX = collidesRight();

        if (collisionX) {
            setX(oldX);
            vel.x = 0;
        }

        setY(getY() + vel.y);

        if (vel.y < 0) {
            collisionY = collidesBottom();
        } else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY || collidesSlopeRight() || collidesSlopeLeft() || collidesPlatform() || detectPlatforms()) {
            setY(oldY);
            vel.y = 0;
        }
        xDivider1 = getX() + getWidth() / 3;
        xDivider2 = xDivider1 + getWidth() / 3;
        yDivider1 = getY() + getHeight() / 3;
        yDivider2 = yDivider1 + getHeight() / 3;
    }

    protected void chooseSprite()
        {
            setRegion(idle);
        }

    public void explode(Sprite sprite) {

    }

    public boolean checkCollisionSpikes(float x, float y) {
        if (x > getX() && x < xDivider1 && y > yDivider1 && y < yDivider2)
            return true;
        if (x > xDivider1 && x < xDivider2 && y > getY() && y < yDivider1)
            return true;
        if (x > xDivider1 && x < xDivider2 && y > yDivider2 && y < getY() + getHeight())
            return true;
        if (x > xDivider2 && x < getX() + getWidth() && y > yDivider1 && y < yDivider2)
            return true;
        return false;
    }

    public boolean isSolid(float x, float y) {
        return (x > xDivider1 && x < xDivider2 && y > yDivider1 && y < yDivider2);
    }

    protected void decaying() {
        animationTime = 0;
        setState(DECAYING);
        vel.x = 0;
        vel.y = 0;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                destroy();
            }
        }, 0.3f);
        timer.start();
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return !((state == DECAYING || state == DESTROYED) && cState != DESTROYED);
    }

    public boolean vulnerable(Vector2 velocity) {
        return true;
    }

}
