package com.inoculates.knight.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.inoculates.knight.enemies.*;
import com.inoculates.knight.gems.Diamond;
import com.inoculates.knight.gems.Emerald;
import com.inoculates.knight.gems.Ruby;
import com.inoculates.knight.gems.Sapphire;
import com.inoculates.knight.objects.*;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.platforms.Platform;
import com.inoculates.knight.platforms.Platform1;
import com.inoculates.knight.platforms.Platform2;
import com.inoculates.knight.screens.GameScreen;
import com.inoculates.knight.screens.LoadingScreen;

public class World {
    private GameScreen screen;
    private LoadingScreen lScreen;
    private TiledMap map;
    private PixelKnight pk;
    private Array<TextureAtlas> atlases;
    private Array<TextureAtlas> uAtlases;
    private Array<TextureAtlas> wAtlases;
    private TextureAtlas oAtlas;
    //Bounds for the various enemies and platforms.
    private Array<Rectangle> dollBounds = new Array<Rectangle>();
    private Array<Rectangle> bfishBounds = new Array<Rectangle>();
    private Array<Rectangle> jfishBounds = new Array<Rectangle>();
    private Array<Rectangle> birdVBounds = new Array<Rectangle>();
    private Array<Rectangle> birdHBounds = new Array<Rectangle>();
    private Array<Rectangle> zombieBounds = new Array<Rectangle>();
    private Array<Rectangle> platform1Bounds = new Array<Rectangle>();
    private Array<Rectangle> platform2Bounds = new Array<Rectangle>();
    //Activating areas for the spiked blocks.
    private Array<Rectangle> activators = new Array<Rectangle>(), spawns = new Array<Rectangle>();
    //The objects related to the levers, which are activated by the levers.
    private Array<Destructible> connectors = new Array<Destructible>();
    private Array<SpikeBlock> blocks = new Array<SpikeBlock>();
    private Rectangle mainSpawn, cliff, endDoor;

    public Level level;

    public World(GameScreen screen, TiledMap map, PixelKnight pk, Array<TextureAtlas> atlases, Array<TextureAtlas> wAtlases, LoadingScreen lScreen) {
        this.screen = screen;
        this.lScreen = lScreen;
        this.map = map;
        this.pk = pk;
        this.atlases = atlases;
        this.wAtlases = wAtlases;
        this.uAtlases = screen.uAtlases;
        this.oAtlas = screen.oAtlas;
        generateObjects();
    }

    private void setLevel() {
        level = new Level(screen, this, cliff, mainSpawn, endDoor, blocks, spawns);
        Gdx.input.setInputProcessor(pk);
    }

