package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

//Options rectangle.
public class Options extends UI {
    TextureAtlas.AtlasRegion options1 = atlas.findRegion("options");
    TextureAtlas.AtlasRegion options2 = atlas.findRegion("options2");

    public Options(float sX, float sY, TextureAtlas atlas, GameScreen screen) {
        super(null, atlas, screen, sX, sY, 50, 10, 0);
        setX(sX);
        setY(sY);
        setRegion(options1);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void selected() {
        setRegion(options2);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setRegion(options1);
            }
        }, 0.1f);
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
