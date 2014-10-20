package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

//Knight in the overworld screen.
public class OverworldKnight extends UI {
    Animation animate;

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");
    TextureAtlas.AtlasRegion run5 = atlas.findRegion("run5");
    TextureAtlas.AtlasRegion run6 = atlas.findRegion("run6");

    public OverworldKnight(float sX, float sY, TextureAtlas atlas) {
        super(null, atlas, null, sX, sY, 8, 15.5f, 0);
        createAnimations();
        setX(sX);
        setY(sY);
        setRegion(run1);
        setState(ACTING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove() {
        setX(getX() + vel.x);
        setY(getY() + vel.y);
    }

    protected void chooseSprite()
    {
        flipSprite();
        setRegion(animate.getKeyFrame(animationTime, true));
    }

    private void flipSprite() {
        if (vel.x > 0 || (vel.x == 0 && vel.y == 0)) {
            if (run1.isFlipX())
                run1.flip(true, false);
            if (run2.isFlipX())
                run2.flip(true, false);
            if (run3.isFlipX())
                run3.flip(true, false);
            if (run4.isFlipX())
                run4.flip(true, false);
            if (run5.isFlipX())
                run5.flip(true, false);
            if (run6.isFlipX())
                run6.flip(true, false);
        } else if (vel.x < 0) {
            if (!run1.isFlipX())
                run1.flip(true, false);
            if (!run2.isFlipX())
                run2.flip(true, false);
            if (!run3.isFlipX())
                run3.flip(true, false);
            if (!run4.isFlipX())
                run4.flip(true, false);
            if (!run5.isFlipX())
                run5.flip(true, false);
            if (!run6.isFlipX())
                run6.flip(true, false);
        }
    }

    private void createAnimations() {
        animate = new Animation(0.1666667f, run1, run2, run3, run4, run5, run6);
    }

    public void setVelocity(float x, float y) {
        vel = new Vector2(x, y);
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
