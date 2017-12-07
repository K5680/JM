package com.lonestones.jamppamaalla;

/**
 * Created by Vesada on 29.11.2017.
 */


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

// Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).
public class PeliRuutu implements Screen {
    private final JamppaMaalla game;     // "final" defines an entity that can only be assigned once
    private final Screen parent;         // MainMenuRuutu, jotta siihen voidaan palata

    private Texture taivaskuva;
    private OrthographicCamera kamera;
    private Sound hitSound;
    private Music jamppaMusic;

    private Array<Este> esteet;
    private Array<Tausta> taustat;
    private Array<Tausta> maisema;
    private Este este;
    private Tausta tausta;
    private long taustaIntervalli = 1000;
    private long perusVauhti = 250;
    private long esteVauhti = perusVauhti;
    private long maisemaVauhti = perusVauhti-50;

    private long esteEsiinAika;
    private long taustatEsiinAika;
    private long maisemaEsiinAika;
    private int tormaysMaara;
    private Jamppa jamppa;
    private Ruohonleikkuri leikkuri;

    private Stage stage; // onko oltava?
    private ParticleEffect pe;  // savu- ym efektit
    private ParticleEffect vesi;

    public static boolean peliAlkaaNyt = true; // eka käynnistys
    private double nurmeaLeikattu;
    private int kerätytKolikot;
    private double prosentti;
    private double nurmiPotentiaali;



