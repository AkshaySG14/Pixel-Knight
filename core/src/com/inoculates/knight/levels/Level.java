package com.inoculates.knight.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.enemies.Creature;
import com.inoculates.knight.objects.SpikeBlock;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.screens.GameScreen;

public class Level {
    GameScreen screen;
    World world;
    Array<Rectangle> spawns = new Array<Rectangle>();
    Array<SpikeBlock> blocks = new Array<SpikeBlock>();
    Rectangle respawner, cliff, cameraBounds, endDoor;
    PixelKnight pK;

    public Level(GameScreen screen, World world, Rectangle cliff, Rectangle mainSpawn, Rectangle endDoor, Array<SpikeBlock> blocks, Array<Rectangle> spawns) {
        this.screen = screen;
        this.world = world;
        this.cliff = cliff;
        this.endDoor = endDoor;
        this.blocks = blocks;
        this.spawns = spawns;
        respawner = mainSpawn;
        cameraBounds = respawner;
        pK = screen.pKnight;
        setLevel();
    }

    //Checks whether the pixel knight has proceeded past a certain respawn point, creating a new one.
    public void update() {
        for (Rectangle spawn : spawns)
            for (float x = pK.getX(); x < pK.getX() + pK.getWidth(); x ++)
                for (float y = pK.getY(); y < pK.getY() + pK.getHeight(); y ++)
                    if (spawn.contains(x, y))
                        respawner = spawn;

        //Checks for the activation of a spike block, corresponding to the activator region.
        for (SpikeBlock block : blocks) {
            for (float x = pK.getX(); x < pK.getX() + pK.getWidth(); x ++)
                for (float y = pK.getY(); y < pK.getY() + pK.getHeight(); y ++)
                    if (block.getActivator().contains(x, y))
                        block.activate();
        }

        //Checks if the pixel knight falls below a certain level, killing him if he does.
        if (pK.getY() + pK.getHeight() < cliff.getY() && pK.getState() != 26)
            pK.death();

        //Same as the knight except in respect to the enemies.
        for (Creature creature : screen.creatures)
            if (creature.getY() + creature.getHeight() < cliff.getY() && !creature.dead)
                creature.death();

        //Changes the camera if any of the camera change regions are touched by the pixel knight.
        for (MapObject object : screen.map.getLayers().get("Camera Bounds").getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                if (object.getProperties().containsKey("camerachange") && rect.contains(pK.getX(), pK.getY())) {
                    cameraBounds = rect;
                }
            }
        }

        //This slowly scrolls the camera to the new position by delaying the camera pan over an interval.
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(2);
        float newY = cameraBounds.getY() + screen.camera.viewportHeight / 2 - layer.getTileHeight() * 2;
        float deltaTime = 0;
        if (screen.getCamY() != newY) {
            if (screen.getCamY() < newY) {
                final Rectangle currentRect = cameraBounds;
                for (float y = screen.getCamY(); y <= newY; y++) {
                    final float fY = y;
                    Timer timer = new Timer();
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            if (isCurrent(currentRect))
                                screen.setCamY(fY);
                        }
                    }, deltaTime);
                    timer.start();
                    deltaTime += 0.001f;
                }
            }
            else {
                final Rectangle currentRect = cameraBounds;
                for (float y = screen.getCamY(); y >= newY; y--) {
                    final float fY = y;
                    Timer timer = new Timer();
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            if (isCurrent(currentRect))
                            screen.setCamY(fY);
                        }
                    }, deltaTime);
                    timer.start();
                    deltaTime += 0.001f;
                }
            }
        }
    }

    //If the pixel knight is dead, this sets his position to the most recent respawn.
    public void setKnightPosition() {
        pK.setPosition(respawner.getX(), respawner.getY());
    }

    //This sets the screen background depending on the level.
    private void setLevel() {
        switch (screen.storage.level) {
            case 0:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/background1.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky1.png"))));
                break;
            case 1:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky2.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky2.png"))));
                break;
            case 2:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/background1.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky1.png"))));
                break;
            case 3:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky2.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky2.png"))));
                break;
            case 4:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/background1.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky1.png"))));
                break;
            case 5:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky2.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky2.png"))));
                break;
            case 6:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/background1.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky1.png"))));
                break;
            case 7:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/background1.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky1.png"))));
                break;
            case 8:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/background1.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky1.png"))));
                break;
            case 9:
                screen.setBackgrounds(new TextureRegion(new Texture(Gdx.files.internal("data/levels/background1.png"))), new TextureRegion(new Texture(Gdx.files.internal("data/levels/sky1.png"))));
                break;
        }
    }

    //Similarly to the backgrounds, this sets the screen's music dependent on the level.
    public Sound getMusic() {
        int l = screen.storage.level;
        if (l == 0 || l == 2 || l == 4 || l == 6 || l == 7 || l == 8 || l == 9)
            return Gdx.audio.newSound(Gdx.files.internal("data/sounds/OpenGame.wav"));
        else return Gdx.audio.newSound(Gdx.files.internal("data/sounds/ClosedGame.wav"));
    }

    //Launches if the player pressed the up button, and checks if the pixel knight is in the end door.
    public boolean exitLevel() {
        for (float x = pK.getX(); x < pK.getX() + pK.getWidth(); x ++)
            for (float y = pK.getY(); y < pK.getY() + pK.getHeight(); y ++)
                if (endDoor.contains(x, y)) {
                    screen.nextLevel();
                    return true;
                }
        return false;
    }

    private boolean isCurrent(Rectangle rectangle) {
        return rectangle.overlaps(cameraBounds);
    }
}
