package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.projectiles.Beam;
import com.inoculates.knight.screens.GameScreen;

public class FloatingHead extends Creature {
    static final int ATTACKING = 4;

    boolean cooldown = false;

    float angle = 0;

    Animation idle;
    Animation attack;

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("idle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("idle2");
    TextureAtlas.AtlasRegion idle3 = atlas.findRegion("idle3");
    TextureAtlas.AtlasRegion attack1 = atlas.findRegion("attack1");
    TextureAtlas.AtlasRegion attack2 = atlas.findRegion("attack2");
    TextureAtlas.AtlasRegion attack3 = atlas.findRegion("attack3");

    private Sound shoot = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Laser_Shoot3.wav"));

    public FloatingHead(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 1, 0, false, false, sX, sY);
        setX(sX);
        setY(sY + 5);
        createAnimations();
        setRegion(idle1);
        setSize(13, 11);
        setState(IDLE);
        dir = STILL;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update(float deltaTime)
    {
        updateTime(deltaTime);
        chooseSprite();
        flipSprite();
        detectBalls();
        detectSpikers();
        period();
        tryMove();
        checkAttack();
        angle += deltaTime;
    }

    protected void tryMove()
    {
        move();
        setX(getX() + vel.x);
        setY(getY() + vel.y);
    }

    //Moves the head in a circular motion based on the angle float.
    private void move() {
        SVX((float) Math.cos(angle) * 0.25f);
        SVY((float) Math.sin(angle) * 0.25f);
    }

    //Checks if the pixel knight is within a certain distance of the head.
    private void checkAttack() {
        float dX = Math.abs(getX() - screen.pKnight.getX());
        float dY = Math.abs(getY() - screen.pKnight.getY());
        if (dX < screen.camera.viewportWidth / 4 && dY < screen.camera.viewportHeight / 3 && !cooldown)
            attack();
    }

    //Shoots a mud bullet when the knight is within a certain distance.
    private void attack() {
        shoot.play((float) screen.storage.soundE / 100);
        animationTime = 0;
        setState(ATTACKING);
        float dX = pK.getX() - getX();
        float dY = pK.getY() - getY();
        Beam beam = new Beam(getX(), getY(), dX / 40, dY / 40, atlas, layer, screen, true);
        screen.projectiles.add(beam);
        cooldown = true;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cooldown = false;
            }
        }, 3);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(IDLE);
            }
        }, 1);
        timer.start();
    }

    protected void chooseSprite()
        {
            if (state == IDLE)
                setRegion(idle.getKeyFrame(animationTime, true));
            if (state == ATTACKING)
                setRegion(attack.getKeyFrame(animationTime, false));
        }

    private void createAnimations() {
        idle = new Animation(0.333333f, idle1, idle2, idle3);
        attack = new Animation(0.2f, attack1, attack2, attack3);
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

    }

    protected boolean harmful() {
        return true;
    }

    public boolean vulnerable(Object object) {
        return true;
    }

}
