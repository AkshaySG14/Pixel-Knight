
package com.inoculates.knight.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;
import com.inoculates.knight.UI.LoadingText;

public class LoadingScreen implements Screen {
    Sprite loading;
    LoadingText text;
    SpriteBatch batch;
    Game game;
    GameScreen mainScreen;

    float time = 0;
    float fadeTime = 1;

    public boolean transitioningOut = false;
    boolean loadingGame = false;

    public LoadingScreen(Game game, Storage storage) {
		this.game = game;
        mainScreen = new GameScreen(game, this, storage);
    }

	@Override
	public void show () {
        loading = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/loadingscreen.png")), 0, 0, 800, 600));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 600);
        AssetManager manager = new AssetManager();
        manager.load("data/screens/loadingtext.pack", TextureAtlas.class);
        manager.finishLoading();
        text = new LoadingText(200, 200, (TextureAtlas) manager.get("data/screens/loadingtext.pack"));
        loading.setAlpha(fadeTime);
        text.setAlpha(fadeTime);
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        loading.draw(batch);
        text.update(delta);
        text.draw(batch);
        batch.end();
        time += delta;
        if (fadeTime > 0.01 && transitioningOut)
                fadeTime -= delta / 2;
        loading.setAlpha(fadeTime);
        text.setAlpha(fadeTime);
        if (!loadingGame) {
            loadingGame = true;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    mainScreen.generateWorld();
                }
            }, 1);
        }
    }

	@Override
	public void hide () {
        loading.getTexture().dispose();
	}

    @Override
    public void resize (int width, int height) {
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        loading.getTexture().dispose();
    }

    //Loads when all objects are created.
    public void loadGame() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(mainScreen);
            }
        }, 3);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                transitioningOut = true;
            }
        }, 1);
        timer.start();
    }

}
