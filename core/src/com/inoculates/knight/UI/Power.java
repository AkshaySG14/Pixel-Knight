package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.knight.screens.GameScreen;

//Text for the power.
public class Power extends UI {
    TextureAtlas.AtlasRegion power = atlas.findRegion("power");

    public static final int NOTHING = 0;
    public static final int MUD = 1;
    public static final int CHARGE = 2;
    public static final int GLIDE = 3;
    public static final int HJUMP = 4;
    public static final int ROLL = 5;

    static final int SQUARE = 0;
    static final int RECTANGLEV = 1;
    static final int RECTANGLEH = 2;

    public Power(float sX, float sY, TextureAtlas atlas, GameScreen screen) {
        super(null, atlas, screen, sX, sY, 40, 19, 0);
        setX(sX);
        setY(sY);
        setRegion(power);
        setState(ACTING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //Changes the icon inside of the rectangle depending on the pixel knight's power.
    public void update(int power) {
        switch (power) {
            case NOTHING:
                screen.pDUI.setDisplay(screen.atlases.get(0).findRegion("idle1"), true, SQUARE);
                break;
            case CHARGE:
                screen.pDUI.setDisplay(screen.atlases.get(2).findRegion("idle1"), false, RECTANGLEV);
                break;
            case GLIDE:
                screen.pDUI.setDisplay(screen.atlases.get(3).findRegion("batidle1"), false, SQUARE);
                break;
            case MUD:
                screen.pDUI.setDisplay(screen.atlases.get(4).findRegion("run1"), false, RECTANGLEV);
                break;
            case HJUMP:
                screen.pDUI.setDisplay(screen.atlases.get(5).findRegion("idle1"), false, SQUARE);
                break;
            case ROLL:
                screen.pDUI.setDisplay(screen.atlases.get(8).findRegion("idle1"), false, SQUARE);
                break;
        }
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
