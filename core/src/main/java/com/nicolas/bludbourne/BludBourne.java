package com.nicolas.bludbourne;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nicolas.bludbourne.screens.MainGameScreen;

/** {@link com.badlogic.gdx.Game} implementation shared by all platforms. */
public class BludBourne extends Game {

    public static final MainGameScreen mainGameScreen = new MainGameScreen();

    @Override
    public void create() {
        setScreen(mainGameScreen);
    }

    @Override
    public void dispose() {
        mainGameScreen.dispose();
    }
}
