package com.inoculates.knight.pixelknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class DmgNotif extends Sprite {

    public DmgNotif(TextureAtlas.AtlasRegion region) {
        super(region);
    }

    public void draw(Batch batch) {
        super.draw(batch);
        move(Gdx.graphics.getDeltaTime());
    }

    abstract void move(float deltaTime);
}
