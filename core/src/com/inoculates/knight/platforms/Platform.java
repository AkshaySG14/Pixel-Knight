package com.inoculates.knight.platforms;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.inoculates.knight.projectiles.Projectile;
import com.inoculates.knight.enemies.Creature;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.screens.GameScreen;

public abstract class Platform extends Sprite {
    float animationTime = 0;
    float time = 0;
    float spawnX, spawnY;

    public Vector2 vel = new Vector2();
    GameScreen screen;
    TiledMapTileLayer layer;
    TextureAtlas atlas;
    PixelKnight pK;

    public Platform(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, float x, float y) {
        super(atlas.findRegion("platform"));
        this.atlas = atlas;
        this.layer = layer;
        this.screen = screen;
        spawnX = x;
        spawnY = y;
        setRegion(atlas.findRegion("platform"));
        pK = screen.pKnight;
        setSize(32, 16);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update(float deltaTime) {
        period();
    }

    protected void period() {
        tryMove();
    }

    //Detects whether the pixel knight is on top of the platform or not.
    public boolean detectCollision(Object object) {
        if (object instanceof PixelKnight || object instanceof Creature || object instanceof Projectile) {
            Sprite sprite = (Sprite) object;
            float distanceX = Math.abs(getX() - sprite.getX());
            float height = sprite.getY() - getY();
            return distanceX < getWidth() - 6 && sprite.getX() > getX() - 14 && height > getHeight() - 4 &&
                    height < getHeight();
        }
        return false;
    }

    protected void tryMove() {
        move();
        setX(getX() + vel.x);
        setY(getY() + vel.y);
    }

    abstract void move();

}
