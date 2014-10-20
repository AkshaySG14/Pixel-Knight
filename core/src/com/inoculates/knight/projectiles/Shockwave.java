package com.inoculates.knight.projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Shockwave extends Projectile {
    Animation move;
    int dir;

    TextureAtlas.AtlasRegion move1 = atlas.findRegion("shockwave1");
    TextureAtlas.AtlasRegion move2 = atlas.findRegion("shockwave2");
    TextureAtlas.AtlasRegion move3 = atlas.findRegion("shockwave3");

    public Shockwave(float sX, float sY, float vX, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, boolean enemy, int dir) {
        super(layer, atlas, screen, sX, sY, 0);
        createAnimations();
        setX(sX);
        setY((int) (sY / layer.getTileHeight()) * layer.getTileHeight());
        vel.x = vX;
        lethal = true;
        setState(FLYING);
        setRegion(move1);
        setSize(20, 20);
        this.enemy = enemy;
        this.dir = dir;
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
            setRegion(move.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        move = new Animation(0.33333333f, move1, move2, move3);
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
        pK.setModifier(0.5f, 0.5f, 1);
    }

    private void flipSprite() {
        if (dir == RIGHT) {
            if (!move1.isFlipX())
                move1.flip(true, false);
            if (!move2.isFlipX())
                move2.flip(true, false);
            if (!move3.isFlipX())
                move3.flip(true, false);
        }
        else {
            if (move1.isFlipX())
                move1.flip(true, false);
            if (move2.isFlipX())
                move2.flip(true, false);
            if (move3.isFlipX())
                move3.flip(true, false);
        }
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
