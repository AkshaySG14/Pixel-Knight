package com.inoculates.knight.pixelknight;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.projectiles.Mud;
import com.inoculates.knight.screens.GameScreen;

public class Powers {
    GameScreen screen;
    PixelKnight pK;
    TiledMapTileLayer layer;
    TextureAtlas atlas;

    boolean cooldown = false;

    public Powers(PixelKnight pKnight, GameScreen screen, TiledMapTileLayer layer, TextureAtlas atlas) {
        this.screen = screen;
        pK = pKnight;
        this.layer = layer;
        this.atlas = atlas;
    }

    public void throwMud(float x, float y) {
        if (cooldown) return;
        else cooldown = true;
        Vector3 target = new Vector3(x, y, 0);
        Vector3 position = new Vector3(pK.getX(), pK.getY(), 0);
        target = screen.camera.unproject(target);
        float angle = (float) Math.atan2(target.y - position.y, target.x - position.x);
        float dX = Math.abs(target.x - position.x);
        float dY = Math.abs(target.y - position.y);
        float fX = (float) (dX / 2 + Math.random() * 5) / 8;
        float fY = (float) (dY / 2 + Math.random() * 5) / 10;
        if (fX > 4) fX = 4;
        if (fY > 4) fY = 4;
        float oX = (float) Math.cos(angle);
        float oY = (float) Math.sin(angle);
        Mud mud = new Mud(pK.getX() + pK.dir, pK.getY() + pK.getHeight() / 2, oX, oY, fX, fY, screen.atlases.get(4), layer, screen, false);
        screen.projectiles.add(mud);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cooldown = false;
            }
        }, 5f);
        timer.start();
    }
}
