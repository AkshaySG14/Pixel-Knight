package com.inoculates.knight.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Cart extends Destructible {
    TextureAtlas.AtlasRegion idle = atlas.findRegion("cart");
    Wheel left,right;

    public Cart(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen) {
        super(layer, atlas, screen, sX, sY, true, true);
        setX(sX);
        setY(sY);
        setState(IDLE);
        setRegion(idle);
        setSize(32, 22);
        left = new Wheel(sX, sY, atlas, layer, screen, this, false);
        screen.destructibles.add(left);
        right = new Wheel(sX, sY, atlas, layer, screen, this, true);
        screen.destructibles.add(right);
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
        //Sets the cart atop the wheel at all times.
        setY(left.getY() + left.getHeight() - 1);
    }

    protected void chooseSprite()
        {
            setRegion(idle);
        }

    public void explode(Sprite sprite) {
        if (sprite != null)
            return;
        right.explode(this);
        left.explode(this);
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

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return !((state == DECAYING || state == DESTROYED) && cState != DESTROYED);
    }

    public void setVelocity(float x) {
        vel.x = x;
    }

}
