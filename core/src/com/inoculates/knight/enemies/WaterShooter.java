package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.projectiles.WaterBeam;
import com.inoculates.knight.screens.GameScreen;

public class WaterShooter extends Creature {
    boolean cooldown = false;

    Animation idle;

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("idle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("idle2");

    private Sound shoot = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Laser_Shoot4.wav"));

    public WaterShooter(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 1, 0, false, true, sX, sY);
        setX(sX);
        setY(sY + 5);
        createAnimations();
        setRegion(idle1);
        setSize(20, 20);
        setState(IDLE);
        dir = STILL;
    }

    public void draw(Batch batch) {
        update();
        super.draw(batch);
    }

    protected void update()
    {
        checkAttack();
    }

    protected void tryMove()
    {

    }

    private void checkAttack() {
        float dX = Math.abs(getX() - screen.pKnight.getX());
        float dY = Math.abs(getY() - screen.pKnight.getY());
        if (dX < screen.camera.viewportWidth / 4 && dY < screen.camera.viewportHeight / 3 && !cooldown)
            attack();
    }

    //Shoots a ball at the knight if he comes within a certain distance.
    private void attack() {
        shoot.play((float) screen.storage.soundE / 100);
        animationTime = 0;
        float dX = pK.getX() - getX();
        float dY = pK.getY() - getY();
        float angle = (float) Math.atan2(dY, dX);
        WaterBeam beam = new WaterBeam(getX(), getY(), (float) Math.cos(angle) * 2, (float) Math.sin(angle) * 2, atlas, layer, screen, true);
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
        }

    private void createAnimations() {
        idle = new Animation(0.5f, idle1, idle1, idle1, idle2);
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

    public boolean detectCollision() {
        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;
        float radius = getWidth() / 2;

        boolean check1 = Math.abs(pK.getX() - centerX) < radius && Math.abs(pK.getY() - centerY) < radius;
        boolean check2 = Math.abs(pK.getX() + pK.getWidth() - centerX) < radius && Math.abs(pK.getY() + pK.getHeight() / 2 - centerY) < radius;
        boolean check3 = Math.abs(pK.getX() + pK.getWidth() / 2 - centerX) < radius && Math.abs(pK.getY() + pK.getHeight() - centerY) < radius;

        return (check1 || check2 || check3);
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
