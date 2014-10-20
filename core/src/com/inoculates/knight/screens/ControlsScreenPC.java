
package com.inoculates.knight.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;

public class ControlsScreenPC implements Screen {
    Sprite controls;
    SpriteBatch batch;
    Game game;
    Storage storage;
    //The boxes for the controls.
    Rectangle back, up, left, down, right, attack, specattack, losepower;
    Label upL, leftL, downL, rightL, attackL, specattackL, losepowerL, changeKey;
    Screen screen;
    Array<Label> labels = new Array<Label>();
    InputProcessor processor;

    float time = 0;
    float widthRatio, heightRatio;
    int level = 0;
    boolean changing = false;

    public boolean transitioningOut = false;

    private Sound button1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select.wav")),
            button2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select2.wav")), button3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Blip_Select3.wav"));

    private Array<Sound> sounds;

    public ControlsScreenPC(Game game, Storage storage, Screen screen) {
		this.game = game;
        this.storage = storage;
        this.screen = screen;
        level = storage.level;
        sounds = new Array<Sound>();
        sounds.addAll(button1, button2, button3);
    }

    //Sets the input processor so that the screen can interpret commands.
	@Override
	public void show () {
        controls = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("data/screens/controls.png")), 0, 0, 800, 600));
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 600);
        AssetManager manager = new AssetManager();
        manager.finishLoading();
        setRectangles();
        setLabels();
        processor = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE)
                    clearSelection();
                else changeKey(keycode);
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if ((button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) && changing && changeKey != null)
                    changeKey(button);
                else checkInput();
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };
        Gdx.input.setInputProcessor(processor);
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        controls.draw(batch);
        for (Label label : labels)
            label.draw(batch, 1);
        batch.end();
        setPositions();
        time += delta;
    }

	@Override
	public void hide () {
        controls.getTexture().dispose();
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
        controls.getTexture().dispose();
    }

    //Changes any non-convertible keys (shift, arrowkeys, etc.) to legible strings.
    private String convertText(final int input) {
        if (Input.Keys.toString(input).length() > 1)
            switch (input) {
                case 0:
                    return "M1";
                case 1:
                    return "M2";
                case 2:
                    return "M3";
                case 19:
                    return "Up";
                case 20:
                    return "Down";
                case 21:
                    return "Left";
                case 22:
                    return "Right";
                case 59:
                    return "Shift";
                case 62:
                    return "Bar";
                case 129:
                    return "Cntrl";
            }
        else return Input.Keys.toString(input);
        return "UNKNOWN";
    }

    private void checkInput() {
        //Clears all green boxes.
        clearSelection();

        //If the player pressed the back button, it goes back to the options screen.
        if (back.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            transitioningOut = true;
            controls.setRegion(new Texture(Gdx.files.internal("data/screens/controlshighlighted.png")));
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    controls.setRegion(new Texture(Gdx.files.internal("data/screens/controls.png")));
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

        //Highlights and changes the key corresponding to the box selected.
        if (up.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            upL.setColor(Color.GREEN);
            changeKey = upL;
            changing = true;
        }
        else if (left.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            leftL.setColor(Color.GREEN);
            changeKey = leftL;
            changing = true;
        }
        else if (right.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            rightL.setColor(Color.GREEN);
            changeKey = rightL;
            changing = true;
        }
        else if (down.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            downL.setColor(Color.GREEN);
            changeKey = downL;
        }
        else if (attack.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            attackL.setColor(Color.GREEN);
            changeKey = attackL;
            changing = true;
        }
        else if (specattack.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            specattackL.setColor(Color.GREEN);
            changeKey = specattackL;
            changing = true;
        }
        else if (losepower.contains(Gdx.input.getX(), Gdx.input.getY())) {
            int random = (int) (Math.random() * sounds.size);
            sounds.get(random).play((float) storage.soundE / 100);
            losepowerL.setColor(Color.GREEN);
            changeKey = losepowerL;
            changing = true;
        }
    }

    //Sets the size and positions of the control boxes.
    private void setRectangles() {
        back = new Rectangle(554 * widthRatio, 20 * heightRatio, 55 * widthRatio, 20 * heightRatio);
        up = new Rectangle(480 * widthRatio, 30 * heightRatio, 42 * widthRatio, 44 * heightRatio);
        left = new Rectangle(480 * widthRatio, 85 * heightRatio, 42 * widthRatio, 44 * heightRatio);
        right = new Rectangle(480 * widthRatio, 145 * heightRatio, 42 * widthRatio, 44 * heightRatio);
        down = new Rectangle(480 * widthRatio, 200 * heightRatio, 42 * widthRatio, 44 * heightRatio);
        attack = new Rectangle(480 * widthRatio, 262 * heightRatio, 42 * widthRatio, 44 * heightRatio);
        specattack = new Rectangle(480 * widthRatio, 326 * heightRatio, 42 * widthRatio, 44 * heightRatio);
        losepower = new Rectangle(480 * widthRatio, 394 * heightRatio, 42 * widthRatio, 44 * heightRatio);
    }

    //Changes the text inside the boxes.
    private void setLabels() {
        Texture texture = new Texture(Gdx.files.internal("data/fonts/main2.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        BitmapFont font = new BitmapFont(Gdx.files.internal("data/fonts/main2.fnt"), region, false);
        font.setUseIntegerPositions(false);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        upL = new Label(convertText(storage.moveUp), style);
        leftL = new Label(convertText(storage.moveLeft), style);
        rightL = new Label(convertText(storage.moveRight), style);
        downL = new Label(convertText(storage.moveDown), style);
        attackL = new Label(convertText(storage.attack), style);
        specattackL = new Label(convertText(storage.specAttack), style);
        losepowerL = new Label(convertText(storage.losePower), style);
        labels.add(upL);
        labels.add(leftL);
        labels.add(rightL);
        labels.add(downL);
        labels.add(attackL);
        labels.add(specattackL);
        labels.add(losepowerL);
    }

    //Sets the positions of the labels.
    private void setPositions() {
        float x = 625;
        upL.setPosition(x - upL.getWidth() / 2, 525);
        leftL.setPosition(x - leftL.getWidth() / 2, 455);
        rightL.setPosition(x - rightL.getWidth() / 2, 385);
        downL.setPosition(x - downL.getWidth() / 2, 315);
        attackL.setPosition(x - attackL.getWidth() / 2, 235);
        specattackL.setPosition(x - specattackL.getWidth() / 2, 155);
        losepowerL.setPosition(x - losepowerL.getWidth() / 2, 70);
    }

    //Clears all green boxes.
    private void clearSelection() {
        for (Label label : labels)
            label.setColor(Color.WHITE);
        changeKey = null;
        changing = false;
    }

    //Changes the string of the box.
    private void changeKey(int key) {
        if (changeKey == null)
            return;

        for (Label label : labels) {
            if (label.getText().toString().equals(convertText(key)))
                return;
        }

        if (convertText(key).contentEquals("UNKNOWN"))
            return;

        changeKey.setText(convertText(key));
        changeKey.setSize(changeKey.getTextBounds().width, changeKey.getHeight());

        //Changes the associated key.
        if (changeKey.equals(upL))
            storage.setMoveUp(key);
        if (changeKey.equals(leftL))
            storage.setMoveLeft(key);
        if (changeKey.equals(rightL))
            storage.setMoveRight(key);
        if (changeKey.equals(downL))
            storage.setMoveDown(key);
        if (changeKey.equals(attackL))
            storage.setAttack(key);
        if (changeKey.equals(specattackL))
            storage.setSpecAttack(key);
        if (changeKey.equals(losepowerL))
            storage.setLosePower(key);

        clearSelection();
    }

}
