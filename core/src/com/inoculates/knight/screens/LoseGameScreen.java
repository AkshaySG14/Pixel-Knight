
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

public class LoseGameScreen implements Screen {
    SpriteBatch batch;
    Game game;
    Storage storage;

    boolean transitioningOut = false;
    boolean buttonPressed = false;
    int level;
    float time = 0, fadeTime = 1;
    float widthRatio, heightRatio;

    Rectangle tryAgain, overworld;
    Sprite loseGame;

    private Sound button1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select.wav")),
            button2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select2.wav")), button3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select3.wav"));

    private Sound lScreenMusic = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Defeat.wav"));

    private Array<Sound> sounds = new Array<Sound>();

    public LoseGameScreen(Game game, Storage storage) {
		this.game = game;
        this.storage = storage;
        level = storage.level;
        sounds = new Array<Sound>();
        sounds.addAll(button1, button2, button3);
    }

	@Override
	public void show () {
        loseGame = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/gameoverscreen.png")), 0, 0, 800, 600));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 600);
        setRectangles();
        buttonPressed = false;
        lScreenMusic.play((float) storage.soundM / 100);
        lScreenMusic.loop();
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        loseGame.draw(batch);
        loseGame.setAlpha(fadeTime);
        batch.end();
        time += delta;
        if (transitioningOut)
            fadeTime -= delta * 5;
        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isTouched()) && !buttonPressed)
            chooseInput();
    }

	@Override
	public void hide () {
        loseGame.getTexture().dispose();
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
        loseGame.getTexture().dispose();
    }

    private void chooseInput() {
        //Goes to overworld.
        if (overworld.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            lScreenMusic.stop();

            buttonPressed = true;
            transitioningOut = true;
            loseGame.setRegion(new Texture(Gdx.files.internal("data/screens/gameoverscreenhighlighted2.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    loseGame.setRegion(new Texture(Gdx.files.internal("data/screens/gameoverscreen.png")));
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
        //Tries again.
        if (tryAgain.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            lScreenMusic.stop();

            buttonPressed = true;
            transitioningOut = true;
            loseGame.setRegion(new Texture(Gdx.files.internal("data/screens/gameoverscreenhighlighted1.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    loseGame.setRegion(new Texture(Gdx.files.internal("data/screens/gameoverscreen.png")));
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
        tryAgain = new Rectangle(182 * widthRatio, 187 * heightRatio, 283 * widthRatio, 84 * heightRatio);
        overworld = new Rectangle(31 * widthRatio, 321 * heightRatio, 579 * widthRatio, 74 * heightRatio);
    }
}
