package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.projectiles.MudBullet;
import com.inoculates.knight.projectiles.Projectile;
import com.inoculates.knight.projectiles.Shockwave;
import com.inoculates.knight.screens.GameScreen;

public class MudGolem extends Creature {
    static final int IDLE = 4;
    static final int RUN = 5;
    static final int ATTACK = 6;
    static final int SHOOT = 7;

    Animation idle;
    Animation run;
    Animation attack;
    Animation shoot;

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("idle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("idle2");

    TextureAtlas.AtlasRegion shoot1 = atlas.findRegion("throw1");
    TextureAtlas.AtlasRegion shoot2 = atlas.findRegion("throw2");
    TextureAtlas.AtlasRegion shoot3 = atlas.findRegion("throw3");

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");

    TextureAtlas.AtlasRegion attack1 = atlas.findRegion("attack1");
    TextureAtlas.AtlasRegion attack2 = atlas.findRegion("attack2");
    TextureAtlas.AtlasRegion attack3 = atlas.findRegion("attack3");
    TextureAtlas.AtlasRegion attack4 = atlas.findRegion("attack4");
    TextureAtlas.AtlasRegion attack5 = atlas.findRegion("attack5");
    TextureAtlas.AtlasRegion attack6 = atlas.findRegion("attack6");

    private Sound shootS = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Laser_Shoot2.wav"));

    public MudGolem(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 5, 0, true, false, sX, sY);
        state = IDLE;
        setX(sX);
        setY(sY);
        createAnimations();
        setRegion(idle.getKeyFrame(animationTime, true));
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove() {
        collidesSlopeLeft();
        collidesSlopeRight();

        float dX = Math.abs(getX() - screen.pKnight.getX());
        float dY = Math.abs(getY() - screen.pKnight.getY());

        if (state != ATTACK && state != SHOOT)
            move();
        if (time > 5)  {
            if (dX < screen.camera.viewportWidth / 2 && dY < getHeight() * 2)
            attack();
            time = 0;
        }

        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        if (vel.x < 0) {
            collisionX = collidesLeft();
            if (collidesLeft())
                dir = RIGHT;
        }
        else if (vel.x > 0) {
            collisionX = collidesRight();
            if (collidesRight()) {
                dir = LEFT;
            }
        }

        if (collisionX)
            setX(oldX);

        setY(getY() + vel.y);

        if (vel.y < 0) {
            collisionY = collidesBottom();
        } else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY) {
            setY(oldY);
            vel.y = 0;
        }
    }

    private void move() {
        final float dX, dY;
        dX = Math.abs(getX() - screen.pKnight.getX());
        dY = Math.abs(getY() - screen.pKnight.getY());
        if (dX > screen.camera.viewportWidth / 2 || dY > getHeight() * 2) {
            setState(IDLE);
            return;
        }
        final int cDir = (int) Math.signum(pK.getX() - getX());
        if (cDir != dir) {
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    dir = cDir;
                }
            }, 0.5f);
            timer.start();
        }

        setState(RUN);

        if (dir == RIGHT)
            SVX(0.2f);
        if (dir == LEFT)
            SVX(-0.2f);

        setX(getX() + vel.x);
    }

    //Shoots the knight if not at the same height, otherwise launches a shockwave towards it.
    private void attack() {
        float dY = Math.abs(getY() - screen.pKnight.getY());
        if (dY < 1)
            shockwave();
        else shoot();
    }

    private void shockwave() {
        setState(ATTACK);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(IDLE);
            }
        }, 2.33333333f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                final Shockwave shockwave = new Shockwave(getX(), getY(), 5 * dir, atlas, layer, screen, true, dir);
                screen.projectiles.add(shockwave);
                shootS.play((float) screen.storage.soundE / 100);
                final Timer timer2 = new Timer();
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.projectiles.removeValue(shockwave, false);
                    }
                }, 5);
                timer2.start();
            }
        }, 1.1666666667f);
        timer.start();
    }

    private void shoot() {
        float dX, dY, posX;

        setState(SHOOT);

        if (dir == RIGHT) {
            dX = pK.getX() - (getX() + getWidth());
            posX = getX() + getWidth();
        }
        else {
            dX = pK.getX() - getX();
            posX = getX();
        }
        dY = pK.getY() - (getY() + getHeight() / 2);

        final float angle = (float) Math.atan2(dY, dX);
        final float pos = posX;

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(IDLE);
            }
        }, 1.5f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                final MudBullet bullet = new MudBullet(pos, getY() + getHeight() / 2, (float) (4 * Math.cos(angle)), (float) (4 * Math.sin(angle)), atlas, layer, screen, true);
                screen.projectiles.add(bullet);
                shootS.play((float) screen.storage.soundE / 100);
                final Timer timer2 = new Timer();
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.projectiles.removeValue(bullet, false);
                    }
                }, 5);
                timer2.start();
            }
        }, 1);
        timer.start();
    }

    protected void chooseSprite() {
        Animation anim = idle;

        if (state == IDLE) {
            setRegion(idle.getKeyFrame(animationTime, true));
            anim = idle;
        }
        if (state == RUN) {
            setRegion(run.getKeyFrame(animationTime, true));
            anim = run;
        }
        if (state == ATTACK) {
            setRegion(attack.getKeyFrame(animationTime, true));
            anim = attack;
        }
        if (state == SHOOT) {
            setRegion(shoot.getKeyFrame(animationTime, true));
            anim = shoot;
        }
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }

    private void createAnimations() {
        idle = new Animation(0.5f, idle1, idle1, idle1, idle2);
        shoot = new Animation(0.5f, shoot1, shoot2, shoot3);
        run = new Animation(0.25f, run1, run2, run1, run3, run4, run3);
        attack = new Animation(0.1666666667f, attack1, attack2, attack3, attack4, attack5, attack5, attack5,  attack6, attack6, attack6, attack4, attack3, attack2, attack1);
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected void dying() {
        setState(DYING);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                death();
            }
        }, 0.2f);
        timer.start();
    }

    public void effects() {
        pK.setModifier(0.5f, 0.5f, 1);
    }

    protected void flipSprite() {
        if (dir == LEFT)
            setFlip(false, false);
        else
            setFlip(true, false);
    }

    public boolean vulnerable(Object object) {
        return true;
    }


    protected boolean harmful() {
        return true;
    }

}
