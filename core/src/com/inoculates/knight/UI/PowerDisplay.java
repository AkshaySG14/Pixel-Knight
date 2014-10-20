package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

//The icon inside the power rectangle.
public class PowerDisplay extends UI {
    static final int SQUARE = 0;
    static final int RECTANGLEV = 1;
    public int shape = SQUARE;
    TextureAtlas.AtlasRegion display;

    public PowerDisplay(float sX, float sY, TextureAtlas atlas) {
        super(null, atlas, null, sX, sY, 0, 0, 0);
        setX(sX);
        setY(sY);
        setRegion(atlas.findRegion("idle1"));
        display = atlas.findRegion("idle1");
        setState(ACTING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove() {

    }

    protected void chooseSprite() {
        setRegion(display);
    }

    //Changes size of the image.
    public void setDisplay(TextureAtlas.AtlasRegion display, boolean nothing, int shape) {
        this.display = display;
        if (nothing)
            setSize(0, 0);
        else {
            if (shape == SQUARE) setSizeBoth(12, 12);
            if (shape == RECTANGLEV) setSizeBoth(10, 15);
            this.shape = shape;
        }
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
