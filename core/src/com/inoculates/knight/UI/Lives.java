package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

//Crimson X next to the pixel knight.
public class Lives extends UI {
    TextureAtlas.AtlasRegion frame1 = atlas.findRegion("lives");

    public Lives(float sX, float sY, TextureAtlas atlas) {
        super(null, atlas, null, sX, sY, 9, 9, 0);
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

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
