package com.lonestones.jamppamaalla;

/**
 * Created by Vesada on 29.11.2017.
 */

import android.graphics.Paint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Iterator;

// Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).
public class PeliRuutu implements Screen {
    private final JamppaMaalla game;     // "final" defines an entity that can only be assigned once
    private final Screen parent;         // MainMenuRuutu, jotta siihen voidaan palata

    private Texture taivaskuva;
    private OrthographicCamera kamera;
    private Sound hitSound;
    Music omaMusic;

    private Array<Este> esteet;
    private Array<Tausta> taustat;
    private Array<Tausta> maisema;
    private Este este;
    private Tausta tausta;
    private long taustaIntervalli = 1000;
    private long esteVauhti = 200;
    private long maisemaVauhti = 150;
    private long esteEsiinAika;
    private long taustatEsiinAika;
    private long maisemaEsiinAika;
    private int tormaysMaara;
    private Jamppa jamppa;

    private Stage stage; // onko oltava?
    private ParticleEffect pe;


    //animaatio
    float stateTime;
    private static final int FRAME_COLS = 3, FRAME_ROWS = 3;
    Animation<TextureRegion> walkAnimation; // frame type = textureregion
    Texture walkSheet;





    public static boolean peliAlkaaNyt = true; // eka käynnistys

