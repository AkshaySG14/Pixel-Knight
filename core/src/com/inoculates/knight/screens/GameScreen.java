
package com.inoculates.knight.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;
import com.inoculates.knight.UI.*;
import com.inoculates.knight.objects.Destructible;
import com.inoculates.knight.projectiles.Projectile;
import com.inoculates.knight.effects.*;
import com.inoculates.knight.enemies.Creature;
import com.inoculates.knight.gems.*;
import com.inoculates.knight.levels.World;
import com.inoculates.knight.pixelknight.DmgNotif;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.platforms.Platform;

import java.util.Iterator;


public class GameScreen implements Screen {
    //These objects are involved in the rendering of the map, except for the storage object, which stores information.
    public Batch batch;
    public OrthographicCamera camera;
    public TiledMap map;
    public OrthogonalTiledMapRenderer renderer;
    public Storage storage;

    //The UI for the game screen.
    public Health healthUI;
    public Power powerUI;
    public Gems gemUI;
    public Lives lifeUI;
    public PixelKnightDisplay pKDUI;
    public PowerDisplay pDUI;
    public ShaderProgram fontShader;
    public Label gemLabel, lifeLabel;
    public Options options;

    //These are the moving sprites involved in the game, which take care of their own actions.
    public PixelKnight pKnight;
    public Array<Creature> creatures = new Array<Creature>();
    public Array<DmgNotif> dmg = new Array<DmgNotif>();
    public Array<Gem> gems = new Array<Gem>();
    public Array<Destructible> destructibles = new Array<Destructible>();
    public Array<Destructible> dManager = new Array<Destructible>();
    public Array<Projectile> projectiles = new Array<Projectile>();
    public Array<Platform> platforms = new Array<Platform>();
    public Array<Effect> effects = new Array<Effect>();
    public Array<UI> ui = new Array<UI>();

    //All textures used for frames and animations.
    public Array<TextureAtlas> atlases = new Array<TextureAtlas>();
    public Array<TextureAtlas> uAtlases = new Array<TextureAtlas>();
    public Array<TextureAtlas> wAtlases = new Array<TextureAtlas>();
    public TextureAtlas oAtlas = new TextureAtlas();

    //The world handles the generation of objects and sprites. The game determines which screen the player sees.
    //The loading screen is used to mask the actual loading of the game screen, while the backgrounds and sky are used
    //to fill out the screen's background. The bounds are used for camera bounds, with the exception of the option bounds,
    //which is used by the player to enter the options screen.
    private World world;
    private Game game;
    private LoadingScreen screen;
    private Background backgroundS, backgroundS2;
    private Sky sky;
    public Rectangle leftBounds, rightBounds, lowerBounds, optionBounds;

    //The y-position of the camera, which does not change periodically like the x-position does.
    private float camY;

    //This boolean stops the controls screen initiation from occurring too much.
    private boolean selected = false;

    //These are the layers of the tiledmap.
    private int[] objects = new int[] {0}, foreground = new int[] {1}, background = new int[] {2}, wallpaper = new int[] {3};

