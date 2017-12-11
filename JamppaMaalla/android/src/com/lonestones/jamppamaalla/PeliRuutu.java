package com.lonestones.jamppamaalla;

/**
 * Created by Vesada on 29.11.2017.
 */


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
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

import java.io.FileOutputStream;
import java.util.Iterator;

import static android.provider.Telephony.Mms.Part.FILENAME;

// Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).
public class PeliRuutu implements Screen {
    private JamppaMaalla game;          // "final" defines an entity that can only be assigned once

    private Texture taivaskuva;

    private OrthographicCamera kamera;
    private Sound hitSound;
    private Sound kolikkoSound;
    private Sound latakkoSound;
    private Music jamppaMusic;
    private boolean latakkoSoundPlaying;
    private long latakkoSoundPlayingAjastin;

    private Array<Este> esteet;
    private Array<Tausta> taustat;
    private Array<Tausta> maisema;
    private Este este;
    private Tausta tausta;

    private Iterator<Este> iter;
    private Iterator<Tausta> iter2;
    private Iterator<Tausta> iter3;

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
    private int lopunAlku;  // kentän vaihtuminen
    private double kentassaNurmikoita = 4;     // TODO 100

    private Preferences pref;



    public PeliRuutu(final JamppaMaalla peli) {
        this.game = peli;


       // Jamppa kehiin
        jamppa = new Jamppa(0);
        leikkuri = new Ruohonleikkuri();
        leikkuri.setX(jamppa.getX());   // leikkuri jamppan käteen
        leikkuri.setY(jamppa.getY());

        pref =  Gdx.app.getPreferences("JamppaMaallaPrefs"); // haetaan prefsseistä tallennetut arvot
        haePrefs(); // haetaan preferenceistä talletetut tiedot (rahat, leikkuri ym.)


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
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/244983__ani-music__ani-big-pipe-hit.wav"));
        jamppaMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/old_folks_by_vesada.mp3"));
        kolikkoSound = Gdx.audio.newSound(Gdx.files.internal("sounds/363090__fractalstudios__coins-being-dropped-assorted.mp3"));
        latakkoSound = Gdx.audio.newSound(Gdx.files.internal("sounds/390005__morganveilleux__water-splash-small.mp3"));
        jamppaMusic.setLooping(true);

        // create the camera and the SpriteBatch
        kamera = new OrthographicCamera();
        kamera.setToOrtho(false, 800, 480);     // ruudun koko "unitteina"

        taivaskuva = new Texture(Gdx.files.internal("taivas2.png"));         // taivas ruudun yläreunaan

        // luodaan "esteet"-taulukko  &  taustan "esteet"
        esteet = new Array<Este>();
        taustat = new Array<Tausta>(0);
        maisema = new Array<Tausta>(1);

        // ja spawnataan esteet esiin
        esteEsiin(0);
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
        Gdx.gl.glClearColor((0/255.0f), (255/255.0f), (0/255.0f), 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        kamera.update(); // tell the camera to update its matrices
        game.batch.setProjectionMatrix(kamera.combined);    // tell the SpriteBatch to render in the coordinate system specified by the camera


        /*   Pixmap pixmap = new Pixmap(leikkuri.getRectxW(),leikkuri.getRectyW(), Pixmap.Format.RGBA8888);        // testikäyttöön neliö
           Texture tarkistusnelio = new Texture(pixmap);
        */

        if (nurmeaLeikattu > 0) prosentti =  100*(nurmeaLeikattu/nurmiPotentiaali); // lasketaan paljonko nurmea leikattu prosentuaalisesti
        double round = Math.pow(10,1);
        prosentti = Math.round(prosentti*round)/round;


        // aloita "batch", piirrä "Jamppa" ja esteet ym                    // BATCH BEGIN
        game.batch.begin();
            piirraObjektit(); // objektit ruutuun
            // tekstit ruutuun
            game.font.draw(game.batch, "Törmäilyt: " + tormaysMaara + "  Leikkaustarkkuus:  " + prosentti + "  Kolikot: " + kerätytKolikot, 0, 480);

        /*  // väliaikainen testineliö törmäyksille
            Gdx.app.log("tag","msg");
            pixmap.setColor(100,50,0,100);
            pixmap.fillRectangle(0,0,leikkuri.getRectxW(),leikkuri.getRectyW());
            tarkistusnelio.draw(pixmap,0,0);

           game.batch.draw(tarkistusnelio, leikkuri.getX()+leikkuri.getHienosaatoX(),leikkuri.getY()+leikkuri.getHienosaatoY(), leikkuri.getRectxW(),leikkuri.getRectyW());
        */
        game.batch.end();                                                   // BATCH END


        // äänien nollaus
        if (latakkoSoundPlaying) {
            if (TimeUtils.nanoTime() > TimeUtils.nanoTime() + latakkoSoundPlayingAjastin){
                latakkoSoundPlaying = false;
            }
        }


        // Jamppa osuu esteeseen, pysäytetään ruudunvieritys, mitä tapahtuu jampalle?
        if ((jamppa.jamppaTormaa || leikkuri.leikkuriTormaa) && lopunAlku < 3){

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
                pe.reset();     // savuefektin resetointi
                vesi.reset();   // vesiefektin resetointi
                //     if (pe.isComplete())   // tarpeen?
                }

        } else {
            if (lopunAlku < 3) {    // Kentän lopussa ei voi ohjata
                //  Kosketusnäyttötoiminnot - - - - - - - - - - - - - - - - - -
                if (Gdx.input.isTouched()) {
                    Vector3 touchPos = new Vector3();
                    touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

                    kamera.unproject(touchPos);
                    jamppa.setX(touchPos.x - 64 / 2); // jamppa vastaamaan kosketusnäytön ohjausta
                    jamppa.setY(touchPos.y - 64 / 2);

                    leikkuri.setX(jamppa.getX());   // leikkuri jamppan käteen
                    leikkuri.setY(jamppa.getY());
                }
            } else if (lopunAlku < 4){    // Talliin automaattiohjaus
                if (jamppa.getY() > 30) {
                    jamppa.setY(jamppa.getY() - 5);
                } else if (jamppa.getY() < 20) {
                    jamppa.setY(jamppa.getY() + 5);
                }

                taustatEsiinAika = TimeUtils.nanoTime();   // käytetään tässä jampan loppujuoksuun
                leikkuri.setX(jamppa.getX());   // leikkuri jamppan käteen
                leikkuri.setY(jamppa.getY());
            }
        }


        esteidenHallinta();

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
                            kolikkoSound.play();
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
                        } else if (este.getTyyppi() == "ruoho") {   // jos osutaan nurmeen, lisätään leikattujen laskuriin kerran
                            if (!este.getLeikattu()) {
                                nurmeaLeikattu += 1;
                                este.setLeikattu(true);
                            }
                        } else if (este.getTyyppi() == "latakko") {
                            leikkuri.leikkuriCrash(este.getTyyppi());
                            if (!latakkoSoundPlaying) {
                                latakkoSound.play();
                                latakkoSoundPlaying = true;
                                latakkoSoundPlayingAjastin = TimeUtils.nanoTime();
                            }
                        } else if (este.getTyyppi() == "talli") {
                            // TODO talli sound
                            if (lopunAlku == 2) {   // seuraava kohta lopunAlku-switchissä, kun osutaan talliin
                                lopunAlku = 3;

                                esteEsiinAika = TimeUtils.nanoTime();
                                hitSound.play();
                                Log.d("    C>  talliosumassa", " " + lopunAlku + " " + esteEsiinAika);

                            }
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
                if (tausta.getX() < tausta.getXMin() - 300)
                    iter2.remove();
            }
            // liikuta maisemaa (pilvet ym)
            Iterator<Tausta> iter3 = maisema.iterator();
            while (iter3.hasNext()) {
                Este tausta = iter3.next();
                tausta.setX(tausta.getX() - maisemaVauhti * Gdx.graphics.getDeltaTime());
                // poista ruudun ulkopuolella
                if (tausta.getX() < tausta.getXMin() - 300)
                    iter3.remove();
            }
            // ------------<   ESTEIDEN HALLINTA  <-----------------------------------------------


