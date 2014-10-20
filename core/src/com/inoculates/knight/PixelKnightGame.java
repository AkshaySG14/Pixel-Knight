
package com.inoculates.knight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.inoculates.knight.screens.*;

public class PixelKnightGame extends Game {
    Storage storage;

    public PixelKnightGame() {

    }

    @Override
    public void create () {
        setScreen(new TitleScreen(this, storage));
    }
}
