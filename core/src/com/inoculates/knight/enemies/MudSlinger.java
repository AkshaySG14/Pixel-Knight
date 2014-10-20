package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.projectiles.Mud;
import com.inoculates.knight.screens.GameScreen;

public class MudSlinger extends Creature {
    static final int MOVING = 4;

    boolean throwable = true;
    int facing = LEFT;

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");

    private Sound throwMudS = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Laser_Shoot.wav"));

    Animation run;
    Animation throwMud;

    public MudSlinger(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 2, 0, true, false, sX, sY);
        setX(sX);
        setY(sY);
        createAnimations();
        setState(MOVING);
        setSize(21, 35);
        setRegion(run.getKeyFrame(animationTime, true));
    }

    public void draw(Batch batch) {
        super.draw(batch);
        update();
    }

    protected void update()    {
        if (getX() - pK.getPosX() > 0)
            facing = LEFT;
        else facing = RIGHT;

        if (time > 0.9f) {
            time = 0;
            movement();
        }
    }

    protected void tryMove()  {
        collidesSlopeLeft();
        collidesSlopeRight();

        if (Math.abs(getX() - screen.pKnight.getX()) < screen.camera.viewportWidth / 3 && throwable && checkIntereference())
        {
            throwMud();
            throwable = false;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    throwable = true;
                }
            }, 3);
            timer.start();
        }

        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft();
        else if (vel.x > 0)
            collisionX = collidesRight();

        if (collisionX)
            setX(oldX);

        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = collidesBottom();
        else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY) {
            setY(oldY);
            vel.y = 0;
        }
    }

    //Moves the mud slinger, but does not change the facing.
    private void movement() {
        int random = (int) (Math.random() * 2);
        if (random == 0)
                dir = LEFT;
        if (random == 1)
                dir = RIGHT;

        checkCollision();
        if (detectCliffRight())
            dir = LEFT;
        if (detectCliffLeft())
            dir = RIGHT;

        if (dir == RIGHT)
            SVX(0.5f);
        if (dir == LEFT)
            SVX(-0.5f);
    }

    protected void chooseSprite() {
        setRegion(run.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        run = new Animation(0.2f, run1, run2, run3, run4);
        throwMud = new Animation(0.2f, run1, run2, run3, run4);
    }

    //Throws the mud based on the angle between the slinger and the knight.
    private void throwMud() {
        throwMudS.play((float) screen.storage.soundE / 100);
        float angle = (float) Math.atan2(screen.pKnight.getY() - getY(), screen.pKnight.getX() - getX());
        float oX = (float) Math.cos(angle);
        float dX = Math.abs(screen.pKnight.getX() - getX());
        float dY = Math.abs(screen.pKnight.getY() - getY());
        float fX = (float) (dX / 2 + Math.random() * 5) / 30;
        float fY = (float) (dY / 2 + Math.random() * 5) / 25 + 2.5f;
        Mud mud = new Mud(getX() + facing, getY() + getHeight() / 2, oX, 1, fX, fY, atlas, layer, screen, true);
        screen.projectiles.add(mud);
    }

    private void checkCollision() {
        for (float i = getX(); i < getX() + 20; i += layer.getTileHeight()) {
            if (isCellBlocked(i, getY()))
                dir = LEFT;
        }
        for (float i = getX(); i > getX() - 20; i -= layer.getTileHeight()) {
            if (isCellBlocked(i, getY()))
                dir = RIGHT;
        }
    }

    private boolean checkIntereference() {
        if (screen.pKnight.getX() > getX())
            for (float i = getX(); i < getX() + 10; i += layer.getTileHeight()) {
                if (isCellBlocked(i, getY()))
                    return false;
        }
        else
            for (float i = getX(); i > getX() - 10; i -= layer.getTileHeight()) {
                if (isCellBlocked(i, getY()))
                    return false;
            }
        return true;
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
        if (facing == LEFT)
            setFlip(true, false);
        else
            setFlip(false, false);
    }

    public boolean vulnerable(Object object) {
        return true;
    }


    protected boolean harmful() {
        return true;
    }

}
