package com.inoculates.knight.projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.enemies.Creature;
import com.inoculates.knight.screens.GameScreen;

public class Beam extends Projectile {
    static final int LEFT = -1;
    static final int RIGHT = 1;

    float forceX;
    float forceY;
    float offsetX;
    float offsetY;

    Animation fly;

    TextureAtlas.AtlasRegion flying1 = atlas.findRegion("shot1");
    TextureAtlas.AtlasRegion flying2 = atlas.findRegion("shot2");
    TextureAtlas.AtlasRegion flying3 = atlas.findRegion("shot3");

    int dir = LEFT;

    public Beam(float sX, float sY, float vX, float vY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, boolean enemy) {
        super(layer, atlas, screen, sX, sY, 0);
        createAnimations();
        setX(sX);
        setY(sY);
        vel.x = vX;
        vel.y = vY;
        lethal = true;
        this.enemy = enemy;
        setState(FLYING);
        setRegion(flying1);
        setSize(6, 6);
        //Makes the beam constantly rotate and disappear after a few seconds.
        rotate((float) Math.atan2(vY, vX));
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                decaying();
            }
        }, 5);
        timer.start();
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {
        setX(getX() + vel.x);
        setY(getY() + vel.y);
    }

    protected void chooseSprite()
        {
            flipSprite();
            setRegion(fly.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        fly = new Animation(0.3333333f, flying1, flying2, flying3);
    }

    protected void decaying() {
        setState(DECAYING);
        vel.x = 0;
        vel.y = 0;
        destroy();
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    public void effects(Object object) {
        if (object instanceof Creature) {
            Creature creature = (Creature) object;
            creature.damage(1);
        }
    }

    private void flipSprite() {
        if (vel.x < 0) {
            if (flying1.isFlipX())
                flying1.flip(true, false);
            if (flying2.isFlipX())
                flying2.flip(true, false);
            if (flying3.isFlipX())
                flying3.flip(true, false);
        }
        if (vel.x > 0) {
            if (!flying1.isFlipX())
                flying1.flip(true, false);
            if (!flying2.isFlipX())
                flying2.flip(true, false);
            if (!flying3.isFlipX())
                flying3.flip(true, false);
        }
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
