package com.inoculates.knight.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.knight.screens.GameScreen;

public class Sky extends Effect {
    public Sky(float sX, float sY, TextureRegion region, TiledMapTileLayer layer, GameScreen screen) {
        super(layer, null, screen, sX, sY, 800, 800, 0);
        setX(sX);
        setY(sY);
        setRegion(region);
        setState(ACTING);
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

    //Sets the sky slightly above the background.
    public void update(float deltaTime) {
            setX(screen.camera.position.x / 1.1f - screen.camera.viewportWidth / 2);
            setY(screen.getS1().getY() + screen.getS1().getHeight());
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
