
package com.inoculates.knight.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;
import com.inoculates.knight.UI.Options;
import com.inoculates.knight.UI.OverworldKnight;

public class OverworldScreen implements Screen {
    Sprite overworld;

    SpriteBatch batch;
    Game game;

    boolean transitioningOut = false, pressed = false;
    float time = 0, fadeTime = 1;
    float widthRatio, heightRatio;
    int node, maxNodes;
    OverworldKnight oK;
    Storage storage;
    Options options;
    Rectangle optionBounds;
    Label levelText;
    ShaderProgram fontShader;

    private Sound button1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select.wav")),
            button2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select2.wav")), button3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select3.wav"));

    private Sound oScreenMusic = Gdx.audio.newSound(Gdx.files.internal("data/sounds/OverworldTheme.wav"));

    private Array<Sound> sounds = new Array<Sound>();

    public OverworldScreen(Game game, Storage storage) {
		this.game = game;
        this.storage = storage;
        sounds = new Array<Sound>();
        sounds.addAll(button1, button2, button3);
    }

	@Override
	public void show () {
        overworld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/overworlds/peninsula.png")), 0, 0, 400, 300));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 400, 300);
        AssetManager manager = new AssetManager();
        manager.load("data/pixelknight/pixelknight.pack", TextureAtlas.class);
        manager.load("data/ui/options.pack", TextureAtlas.class);
        manager.load("data/ui/go.pack", TextureAtlas.class);
        manager.finishLoading();
        oK = new OverworldKnight(27.5f, 140, (TextureAtlas) manager.get("data/pixelknight/pixelknight.pack"));
        options = new Options(0, 290, (TextureAtlas) manager.get("data/ui/options.pack"), null);
        pressed = false;
        node = storage.level;
        maxNodes = storage.mLevel;
        storage.updatePreferences();
        oScreenMusic.loop((float) storage.soundM / 100);
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        overworld.draw(batch);
        oK.draw(batch);
        if (fadeTime == 1)
            batch.setShader(fontShader);
        levelText.draw(batch, fadeTime);
        levelText.setColor(0, 0, 0, 1);
        batch.setShader(null);
        options.draw(batch);
        overworld.setAlpha(fadeTime);
        oK.setAlpha(fadeTime);
        oK.update(delta);
        options.setAlpha(fadeTime);
        batch.end();
        time += delta;
        if (fadeTime > 0.01 && transitioningOut)
        fadeTime -= delta / 2;
        if (time > 0.5)
            processKeys();
        if (Gdx.input.isTouched())
            checkInput(Gdx.input.getX(), Gdx.input.getY());
        setRectangles();
    }

    //Sets the knight on the map's position according to the node he should be on.
    private void setPosition() {
        switch (node) {
            case 0:
                oK.setPosition(11, 105);
                break;
            case 1:
                oK.setPosition(83, 122.5f);
                break;
            case 2:
                oK.setPosition(30, 145);
                break;
            case 3:
                oK.setPosition(125, 97.5f);
                break;
            case 4:
                oK.setPosition(227.5f, 50);
                break;
            case 5:
                oK.setPosition(190, 117.5f);
                break;
            case 6:
                oK.setPosition(257.5f, 147.5f);
                break;
            case 7:
                oK.setPosition(317.5f, 182.5f);
                break;
            case 8:
                oK.setPosition(215, 212.5f);
                break;
            case 9:
                oK.setPosition(97.5f, 202.5f);
                break;
        }
        setLabels();
    }

    //Moves right depending on the node the knight is on.
    private void initiateMovementRight() {
        switch (node) {
            case 0:
                if (maxNodes < 1)
                    break;
                oK.setVelocity(0.5f, 0);
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 1.25f);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setY(122.5f);
                        oK.setVelocity(0.5f, 0);
                    }
                }, 1.9f);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 3.05f);
                timer.start();
                node++;
                break;
            case 2:
                oK.setVelocity(0.5f, 0);
                Timer timer3 = new Timer();
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, -0.5f);
                    }
                }, 1.8f);
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 2.6f);
                timer3.start();
                node --;
                break;
            case 3:
                if (maxNodes < 5)
                    break;
                oK.setVelocity(0.5f, 0);
                Timer timer4 = new Timer();
                timer4.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 2.2f);
                timer4.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 2.8f);
                timer4.start();
                node += 2;
                break;
            case 5:
                if (maxNodes < 6)
                    break;
                oK.setVelocity(0.5f, 0);
                Timer timer5 = new Timer();
                timer5.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 2.35f);
                timer5.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 3.25f);
                timer5.start();
                node ++;
                break;
            case 6:
                if (maxNodes < 7)
                    break;
                oK.setVelocity(0.5f, 0);
                Timer timer6 = new Timer();
                timer6.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 2.05f);
                timer6.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 3.25f);
                timer6.start();
                node ++;
                break;
            case 8:
                oK.setVelocity(0.5f, 0);
                Timer timer7 = new Timer();
                timer7.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, -0.5f);
                    }
                }, 0.95f);
                timer7.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0.5f, 0);
                    }
                }, 1.85f);
                timer7.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 4.3f);
                timer7.start();
                node --;
                break;
        }

    }

    //Moves left depending on the node the knight is on.
    private void initiateMovementLeft() {
        switch (node) {
            case 1:
                oK.setVelocity(-0.5f, 0);
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, -0.5f);
                    }
                }, 1.25f);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setY(105);
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 1.79f);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 3.05f);
                timer.start();
                node --;
                break;
            case 3:
                oK.setVelocity(-0.5f, 0);
                Timer timer2 = new Timer();
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 1.4f);
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 2.25f);
                timer2.start();
                node -= 2;
                break;
            case 7:
                if (maxNodes < 8)
                    break;
                oK.setVelocity(-0.5f, 0);
                Timer timer3 = new Timer();
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 2.45f);
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 3.35f);
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 4.3f);
                timer3.start();
                node ++;
                break;
        }
    }

    //Moves up depending on the node the knight is on.
    private void initiateMovementUp() {
        switch (node) {
            case 1:
                if (maxNodes < 2)
                    break;
                oK.setVelocity(0, 0.5f);
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 0.8f);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 2.5f);
                timer.start();
                node ++;
                break;
            case 4:
                oK.setVelocity(0, 0.5f);
                Timer timer2 = new Timer();
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 0.35f);
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 3.8f);
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 5f);
                timer2.start();
                node --;
                break;
        }
    }

    //Moves down depending on the node the knight is on.
    private void initiateMovementDown() {
        switch (node) {
            case 1:
                if (maxNodes < 3)
                    break;
                oK.setVelocity(0, -0.5f);
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0.5f, 0);
                    }
                }, 0.85f);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 2.25f);
                timer.start();
                node += 2;
                break;
            case 3:
                if (maxNodes < 4)
                    break;
                oK.setVelocity(0, -0.5f);
                Timer timer2 = new Timer();
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0.5f, 0);
                    }
                }, 1.2f);
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, -0.5f);
                    }
                }, 4.65f);
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 5f);
                timer2.start();
                node ++;
                break;
            case 5:
                oK.setVelocity(0, -0.5f);
                Timer timer3 = new Timer();
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 0.6f);
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 2.8f);
                timer3.start();
                node -= 2;
                break;
            case 6:
                oK.setVelocity(0, -0.5f);
                Timer timer4 = new Timer();
                timer4.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 0.9f);
                timer4.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 3.25f);
                timer4.start();
                node --;
                break;
            case 7:
                oK.setVelocity(0, -0.5f);
                Timer timer5 = new Timer();
                timer5.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 1.2f);
                timer5.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 3.25f);
                timer5.start();
                node --;
                break;
            case 8:
                if (maxNodes < 9)
                    break;
                oK.setVelocity(0, -0.5f);
                Timer timer6 = new Timer();
                timer6.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(-0.5f, 0);
                    }
                }, 1.2f);
                timer6.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 5.1f);
                timer6.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 6.1f);
                timer6.start();
                node ++;
                break;
            case 9:
                oK.setVelocity(0, -0.5f);
                Timer timer7 = new Timer();
                timer7.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0.5f, 0);
                    }
                }, 1);
                timer7.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0.5f);
                    }
                }, 4.9f);
                timer7.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        oK.setVelocity(0, 0);
                        setPosition();
                    }
                }, 6.1f);
                timer7.start();
                node --;
                break;
        }

    }

    //Goes into the level using the loading screen.
    private void enterLevel() {
        if (transitioningOut)
            return;
        oScreenMusic.stop();
        transitioningOut = true;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new LoadingScreen(game, storage));
            }
        }, 2);
        timer.start();
    }

    //Moves in the directionc oreesponding to the arrow key presed.
    private void processKeys() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && oK.vel.x == 0 && oK.vel.y == 0) {
            oScreenMusic.stop();
            game.setScreen(new PausedScreenOverworld(game, storage, this));
        }
        if (oK.vel.x != 0 || oK.vel.y != 0 || transitioningOut)
            return;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            initiateMovementRight();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            initiateMovementLeft();
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            initiateMovementUp();
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            initiateMovementDown();
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            enterLevel();
        }
        storage.setLevel(node);
    }

	@Override
	public void hide () {
        overworld.getTexture().dispose();
	}

    @Override
    public void resize (int width, int height) {
        if (storage.cam != null) {
            storage.cam.viewportWidth = width / 1.8f;
            storage.cam.viewportHeight = height / 1.8f;
            storage.setWidthRatio(storage.cam.viewportWidth / 355.55557f);
            storage.setHeightRatio(storage.cam.viewportHeight / 266.6667f);
            widthRatio = storage.widthRatio;
            heightRatio = storage.heightRatio;
        }
        else {
            float viewportWidth = width / 1.8f;
            float viewportHeight = height / 1.8f;
            widthRatio = viewportWidth / 355.55557f;
            heightRatio = viewportHeight / 266.6667f;
        }
        Texture texture = new Texture(Gdx.files.internal("data/fonts/main3.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);        float viewportWidth = width / 1.8f;
        float viewportHeight = height / 1.8f;
        widthRatio = viewportWidth / 355.55557f;
        heightRatio = viewportHeight / 266.6667f;

        BitmapFont font = new BitmapFont(Gdx.files.internal("data/fonts/main3.fnt"), region, false);
        font.setScale(widthRatio, heightRatio);
        font.setUseIntegerPositions(false);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        levelText = new Label("", style);
        levelText.setPosition(5, 10);
        fontShader = new ShaderProgram(Gdx.files.internal("data/fonts/font.vert"), Gdx.files.internal("data/fonts/font.frag"));
        setPosition();
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        overworld.getTexture().dispose();
    }

    //Checks if the option button is pressed.
    private void checkInput(float x, float y) {
        if (pressed) return;
        final OverworldScreen screen = this;
        if (optionBounds != null && optionBounds.contains(x, y) && oK.vel.x == 0 && oK.vel.y == 0) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            oScreenMusic.stop();

            options.selected();
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(new PausedScreenOverworld(game, storage, screen));
                }
            }, 0.2f);
            pressed = true;
            timer.start();
        }
    }

    //Sets the overworld text depending on the node the knight is on.
    private void setLabels() {
        switch (node) {
            case 0:
                levelText.setText("Level One - Muddy Fields");
                break;
            case 1:
                levelText.setText("Level Two - Metal Fortress");
                break;
            case 2:
                levelText.setText("Level Three - Leafy Treetops");
                break;
            case 3:
                levelText.setText("Level Four - Red Tower");
                break;
            case 4:
                levelText.setText("Level Five - Ancient Bridge");
                break;
            case 5:
                levelText.setText("Level Six - Dark Cave");
                break;
            case 6:
                levelText.setText("Level Seven - Lake Dron");
                break;
            case 7:
                levelText.setText("Level Eight - Machu Falls");
                break;
            case 8:
                levelText.setText("Level Nine - Rolling Hills");
                break;
            case 9:
                levelText.setText("Level Ten - Final Gate");
                break;
        }
    }

    private void setRectangles() {
        optionBounds = new Rectangle(0, 0, 90 * widthRatio, 20 * heightRatio);
    }

}
