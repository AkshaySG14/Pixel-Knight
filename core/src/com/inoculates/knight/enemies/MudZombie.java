package com.inoculates.knight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.effects.ZombieEruption;
import com.inoculates.knight.screens.GameScreen;

public class MudZombie extends Creature {
    static final int SPAWN = 4;
    static final int RUN = 5;
    static final int DESPAWN = 6;

    Animation spawn;
    Animation run;
    Animation despawn;
    MudZombieSpawner spawner;

    TextureAtlas.AtlasRegion spawn1 = atlas.findRegion("spawn1");
    TextureAtlas.AtlasRegion spawn2 = atlas.findRegion("spawn2");
    TextureAtlas.AtlasRegion spawn3 = atlas.findRegion("spawn3");

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");


    public MudZombie(final GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY, MudZombieSpawner spawner) {
        super(screen, atlas, layer, map, 1, 0, true, false, sX, sY);
        setState(SPAWN);
        setX(sX);
        setY(sY);
        createAnimations();
        this.spawner = spawner;
        setSize(13, 24);
        setRegion(spawn.getKeyFrame(animationTime, true));
        final ZombieEruption eruption = new ZombieEruption(getX(), getY(), atlas, layer, screen, this);
        screen.effects.add(eruption);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(RUN);
                eruption.decaying();
            }
        }, 1);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                dying();
            }
        }, 10);
        timer.start();
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {
        collidesSlopeLeft();
        collidesSlopeRight();

        dir = (int) Math.signum(pK.getX() - getX());
        if (state == RUN)
            vel.x = 0.5f * dir;
        else vel.x = 0;

        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);
        if (vel.x < 0) {
            collisionX = collidesLeft();
            if (collidesLeft())
                dir = RIGHT;
        }
        else if (vel.x > 0) {
            collisionX = collidesRight();
            if (collidesRight())
                dir = LEFT;
        }

        if (collisionX) {
            setX(oldX);
            vel.x = 0;
        }

        setY(getY() + vel.y);
        if (vel.y < 0) {
            collisionY = collidesBottom();
        } else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY) {
            setY(oldY);
            vel.y = 0;
        }
    }

    protected void chooseSprite()
        {
            if (state == SPAWN)
                setRegion(spawn.getKeyFrame(animationTime, false));
            if (state == RUN)
                setRegion(run.getKeyFrame(animationTime, true));
            if (state == DESPAWN)
                setRegion(despawn.getKeyFrame(animationTime, false));
        }

    private void createAnimations() {
        spawn = new Animation(0.333333f, spawn1, spawn2, spawn3);
        despawn = new Animation(0.333333f, spawn3, spawn2, spawn1);
        run = new Animation(0.25f, run1, run2, run3, run4);
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected void dying() {
        if (!screen.creatures.contains(this, false))
            return;
        final ZombieEruption eruption = new ZombieEruption(getX(), getY(), atlas, layer, screen, this);
        screen.effects.add(eruption);
        setState(DESPAWN);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                eruption.decaying();
                death();
            }
        }, 1);
        timer.start();
    }

    public void death() {
        setState(DEAD);
        spawner.zombies.removeValue(this, false);
        screen.creatures.removeValue(this, false);
    }

    public void effects() {

    }

    protected void flipSprite() {
        if (dir == LEFT)
            setFlip(true, false);
        else
            setFlip(false, false);
    }
    protected boolean harmful() {
        return (state != SPAWN && state != DESPAWN);
    }

    public boolean vulnerable(Object object) {
        return state != SPAWN && state != DESPAWN;
    }
}
