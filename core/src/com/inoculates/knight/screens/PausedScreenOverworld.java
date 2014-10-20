
package com.inoculates.knight.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;

public class PausedScreenOverworld implements Screen {
    SpriteBatch batch;
    Game game;
    Storage storage;
    OverworldScreen screen;

    boolean transitioningOut = false;
    boolean buttonPressed = false;
    boolean isNew = true;
    int level;
    float time = 0, fadeTime = 1;
    float widthRatio, heightRatio;
    long soundID;

    Rectangle goTitleScreen;
    Rectangle controls;
    Rectangle back;
    Stage stage;
    Slider SFXSlider;
    Slider MSlider;
    Sprite paused;

    private Sound button1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select.wav")),
            button2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select2.wav")), button3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select3.wav"));

    private Sound pScreenMusic = Gdx.audio.newSound(Gdx.files.internal("data/sounds/PausedMusic.wav"));

    private Array<Sound> sounds = new Array<Sound>();

    public PausedScreenOverworld(Game game, Storage storage, OverworldScreen screen) {
        this.game = game;
        this.storage = storage;
        this.screen = screen;
        level = storage.level;
        sounds = new Array<Sound>();
        sounds.addAll(button1, button2, button3);
    }

    @Override
    public void show () {
        paused = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/optionscreenoverworld.png")), 0, 0, 800, 600));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(150, 100, 500, 400);
        AssetManager manager = new AssetManager();
        manager.finishLoading();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        setStage();
        setRectangles();
        buttonPressed = false;
        if (isNew)
            soundID = pScreenMusic.loop((float) storage.soundM / 100);
        isNew = false;
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        paused.draw(batch);
        paused.setAlpha(fadeTime);
        SFXSlider.setColor(1, 1, 1, fadeTime);
        MSlider.setColor(1, 1, 1, fadeTime);
        batch.end();
        stage.draw();
        stage.act(delta);
        time += delta;
        if (transitioningOut)
            fadeTime -= delta * 2;
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isTouched()) && !buttonPressed)
            chooseInput();

        setRectangles();
        updateStage();

        pScreenMusic.setVolume(soundID, (float) storage.soundM / 100);
    }

    @Override
    public void hide () {
        paused.getTexture().dispose();
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
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        paused.getTexture().dispose();
        stage.dispose();
    }

    private void chooseInput() {
        if (goTitleScreen.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            pScreenMusic.stop();

            buttonPressed = true;
            transitioningOut = true;
            paused.setRegion(new Texture(Gdx.files.internal("data/screens/optionscreenoverworldhighlighted1.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    paused.setRegion(new Texture(Gdx.files.internal("data/screens/optionscreenoverworld.png")));
                }
            }, 0.1f);
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    chooseTitleScreen();
                }
            }, 0.2f);
            timer.start();
        }
        if (controls.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);

            buttonPressed = true;
            paused.setRegion(new Texture(Gdx.files.internal("data/screens/optionscreenoverworldhighlighted2.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    paused.setRegion(new Texture(Gdx.files.internal("data/screens/optionscreenoverworld.png")));
                }
            }, 0.1f);
            final PausedScreenOverworld screen = this;
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(new ControlsScreenPC(game, storage, screen));
                }
            }, 0.2f);
            timer.start();
        }
        if (back.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            pScreenMusic.stop();

            buttonPressed = true;
            transitioningOut = true;
            paused.setRegion(new Texture(Gdx.files.internal("data/screens/optionscreenoverworldhighlighted3.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    paused.setRegion(new Texture(Gdx.files.internal("data/screens/optionscreenoverworld.png")));
                }
            }, 0.1f);
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(screen);
                }
            }, 0.2f);
            timer.start();
        }
    }

    private void chooseTitleScreen() {
        game.setScreen(new TitleScreen(game, storage));
    }

    private void setStage() {
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = new SpriteDrawable(new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/slider.png")))));
        style.knob = new SpriteDrawable(new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/knob.png")))));
        SFXSlider = new Slider(0, 100, 1, false, style);
        SFXSlider.setPosition(165 * storage.widthRatio, 150 * storage.heightRatio);
        SFXSlider.setSize(300 * storage.widthRatio, 50 * storage.heightRatio);
        SFXSlider.setValue(storage.soundE);
        SFXSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                storage.setSoundE((int) SFXSlider.getValue());
            }
        });
        MSlider = new Slider(0, 100, 1, false, style);
        MSlider.setPosition(165 * storage.widthRatio, 280 * storage.heightRatio);
        MSlider.setSize(300 * storage.widthRatio, 50 * storage.heightRatio);
        MSlider.setValue(storage.soundM);
        MSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                storage.setSoundM((int) MSlider.getValue());
            }
        });
        stage.addActor(SFXSlider);
        stage.addActor(MSlider);
    }

    private void updateStage() {
        SFXSlider.setPosition(165 * widthRatio, 150 * heightRatio);
        SFXSlider.setSize(300 * widthRatio, 50 * heightRatio);
        MSlider.setPosition(165 * widthRatio, 280 * heightRatio);
        MSlider.setSize(300 * widthRatio, 50 * heightRatio);
    }

    private void setRectangles() {
        goTitleScreen = new Rectangle(30 * widthRatio, 360 * heightRatio, 348 * widthRatio, 90 * heightRatio);
        controls = new Rectangle(453 * widthRatio, 360 * heightRatio, 155 * widthRatio, 77 * heightRatio);
        back = new Rectangle(18 * widthRatio, 22 * heightRatio, 82 * widthRatio, 76 * heightRatio);
    }
}
