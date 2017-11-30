package com.lonestones.jamppamaalla;

/**
 * Created by Vesada on 29.11.2017.
 */

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle; //add this import and NOT the one in the standard library
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

// Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).
public class PeliRuutu implements Screen{
    // "final" defines an entity that can only be assigned once
    private final JamppaMaalla game;

    private Sound hitSound;
    Music rainMusic;
    private OrthographicCamera kamera;
    private Array<Este> esteet;
    private Este este;
    private long esteEsiinAika;
    private int tormaysMaara;
    private Jamppa jamppa;


    public PeliRuutu(final JamppaMaalla peli) {
        this.game = peli;
        jamppa = new Jamppa();

        // load the sound effects and background "music"
        hitSound = Gdx.audio.newSound(Gdx.files.internal("244983__ani-music__ani-big-pipe-hit.wav"));
        // rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        // rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        kamera = new OrthographicCamera();
        kamera.setToOrtho(false, 800, 480);

        // luodaan "esteet"-taulukko
        esteet = new Array<Este>();
        // ja spawnataan esteet esiin
        esteEsiin();
    }


    // tuodaan esteet ruutuun
    public void esteEsiin() {
            Este este = new Este();
            esteet.add(este);
            esteEsiinAika = TimeUtils.nanoTime();
    }


    @Override
    public void render(float delta) {
        // clear the screen with a color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 100f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        kamera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(kamera.combined);


        // aloita "batch", piirrä "Jamppa" ja esteet ym
        game.batch.begin();
            game.font.draw(game.batch, "Jampan törmäilyt: " + tormaysMaara + "  "+jamppa.getX(), 0, 480);
            piirraObjektit();
        game.batch.end();




        // ------------   ESTEIDEN HALLINTA -----------------------------------------------
        // tehdään uusi este jos aikaa kulunut tarpeeksi
        if (TimeUtils.nanoTime() - esteEsiinAika > 1000000000) {
            esteEsiin();
            esteEsiinAika = TimeUtils.nanoTime();
        }

        // liikuta esteitä, poista esteet ruudun ulkopuolella / osuneet
        Iterator<Este> iter = esteet.iterator();
        while (iter.hasNext()) {
            Este este = iter.next();
            este.setX( este.getX() - 200 * Gdx.graphics.getDeltaTime());

            if (este.getX() + 64 < este.getXMin())
                iter.remove();

            if (este.getEsteRect().overlaps(jamppa.getJamppaRect())) {
                tormaysMaara++;


                hitSound.play();
                //   iter.remove();
            }
        // ------------   ESTEIDEN HALLINTA -----------------------------------------------
        }



        // Kosketusnäytön toiminnot
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

            kamera.unproject(touchPos);
            jamppa.setX(touchPos.x - 64 / 2);
            jamppa.setY(touchPos.y - 64 / 2);

        }
    }



    // piirrä ruutuun jampat ja muut
    public void piirraObjektit() {

        for (Este este : esteet) {
            game.batch.draw(este.getEsteKuva(), este.getX(), este.getY());
        }

        game.batch.draw(jamppa.getJamppaKuva(), jamppa.getX(), jamppa.getY());
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        //    rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        este.getEsteKuva().dispose();
        jamppa.getJamppaKuva().dispose();
        hitSound.dispose();
        // rainMusic.dispose();
    }

}