    //Generates every single object as described by the tile map's various objects.
    private void generateObjects() {
        int dollCount = 0;
        int bfishCount = 0;
        int jfishCount = 0;
        int birdVCount = 0;
        int birdHCount = 0;
        int zombieCount = 0;
        int platform1Count = 0;
        int platform2Count = 0;
        int activatorCount = 0;

        //Goes through all spawns and finds the mainspawn, which is the first spawn for the pixel knight.
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject && object.getProperties().containsKey("spawnmain")) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                mainSpawn = rect;
                pk.setPosition(rect.x, rect.y);
            }

        //Adds all respawn points to the level class.
        for (MapObject object : map.getLayers().get("Respawn Points").getObjects())
            if (object instanceof RectangleMapObject && object.getProperties().containsKey("rspwn")) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                spawns.add(rect);
            }

        //Adds the end door to the level.
        for (MapObject object : map.getLayers().get("End Door").getObjects())
            if (object instanceof RectangleMapObject && object.getProperties().containsKey("enddoor")) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                endDoor = rect;
            }

        //Adds the lower bound to the level.
        for (MapObject object : map.getLayers().get("Camera Bounds").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                if (object.getProperties().containsKey("lowerbounds")) {
                    cliff = rect;
                    break;
                }
            }

        //Adds all the creature bounds.
        for (MapObject object : map.getLayers().get("Creature Bounds").getObjects()) {
            if (object instanceof RectangleMapObject) {
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
                Rectangle bounds = ((RectangleMapObject) object).getRectangle();
                bounds.setX((int) (bounds.getX() / layer.getTileWidth()) * layer.getTileWidth());
                bounds.setY((int) (bounds.getY() / layer.getTileHeight()) * layer.getTileHeight());
                bounds.setWidth((int) (bounds.getWidth() / layer.getTileWidth()) * layer.getTileWidth());
                bounds.setHeight((int) (bounds.getHeight() / layer.getTileHeight()) * layer.getTileHeight());

                if (object.getProperties().containsKey("dollbounds"))
                    dollBounds.add(bounds);
                if (object.getProperties().containsKey("bfishbounds"))
                    bfishBounds.add(bounds);
                if (object.getProperties().containsKey("jfishbounds"))
                    jfishBounds.add(bounds);
                if (object.getProperties().containsKey("birdVbounds"))
                    birdVBounds.add(bounds);
                if (object.getProperties().containsKey("birdHbounds"))
                    birdHBounds.add(bounds);
                if (object.getProperties().containsKey("zombiebounds"))
                    zombieBounds.add(bounds);
                if (object.getProperties().containsKey("activator"))
                    activators.add(bounds);
            }
        }

        //Same for the platforms.
        for (MapObject object : map.getLayers().get("Platform Bounds").getObjects()) {
            if (object instanceof RectangleMapObject) {
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
                Rectangle bounds = ((RectangleMapObject) object).getRectangle();
                bounds.setX((int) (bounds.getX() / layer.getTileWidth()) * layer.getTileWidth());
                bounds.setY((int) (bounds.getY() / layer.getTileHeight()) * layer.getTileHeight());
                bounds.setWidth((int) (bounds.getWidth() / layer.getTileWidth()) * layer.getTileWidth());
                bounds.setHeight((int) (bounds.getHeight() / layer.getTileHeight()) * layer.getTileHeight());

                if (object.getProperties().containsKey("platform1bounds"))
                    platform1Bounds.add(bounds);
                if (object.getProperties().containsKey("platform2bounds"))
                    platform2Bounds.add(bounds);
            }
        }

        //Adds all the enemies according to the spawn type.
        for (MapObject object : map.getLayers().get("Spawns").getObjects()) {
            Creature creature = null;

            if (object instanceof RectangleMapObject) {
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float oX = rect.x + rect.getWidth() / 2;
                float oY = (int) ((rect.y + rect.getHeight()) / layer.getTileHeight()) * layer.getTileHeight();

                if (object.getProperties().containsKey("doll")) {
                    creature = new MudDoll(screen, atlases.get(1), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY, dollBounds.get(dollCount), dollBounds.get(dollCount + 1));
                    dollCount += 2;
                }

                if (object.getProperties().containsKey("charger"))
                    creature = new Charger(screen, atlases.get(2), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (object.getProperties().containsKey("bat"))
                    creature = new Bat(screen, atlases.get(3), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (object.getProperties().containsKey("slinger"))
                    creature = new MudSlinger(screen, atlases.get(4), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (object.getProperties().containsKey("jumper"))
                    creature = new Jumper(screen, atlases.get(5), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (object.getProperties().containsKey("bfish")) {
                    creature = new BasicFish(screen, atlases.get(6), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY, bfishBounds.get(bfishCount), bfishBounds.get(bfishCount + 1));
                    bfishCount += 2;
                }

                if (object.getProperties().containsKey("head"))
                    creature = new FloatingHead(screen, atlases.get(7), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (object.getProperties().containsKey("roller"))
                    creature = new Roller(screen, atlases.get(8), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (object.getProperties().containsKey("jfish")) {
                    creature = new Jellyfish(screen, atlases.get(9), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY, jfishBounds.get(jfishCount), jfishBounds.get(jfishCount + 1));
                    jfishCount += 2;
                }

                if (object.getProperties().containsKey("watershooter"))
                    creature = new WaterShooter(screen, atlases.get(10), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (object.getProperties().containsKey("birdV")) {
                    creature = new Bird(screen, atlases.get(11), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY, birdVBounds.get(birdVCount), birdVBounds.get(birdVCount + 1), true);
                    birdVCount += 2;
                }

                if (object.getProperties().containsKey("birdH")) {
                    creature = new Bird(screen, atlases.get(11), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY, birdHBounds.get(birdHCount), birdHBounds.get(birdHCount + 1), false);
                    birdHCount += 2;
                }

                if (object.getProperties().containsKey("zombies")) {
                    creature = new MudZombieSpawner(screen, atlases.get(12), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY, zombieBounds.get(zombieCount), zombieBounds.get(zombieCount + 1));
                    jfishCount += 2;
                }

                if (object.getProperties().containsKey("golem"))
                    creature = new MudGolem(screen, atlases.get(13), (TiledMapTileLayer) map.getLayers().get(2), map, oX, oY);

                if (creature != null) screen.creatures.add(creature);
            }
        }

        //Same for destructibles.
        for (MapObject object : map.getLayers().get("Destructibles").getObjects()) {
            Destructible destructible = null;

            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
                float oX = (int) (rect.x / layer.getTileWidth()) * layer.getTileWidth();
                float oY = (int) (rect.y / layer.getTileHeight()) * layer.getTileHeight();

                //Note crates and boxes were NOT implemented.
                if (object.getProperties().containsKey("crate"))
                    destructible = new Crate(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                if (object.getProperties().containsKey("steelcrate"))
                    destructible = new SteelCrate(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                if (object.getProperties().containsKey("boxsmall"))
                    destructible = new Box(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                if (object.getProperties().containsKey("box"))
                    destructible = new BoxLarge(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                if (object.getProperties().containsKey("raft")) {
                    destructible = new Raft(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                    if (object.getProperties().containsKey("connector"))
                        connectors.add(destructible);
                }
                if (object.getProperties().containsKey("cart")) {
                    destructible = new Cart(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                    if (object.getProperties().containsKey("connector"))
                        connectors.add(destructible);
                }
                if (object.getProperties().containsKey("gate")) {
                    destructible = new Gate(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                    if (object.getProperties().containsKey("connector"))
                        connectors.add(destructible);
                }
                if (object.getProperties().containsKey("spiker")) {
                    destructible = new Spiker(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen);
                    if (object.getProperties().containsKey("connector"))
                        connectors.add(destructible);
                }

                if (object.getProperties().containsKey("spikeblockdown")) {
                    destructible = new SpikeBlock(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen, 0, activators.get(activatorCount));
                    activatorCount++;
                    blocks.add((SpikeBlock) destructible);
                }
                if (object.getProperties().containsKey("spikeblockright")) {
                    destructible = new SpikeBlock(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen, 1, activators.get(activatorCount));
                    activatorCount++;
                    blocks.add((SpikeBlock) destructible);
                }
                if (object.getProperties().containsKey("spikeblockleft")) {
                    destructible = new SpikeBlock(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen, 2, activators.get(activatorCount));
                    activatorCount++;
                    blocks.add((SpikeBlock) destructible);
                }
                if (object.getProperties().containsKey("spikeblockup")) {
                    destructible = new SpikeBlock(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen, 3, activators.get(activatorCount));
                    activatorCount++;
                    blocks.add((SpikeBlock) destructible);
                }
            }

            if (destructible != null) {
                screen.destructibles.add(destructible);
                screen.dManager.add(destructible);
            }
        }

        //Adds all the levers and links them to the connectors.
        int connectorCount = 0;
        for (MapObject object : map.getLayers().get("Destructibles").getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float oX = rect.x + rect.getWidth() / 2;
                float oY = rect.y + rect.getHeight() / 2;
                if (object.getProperties().containsKey("lever")) {
                    Lever lever = new Lever(oX, oY, oAtlas, (TiledMapTileLayer) map.getLayers().get(2), screen, connectors.get(connectorCount));
                    screen.destructibles.add(lever);
                    connectorCount ++;
                }
            }
        }

        //Adds the slopes to the pixel knight.
        for (MapObject object : map.getLayers().get("Slopes").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;

                if (object.getProperties().containsKey("sloperight"))
                    pk.addSlope(true, rectObject);

                if (object.getProperties().containsKey("slopeleft"))
                    pk.addSlope(false, rectObject);
            }

        //Adds the platforms to the game.
        for (MapObject object : map.getLayers().get("Platforms").getObjects()) {
            Platform platform = null;

            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
                float oX = (int) (rect.x / layer.getTileWidth()) * layer.getTileWidth();
                float oY = (int) (rect.y / layer.getTileHeight()) * layer.getTileHeight();

                if (object.getProperties().containsKey("platform1")) {
                    platform = new Platform1(screen, wAtlases.get(0), (TiledMapTileLayer) map.getLayers().get(2), oX, oY, platform1Bounds.get(platform1Count), platform1Bounds.get(platform1Count + 1));
                    platform1Count += 2;
                }

                if (object.getProperties().containsKey("platform2")) {
                    platform = new Platform2(screen, wAtlases.get(0), (TiledMapTileLayer) map.getLayers().get(2), oX, oY, platform2Bounds.get(platform2Count), platform2Bounds.get(platform2Count + 1));
                    platform2Count += 2;
                }
            }
            if (platform != null) screen.platforms.add(platform);
        }

        //Adds the gems to the game.
        for (MapObject object : map.getLayers().get("Gems").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);
                float oX = ((int) (rect.x / layer.getTileWidth()) + 1) * layer.getTileWidth() - 7;
                float oY = ((int) (rect.y / layer.getTileHeight()) + 1) * layer.getTileHeight() - layer.getTileHeight() / 2;

                if (object.getProperties().containsKey("emerald"))
                    screen.gems.add(new Emerald(oX, oY, uAtlases.get(2), (TiledMapTileLayer) map.getLayers().get(2), screen));
                if (object.getProperties().containsKey("ruby"))
                    screen.gems.add(new Ruby(oX, oY, uAtlases.get(2), (TiledMapTileLayer) map.getLayers().get(2), screen));
                if (object.getProperties().containsKey("sapphire"))
                    screen.gems.add(new Sapphire(oX, oY, uAtlases.get(2), (TiledMapTileLayer) map.getLayers().get(2), screen));
                if (object.getProperties().containsKey("diamond"))
                    screen.gems.add(new Diamond(oX, oY, uAtlases.get(2), (TiledMapTileLayer) map.getLayers().get(2), screen));
            }

        setLevel();
        lScreen.loadGame();
    }

}
