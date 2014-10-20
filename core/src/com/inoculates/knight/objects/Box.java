package com.inoculates.knight.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.screens.GameScreen;

public class Box extends Destructible {
    Animation explode;

    TextureAtlas.AtlasRegion idle = atlas.findRegion("boxS");
    TextureAtlas.AtlasRegion exploding1 = atlas.findRegion("boxS_break1");
    TextureAtlas.AtlasRegion exploding2 = atlas.findRegion("boxS_break2");
    TextureAtlas.AtlasRegion exploding3 = atlas.findRegion("boxS_break3");
    TextureAtlas.AtlasRegion exploding4 = atlas.findRegion("boxS_break4");
    TextureAtlas.AtlasRegion exploding5 = atlas.findRegion("boxS_break5");

    public Box(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen) {
        super(layer, atlas, screen, sX, sY, true, true);
        createAnimations();
        setX(sX);
        setY(sY);
        setState(IDLE);
        setRegion(idle);
        setSize(16, 16);
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

    protected void chooseSprite()
        {
            if (state == EXPLODE)
                setRegion(explode.getKeyFrame(animationTime));
            else setRegion(idle);
        }

    private void createAnimations() {
        explode = new Animation(0.1f, exploding1, exploding2, exploding3, exploding4, exploding5);
    }

    public void explode(Sprite sprite) {
        if (!(sprite instanceof PixelKnight))
            return;
        if (pK.getState() != 14 && pK.getState() != 15 && pK.getState() != 16)
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

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return !((state == DECAYING || state == DESTROYED) && cState != DESTROYED);
    }
}
