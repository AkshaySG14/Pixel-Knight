package com.inoculates.knight.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Bat extends Creature {
    static final int FLYING = 4;
    static final int HIBERNATING = 5;

    Animation idle;
    Animation run;
    Animation sleeping;

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("batidle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("batidle2");
    TextureAtlas.AtlasRegion run1 = atlas.findRegion("batflying1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("batflying2");
    TextureAtlas.AtlasRegion sleep1 = atlas.findRegion("batsleeping1");
    TextureAtlas.AtlasRegion sleep2 = atlas.findRegion("batsleeping2");

    public Bat(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 1, 0, false, false, sX, sY);
        setX(sX);
        setY(sY);
        createAnimations();
        hibernate();
        setRegion(idle.getKeyFrame(animationTime, true));
        setSize(21, 14);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //If the pixel knight is within a certain distance of the pixel knight, the bat will react.
    protected void tryMove()
    {
        //Breaks the bat from hibernation
        if (Math.abs(getX() - pK.getX()) < screen.camera.viewportWidth / 2 && Math.abs(getY() - pK.getY()) < screen.camera.viewportHeight / 2 &&
                state == FLYING)
        {
            //Sets the direction of the bat
            if (getX() - screen.pKnight.getX() > 0)
                dir = LEFT;
            else dir = RIGHT;
            homeIn();
            //Tells the bat to retract after 10 seconds.
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (state == FLYING) setState(IDLE);
                }
            }, 10);
            timer.start();
        }

        //Retracts the bat after a certain amount of time
        if (state == IDLE)
            retract();

        setX(getX() + vel.x);
        setY(getY() + vel.y);
    }

    protected void chooseSprite()
        {
            if (state == IDLE)
                setRegion(idle.getKeyFrame(animationTime, true));
            if (state == FLYING)
                setRegion(run.getKeyFrame(animationTime, true));
            if (state == HIBERNATING)
                setRegion(sleeping.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        idle = new Animation(0.2f, idle1, idle2);
        run = new Animation(0.2f, run1, run2);
        sleeping = new Animation(0.2f, sleep1, sleep2);
    }

    //Homes in on the pixel knight by adjusting velocities based on the angle between the knight and the bat
    private void homeIn() {
        if ((Math.abs(pK.getX() - getX()) + Math.abs(pK.getY() - getY())) < getHeight() + getWidth())
            return;
        double angle = Math.atan2(pK.getY() + pK.getHeight() / 2 - 0.5f - getY(), screen.pKnight.getX() - getX());
        SVX((float) Math.cos(angle) * 0.8f);
        SVY((float) Math.sin(angle) * 0.8f);
        if (vel.x < 0.5 && vel.x > 0)
            SVX(0.5f);
        if (vel.x > -0.5 && vel.x < 0)
            SVX(-0.5f);
        if (vel.y < 0.5 && vel.y > 0)
            SVY(0.5f);
        if (vel.y > -0.5 && vel.y < 0)
            SVY(-0.5f);
    }

    //Sends the bat back to the spawn position based on the angle between it and the position.
    private void retract() {
        double angle = Math.atan2(spawnY - getY(), spawnX - getX());
        vel.x = (float) Math.cos(angle);
        vel.y = (float) Math.sin(angle);
        if (Math.abs(getX() - spawnX) < 0.5f & Math.abs(getY() - spawnY) < 0.5f)
            hibernate();
    }

    //Makes the bat sleep for 10 seconds.
    private void hibernate() {
        setState(HIBERNATING);
        vel.x = 0;
        vel.y = 0;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(FLYING);
            }
        }, 10);
        timer.start();
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected void dying() {
        setState(DYING);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                death();
            }
        }, 0.5f);
        timer.start();
    }

    public void effects() {

    }

    protected boolean harmful() {
        return true;
    }

    public boolean vulnerable(Object object) {
        return true;
    }

    protected void flipSprite() {
        if (dir == LEFT)
            setFlip(true, false);
        else
            setFlip(false, false);
    }

}
