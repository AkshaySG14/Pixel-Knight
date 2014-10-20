package com.inoculates.knight.effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.knight.screens.GameScreen;

public class RollingMud extends Effect {
    Animation run;
    Sprite owner;

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");

    public RollingMud(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, Sprite owner, int dir) {
        super(layer, atlas, screen, sX, sY, 16, 11, 0);
        createAnimations();
        setX(sX);
        setY(sY);
        setRegion(run1);
        this.owner = owner;
        this.dir = dir;
        setState(ACTING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //Sets the mud explosion to slightly behind the owner of the mud explosion.
    protected void tryMove()
    {
        if (state == ACTING);
        if (dir == RIGHT)
            setX(owner.getX() - getWidth() + 3);
        else
            setX(owner.getX() + owner.getWidth() - 3);
        setY(owner.getY());
    }

    protected void chooseSprite() {
        setRegion(run.getKeyFrame(animationTime, true));
    }

    private void createAnimations() {
        run = new Animation(0.125f, run1, run2, run3, run4);
    }

    public void decaying() {
        setState(DECAYING);
        destroy();
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected void flipSprite() {
        if (dir == LEFT)
            setFlip(false, false);
        else
            setFlip(true, false);
    }

    public void setDir(int cDir) {
        dir = cDir;
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
