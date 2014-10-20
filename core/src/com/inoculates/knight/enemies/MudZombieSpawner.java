package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class MudZombieSpawner extends Creature {
    Rectangle leftBounds, rightBounds;
    Array<MudZombie> zombies = new Array<MudZombie>();

    public MudZombieSpawner(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY,
                            Rectangle l, Rectangle r) {
        super(screen, atlas, layer, map, 1, 0, true, false, sX, sY);
        state = SPAWN;
        setX(sX);
        setY(sY);
        leftBounds = l;
        rightBounds = r;
        setRegion(atlas.findRegion("run1"));
        setSize(0, 0);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {
        act(Gdx.graphics.getDeltaTime());
    }

    //Generates the zombie if the knight is between the zombie bounds.
    private void act(float deltaTime) {
        time += deltaTime;
        if (time > 2 && zombies.size < 4 && pK.getX() > leftBounds.getX() && pK.getX() < rightBounds.getX() && pK.grounded) {
            float zombieY = (int) (pK.getY() / layer.getTileHeight()) * layer.getTileHeight();
            MudZombie zombie = new MudZombie(screen, atlas, layer, map, pK.getX(), zombieY, this);
            zombies.add(zombie);
            screen.creatures.add(zombie);
            time = 0;
        }
    }

    protected void chooseSprite()
        {

        }

    protected boolean priorities(int cState) {
        return true;
    }

    protected void dying() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                death();
            }
        }, 0.5f);
        timer.start();
    }

    public void effects() {

    }

    protected void flipSprite() {

    }

    protected boolean harmful() {
        return false;
    }

    public boolean vulnerable(Object object) {
        return true;
    }
}
