package com.inoculates.knight.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.knight.effects.Effect;

//Loading text on loading screen.
public class LoadingText extends UI {
    Animation animate;

    TextureAtlas.AtlasRegion frame1 = atlas.findRegion("loadingText1");
    TextureAtlas.AtlasRegion frame2 = atlas.findRegion("loadingText2");
    TextureAtlas.AtlasRegion frame3 = atlas.findRegion("loadingText3");
    TextureAtlas.AtlasRegion frame4 = atlas.findRegion("loadingText4");

    public LoadingText(float sX, float sY, TextureAtlas atlas) {
        super(null, atlas, null, sX, sY, 400, 200, 0);
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
        animate = new Animation(0.25f, frame1, frame2, frame3, frame4);
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    protected boolean priorities(int cState) {
        return true;
    }

}
