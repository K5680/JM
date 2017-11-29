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
    final JamppaMaalla game;

    Texture esteKuva;
    Texture jamppaKuva;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera kamera;

    Array<Rectangle> esteet;
    long esteEsiinAika;
    int tormaysMaara;
    Jamppa jamppa;

    public PeliRuutu(final JamppaMaalla peli) {
        this.game = peli;
        jamppa = new Jamppa();


        // load the images for the droplet and the jamppa, 64x64 pixels each
        esteKuva = new Texture(Gdx.files.internal("kivi.png"));


     //   jamppaKuva = new Texture(Gdx.files.internal("jamppa.png"));


        // load the sound effects and background "music"
        // dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        // rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        // rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        kamera = new OrthographicCamera();
        kamera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the jamppa
     //   jamppa = new Rectangle();
     //   jamppa.x = 800 / 2 - 64 / 2; // center the jamppa horizontally
     //   jamppa.y = 20; // bottom left corner of the jamppa is 20 pixels above the bottom screen edge
     //   jamppa.width = 64;
     //   jamppa.height = 64;


        // luodaan "esteet"-taulukko ja spawnataan esiin
        esteet = new Array<Rectangle>();
        esteEsiin();

    }

    private void esteEsiin() {
        Rectangle este = new Rectangle();
        este.x = MathUtils.random(0, 800 - 64);
        este.y = 480;
        este.width = 64;
        este.height = 64;
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
            game.font.draw(game.batch, "Jampan törmäilyt: " + tormaysMaara, 0, 480);

            piirraObjektit();

            for (Rectangle este : esteet) {
                game.batch.draw(esteKuva, este.x, este.y);
            }

            game.batch.end();


        // userinputti
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            kamera.unproject(touchPos);
            jamppa.setX(touchPos.x - 64 / 2);
        }

        // näppäinohjaus?
//        if (Gdx.input.isKeyPressed(Keys.LEFT))
//            jamppa.x -= 200 * Gdx.graphics.getDeltaTime();
//        if (Gdx.input.isKeyPressed(Keys.RIGHT))
//            jamppa.x += 200 * Gdx.graphics.getDeltaTime();



        // tehdään uusi este jos aikaa kulunut tarpeeksi
        if (TimeUtils.nanoTime() - esteEsiinAika > 1000000000)
            esteEsiin();

        // move the esteet, remove any that are beneath the bottom edge of
        // the screen or that hit the jamppa. In the later case we play back
        // a sound effect as well.
        Iterator<Rectangle> iter = esteet.iterator();
        while (iter.hasNext()) {
            Rectangle este = iter.next();
            este.y -= 200 * Gdx.graphics.getDeltaTime();
            if (este.y + 64 < 0)
                iter.remove();
            if (este.overlaps(jamppa.getJamppaRect())) {
                tormaysMaara++;
                //      dropSound.play();
                iter.remove();
            }
        }
    }

    public void piirraObjektit() {
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
        esteKuva.dispose();
        jamppaKuva.dispose();
        //  dropSound.dispose();
        // rainMusic.dispose();
    }

}