    //Sounds of the buttons.
    private Sound button1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select.wav")),
            button2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select2.wav")), button3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select3.wav"));

    //Music involved in the game.
    private Sound gameMusic;

    public GameScreen (Game game, LoadingScreen screen, Storage storage) {
		this.game = game;
        this.storage = storage;
        storage.setMainScreen(this);
        this.screen = screen;
	}

    //Sets the camera positions if one of the camera vertices goes off bounds.
    private void setCameraBounds() {
        if (camera.position.x - camera.viewportWidth / 2 < leftBounds.getX())
            camera.position.set(leftBounds.getX() + camera.viewportWidth / 2, camY, 1);
        if (pKnight.getX() + camera.viewportWidth / 2 > rightBounds.getX())
            camera.position.set(rightBounds.getX() - camera.viewportWidth / 2, camY, 1);
        if (camera.position.y - camera.viewportHeight / 2 < lowerBounds.getY())
            camera.position.set(camera.position.x, lowerBounds.getY() + camera.viewportHeight / 2, 1);
    }

    //Sets the camera bounds, plays the game music, and reroutes the input to the pixel knight
	@Override
	public void show () {
        for (MapObject object : map.getLayers().get("Camera Bounds").getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                if (object.getProperties().containsKey("leftbounds"))
                    leftBounds = rect;
                else if (object.getProperties().containsKey("rightbounds"))
                    rightBounds = rect;
                else if (object.getProperties().containsKey("lowerbounds"))
                    lowerBounds = rect;
            }
        }
        Gdx.input.setInputProcessor(pKnight);

        gameMusic.loop((float) storage.soundM / 100);
        selected = false;
    }

    //Renders all objects, and updates them.
	@Override
	public void render (float delta) {
        update(delta);
        setPositions();
        camera.update();
        world.level.update();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        backgroundS.draw(batch);
        backgroundS2.draw(batch);
        sky.draw(batch);
        batch.end();
        drawTiles();
        batch.begin();
        batch.setShader(fontShader);
        gemLabel.draw(batch, 1);
        lifeLabel.draw(batch, 1);
        batch.setShader(null);
        for (Creature creature : creatures)
            if (checkDraw(creature))
                creature.draw(batch);
        for (Destructible destructible : destructibles) {
            if (destructible.getState() == 2)
                destructibles.removeValue(destructible, false);
            else if (checkDraw(destructible))
                destructible.draw(batch);
        }
        for (DmgNotif dam : dmg)
            if (checkDraw(dam))
                dam.draw(batch);
        for (Projectile projectile : projectiles) {
            if (projectile.getState() == 3)
                projectiles.removeValue(projectile, false);
            else if (checkDraw(projectile))
                projectile.draw(batch);
        }
        for (Effect effect : effects) {
            if (effect.getState() == 2)
                effects.removeValue(effect, false);
            else if (checkDraw(effect))
                effect.draw(batch);
        }
        for (UI uI : ui) {
            if (uI.getState() == 1)
                ui.removeValue(uI, false);
            else
                uI.draw(batch);
        }

        for (Gem gem : gems) {
            if (gem.getState() == 1)
                gems.removeValue(gem, false);
            else if (checkDraw(gem))
                gem.draw(batch);
        }
        for (Platform platform : platforms)
            if (checkDraw(platform))
                platform.draw(batch);

        pKnight.draw(batch);
        batch.end();
    }

    private void update(float deltaTime) {
        for (Creature creature : creatures)
        if (checkDraw(creature))
            creature.update(deltaTime);
        for (Destructible destructible : destructibles)
        if (checkDraw(destructible))
            destructible.update(deltaTime);
        for (Projectile projectile : projectiles)
        if (checkDraw(projectile))
            projectile.update(deltaTime);
        for (Effect effect : effects)
        if (checkDraw(effect))
            effect.update(deltaTime);
        for (Gem gem : gems)
        if (checkDraw(gem))
            gem.update(deltaTime);
        for (Platform platform : platforms)
            platform.update(deltaTime);
        for (UI uI : ui)
            uI.update(deltaTime);
        camera.position.set(pKnight.getPosX(), camY, 1);
        setCameraBounds();
        backgroundS.update(deltaTime);
        backgroundS2.update(deltaTime);
        sky.update(deltaTime);
        updateRectangles();
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) && optionBounds != null)
            checkOptions();
    }

	@Override
	public void hide () {
	}

    //This resizes the camera and window of the game, as well a resizing all UI elements accordingly, including fonts.
    @Override
    public void resize (int width, int height) {
        camera.viewportWidth = width / 1.8f;
        camera.viewportHeight = height / 1.8f;
        storage.setWidthRatio(camera.viewportWidth / 355.55557f);
        storage.setHeightRatio(camera.viewportHeight / 266.6667f);
        for (UI uI : ui)
            uI.setSize(uI.getSizeWidth() * storage.widthRatio, uI.getSizeHeight() * storage.heightRatio);
        Texture texture = new Texture(Gdx.files.internal("data/fonts/main.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        BitmapFont font = new BitmapFont(Gdx.files.internal("data/fonts/main.fnt"), region, false);
        font.setScale(storage.widthRatio, storage.heightRatio);
        font.setUseIntegerPositions(false);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        gemLabel.setStyle(style);
        lifeLabel.setSize(gemLabel.getTextBounds().width, gemLabel.getHeight());
        lifeLabel.setStyle(style);
        lifeLabel.setSize(lifeLabel.getTextBounds().width, lifeLabel.getHeight());
        backgroundS.setSize(backgroundS.getWidth(), camera.viewportHeight * 1.05f);
        backgroundS2.setSize(backgroundS.getWidth(), camera.viewportHeight * 1.05f);
        backgroundS.setY(lowerBounds.getY());
        backgroundS2.setY(lowerBounds.getY());
        camera.update();
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        map.dispose();
        renderer.dispose();
        pKnight.getTexture().dispose();
    }

    //This loads all the objects in the game, as well as the fonts of the game.
    public void generateWorld() {
        loadObjects();
        map = getMap();
        pKnight = new PixelKnight(atlases.get(0), (TiledMapTileLayer) map.getLayers().get(2), this, map);
        renderer = new OrthogonalTiledMapRenderer(map);
        camera = new OrthographicCamera();
        renderer.setView(camera);
        storage.setCamera(camera);

        healthUI = new Health(0, 0, uAtlases.get(1), this);
        ui.add(healthUI);
        gemUI = new Gems(0, 0, uAtlases.get(2));
        ui.add(gemUI);
        powerUI = new Power(0, 0, uAtlases.get(3), this);
        ui.add(powerUI);
        lifeUI = new Lives(0, 0, uAtlases.get(4));
        ui.add(lifeUI);
        pKDUI = new PixelKnightDisplay(0, 0, null, atlases.get(0).findRegion("idle1"));
        ui.add(pKDUI);
        pDUI = new PowerDisplay(0, 0, atlases.get(0));
        ui.add(pDUI);
        options = new Options(0, 0, uAtlases.get(5), this);
        ui.add(options);

        Texture texture = new Texture(Gdx.files.internal("data/fonts/main.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        BitmapFont font = new BitmapFont(Gdx.files.internal("data/fonts/main.fnt"), region, false);
        font.setUseIntegerPositions(false);
        Label.LabelStyle style2 = new Label.LabelStyle();
        style2.font = font;
        gemLabel = new Label(Integer.toString(storage.gems), style2);
        lifeLabel = new Label(Integer.toString(storage.lives), style2);
        fontShader = new ShaderProgram(Gdx.files.internal("data/fonts/font.vert"), Gdx.files.internal("data/fonts/font.frag"));

        batch = renderer.getSpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        world = new World(this, map, pKnight, atlases, wAtlases, screen);
        gameMusic = world.level.getMusic();
    }

    //This method finds all non-static tiles and animates them.
    private void animateTiles(String key, String section, String tileset, float time) {
        Array<StaticTiledMapTile> frameTiles = new Array<StaticTiledMapTile>();
        Iterator<TiledMapTile> tiles = map.getTileSets().getTileSet(tileset).iterator();

        while (tiles.hasNext()) {
            TiledMapTile tile = tiles.next();
            if (tile.getProperties().containsKey(key))
            frameTiles.add((StaticTiledMapTile) tile);
        }

        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(time, frameTiles);
        for (TiledMapTile tile : frameTiles)
            animatedTile.getProperties().putAll(tile.getProperties());

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(section);

        for (int i = 0; i < layer.getWidth(); i++) {
            for (int o = 0; o < layer.getHeight(); o++) {
                TiledMapTileLayer.Cell cell = layer.getCell(i, o);
                if (cell != null && cell.getTile().getProperties().containsKey(key))
                    cell.setTile(animatedTile);
            }
        }
    }

    //Loads all the textures the game requires.
    private void loadObjects() {
        AssetManager manager = new AssetManager();
        manager.load("data/pixelknight/pixelknight.pack", TextureAtlas.class);
        manager.load("data/enemies/muddoll.atlas", TextureAtlas.class);
        manager.load("data/ui/dmgpack.atlas", TextureAtlas.class);
        manager.load("data/enemies/charger.pack", TextureAtlas.class);
        manager.load("data/enemies/bat.pack", TextureAtlas.class);
        manager.load("data/enemies/mudslinger.pack", TextureAtlas.class);
        manager.load("data/enemies/jumper.pack", TextureAtlas.class);
        manager.load("data/enemies/basicfish.pack", TextureAtlas.class);
        manager.load("data/enemies/floatinghead.pack", TextureAtlas.class);
        manager.load("data/enemies/roller.pack", TextureAtlas.class);
        manager.load("data/enemies/jellyfish.pack", TextureAtlas.class);
        manager.load("data/enemies/mudgolem.pack", TextureAtlas.class);
        manager.load("data/enemies/mudzombie.pack", TextureAtlas.class);
        manager.load("data/enemies/watershooter.pack", TextureAtlas.class);
        manager.load("data/enemies/bird.pack", TextureAtlas.class);
        manager.load("data/levels/mudworld.pack", TextureAtlas.class);
        manager.load("data/levels/waterfall.pack", TextureAtlas.class);
        manager.load("data/ui/health.pack", TextureAtlas.class);
        manager.load("data/ui/gems.pack", TextureAtlas.class);
        manager.load("data/ui/power.pack", TextureAtlas.class);
        manager.load("data/ui/lives.pack", TextureAtlas.class);
        manager.load("data/ui/gems.pack", TextureAtlas.class);
        manager.load("data/ui/options.pack", TextureAtlas.class);
        manager.load("data/destructibles/objects.pack", TextureAtlas.class);
        manager.finishLoading();

        atlases.add((TextureAtlas) manager.get("data/pixelknight/pixelknight.pack"));

        atlases.add((TextureAtlas) manager.get("data/enemies/muddoll.atlas"));
        atlases.add((TextureAtlas) manager.get("data/enemies/charger.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/bat.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/mudslinger.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/jumper.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/basicfish.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/floatinghead.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/roller.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/jellyfish.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/watershooter.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/bird.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/mudzombie.pack"));
        atlases.add((TextureAtlas) manager.get("data/enemies/mudgolem.pack"));

        oAtlas = manager.get("data/destructibles/objects.pack");

        uAtlases.add((TextureAtlas) manager.get("data/ui/dmgpack.atlas"));
        uAtlases.add((TextureAtlas) manager.get("data/ui/health.pack"));
        uAtlases.add((TextureAtlas) manager.get("data/ui/gems.pack"));
        uAtlases.add((TextureAtlas) manager.get("data/ui/power.pack"));
        uAtlases.add((TextureAtlas) manager.get("data/ui/lives.pack"));
        uAtlases.add((TextureAtlas) manager.get("data/ui/options.pack"));

        wAtlases.add((TextureAtlas) manager.get("data/levels/mudworld.pack"));
        wAtlases.add((TextureAtlas) manager.get("data/levels/waterfall.pack"));
    }

    //Sets the positions of all UI elements perodically.
    private void setPositions() {
        healthUI.setPosition(camera.position.x - healthUI.getWidth() / 2, camera.position.y + camera.viewportHeight / 2 - healthUI.getHeight());
        gemUI.setPosition(camera.position.x - camera.viewportWidth / 4, camera.position.y + camera.viewportHeight / 2 - gemUI.getHeight());
        gemLabel.setPosition(gemUI.getX() + gemUI.getWidth() * 1.2f, gemUI.getY() + camera.viewportHeight / 40 - gemLabel.getHeight() / 2);
        gemLabel.setText(Integer.toString(storage.gems));
        powerUI.setPosition(camera.position.x + camera.viewportWidth / 2 - powerUI.getWidth(), camera.position.y + camera.viewportHeight / 2 - powerUI.getHeight());
        lifeUI.setPosition(camera.position.x - camera.viewportWidth / 2 + lifeUI.getWidth() * 2, gemUI.getY() + lifeUI.getHeight() / 8);
        lifeLabel.setPosition(lifeUI.getX() + lifeUI.getWidth() * 1.2f, gemUI.getY() + camera.viewportHeight / 40 - gemLabel.getHeight() / 2);
        lifeLabel.setText(Integer.toString(storage.lives));
        options.setPosition(camera.position.x + camera.viewportWidth / 6, camera.position.y + camera.viewportHeight / 2 - options.getHeight());
        pKDUI.setPosition(camera.position.x - camera.viewportWidth / 2 + lifeUI.getWidth() / 1.3f, camera.position.y + camera.viewportHeight / 2 - pKDUI.getHeight() * 1.01f);
        if (pDUI.shape == 0)
            pDUI.setPosition(powerUI.getX() + powerUI.getWidth() / 1.55f, powerUI.getY() + powerUI.getHeight() / 4);
        else if (pDUI.shape == 1)
            pDUI.setPosition(powerUI.getX() + powerUI.getWidth() / 1.525f, powerUI.getY() + powerUI.getHeight() / 8);
    }

    //Checks whether the game options button has been pressed.
    private void checkOptions() {
        if ((optionBounds.contains(Gdx.input.getX(), Gdx.input.getY()) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) && !selected) {
            Array<Sound> sounds = new Array<Sound>();
            sounds.addAll(button1, button2, button3);
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            selected = true;

            options.selected();
            gameMusic.stop();
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(new PausedScreenGame(game, storage));
                }
            }, 0.2f);
            timer.start();
        }
    }

    //Sets the option bounds periodically.
    private void updateRectangles() {
        optionBounds = new Rectangle(430 * storage.widthRatio, 0, 90 * storage.widthRatio, 20 * storage.heightRatio);
    }

    public boolean exitLevel() {
        return world.level.exitLevel();
    }

    //If the max level is the current level, this method increases the max level.
    public void nextLevel() {
        if (storage.level == storage.mLevel) {
            storage.setMLevel(storage.mLevel + 1);
        }
        gameMusic.stop();
        game.setScreen(new EndLevelScreen(game, storage));
    }

    //Checks if the object is in the screen, so that it may be drawn. This reduces lag of the game.
    private boolean checkDraw(Sprite sprite) {
        float posX = camera.position.x, posY = camera.position.y;
        float width = camera.viewportWidth, height = camera.viewportHeight;
        return sprite.getX() > posX - width && sprite.getX() + sprite.getWidth() < posX + width && sprite.getY() > posY - height && sprite.getY() + sprite.getHeight() < posY + height;
    }

    //This draws all the tiles, as well as animates them depending on the camera's view.
    private void drawTiles() {
        renderer.setView(camera);
        renderer.render(objects);
        renderer.render(foreground);
        renderer.render(background);
        renderer.render(wallpaper);

        animateTiles("torch", "Foreground", "mudworld", 0.25f);
        animateTiles("torchS", "Foreground", "mudworld", 0.25f);
        if (storage.level == 7)
            animateTiles("waterfall", "Background", "waterfall", 0.33333f);
        animateTiles("wave", "Background", "mudworld", 0.25f);
    }

    public void setCamY(float y) {
        camY = y;
    }

    public float getCamY() {
        return camY;
    }

    public Background getS1() {
        return backgroundS;
    }

    //This sets the position of the backgrounds.
    public void setBackgrounds(TextureRegion background, TextureRegion sky) {
        backgroundS = new Background(pKnight.getX(), pKnight.getY(), background, (TiledMapTileLayer) map.getLayers().get(2), this, null);
        backgroundS2 = new Background(pKnight.getX(), pKnight.getY(), background, (TiledMapTileLayer) map.getLayers().get(2), this, backgroundS);
        this.sky = new Sky(pKnight.getX(), pKnight.getY(), sky, (TiledMapTileLayer) map.getLayers().get(2), this);
    }

    //Obtains the tiledmap depending on the level.
    private TiledMap getMap() {
        TmxMapLoader loader = new TmxMapLoader();
        switch (storage.level) {
            case 0:
                return loader.load("data/tilemaps/L1.tmx");
            case 1:
                return loader.load("data/tilemaps/L2.tmx");
            case 2:
                return loader.load("data/tilemaps/L3.tmx");
            case 3:
                return loader.load("data/tilemaps/L4.tmx");
            case 4:
                return loader.load("data/tilemaps/L5.tmx");
            case 5:
                return loader.load("data/tilemaps/L6.tmx");
            case 6:
                return loader.load("data/tilemaps/L7.tmx");
            case 7:
                return loader.load("data/tilemaps/L8.tmx");
            case 8:
                return loader.load("data/tilemaps/L9.tmx");
            case 9:
                return loader.load("data/tilemaps/L10.tmx");
        }
        return null;
    }

    //Music is stopped and the game informs the player he lost.
    public void gameOver() {
        gameMusic.stop();
        game.setScreen(new LoseGameScreen(game, storage));
    }

    public void knightDeath() {
        world.level.setKnightPosition();
    }
}
