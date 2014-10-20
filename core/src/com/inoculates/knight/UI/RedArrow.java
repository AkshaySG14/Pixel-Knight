package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.knight.effects.Effect;

//Red arrow in the title screen.
public class RedArrow extends UI {
    Animation animate;

    TextureAtlas.AtlasRegion frame1 = atlas.findRegion("selector1");
    TextureAtlas.AtlasRegion frame2 = atlas.findRegion("selector2");

    public RedArrow(float sX, float sY, TextureAtlas atlas) {
        super(null, atlas, null, sX, sY, 21, 22, 0);
        createAnimations();
        setX(sX);
        setY(sY);
        setRegion(frame1);
        setState(ACTING);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove() {

    }

    protected void chooseSprite() {
        setRegion(animate.getKeyFrame(animationTime, true));
    }

    private void createAnimations() {
        animate = new Animation(0.5f, frame1, frame2);
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