    if (Gdx.input.isKeyPressed(Keys.BACK)){
        // back-napilla takaisin main menuun
        jamppaMusic.stop();

        game.setScreen(new MainMenuRuutu(game));
                    //game.setScreen(parent);

        //dispose(); // peliruutu poistoon
    }
}


// ------------>   ESTEIDEN HALLINTA  >-----------------------------------------------
public void esteidenHallinta() {
    if (nurmiPotentiaali < kentassaNurmikoita) {    // lisätään esteitä kunnes "pelto" loppuu
        // tehdään uusi este jos aikaa kulunut tarpeeksi
        if (TimeUtils.nanoTime() - esteEsiinAika > 250000000) {
            esteEsiin(0);
            esteEsiinAika = TimeUtils.nanoTime();
        }

    } else switch (lopunAlku) { // kentän loppuminen, talliin ajo ym.
        case 0:
            esteEsiinAika = TimeUtils.nanoTime();
            lopunAlku = 1;
            break;
        case 1:
            if (TimeUtils.nanoTime() - esteEsiinAika > TimeUtils.millisToNanos(2000)) {
                esteEsiin(18);  // tyyppi 18 = talli
                lopunAlku = 2;
                esteEsiinAika = TimeUtils.nanoTime();
            }
            break;
        case 2:     // viivästys tallin esiintulon jälkeen
            if (TimeUtils.nanoTime() - esteEsiinAika > TimeUtils.millisToNanos(2000)) {
                lopunAlku = 3;
                esteEsiinAika = TimeUtils.nanoTime();
            }
            break;
        // Case 3 tulee myös overlaps(talli):in kautta, mutta varulta muutenkin (jos ajaa tallin ohi?)
        case 3:
            if (TimeUtils.nanoTime() - esteEsiinAika > TimeUtils.millisToNanos(1000)){
                lopunAlku = 4;
                esteEsiinAika = TimeUtils.nanoTime();

            }
            break;
        case 4:
            leikkuri.leikkuriCrash("talli");   // viedään osuman tyyppi leikkuriluokkaan
            leikkuri.setX(-300);                      // leikkuri jää talliin, jamppa säntää juoksuun
            if (TimeUtils.nanoTime() - esteEsiinAika > TimeUtils.millisToNanos(2000)){
                lopunAlku = 5;
            }
            break;
        case 5:
            jamppaMusic.stop();
            talletaTiedot();    // talleta muuttujat = rahat, leikkurin malli ym
            game.setScreen(new ValiRuutu(game));
            break;
    }
}



    public void talletaTiedot() {
        pref.putInteger("kolikot", kerätytKolikot);
        pref.putFloat("leikkaustarkkuus", (float)prosentti);
        pref.putInteger("leikkuri", leikkuri.getTaso());    // tämä kauppaan
        pref.flush();
    }



    public void haePrefs() {
        if(pref==null){
            pref.putInteger("enkka", 0);
            pref.putInteger("kolikot", 0);
            pref.putInteger("taskurahat", 0);
            pref.putFloat("leikkaustarkkuus", 0);
            pref.putInteger("leikkuri", 0);
            pref.putBoolean("soundOn", true);
            pref.flush();
        } else {
            kerätytKolikot = pref.getInteger("kolikot");
            leikkuri.setTaso(pref.getInteger("leikkuri"));
        }
    }



    // tuodaan esteet ruutuun
    public void esteEsiin(int tyyp) {
        Este este = new Este(tyyp);
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

        if (lopunAlku > 0)  {   // kentän lopussa talli piirretään jampan päälle, muut esteet alle
            game.batch.draw(jamppa.getJamppaKuva(), jamppa.getX(), jamppa.getY());              // piirrä Jamppa-freimi
            game.batch.draw(leikkuri.getLeikkuriKuva(),  leikkuri.getX(), leikkuri.getY());     // piirrä ruohonleikkuri
        }

        // piirrä esteet
        for (Este este : esteet) {
            if (este.tyyppi == "kolikko") { // // kolikot liian isoja ruutuun, skaalataan reaaliajassa
                game.batch.draw(este.getEsteKuva(), este.getX(), este.getY(), este.getEsteKuva().getWidth()/2, este.getEsteKuva().getHeight()/2);
            } else {
                game.batch.draw(este.getEsteKuva(), este.getX(), este.getY());
            }
        }
        if (lopunAlku < 1) {
            game.batch.draw(jamppa.getJamppaKuva(), jamppa.getX(), jamppa.getY());              // piirrä Jamppa-freimi
            game.batch.draw(leikkuri.getLeikkuriKuva(),  leikkuri.getX(), leikkuri.getY());     // piirrä ruohonleikkuri
        }


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
