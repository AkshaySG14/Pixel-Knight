
package com.inoculates.knight.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;
import com.inoculates.knight.UI.RedArrow;

public class TitleScreen implements Screen {
    Sprite intro;
    SpriteBatch batch;
    Game game;
    RedArrow indicator;
    Storage storage;

    float time = 0;
    float fadeTime = 0;
    float widthRatio, heightRatio, height, width;
    int option = 0;

    final static int NEWGAME = 0;
    final static int CONTINUEGAME = 1;
    final static int OPTIONS = 2;

    boolean transitioningIn = true;

    private Sound button1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select.wav")),
            button2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select2.wav")), button3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select3.wav"));

    private Sound tScreenMusic = Gdx.audio.newSound(Gdx.files.internal("data/sounds/TitleScreen.wav"));

    public TitleScreen(Game game, Storage storage) {
		this.game = game;
        this.storage = storage;
    }

	@Override
	public void show () {
        intro = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/startscreen.png")), 0, 0, 800, 600));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 600);
        AssetManager manager = new AssetManager();
        manager.load("data/screens/redindicator.pack", TextureAtlas.class);
        manager.load("data/screens/enter.pack", TextureAtlas.class);
        manager.finishLoading();
        indicator = new RedArrow(300, 340, (TextureAtlas) manager.get("data/screens/redindicator.pack"));
        intro.setAlpha(fadeTime);
        indicator.setAlpha(fadeTime);
        if (storage == null)
            storage = new Storage();
        tScreenMusic.play((float) storage.soundM / 100);
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        intro.draw(batch);
        indicator.draw(batch);
        batch.end();
        time += delta;
        if (transitioningIn) {
            fadeTime += delta / 2;
            if (fadeTime > 0.99)
                fadeTime = 1;
        } else if (fadeTime > 0.01)
            fadeTime -= delta / 2;
        intro.setAlpha(fadeTime);
        indicator.setAlpha(fadeTime);
        if (time > 0.5)
            selectMode();
    }

	@Override
	public void hide () {
        intro.getTexture().dispose();
	}

    @Override
    public void resize (int width, int height) {
        float viewportWidth = width / 1.8f;
        float viewportHeight = height / 1.8f;
        widthRatio = viewportWidth / 355.55557f;
        heightRatio = viewportHeight / 266.6667f;
        this.height = viewportHeight;
        this.width = viewportWidth;
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        intro.getTexture().dispose();
    }

    //This interprets the arrow keys and moves the indicator accordingly.
    private void selectMode() {
        Array<Sound> sounds = new Array<Sound>();
        sounds.addAll(button1, button2, button3);

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            option ++;
            if (option > 2) option = NEWGAME;
            moveIndicator();
            time = 0;
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            option --;
            if (option < 0) option = OPTIONS;
            moveIndicator();
            time = 0;
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && fadeTime == 1) {
            decideMode();
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
        }

    }

    //Acts out whichever command is needed. Starts a new game, continues a preexisting one, or goes to options.
    private void decideMode() {
        tScreenMusic.stop();
        tScreenMusic.dispose();

        switch (option) {
            case NEWGAME:
                transitioningIn = false;
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        game.setScreen(new OverworldScreen(game, storage));
                    }
                }, 2);
                timer.start();
                storage.wipe();
                break;
            case CONTINUEGAME:
                transitioningIn = false;
                Timer timer2 = new Timer();
                timer2.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        game.setScreen(new OverworldScreen(game, storage));
                    }
                }, 2);
                timer2.start();
                break;
            case OPTIONS:
                transitioningIn = false;
                Timer timer3 = new Timer();
                timer3.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        game.setScreen(new PausedScreenTitle(game, storage));
                    }
                }, 2);
                timer3.start();
                break;
        }
    }

    //Sets the position of the indicator based on which text it should be hovering over.
    private void moveIndicator() {
        switch (option) {
            case NEWGAME:
                indicator.setX(300);
                indicator.setY(340);
                break;
            case CONTINUEGAME:
                indicator.setX(275);
                indicator.setY(285);
                break;
            case OPTIONS:
                indicator.setX(325);
                indicator.setY(227.5f);
                break;
        }
    }

}
