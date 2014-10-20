package com.inoculates.knight.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.screens.GameScreen;

public class Lever extends Destructible {
    Sprite connector;
    boolean isON = false;
    TextureAtlas.AtlasRegion off = atlas.findRegion("lever1");
    TextureAtlas.AtlasRegion on = atlas.findRegion("lever2");

    private Sound hurtS = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt.wav"));

    public Lever(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, Sprite connector) {
        super(layer, atlas, screen, sX, sY, true, false);
        setX(sX);
        setY(sY);
        setState(IDLE);
        setRegion(off);
        setSize(16, 14);
        this.connector = connector;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {
        float oldY = getY();
        boolean collisionY = false;

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
            if (isON)
                setRegion(on);
            else setRegion(off);
        }

    //If it is hit, the lever will activate.
    public void explode(Sprite sprite) {
        if (sprite instanceof PixelKnight)
            isON = !isON;
        activate();
    }

    //Plays sound and acts accordingly.
    private void activate() {
        hurtS.play((float) screen.storage.soundE / 100);
        if (connector instanceof Gate) {
            Gate gate = (Gate) connector;
            gate.activate(isON);
        }
        if (connector instanceof Cart) {
            Cart cart = (Cart) connector;
            if (isON)
                cart.setVelocity(1);
            else cart.setVelocity(-1);

        }

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
