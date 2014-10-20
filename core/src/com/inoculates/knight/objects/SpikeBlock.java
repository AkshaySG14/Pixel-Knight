package com.inoculates.knight.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class SpikeBlock extends Destructible {
    int direction;
    float spawnX, spawnY;

    public static final int DOWN = 0;
    public static final int RIGHT = 1;
    public static final int LEFT = 2;
    public static final int UP = 3;

    TextureAtlas.AtlasRegion down = atlas.findRegion("spike_platform_down");
    TextureAtlas.AtlasRegion left = atlas.findRegion("spike_platform_left");
    TextureAtlas.AtlasRegion right = atlas.findRegion("spike_platform_right");
    TextureAtlas.AtlasRegion up = atlas.findRegion("spike_platform_up");

    Rectangle activator;

    public SpikeBlock(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, int direction, Rectangle activator) {
        super(layer, atlas, screen, sX, sY, false, false);
        setX(sX);
        setY(sY);
        spawnX = sX;
        spawnY = sY;
        setState(IDLE);
        setRegion(down);
        setSize(16, 16);
        this.direction = direction;
        this.activator = activator;
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
            vel.x = -vel.x;
        }

        setY(getY() + vel.y);

        if (vel.y < 0) {
            collisionY = collidesBottom();
        } else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY || collidesSlopeRight() || collidesSlopeLeft() || collidesPlatform() || detectPlatforms()) {
            setY(oldY);
            vel.y = -vel.y;
        }

        //If the spikeblock goes too far past its spawn point, it will stop moving.
        switch (direction) {
            case UP:
                if (getY() <  spawnY) {
                    setY(spawnY);
                    vel.y = 0;
                }
                break;
            case DOWN:
                if (getY() > spawnY) {
                    setY(spawnY);
                    vel.y = 0;
                }
                break;
            case RIGHT:
                if (getX() < spawnX) {
                    setX(spawnX);
                    vel.x = 0;
                }
                break;
            case LEFT:
                if (getX() > spawnX) {
                    setX(spawnX);
                    vel.x = 0;
                }
                break;
        }
    }

    //Sets frame based on direction.
    protected void chooseSprite()
        {
            switch (direction) {
                case DOWN:
                    setRegion(down);
                    break;
                case RIGHT:
                    setRegion(right);
                    break;
                case LEFT:
                    setRegion(left);
                    break;
                case UP:
                    setRegion(up);
                    break;
            }
        }

    public void explode(Sprite sprite) {

    }

    //Sets the velocity depending on the direction.
    public void activate() {
        if (Math.abs(getX() - spawnX) > 0.5f || Math.abs(getY() - spawnY) > 0.5f)
            return;
        switch (direction) {
            case DOWN:
                vel.y = -2;
                break;
            case UP:
                vel.y = 2;
                break;
            case RIGHT:
                vel.x = 2;
                break;
            case LEFT:
                vel.x = 2;
                break;
        }
    }

    public Rectangle getActivator() {
        return activator;
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

    public int getDirection() {
        return direction;
    }

}
