package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.objects.*;
import com.inoculates.knight.pixelknight.DmgText;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.platforms.Platform;
import com.inoculates.knight.projectiles.Projectile;
import com.inoculates.knight.screens.GameScreen;

public abstract class Creature extends Sprite {
    public int health;
    public int armor;
    public int dir = RIGHT;
    public int state;
    //Booleans that determine whether the creature will fall down, or be affected by water.
    public boolean gravity;
    public boolean aquatic;
    public boolean dead = false, dying = false;
    public boolean onSlopeRight = false;
    public boolean onSlopeLeft = false;
    public boolean pX = false;
    public boolean onPlatform = false;
    public boolean swimming;
    float animationTime = 0, time = 0;
    float spawnX, spawnY;
    float modifierX = 0;
    float modifierY = 0;
    static final float GRAVITY = 0.098f;
    static final int SPAWN = 0;
    static final int IDLE = 1;
    static final int DYING = 2;
    static final int DEAD = 3;
    static final int STILL = 0;
    static final int LEFT = -1;
    static final int RIGHT = 1;

    Vector2 vel = new Vector2();

    TextureAtlas atlas;
    TiledMap map;
    TiledMapTileLayer layer;
    GameScreen screen;
    Array<RectangleMapObject> rSlopes = new Array<RectangleMapObject>();
    Array<RectangleMapObject> lSlopes = new Array<RectangleMapObject>();
    PixelKnight pK;

    private Sound hurtS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt.wav")),
            hurtS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt2.wav")),
            hurtS3 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt.wav"));

    private Sound deathS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Explosion.wav")),
        deathS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Explosion2.wav")),
        deathS3 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Explosion3.wav"));

    public Creature(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, int h, int a,
                    boolean gravity, boolean aq, float sX, float sY) {
        this.atlas = atlas;
        health = h;
        armor = a;
        aquatic = aq;
        spawnX = sX;
        spawnY = sY;
        this.layer = layer;
        this.screen = screen;
        this.map = map;
        this.gravity = gravity;
        rSlopes = screen.pKnight.getSlope(true);
        lSlopes = screen.pKnight.getSlope(false);
        pK = screen.pKnight;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void update(float deltaTime) {
        updateTime(deltaTime);
        chooseSprite();
        flipSprite();
        detectBalls();
        detectSpikers();
        period();
        tryMove();
    }

    //Slows the creature down if it is in water.
    protected void period() {
        swimming = detectSwimming(getX(), getY());

        if (gravity && !swimming) {
            vel.y -= GRAVITY;

            if (vel.y < -3.5f)
                vel.y = -3.5f;
        }
        else if (swimming && gravity)
            vel.y = -0.8f;
        if (swimming && !aquatic)
            setModifier(-0.5f, -0.5f, 1);

        if (!detectPlatforms() && !collidesBottom())
            pX = false;
    }

    protected boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
    }

    protected boolean isCellPlatform(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("platform")
                && onTop(cell.getTile().getOffsetY());
    }

    private boolean isCellSpike(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    private boolean isCellLog(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("log") && onTop(cell.getTile().getOffsetY() - 2)) {
            setY((int) (getY() / layer.getTileHeight() + 1) * layer.getTileHeight() - 2);
            return true;
        }
        return false;
    }