    public PeliRuutu(final JamppaMaalla peli, Screen parent) {
        this.parent = parent;
        this.game = peli;
       // stage = new Stage(new ScreenViewport()); // onko oltava?

        // Jamppa kehiin
        jamppa = new Jamppa();



        // Load the sprite sheet as a Texture
        walkSheet = new Texture(Gdx.files.internal("jamppa_anim.png"));

        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        walkAnimation = new Animation<TextureRegion>(0.025f, walkFrames);

        // Instantiate a SpriteBatch for drawing and reset the elapsed animation
        // time to 0
        stateTime = 0f;




        // partikkeliefektit
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("tuliefekti2.p"),Gdx.files.internal(""));
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);


        // otetaan "back" -nappula haltuun
        Gdx.input.setCatchBackKey(true);

        // load the sound effects and background "music"
        hitSound = Gdx.audio.newSound(Gdx.files.internal("244983__ani-music__ani-big-pipe-hit.wav"));
        // rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        // rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        kamera = new OrthographicCamera();
        kamera.setToOrtho(false, 800, 480);

         // taivas ruudun yläreunaan
        taivaskuva = new Texture(Gdx.files.internal("taivas2.png"));



        // luodaan "esteet"-taulukko  &  taustan "esteet"
        esteet = new Array<Este>();
        taustat = new Array<Tausta>(0);
        maisema = new Array<Tausta>(1);

        // ja spawnataan esteet esiin
        esteEsiin();
        taustatEsiin();
        pe.start();
        peliAlkaaNyt = false;
    }






    @Override
    public void render(float delta) {

        // clear the screen with a color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 30f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        kamera.update(); // tell the camera to update its matrices
        game.batch.setProjectionMatrix(kamera.combined);    // tell the SpriteBatch to render in the coordinate system specified by the camera





    //    Pixmap pixmap = new Pixmap(jamppa.getJamppaKuva().getWidth(),jamppa.getJamppaKuva().getHeight(), Pixmap.Format.RGBA8888);
    //    Texture texture = new Texture(pixmap);


        // aloita "batch", piirrä "Jamppa" ja esteet ym  // BATCH BEGIN
        game.batch.begin();
            piirraObjektit(); // objektit ruutuun
            game.font.draw(game.batch, "Törmäilyt: " + tormaysMaara + "  " + jamppa.getY() + " " + jamppa.getX(), 0, 480); // teksti ruutuun

            // Gdx.app.log("tag","msg");
        //    pixmap.setColor(1,1,0,100);
        //    pixmap.fillRectangle(0,0,jamppa.getJamppaKuva().getWidth(),jamppa.getJamppaKuva().getHeight());
        //    texture.draw(pixmap,0,0);
        //    game.batch.draw(texture, jamppa.getX(),jamppa.getY(), jamppa.getJamppaKuva().getWidth(),jamppa.getJamppaKuva().getHeight());
        game.batch.end();                                // BATCH END



        // Jamppa osuu esteeseen, pysäytetään ruudunvieritys, mitä tapahtuu jampalle?
        if (jamppa.jamppaTormaa){

            esteEsiinAika = TimeUtils.nanoTime();    // nollataan ajastimia
            taustatEsiinAika = TimeUtils.nanoTime(); // koska ei liikuta
            maisemaEsiinAika = TimeUtils.nanoTime();
            taustaIntervalli = TimeUtils.millisToNanos(MathUtils.random(1000, 5000));

            if (TimeUtils.nanoTime() > jamppa.jamppaMaissa + TimeUtils.millisToNanos(1000)) {
                jamppa.jamppaTormaa = false;
                // jamppa must go on
                esteVauhti = 200;
                maisemaVauhti = 150;
                pe.reset(); // savuefektin resetointi
                //     if (pe.isComplete())   // effect reset, tarpeen?
                }

        }


            // ------------>   ESTEIDEN HALLINTA  >-----------------------------------------------
            // tehdään uusi este jos aikaa kulunut tarpeeksi
            if (TimeUtils.nanoTime() - esteEsiinAika > 1000000000) {
                esteEsiin();
                esteEsiinAika = TimeUtils.nanoTime();
            }
            // taustan kuvat
            if (TimeUtils.nanoTime() - taustatEsiinAika > TimeUtils.millisToNanos(600)) {
                taustatEsiin();
                taustatEsiinAika = TimeUtils.nanoTime();
            }
            // maiseman kuvat
            if (TimeUtils.nanoTime() - maisemaEsiinAika > taustaIntervalli) {
                maisemaEsiin();
                maisemaEsiinAika = TimeUtils.nanoTime();
                taustaIntervalli = TimeUtils.millisToNanos(MathUtils.random(1000, 3000)); // kuinka usein uusi pilvi tms tulee esiin
            }


            // LIIKUTA esteitä, poista esteet ruudun ulkopuolella / osuneet
            Iterator<Este> iter = esteet.iterator();
            while (iter.hasNext()) {
                Este este = iter.next();
                este.setX(este.getX() - esteVauhti * Gdx.graphics.getDeltaTime());

                if (este.getX() + 64 < este.getXMin())
                    iter.remove();

                // "jamppa" kompuroi
                if (este.getEsteRect().overlaps(jamppa.getJamppaRect())) {

                    if (!jamppa.jamppaTormaa) {
                        tormaysMaara++;
                        hitSound.play();

                        jamppa.jamppaCrash();

                        esteVauhti = 0;
                        maisemaVauhti = 0;
                        iter.remove();
                    }
                }
            }

            // liikuta taustaobjekteja (puurivistö), poista ruudun ulkopuolella
            Iterator<Tausta> iter2 = taustat.iterator();
            while (iter2.hasNext()) {
                Este tausta = iter2.next();
                tausta.setX(tausta.getX() - esteVauhti * Gdx.graphics.getDeltaTime());
                // poista ruudun ulkopuolella
                if (tausta.getX() < tausta.getXMin() - 150)
                    iter2.remove();
            }
            // liikuta maisemaa (pilvet ym)
            Iterator<Tausta> iter3 = maisema.iterator();
            while (iter3.hasNext()) {
                Este tausta = iter3.next();
                tausta.setX(tausta.getX() - maisemaVauhti * Gdx.graphics.getDeltaTime());
                // poista ruudun ulkopuolella
                if (tausta.getX() < tausta.getXMin() - 200)
                    iter3.remove();
            }


            // ------------<   ESTEIDEN HALLINTA  <-----------------------------------------------

            //                 Kosketusnäyttö/näppäintoiminnot - - - - - - - - - - - - - - - - - -
            if (!jamppa.jamppaTormaa && Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

                kamera.unproject(touchPos);
                jamppa.setX(touchPos.x - 64 / 2);
                jamppa.setY(touchPos.y - 64 / 2);

            }



    if (Gdx.input.isKeyPressed(Keys.BACK)){
        // back-napilla takaisin main menuun
        game.setScreen(parent);
        //dispose(); // peliruutu poistoon
    }
}


    // tuodaan esteet ruutuun
    public void esteEsiin() {
        Este este = new Este();
        esteet.add(este);
        esteEsiinAika = TimeUtils.nanoTime();
    }

    // tuodaan tausta ruudun yläosaam
    public void taustatEsiin() {
        // tuodaan myös taustan "esteet"
        Tausta tausta = new Tausta(0);
        taustat.add(tausta);
        taustatEsiinAika = TimeUtils.nanoTime();
    }
    // tuodaan
    // tausta ruudun yläosaam
    public void maisemaEsiin() {
        // tuodaan myös taustan maisema (pilvet tms)
        Tausta tausta = new Tausta(1);
        maisema.add(tausta);
        maisemaEsiinAika = TimeUtils.nanoTime();
    }



    // PIIRRÄ ruutuun Jampat ja muut -------------------------------------------------------
    public void piirraObjektit() {

        // piirrä tausta
        game.batch.draw(taivaskuva,0,480-taivaskuva.getHeight());

        // piirrä maisema
        for (Este tausta : maisema) {
            game.batch.draw(tausta.getEsteKuva(), tausta.getX(), tausta.getY());
        }

        // piirrä taustat
        for (Este tausta : taustat) {
            game.batch.draw(tausta.getEsteKuva(), tausta.getX(), tausta.getY());
        }

        // piirrä kolikko


        // piirrä esteet
        for (Este este : esteet) {
            game.batch.draw(este.getEsteKuva(), este.getX(), este.getY());
        }

        // piirrä Jamppa

        stateTime += Gdx.graphics.getDeltaTime()/2; // Accumulate elapsed animation time
        TextureRegion jamppaFrame = walkAnimation.getKeyFrame(stateTime, true);
        game.batch.draw(jamppaFrame,  jamppa.getX(), jamppa.getY()); // Draw current frame at (50, 50)




        // piirrä ruohonleikkuri

        // SAVU-efektin sijainnin update ja piirto, jos Jamppa törmää
        if (jamppa.jamppaTormaa) {
            pe.update(Gdx.graphics.getDeltaTime());
            pe.setPosition(jamppa.getX() + 50, jamppa.getY() + 10);  // set the position
            pe.draw(game.batch, Gdx.graphics.getDeltaTime()); // draw it
        }

    }



    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        //    omaMusic.play();
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

    @Override // SpriteBatches and Textures must always be disposed
    public void dispose() {
        este.getEsteKuva().dispose();
        jamppa.getJamppaKuva().dispose();
        taivaskuva.dispose();
        hitSound.dispose();
        // rainMusic.dispose();
    }

}
