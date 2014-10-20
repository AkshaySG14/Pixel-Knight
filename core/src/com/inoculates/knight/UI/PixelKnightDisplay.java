package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PixelKnightDisplay extends UI {

    //UI sprite next to the X.
    public PixelKnightDisplay(float sX, float sY, TextureAtlas atlas, TextureRegion display) {
        super(null, atlas, null, sX, sY, 8, 15.5f, 0);
        setX(sX);
        setY(sY);
        setRegion(display);
        setSize(13.6f, 13.6f);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove() {

    }

    protected void chooseSprite() {

    }

    protected void destroy() {
        setState(DESTROYED);
    }


    protected boolean priorities(int cState) {
        return true;
    }

}