    private boolean isCellSwimmable(float x, float y) {
        TiledMapTileLayer background = (TiledMapTileLayer) map.getLayers().get(1);
        TiledMapTileLayer.Cell cell = background.getCell(((int) (x / layer.getTileWidth())), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("water");
    }

    public boolean collidesRight() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 2)
                if ((isCellBlocked(getX() + getWidth(), getY() + step) || collidesDestructibleRight()) && !onSlope())
                    return true;
        return false;
    }

    public boolean collidesDestructibleRight() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
                for (float x = 0; x < getWidth(); x += layer.getTileWidth() / 16)
                    if (getX() + x > destructible.getX() && getX() + x < destructible.getX() + destructible.getWidth() &&
                            getY() + step > destructible.getY() && getY() + step < destructible.getY() + destructible.getHeight()) {
                        if (this instanceof Charger && state == 5) {
                            Charger charger = (Charger) this;
                            if (destructible instanceof Box || destructible instanceof BoxLarge)
                                charger.rebound();
                            destructible.explode(this);
                        }
                        if (destructible instanceof Spiker) {
                            Spiker spiker = (Spiker) destructible;
                            if (spiker.isSolid(getX() + getWidth(), getY() + step))
                                return true;
                        }
                        if (destructible instanceof SpikeBlock) {
                            SpikeBlock block = (SpikeBlock) destructible;
                            if (block.getDirection() == 1)
                                damage(1);
                        }
                        if (destructible.isTangible())
                            return true;
                    }
        return false;
    }

    public boolean collidesLeft() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 2)
                if ((isCellBlocked(getX(), getY() + step) || collidesDestructibleLeft()) && !onSlope())
                    return true;
        return false;
    }

    public boolean collidesDestructibleLeft() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
                for (float x = 0; x < getWidth(); x += layer.getTileWidth() / 16)
                    if (getX() + x> destructible.getX() && getX() + x < destructible.getX() + destructible.getWidth() &&
                            getY() + step > destructible.getY() && getY() + step < destructible.getY() + destructible.getHeight()) {
                        if (this instanceof Charger && state == 5) {
                            Charger charger = (Charger) this;
                            if (destructible instanceof Box || destructible instanceof BoxLarge)
                                charger.rebound();
                            destructible.explode(this);
                        }
                        if (destructible instanceof Spiker) {
                            Spiker spiker = (Spiker) destructible;
                            if (spiker.isSolid(getX(), getY() + step))
                                return true;
                        }
                        if (destructible instanceof SpikeBlock) {
                            SpikeBlock block = (SpikeBlock) destructible;
                            if (block.getDirection() == 2)
                                damage(1);
                        }
                        if (destructible.isTangible())
                            return true;
                    }
        return false;
    }

    public boolean collidesTop() {
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 2)
                if ((isCellBlocked(getX() + step, getY() + getHeight()) || collidesDestructibleTop()) && !onSlope())
                    return true;
        return false;
    }

    public boolean collidesDestructibleTop() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
                if (getX() + step > destructible.getX() && getX() + step < destructible.getX() + destructible.getWidth() &&
                        getY() + getHeight() > destructible.getY() && getY() + getHeight() < destructible.getY() + destructible.getHeight()) {
                    if (destructible instanceof Spiker) {
                        Spiker spiker = (Spiker) destructible;
                        if (spiker.isSolid(getX() + step, getY() + getHeight()))
                            return true;
                    }
                    if (destructible instanceof SpikeBlock) {
                        SpikeBlock block = (SpikeBlock) destructible;
                        if (block.getDirection() == 0)
                            damage(1);
                    }
                    if (destructible.isTangible())
                        return true;
                }
        return false;
    }

    public boolean collidesBottom() {
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16) {
            if (isCellBlocked(getX() + step, getY()))
                return true;
            if (isCellLog(getX() + step, getY() + 3))
                return true;
            if (isCellSpike(getX() + step, getY() + 3))
                damage(1);
            if (collidesDestructibleBottom())
                return true;
            if (collidesPlatform())
                return true;
        }
        return false;
    }

    public boolean collidesDestructibleBottom() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
                if (getX() + step > destructible.getX() && getX() + step < destructible.getX() + destructible.getWidth() &&
                        getY() > destructible.getY() && getY() < destructible.getY() + destructible.getHeight()) {
                    if (destructible instanceof Spiker) {
                        Spiker spiker = (Spiker) destructible;
                        if (spiker.isSolid(getX() + step, getY()))
                            return true;
                    }
                    if (destructible instanceof SpikeBlock) {
                        SpikeBlock block = (SpikeBlock) destructible;
                        if (block.getDirection() == 3)
                            damage(1);
                    }
                    if (destructible instanceof Raft || destructible instanceof Cart) {
                        if (destructible.vel.x != 0) {
                            vel.x = destructible.vel.x;
                            pX = true;
                        }
                        else pX = false;
                    }
                    if (destructible.isTangible())
                        return true;
                }
        return false;
    }

    public boolean collidesPlatform() {
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellPlatform(getX() + step, getY())) {
                return true;
            }
        return false;
    }

    private boolean onTop(float h)
    {
        float c = (getY() / layer.getTileHeight() - h / layer.getTileHeight());
        for (int i = 0; i < 50; i ++)
            if (i - c < 0.2 && i - c > 0)
                return true;
        return false;
    }

    //Damages the creature and creates a damage text to notify the player.
    public void damage(int d)
    {
        if (dying)
            return;

        int damage = d - armor;
        if (damage < 0 || health == 0) damage = 0;

        Array<Sound> sounds = new Array<Sound>();
        sounds.addAll(hurtS1, hurtS2, hurtS3);
        playRandomSound(sounds);

        health -= damage;

        if (damage == 1) {
            DmgText dmg = new DmgText(screen.uAtlases.get(0).findRegion("dmg1"), layer, screen, getX(), getY() + 15);
            screen.dmg.add(dmg);
        }
        if (damage == 2) {
            DmgText dmg = new DmgText(screen.uAtlases.get(0).findRegion("dmg2"), layer, screen, getX(), getY() + 15);
            screen.dmg.add(dmg);
        }
        if (damage == 3) {
            DmgText dmg = new DmgText(screen.uAtlases.get(0).findRegion("dmg3"), layer, screen, getX(), getY() + 15);
            screen.dmg.add(dmg);
        }

        if (health != 0 && damage != 0) {
            setColor(Color.ORANGE);
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setColor(Color.WHITE);
                }
            }, 0.4f);
            timer.start();
        }

        if (health == 0) {
            setColor(Color.RED);
            dying();
        }
    }

    //Determines whether any part of the pixel knight is within the creature bounds.
    public boolean detectCollision() {
        if (pK.isRolling())
            return false;
        boolean xAxis, yAxis;
        float distanceX = Math.abs(screen.pKnight.getPosX() - getX());
        if (getX() < pK.getPosX())
            xAxis = distanceX < getWidth();
        else xAxis = pK.getPosX() + pK.getAttackingWidth() > getX();
        float distanceY = Math.abs(screen.pKnight.getY() - getY());
        if (getY() < pK.getY())
            yAxis = distanceY < getHeight();
        else yAxis = pK.getY() + pK.getHeight() > getY();
        return xAxis && yAxis && !dead && harmful();
    }

    protected void detectBalls() {
        for (Projectile projectile : screen.projectiles) {
            float distanceX = Math.abs(getX() - projectile.getX());
            float distanceY = Math.abs(getY() - projectile.getY());
            if (distanceX < getWidth() && distanceY < getHeight() && !projectile.enemy && vulnerable(projectile) && projectile.lethal) {
                projectile.effects(this);
            }
        }
    }

    protected boolean detectCliffRight() {
        float posX = getX() + getWidth() / 2 + layer.getTileWidth();
        float posY = getY() - layer.getTileHeight();
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellBlocked(posX + step, posY) || isCellPlatform(posX + step, posY) || detectSlopeLeft(posX + step, posY) || detectSlopeRight(posX + step, posY)) {
                return false;
            }
        return true;
    }

    protected boolean detectCliffLeft() {
        float posX = getX() + getWidth() / 2 - layer.getTileWidth();
        float posY = getY() - layer.getTileHeight();
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellBlocked(posX + step, posY) || isCellPlatform(posX + step, posY) || detectSlopeLeft(posX + step, posY) || detectSlopeRight(posX + step, posY)) {
                return false;
            }
        return true;
    }

    protected boolean detectSlopeRight(float x, float y) {
        if (rSlopes == null) return false;
        for (RectangleMapObject rectMapObject : rSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            int tWidth = (int) (rect.getX() + rect.getWidth() / layer.getTileWidth());
            for (float i = tWidth * layer.getTileWidth(); i > 0; i -= layer.getTileWidth() / 100) {
                if (x > rect.getX() + i - 14 && y < rect.getY() + i && x < rect.getX() + rect.getWidth() - 14) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean detectSlopeLeft(float x, float y) {
        if (lSlopes == null)
            return false;
        for (RectangleMapObject rectMapObject : lSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            int tWidth = (int) (rect.getX() + rect.getWidth() / layer.getTileWidth());
            for (float i = 0; i < tWidth * layer.getTileWidth(); i += layer.getTileWidth() / 100)
                if (x < rect.getX() + i - 10 && y < rect.getY() + rect.getHeight() - i + 4 && x > rect.getX() - 5)
                    return true;
        }
        return false;
    }

    public boolean collidesSlopeRight() {
        if (rSlopes == null) return false;
        for (RectangleMapObject rectMapObject : rSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
            float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
            float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + getWidth() / 2;
            float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
            if (getX() + getWidth() > rX && getX() < rX + rWidth) {
                for (float i = rWidth; i > 0; i -= layer.getTileWidth() / 10000) {
                    if (getX() + getWidth() > rX + i && getY() < rY + i && getX() + getWidth() < rX + rWidth && getY() >= rY) {
                        onSlopeRight = true;
                        setY(rY + i);
                        return true;
                    }
                    if (onSlopeRight && getX() + getWidth() > rX + rWidth - getWidth() / 2) {
                        setY(rY + rHeight);
                        if (this instanceof Roller)
                            vel.y = vel.x;
                    }
                    if (onSlopeRight && getX() < rX + getWidth()) {
                        setY(rY + 1);
                    }
                }
            }
        }
        onSlopeRight = false;
        return false;
    }

    public boolean collidesSlopeLeft() {
        if (lSlopes == null)
            return false;
        for (RectangleMapObject rectMapObject : lSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
            float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
            float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() - getWidth() / 2;
            float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
            if (getX() + getWidth() > rX && getX() < rX + rWidth) {
                for (float i = 0; i < rWidth; i += layer.getTileWidth() / 10000) {
                    if (getX() < rX + i && getY() < rY + rHeight - i && getX() > rX && getY() >= rY) {
                        onSlopeLeft = true;
                        setY(rY + rHeight - i);
                        return true;
                    }
                    if (onSlopeLeft && getX() < rX + 1.5f) {
                        setY(rY + rHeight);
                        if (this instanceof Roller)
                            vel.y = -vel.x;
                    }
                }
            }
        }
        onSlopeLeft = false;
        return false;
    }


    protected boolean onSlope() {
        if (rSlopes != null) {
            for (RectangleMapObject rectMapObject : rSlopes) {
                Rectangle rect = rectMapObject.getRectangle();
                float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
                float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
                float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                if (getX() > rX && getX() < rX + rWidth && getY() < rY + rHeight)
                    return true;
            }
        }
        if (lSlopes != null) {
            for (RectangleMapObject rectMapObject : lSlopes) {
                Rectangle rect = rectMapObject.getRectangle();
                float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
                float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
                float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() - getWidth() / 2;
                float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                if (getX() > rX && getX() < rX + rWidth && getY() < rY + rHeight)
                    return true;
            }
        }
        return false;
    }

    public boolean detectSwimming(float x, float y) {
        for (float step = 5f; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellSwimmable(x + step, y))
                return true;
        return false;
    }

    protected void detectSpikers() {
        for (Destructible destructible : screen.destructibles)
            if (destructible instanceof Spiker) {
                Spiker spiker = (Spiker) destructible;
                for (float i = getX(); i < getX() + getWidth(); i ++) {
                    for (float o = getY(); o < getY() + getHeight(); o ++) {
                        if (spiker.checkCollisionSpikes(i, o)) {
                            damage(1);
                            return;
                        }
                    }
                }
            }
    }

    //This method determines if any part of the pixel knight's sword has hit the creature.
    public boolean isHit(int pDir, float displacement) {
        for (float i = 0; i <= displacement; i += 0.1f) {
            float distanceX = Math.abs(pK.getPosX() + i * pDir - getX());
            float distanceY = Math.abs(pK.getY() - getY());
            if (distanceX < getWidth() && distanceY < getHeight())
                return true;
        }
        return false;
    }

    public void setModifier(float modX, float modY, float time) {
        modifierX = modX;
        modifierY = modY;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                resetModifier();
            }
        }, time);
        timer.start();
    }

    private void resetModifier() {
        modifierX = 0;
        modifierY = 0;
    }

    private boolean detectPlatforms() {
        if (this instanceof Bat || this instanceof FloatingHead || this instanceof Bird)
            return false;
        for (Platform platform : screen.platforms) {
            if (platform.detectCollision(this) && vel.y < 0) {
                setY(platform.getY() + platform.getHeight());
                vel.y = -1.5f;
                if (platform.vel.x != 0) {
                    vel.x = platform.vel.x;
                    pX = true;
                }
                else pX = false;
                onPlatform = true;
                return true;
            }
        }
        if (onPlatform) {
            vel.y = 0;
            vel.x = 0;
            onPlatform = false;
            pX = false;
            return false;
        }
        return false;
    }

    protected void SVX(float x) {
        vel.x = x - modifierX * dir;
        if (vel.x < 0 && dir == RIGHT)
            vel.x = 0;
        else if (vel.x > 0 && dir == LEFT)
            vel.x = 0;
    }

    protected void SVY(float y) {
        vel.y = y - modifierY;
        if (vel.y < 0 && y > 0)
            vel.y = 0;
        if (vel.y > 0 && y < 0)
            vel.y = 0;
    }

    protected void setState(int cState) {
        if (priorities(state)) {
            if (cState != state)
                animationTime = 0;
            state = cState;
            if (cState == DYING)
                dying = true;
        }
    }

    protected void updateTime(float deltaTime) {
        time += deltaTime;
        animationTime += deltaTime;
    }

    public void death() {
        if (dead)
            return;
        Array<Sound> sounds = new Array<Sound>();
        sounds.addAll(deathS1, deathS2, deathS3);
        playRandomSound(sounds);
        setState(DEAD);
        dead = true;
        screen.creatures.removeValue(this, false);
    }

    protected void playRandomSound(Array<Sound> sounds) {
        int random = (int) (Math.random() * sounds.size);
        sounds.get(random).play((float) screen.storage.soundE / 100);
    }

    abstract void dying();

    public abstract void effects();

    public abstract boolean vulnerable(Object object);

    abstract void chooseSprite();

    abstract void tryMove();

    abstract boolean harmful();

    abstract boolean priorities(int cState);

    abstract void flipSprite();

}
