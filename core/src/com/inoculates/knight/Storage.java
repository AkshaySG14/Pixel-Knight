package com.inoculates.knight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.inoculates.knight.screens.GameScreen;

public class Storage {
    public static final int UNINITIALIZED = -1;

    public int level = UNINITIALIZED;
    public int mLevel = UNINITIALIZED;
    public int soundM = UNINITIALIZED;
    public int soundE = UNINITIALIZED;
    public int gems = UNINITIALIZED;
    public int lives = UNINITIALIZED;

    public int moveRight = UNINITIALIZED;
    public int moveLeft = UNINITIALIZED;
    public int moveUp = UNINITIALIZED;
    public int moveDown = UNINITIALIZED;
    public int attack = UNINITIALIZED;
    public int specAttack = UNINITIALIZED;
    public int losePower = UNINITIALIZED;

    public float widthRatio;
    public float heightRatio;

    public GameScreen mainScreen;
    public Camera cam;
    public Preferences prefs;

    public Storage() {
        prefs = Gdx.app.getPreferences("Pixel Knight");

        if (prefs.contains("gems"))
            setGems(prefs.getInteger("gems"));
        if (prefs.contains("lives"))
            setLives(prefs.getInteger("lives"));
        if (prefs.contains("max level"))
            setMLevel(prefs.getInteger("max level"));
        if (prefs.contains("level"))
            setLevel(prefs.getInteger("level"));
        if (prefs.contains("music volume"))
            setSoundM(prefs.getInteger("music volume"));
        if (prefs.contains("sound volume"))
            setSoundE(prefs.getInteger("sound volume"));
        if (prefs.contains("up"))
            setMoveUp(prefs.getInteger("up"));
        if (prefs.contains("left"))
            setMoveLeft(prefs.getInteger("left"));
        if (prefs.contains("right"))
            setMoveRight(prefs.getInteger("right"));
        if (prefs.contains("down"))
            setMoveDown(prefs.getInteger("down"));
        if (prefs.contains("attack"))
            setAttack(prefs.getInteger("attack"));
        if (prefs.contains("special attack"))
            setSpecAttack(prefs.getInteger("special attack"));
        if (prefs.contains("lose power"))
            setLosePower(prefs.getInteger("lose power"));

        if (level == UNINITIALIZED)
            level = 0;
        if (mLevel == UNINITIALIZED)
            mLevel = 0;
        if (soundM == UNINITIALIZED)
            soundM = 50;
        if (soundE == UNINITIALIZED)
            soundE = 50;
        if (gems == UNINITIALIZED)
            gems = 0;
        if (lives == UNINITIALIZED)
            lives = 10;
        if (moveRight == UNINITIALIZED)
            moveRight = Input.Keys.D;
        if (moveLeft == UNINITIALIZED)
            moveLeft = Input.Keys.A;
        if (moveUp == UNINITIALIZED)
            moveUp = Input.Keys.W;
        if (moveDown == UNINITIALIZED)
            moveDown = Input.Keys.S;
        if (attack == UNINITIALIZED)
            attack = Input.Buttons.LEFT;
        if (specAttack == UNINITIALIZED)
            specAttack = Input.Buttons.RIGHT;
        if (losePower == UNINITIALIZED)
            losePower = Input.Keys.X;
    }

    public void wipe() {
        level = 0;
        mLevel = 0;
        soundM = 50;
        soundE = 50;
        gems = 0;
        lives = 10;
        moveRight = Input.Keys.D;
        moveLeft = Input.Keys.A;
        moveUp = Input.Keys.W;
        moveDown = Input.Keys.S;
        attack = Input.Buttons.LEFT;
        specAttack = Input.Buttons.RIGHT;
        losePower = Input.Keys.X;

        prefs.putInteger("level", level);
        prefs.putInteger("max level", mLevel);
        prefs.putInteger("music volume", soundM);
        prefs.putInteger("sound volume", soundE);
        prefs.putInteger("gems", gems);
        prefs.putInteger("lives", lives);
        prefs.putInteger("right", moveRight);
        prefs.putInteger("left", moveLeft);
        prefs.putInteger("up", moveUp);
        prefs.putInteger("down", moveDown);
        prefs.putInteger("attack", attack);
        prefs.putInteger("special attack", specAttack);
        prefs.putInteger("lose power", losePower);

        prefs.flush();
    }

    public void setLevel(int l) {
        level = l;
    }

    public void setMLevel(int l) {
        mLevel = l;
    }

    public void setSoundM(int s) {
        soundM = s;
    }

    public void setSoundE(int s) {
        soundE = s;
    }

    public void setGems(int g) {
        gems = g;
    }

    public void setLives(int l) {
        lives = l;
    }

    public void setMoveRight(int moveRight) {
        this.moveRight = moveRight;
    }

    public void setMoveLeft(int moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveUp(int moveUp) {
        this.moveUp = moveUp;
    }

    public void setMoveDown(int moveDown) {
        this.moveDown = moveDown;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setSpecAttack(int specAttack) {
        this.specAttack = specAttack;
    }

    public void setLosePower(int losePower) {
        this.losePower = losePower;
    }

    public void updatePreferences() {
        prefs.putInteger("level", level);
        prefs.putInteger("max level", mLevel);
        prefs.putInteger("gems", gems);
        prefs.putInteger("music volume", soundM);
        prefs.putInteger("sound volume", soundE);
        prefs.putInteger("lives", lives);
        prefs.putInteger("right", moveRight);
        prefs.putInteger("left", moveLeft);
        prefs.putInteger("up", moveUp);
        prefs.putInteger("down", moveDown);
        prefs.putInteger("attack", attack);
        prefs.putInteger("special attack", specAttack);
        prefs.putInteger("lose power", losePower);
        prefs.flush();
    }

    public void setMainScreen(GameScreen screen) {
        mainScreen = screen;
    }

    public void setWidthRatio(float wRatio) {
        widthRatio = wRatio;
    }

    public void setHeightRatio(float hRatio) {
        heightRatio = hRatio;
    }

    public void setCamera(Camera camera) {
        cam = camera;
    }
}
