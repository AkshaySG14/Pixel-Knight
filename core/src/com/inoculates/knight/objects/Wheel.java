package com.inoculates.knight.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Wheel extends Destructible {
    TextureAtlas.AtlasRegion idle = atlas.findRegion("wheel2");
    boolean right;
    Cart owner;
    float angle = 0;

    public Wheel(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, Cart cart, boolean right) {
        super(layer, atlas, screen, sX, sY, true, true);
        setX(sX);
        setY(sY);
        setState(IDLE);
        setRegion(idle);
        owner = cart;
        this.right = right;
        setSize(10, 10);
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
        setPosition();
    }

    protected void chooseSprite()
        {
            setRegion(idle);
        }

    public void explode(Sprite sprite) {
        if (sprite != owner)
            return;
        setState(EXPLODE);
        tangible = false;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                decaying();
            }
        }, 0.5f);
        timer.start();
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

    //Sets the position and rotation of the wheel if it is moving.
    private void setPosition() {
        if (!right)
            setX(owner.getX() + owner.vel.x + 2);
        else setX(owner.getX() + owner.getWidth() - getWidth() + owner.vel.x - 2);

        if (owner.vel.x > 0)
            angle -= 5;
        else if (owner.vel.x < 0)
            angle += 5;
        else angle = 0;

        setOriginCenter();
        setRotation(angle);
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return !((state == DECAYING || state == DESTROYED) && cState != DESTROYED);
    }

}
