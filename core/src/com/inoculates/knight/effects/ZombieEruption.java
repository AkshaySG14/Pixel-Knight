package com.inoculates.knight.effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.knight.screens.GameScreen;

public class ZombieEruption extends Effect {
    Animation run;
    Sprite owner;

    TextureAtlas.AtlasRegion dirt1 = atlas.findRegion("dirt1");
    TextureAtlas.AtlasRegion dirt2 = atlas.findRegion("dirt2");
    TextureAtlas.AtlasRegion dirt3 = atlas.findRegion("dirt3");
    TextureAtlas.AtlasRegion dirt4 = atlas.findRegion("dirt4");

    public ZombieEruption(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, Sprite owner) {
        super(layer, atlas, screen, sX, sY, 10, 10, 0);
        createAnimations();
        setX(sX);
        setY(sY);
        setRegion(dirt1);
        this.owner = owner;
        setState(ACTING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {

    }

    protected void chooseSprite() {
        setRegion(run.getKeyFrame(animationTime, true));
    }

    private void createAnimations() {
        run = new Animation(0.125f, dirt1, dirt2, dirt3, dirt4);
    }

    public void decaying() {
        setState(DECAYING);
        destroy();
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected void flipSprite() {

    }

    public void setDir(int cDir) {
        dir = cDir;
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
