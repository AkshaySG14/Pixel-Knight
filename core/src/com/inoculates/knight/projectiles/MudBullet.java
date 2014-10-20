package com.inoculates.knight.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.enemies.Creature;
import com.inoculates.knight.screens.GameScreen;

public class MudBullet extends Projectile {
    float angle = 0;
    Animation fly;

    TextureAtlas.AtlasRegion fly1 = atlas.findRegion("bullet1");
    TextureAtlas.AtlasRegion fly2 = atlas.findRegion("bullet2");
    TextureAtlas.AtlasRegion fly3 = atlas.findRegion("bullet3");

    public MudBullet(float sX, float sY, float vX, float vY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, boolean enemy) {
        super(layer, atlas, screen, sX, sY, 0);
        createAnimations();
        setX(sX);
        setY(sY);
        vel.x = vX;
        vel.y = vY;
        lethal = true;
        setState(FLYING);
        setRegion(fly1);
        setSize(15, 15);
        this.enemy = enemy;
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
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float deltaTime) {
        updateTime(deltaTime);
        chooseSprite();
        period();
        setOriginCenter();
        setRotation(angle);
        angle += deltaTime;
    }

    protected void tryMove()
    {
        setX(getX() + vel.x);
        setY(getY() + vel.y);
    }

    protected void chooseSprite()
        {
            setRegion(fly.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        fly = new Animation(0.33333333f, fly1, fly2, fly3);
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

    protected boolean priorities(int cState) {
        return true;
    }

}
