package com.inoculates.knight.gems;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.knight.effects.Effect;
import com.inoculates.knight.screens.GameScreen;

public class Emerald extends Gem {
    Animation animate;

    TextureAtlas.AtlasRegion frame1 = atlas.findRegion("emerald1");
    TextureAtlas.AtlasRegion frame2 = atlas.findRegion("emerald2");
    TextureAtlas.AtlasRegion frame3 = atlas.findRegion("emerald3");

    public Emerald(float sX, float sY, TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen) {
        super(layer, atlas, screen, sX, sY);
        setX(sX);
        setY(sY);
        setRegion(frame1);
        setSize(14, 17);
        createAnimations();
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove() {

    }

    protected void chooseSprite() {
        setRegion(animate.getKeyFrame(animationTime, true));
    }

    public void decaying() {
        destroy();
    }

    private void createAnimations() {
        animate = new Animation(0.25f, frame2, frame3);
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
