package com.inoculates.knight.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Gate extends Destructible {
    Animation shut;
    Animation open;
    Animation close;

    TextureAtlas.AtlasRegion closed = atlas.findRegion("gate1");
    TextureAtlas.AtlasRegion act1 = atlas.findRegion("gate2");
    TextureAtlas.AtlasRegion act2 = atlas.findRegion("gate3");

    final static int OPENING = 4;
    final static int CLOSING = 5;

    public Gate(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen) {
        super(layer, atlas, screen, sX, sY, false, true);
        createAnimations();
        setX((int) (sX / layer.getTileWidth()) * layer.getTileWidth());
        setY((int) (sY / layer.getTileHeight()) * layer.getTileHeight());
        setState(IDLE);
        setRegion(closed);
        setSize(5, 32);
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
    }

    //Chooses the animation based on whether the gate is closed or shut.
    protected void chooseSprite()
        {
            Animation anim = shut;
            if (state == IDLE)
                setRegion(closed);
            if (state == CLOSING) {
                setRegion(close.getKeyFrame(animationTime, true));
                anim = close;
            }
            if (state == OPENING) {
                setRegion(open.getKeyFrame(animationTime, true));
                anim = open;
            }
            setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
        }

    private void createAnimations() {
        shut = new Animation(1, closed);
        close = new Animation(0.55f, act1, act2);
        open = new Animation(0.55f, act2, act1);
    }

    //Opens the door and makes it intangible and invisible or closes it and makes it tangible and visible
    public void activate(boolean close) {
        if (close) {
            setState(CLOSING);
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    tangible = false;
                    setAlpha(0);
                }
            }, 1);
            timer.start();
        }
        else {
            setState(OPENING);
            setAlpha(1);
            tangible = true;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setState(IDLE);
                }
            }, 1);
            timer.start();
        }
    }

    public void explode(Sprite sprite) {
        if (sprite != null)
            return;
        setState(EXPLODE);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                decaying();
            }
        }, 1);
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
        }, 0.5f);
        timer.start();
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return !((state == DECAYING || state == DESTROYED) && cState != DESTROYED);
    }

}
