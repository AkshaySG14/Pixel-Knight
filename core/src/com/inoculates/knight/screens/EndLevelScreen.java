
package com.inoculates.knight.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;

public class EndLevelScreen implements Screen {
    SpriteBatch batch;
    Game game;
    Storage storage;

    boolean transitioningOut = false;
    boolean buttonPressed = false;
    int level;
    float time = 0, fadeTime = 1;
    float widthRatio, heightRatio;

    Rectangle continueGame;
    Rectangle replay;
    Sprite endLevel;

    private Sound button1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select.wav")),
            button2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select2.wav")), button3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select3.wav"));

    private Sound eScreenMusic = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Victory.wav"));

    private Array<Sound> sounds;

    public EndLevelScreen(Game game, Storage storage) {
		this.game = game;
        this.storage = storage;
        level = storage.level;
        sounds = new Array<Sound>();
        sounds.addAll(button1, button2, button3);
    }

	@Override
	public void show () {
        endLevel = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/levelcompletescreen.png")), 0, 0, 800, 600));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 600);
        setRectangles();
        buttonPressed = false;

        eScreenMusic.play((float) storage.soundM / 100);
        eScreenMusic.loop();
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        endLevel.draw(batch);
        endLevel.setAlpha(fadeTime);
        batch.end();
        time += delta;
        if (transitioningOut)
            fadeTime -= delta * 5;
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isTouched()) && !buttonPressed)
            chooseInput();
    }

	@Override
	public void hide () {
        endLevel.getTexture().dispose();
	}

    @Override
    public void resize (int width, int height) {
        if (storage.cam != null) {
            storage.cam.viewportWidth = width / 1.8f;
            storage.cam.viewportHeight = height / 1.8f;
            storage.setWidthRatio(storage.cam.viewportWidth / 355.55557f);
            widthRatio = storage.widthRatio;
            storage.setHeightRatio(storage.cam.viewportHeight / 266.6667f);
            heightRatio = storage.heightRatio;
        }
        else {
            float viewportWidth = width / 1.8f;
            float viewportHeight = height / 1.8f;
            widthRatio = viewportWidth / 355.55557f;
            heightRatio = viewportHeight / 266.6667f;
        }
        setRectangles();
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        endLevel.getTexture().dispose();
    }

    private void chooseInput() {
        //Goes back to overworld.
        if (continueGame.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            eScreenMusic.stop();

            buttonPressed = true;
            transitioningOut = true;
            endLevel.setRegion(new Texture(Gdx.files.internal("data/screens/levelcompletescreenhighlighted1.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    endLevel.setRegion(new Texture(Gdx.files.internal("data/screens/levelcompletescreen.png")));
                }
            }, 0.1f);
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    chooseOverworld();
                }
            }, 0.2f);
            timer.start();
        }
        //Plays the level again.
        if (replay.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            eScreenMusic.stop();

            buttonPressed = true;
            transitioningOut = true;
            endLevel.setRegion(new Texture(Gdx.files.internal("data/screens/levelcompletescreenhighlighted2.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    endLevel.setRegion(new Texture(Gdx.files.internal("data/screens/levelcompletescreen.png")));
                }
            }, 0.1f);
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(new LoadingScreen(game, storage));
                }
            }, 0.2f);
            timer.start();
        }
    }

    private void chooseOverworld() {
        game.setScreen(new OverworldScreen(game, storage));
    }

    private void setRectangles() {
        continueGame = new Rectangle(186 * widthRatio, 224 * heightRatio, 250 * widthRatio, 84 * heightRatio);
        replay = new Rectangle(216 * widthRatio, 363 * heightRatio, 190 * widthRatio, 86 * heightRatio);
    }
}
