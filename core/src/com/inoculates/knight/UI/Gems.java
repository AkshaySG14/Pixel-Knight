package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

//Gem sprite next to the number indicating the amount of gems.
public class Gems extends UI {
    Animation animate;

    TextureAtlas.AtlasRegion frame1 = atlas.findRegion("emerald1");

    public Gems(float sX, float sY, TextureAtlas atlas) {
        super(null, atlas, null, sX, sY, 11.2f, 13.6f, 0);
        setX(sX);
        setY(sY);
        setRegion(frame1);
        setState(ACTING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove() {

    }

    protected void chooseSprite() {
        setRegion(frame1);
    }

    public void decaying() {
        destroy();
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
