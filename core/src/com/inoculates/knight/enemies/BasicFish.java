package com.inoculates.knight.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class BasicFish extends Creature {
    Animation idle;
    Animation run;
    Rectangle leftBounds, rightBounds;

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");

    public BasicFish(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY,
                     Rectangle l, Rectangle r) {
        super(screen, atlas, layer, map, 1, 0, false, true, sX, sY);
        state = SPAWN;
        setX(sX);
        setY(sY);
        createAnimations();
        setSize(19, 10);
        leftBounds = l;
        rightBounds = r;
        setRegion(run1);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //This method sets the direction of the fish and its velocity accordingly.
    protected void tryMove()
    {
        if (getX() + getWidth() > rightBounds.getX())
            dir = LEFT;
        else if (getX() < leftBounds.getX())
            dir = RIGHT;

        if (vel.x < 0)
            if (collidesLeft())
                dir = RIGHT;
        else if (vel.x > 0)
            if (collidesRight())
                dir = LEFT;

        if (dir == RIGHT)
            SVX(0.5f);
        if (dir == LEFT)
            SVX(-0.5f);

        setX(getX() + vel.x);
    }

    protected void chooseSprite()
        {
            setRegion(run.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        run = new Animation(0.25f, run1, run2);
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

    protected void flipSprite() {
        if (dir == LEFT)
            setFlip(false, false);
        else
            setFlip(true, false);
    }

    protected boolean harmful() {
        return true;
    }

    public boolean vulnerable(Object object) {
        return true;
    }
}