    public PeliRuutu(final JamppaMaalla peli, Screen parent) {
        this.parent = parent;
        this.game = peli;
       // stage = new Stage(new ScreenViewport()); // onko oltava?

        // Jamppa kehiin
        jamppa = new Jamppa();
        leikkuri = new Ruohonleikkuri();
        leikkuri.setX(jamppa.getX());   // leikkuri jamppan käteen
        leikkuri.setY(jamppa.getY());

        // partikkeliefektit
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("tuliefekti3.p"),Gdx.files.internal(""));
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        vesi = new ParticleEffect();
        vesi.load(Gdx.files.internal("vesiefekti2.p"),Gdx.files.internal(""));
        vesi.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);

        // otetaan "back" -nappula haltuun
        Gdx.input.setCatchBackKey(true);

        // lataa äänet ja musiikki
        hitSound = Gdx.audio.newSound(Gdx.files.internal("244983__ani-music__ani-big-pipe-hit.wav"));
        jamppaMusic = Gdx.audio.newMusic(Gdx.files.internal("old_folks_by_vesada.mp3"));
        jamppaMusic.setLooping(true);

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
        vesi.start();
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


           Pixmap pixmap = new Pixmap(leikkuri.getRectxW(),leikkuri.getRectyW(), Pixmap.Format.RGBA8888);        // testikäyttöön neliö
           Texture tarkistusnelio = new Texture(pixmap);

        if (nurmeaLeikattu > 0) prosentti =  100*(nurmeaLeikattu/nurmiPotentiaali); // lasketaan paljonko nurmea leikattu prosentuaalisesti
        double round = Math.pow(10,1);
        prosentti = Math.round(prosentti*round)/round;




        // aloita "batch", piirrä "Jamppa" ja esteet ym                    // BATCH BEGIN
        game.batch.begin();
            piirraObjektit(); // objektit ruutuun
            game.font.draw(game.batch, "Törmäilyt: " + tormaysMaara + "  Leikkaustarkkuus:  " + prosentti + "  Kolikot: " + kerätytKolikot, 0, 480); // teksti ruutuun

            // väliaikainen testineliö törmäyksille
            Gdx.app.log("tag","msg");
            pixmap.setColor(100,50,0,100);
            pixmap.fillRectangle(0,0,leikkuri.getRectxW(),leikkuri.getRectyW());
            tarkistusnelio.draw(pixmap,0,0);

           game.batch.draw(tarkistusnelio, leikkuri.getX()+leikkuri.getHienosaatoX(),leikkuri.getY()+leikkuri.getHienosaatoY(), leikkuri.getRectxW(),leikkuri.getRectyW());

        game.batch.end();                                                   // BATCH END





        // Jamppa osuu esteeseen, pysäytetään ruudunvieritys, mitä tapahtuu jampalle?
        if (jamppa.jamppaTormaa || leikkuri.leikkuriTormaa){

            esteEsiinAika = TimeUtils.nanoTime();    // nollataan ajastimia
            taustatEsiinAika = TimeUtils.nanoTime(); // koska ei liikuta
            maisemaEsiinAika = TimeUtils.nanoTime();
            taustaIntervalli = TimeUtils.millisToNanos(MathUtils.random(1000, 5000));

            if ((TimeUtils.nanoTime() > jamppa.jamppaMaissa + TimeUtils.millisToNanos(1000)) && (TimeUtils.nanoTime() > leikkuri.leikkuriMaissa + TimeUtils.millisToNanos(1000)) ) {
                jamppa.jamppaTormaa = false;
                leikkuri.leikkuriTormaa = false;
                // jamppa must go on
                esteVauhti = perusVauhti;
                maisemaVauhti = perusVauhti-50;
                pe.reset(); // savuefektin resetointi
                vesi.reset();
                //     if (pe.isComplete())   // effect reset, tarpeen?
                }

        } else {
            //                 Kosketusnäyttö/näppäintoiminnot - - - - - - - - - - - - - - - - - -
            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

                kamera.unproject(touchPos);
                jamppa.setX(touchPos.x - 64 / 2); // jamppa vastaamaan kosketusnäytön ohjausta
                jamppa.setY(touchPos.y - 64 / 2);

                leikkuri.setX(jamppa.getX());   // leikkuri jamppan käteen
                leikkuri.setY(jamppa.getY());
            }
        }


            // ------------>   ESTEIDEN HALLINTA  >-----------------------------------------------
            // tehdään uusi este jos aikaa kulunut tarpeeksi
            if (TimeUtils.nanoTime() - esteEsiinAika > 250000000) {
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

                // ---------------------------------------------------------------------------------- JAMPPA OSUU JOHONKIN
                if (este.getEsteRect().overlaps(jamppa.getJamppaRect())) {

                    if (!jamppa.jamppaTormaa) {

                        if (este.getTyyppi() == "kivi") {   // tutkitaan mihin on osuttu
                            jamppa.jamppaCrash();
                            esteVauhti = 0;
                            maisemaVauhti = 0;
                            iter.remove();
                            tormaysMaara++;
                            hitSound.play();

                        }  else if (este.getTyyppi() == "kolikko"){
                            iter.remove();
                            // kolikko sound
                            // pointseja
                            kerätytKolikot += 1;
                        }
                    }
                }
                // ----------------------------------------------------------------------------------
                // ---------------------------------------------------------------------------------- LEIKKURI OSUU JOHONKIN
                if (este.getEsteRect().overlaps(leikkuri.getleikkuriRect())) {

                    if (!leikkuri.leikkuriTormaa) {

                        if (este.getTyyppi() == "kivi") {   // tutkitaan mihin on osuttu
                            leikkuri.leikkuriCrash(este.getTyyppi());   // viedään osuman tyyppi leikkuriluokkaan
                            esteVauhti = 0;
                            maisemaVauhti = 0;
                            iter.remove();
                            tormaysMaara++;
                            hitSound.play();

                        }  else if (este.getTyyppi() == "ruoho"){   // jos osutaan nurmeen, lisätään leikattujen laskuriin kerran
                            if (!este.getLeikattu()) {
                                nurmeaLeikattu += 1;
                                este.setLeikattu(true);
                            }

                        }  else if (este.getTyyppi() == "kolikko"){

                        } else if (este.getTyyppi() == "latakko"){
                            leikkuri.leikkuriCrash(este.getTyyppi());
                        }
                    }
                }
                // ----------------------------------------------------------------------------------
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



    if (Gdx.input.isKeyPressed(Keys.BACK)){
        // back-napilla takaisin main menuun
        jamppaMusic.stop();
        game.setScreen(parent);
        //dispose(); // peliruutu poistoon
    }
}


    // tuodaan esteet ruutuun
    public void esteEsiin() {
        Este este = new Este();
        esteet.add(este);
        esteEsiinAika = TimeUtils.nanoTime();
        if (este.getTyyppi() == "ruoho") nurmiPotentiaali +=1;  // otetaan talteen nurmikon määrä
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

        // piirrä esteet
        for (Este este : esteet) {
            game.batch.draw(este.getEsteKuva(), este.getX(), este.getY());
        }

        // piirrä Jamppa
        game.batch.draw(jamppa.getJamppaKuva(),  jamppa.getX(), jamppa.getY()); // Draw current frame at (50, 50)

        // piirrä ruohonleikkuri
        game.batch.draw(leikkuri.getLeikkuriKuva(),  leikkuri.getX(), leikkuri.getY()); // Draw current frame at (50, 50)


        // SAVU-efektin sijainnin update ja piirto, jos Jamppa törmää
        if (jamppa.jamppaTormaa ||leikkuri.leikkuriTormaa) {
            if ((leikkuri.getOsumaTyyppi() == "latakko") && !(jamppa.jamppaTormaa)) {
                vesi.update(Gdx.graphics.getDeltaTime());
                vesi.setPosition(leikkuri.getX() + 100, leikkuri.getY() + 20);  // set the position
                vesi.draw(game.batch, Gdx.graphics.getDeltaTime()); // draw it
            } else {
                pe.update(Gdx.graphics.getDeltaTime());
                pe.setPosition(leikkuri.getX() + 100, leikkuri.getY() + 20);  // set the position
                pe.draw(game.batch, Gdx.graphics.getDeltaTime()); // draw it
            }
        }
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        jamppaMusic.play();
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
        jamppa.jampanJuoksu.dispose();
        taivaskuva.dispose();
        hitSound.dispose();
        jamppaMusic.dispose();
    }

}
