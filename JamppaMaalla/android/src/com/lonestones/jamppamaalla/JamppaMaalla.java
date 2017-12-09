package com.lonestones.jamppamaalla;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;



/**
 * Created by Vesada on 29.11.2017.
 */

// The "Game" abstract class provides an implementation of
// ApplicationListener + helper methods to set and handle Screen rendering.

public class JamppaMaalla extends Game {
    public static final float Puuraja = 300;
    SpriteBatch batch;
    BitmapFont font;
    BitmapFont isofont;
    public static Skin gameSkin;                                // ladataan napeille ym "skin"


    public void create() {
        // SpriteBatch is a special class that is used to draw 2D images
        batch = new SpriteBatch();

        // Fontit, säädetään koko
        font = new BitmapFont();
        font.getData().setScale(2,2);
        isofont = new BitmapFont();
        isofont.getData().setScale(4,4);

        // otetaan käyttöön "skin" buttoneita ja labeleita varten
        gameSkin = new Skin(Gdx.files.internal("jampskin/jampskin.json"));

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
