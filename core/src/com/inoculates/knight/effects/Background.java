package com.inoculates.knight.effects;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.knight.screens.GameScreen;

public class Background extends Effect {
    Background connector;

    public Background(float sX, float sY, TextureRegion region, TiledMapTileLayer layer, GameScreen screen, Background background) {
        super(layer, null, screen, sX, sY, 480, 240, 0);
        setX(sX);
        setY(sY);
        setRegion(region);
        setState(ACTING);
        if (background != null)
            connector = background;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {

    }

    protected void chooseSprite() {

    }

    public void decaying() {
        setState(DECAYING);
        destroy();
    }

    //Sets the background to an x-position slightly less than the camera position to imitate parallax scrolling.
    public void update(float deltaTime) {
        if (connector != null)
            setX(connector.getX() + connector.getWidth() - 5);
        else
            setX(screen.camera.position.x / 1.1f - screen.camera.viewportWidth / 2);
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    public void setDir(int cDir) {
        dir = cDir;
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected void flipSprite() {

    }

}
