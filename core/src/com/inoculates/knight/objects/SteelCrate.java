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
import com.inoculates.knight.projectiles.Projectile;
import com.inoculates.knight.screens.GameScreen;

public class SteelCrate extends Destructible {
    Animation explode;

    TextureAtlas.AtlasRegion idle = atlas.findRegion("breakable_stone_small");
    TextureAtlas.AtlasRegion exploding1 = atlas.findRegion("crate_stone_break1");
    TextureAtlas.AtlasRegion exploding2 = atlas.findRegion("crate_stone_break2");
    TextureAtlas.AtlasRegion exploding3 = atlas.findRegion("crate_stone_break3");
    TextureAtlas.AtlasRegion exploding4 = atlas.findRegion("crate_stone_break4");
    TextureAtlas.AtlasRegion exploding5 = atlas.findRegion("crate_stone_break5");

    public SteelCrate(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen) {
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
        if (sprite instanceof Projectile)
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

    public boolean vulnerable(Vector2 velocity) {
        return true;
    }

}
