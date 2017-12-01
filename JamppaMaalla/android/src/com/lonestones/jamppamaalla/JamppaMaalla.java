package com.lonestones.jamppamaalla;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Vesada on 29.11.2017.
 */

// The "Game" abstract class provides an implementation of
// ApplicationListener + helper methods to set and handle Screen rendering.

public class JamppaMaalla extends Game {
    public static final float Puuraja = 300;
    SpriteBatch batch;
    BitmapFont font;

    public void create() {
        // SpriteBatch is a special class that is used to draw 2D images
        batch = new SpriteBatch();

        // Uusi fontti, säädä koko
        font = new BitmapFont();
        font.getData().setScale(2,2);

        // uusi mainmenu ruutu
        this.setScreen(new MainMenuRuutu(this));
    }

    public void render() {
        super.render(); // important
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
