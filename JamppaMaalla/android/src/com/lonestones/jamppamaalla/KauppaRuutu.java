package com.lonestones.jamppamaalla;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/**
 * Created by Vesada on 9.12.2017.
 */


public class KauppaRuutu implements Screen {
    private final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture kaupparuutu_etu;
    private Texture kaupparuutu_taka;
    private Texture kaupassa;
    private Texture ovi;

    private Stage stage;
    private Stage menoStage;
    private Image kolikko;
    private int vaihe = 0;  // "animaation" vaihe
    private Jamppa jamppa;
    private Ruohonleikkuri leikkuri;

    private long ajastin;
    private long viive = 3000;
    private Sound ovenkolaus;
    private Sound hitSound;
    private Sound kolikkoSound;
    private Music jamppaJyraa;

    private Preferences pref;
    private int taskurahat;
    private int kone;
    private String[] koneentyyppi;
    private Label rahatLabel;
    private Label koneLabel;
    private Label hintaLabel;
    private int[] hinta;
    private int leikkuritullessa;


    public KauppaRuutu(final JamppaMaalla peli) {
        game = peli;

        pref =  Gdx.app.getPreferences("JamppaMaallaPrefs");  // haetaan tallennetut arvot
        haePrefs();

        leikkuritullessa = pref.getInteger("leikkuri");
        stage = new Stage(new ScreenViewport());        // "näytös", jossa ollaan kaupassa
        menoStage = new Stage(new ScreenViewport());    // "näytös", jossa J juoksee kauppaan

        jamppa = new Jamppa(2);  // otetaan ruutuun "jamppa", joka juoksee kauppaan, skaalataan 2x
        jamppa.setX(-50);
        jamppa.setY(100);
        jamppa.setXmax(Gdx.graphics.getWidth()*3/4);
        leikkuri = new Ruohonleikkuri();

        // ladataan kuvat
        kaupassa = new Texture(Gdx.files.internal("kaupassa.png"));
        kaupparuutu_etu = new Texture(Gdx.files.internal("kaupparuutu.png"));
        kaupparuutu_taka = new Texture(Gdx.files.internal("kaupparuutu2.png"));
        ovi = new Texture(Gdx.files.internal("ovi.png"));

        // ladataan äänet
        ovenkolaus = Gdx.audio.newSound(Gdx.files.internal("sounds/344360__amholma__door-open-close.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/244983__ani-music__ani-big-pipe-hit.wav"));
        kolikkoSound = Gdx.audio.newSound(Gdx.files.internal("sounds/363090__fractalstudios__coins-being-dropped-assorted.mp3"));
        jamppaJyraa = Gdx.audio.newMusic(Gdx.files.internal("sounds/shortough_by_vesada.mp3"));


        hinta = new int[4]; // Leikkurien hinnat, arvotaan jotta hinnat vaihtelee
        hinta[0] = MathUtils.random(50, 100);
        hinta[1] = 10; //MathUtils.random(1000, 2000);
        hinta[2] = 10; //MathUtils.random(4000, 7000);
        hinta[3] = 10;//MathUtils.random(10900, 15000);
        koneentyyppi = new String[4]; // Leikkurien nimet
        koneentyyppi[0] = "Basic";
        koneentyyppi[1] = "Runner";
        koneentyyppi[2] = "Grass Hop II";
        koneentyyppi[3] = "Knight Ride";



        // rahat
        rahatLabel = new Label("Taskurahaa "+taskurahat+" e", JamppaMaalla.gameSkin,"default");
        rahatLabel.setAlignment(Align.center);
        rahatLabel.setPosition(Gdx.graphics.getWidth()*2/24,Gdx.graphics.getHeight()*3/20);
        rahatLabel.setWidth(Gdx.graphics.getWidth()*1/3);
        //rahatLabel.setFontScale(4f);
        stage.addActor(rahatLabel);  // lisätään "näyttämölle" "näyttelijä"


        // Koneen tyyppi ruutuun nuolten keskelle
        koneLabel = new Label(""+koneentyyppi, JamppaMaalla.gameSkin,"over");
        koneLabel.setAlignment(Align.center);
        koneLabel.setPosition(Gdx.graphics.getWidth()*2/24,Gdx.graphics.getHeight()*2/20);
        koneLabel.setWidth(Gdx.graphics.getWidth()*1/3);
        koneLabel.setFontScale(4f);
        stage.addActor(koneLabel);  // lisätään "näyttämölle" "näyttelijä"


        // Hinta esille
        hintaLabel = new Label(""+koneentyyppi, JamppaMaalla.gameSkin,"over");
        hintaLabel.setAlignment(Align.center);
        hintaLabel.setPosition(Gdx.graphics.getWidth()*2/24,Gdx.graphics.getHeight()*1/20);
        hintaLabel.setWidth(Gdx.graphics.getWidth()*1/3);
        hintaLabel.setFontScale(4f);
        stage.addActor(hintaLabel);  // lisätään "näyttämölle" "näyttelijä"



        // gameSkin napit VASEN nuoli
        ImageTextButton vasenButton = new ImageTextButton("<",JamppaMaalla.gameSkin,"small");
        vasenButton.setWidth(Gdx.graphics.getWidth()/10);
        vasenButton.setHeight(Gdx.graphics.getHeight()/9);
        vasenButton.setPosition(10,10); // vasempaan alareunaan.setPosition(10,10); // vasempaan alareunaan
        vasenButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                kone -= 1;
                if (kone < 0) kone = 3;
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(vasenButton); // lisätään "näyttämölle" "näyttelijä"



        // gameSkin nappi OIKEA nuoli
        ImageTextButton oikeaButton = new ImageTextButton(">",JamppaMaalla.gameSkin,"small");
        oikeaButton.setWidth(Gdx.graphics.getWidth()/10);
        oikeaButton.setHeight(Gdx.graphics.getHeight()/9);
        oikeaButton.setPosition((Gdx.graphics.getWidth()*4/10),10); // sijainti
        oikeaButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                kone += 1;
                if (kone > 3) kone = 0;
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(oikeaButton); // lisätään "näyttämölle" "näyttelijä"


        // gameSkin nappi OSTA
        ImageTextButton ostaButton = new ImageTextButton("Osta",JamppaMaalla.gameSkin);
        ostaButton.setWidth(Gdx.graphics.getWidth()/8);
        ostaButton.setHeight(Gdx.graphics.getHeight()/7);
        ostaButton.setPosition((Gdx.graphics.getWidth()*5/10+10),10);   // sijainti
        ostaButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if (taskurahat >= hinta[kone]) {             // leikkurin osto jos rahat riittää
                    taskurahat = taskurahat - hinta[kone];   // vähennetään hinta taskurahoista
                    pref.putInteger("taskurahat", taskurahat);  // rahat ja uusi leikkuri talteen
                    pref.putInteger("leikkuri", kone);
                    pref.flush();
                    kolikkoSound.play();
                } else {
                    hitSound.play();
                }

            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(ostaButton); // lisätään "näyttämölle" "näyttelijä"



        // gameSkin nappi HOMMIIN
        ImageTextButton hommiinButton = new ImageTextButton("Hommiin",JamppaMaalla.gameSkin);
        hommiinButton.setWidth(Gdx.graphics.getWidth()/4);
        hommiinButton.setHeight(Gdx.graphics.getHeight()/7);
        hommiinButton.setPosition((Gdx.graphics.getWidth()*7/10),10);   // sijainti
        hommiinButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                if (leikkuritullessa != pref.getInteger("leikkuri")){   // jos leikkuri uusittu, jamppa porhaltaa uudella koneella
                        vaihe = 6;
                        jamppa.setXmax(Gdx.graphics.getWidth()+400);
                        jamppa.setX(Gdx.graphics.getWidth()+400);
                        jamppa.setY(100);
                        jamppa.setXmin(-300);
                        leikkuri.setTaso(kone); // aseta leikkurin tyyppi
                        jamppaJyraa.play();
                } else {
                game.setScreen(new PeliRuutu(game));
                dispose();
                }
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(hommiinButton); // lisätään "näyttämölle" "näyttelijä"


        // otetaan "back" -nappula normikäyttöön (peliruudussa napataan sen toiminto)
        Gdx.input.setCatchBackKey(true);   // ulos pelistä back-napilla

        ajastin = TimeUtils.nanoTime();
    }





    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if ((TimeUtils.nanoTime() > ajastin + TimeUtils.millisToNanos(viive))) {
            if (vaihe == 2) {
                vaihe = 3;
                viive = 500;
                ajastin = TimeUtils.nanoTime();
            } else if (vaihe == 3) {
                vaihe = 4;
                viive = 1000;
                ajastin = TimeUtils.nanoTime();
            }else if (vaihe == 4) {
                vaihe = 5;
            }
        }

        if (vaihe == 5) {   // Kaupan sisällä -stage

            koneLabel.setText(""+koneentyyppi[kone]);   // Päivitä valitun koneen nimi ruutuun
            hintaLabel.setText(""+hinta[kone]);         // ja hinta
            rahatLabel.setText(""+taskurahat);          // ja rahat
            stage.act();    // updates all of the Actions connected to an Actor

            stage.getBatch().begin();
                stage.getBatch().draw(kaupassa, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            stage.getBatch().end();
            stage.draw();
        } else {        // kaupan ulkopuolella
            if (vaihe == 6) {   // jamppa tulee kaupasta uuden leikkurin kanssa
                jamppa.setX(jamppa.getX() - 5f);
                    if (jamppa.getX() < -250) {    // takaisin hommiin kun jamppa mennyt ruudun ohi
                        game.setScreen(new PeliRuutu(game));
                        dispose();
                    }
            } else {    // Jamppa juoksee kauppaan -stage
                jamppa.setX(jamppa.getX() + 5f);
                if (jamppa.getX() > (Gdx.graphics.getWidth() * 3 / 7)) {
                    jamppa.setY(jamppa.getY() + 20f);
                    if (vaihe == 0) vaihe = 1;
                }
            }
            menoStage.act();    // updates all of the Actions connected to an Actor
            menoStage.getBatch().begin();
                menoStage.getBatch().draw(kaupparuutu_etu, 0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
                if (vaihe == 1 || vaihe == 2) {                     // oviaukko
                    if (vaihe == 1) ovenkolaus.play() ; vaihe = 2;  // oviääni kerran
                    menoStage.getBatch().draw(ovi, 0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
                }
                if (vaihe == 6) {   // jamppa tulee pois kaupasta, käännetty kuva on jamppa-luokassa getJamppaKuvaKaanto -metodilla haettavissa(flipattu)
                    menoStage.getBatch().draw(kaupparuutu_taka, Gdx.graphics.getWidth()/2, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
                    menoStage.getBatch().draw(jamppa.getJamppaKuvaKaanto(), jamppa.getX(), jamppa.getY());
                    menoStage.getBatch().draw(leikkuri.getLeikkuriKuvaKaanto(), jamppa.getX(), jamppa.getY(), leikkuri.getLeikkuriKuvaKaanto().getRegionWidth()*2,leikkuri.getLeikkuriKuvaKaanto().getRegionHeight()*2);
                } else {
                    menoStage.getBatch().draw(jamppa.getJamppaKuva(), jamppa.getX(), jamppa.getY());
                }
                if (vaihe < 6) menoStage.getBatch().draw(kaupparuutu_taka, Gdx.graphics.getWidth()/2, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
            menoStage.getBatch().end();

            menoStage.draw();
        }


        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            // back-napilla takaisin main menuun
            //  jamppaMusic.stop();
            game.setScreen(new MainMenuRuutu(game));
            dispose();
        }
    }


    private void haePrefs() {
        taskurahat = (pref.getInteger("taskurahat"));
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
        jamppa.jampanJuoksu.dispose();
        kaupparuutu_etu.dispose();
        kaupparuutu_taka.dispose();
        ovi.dispose();
        menoStage.dispose();
        stage.dispose();
        kolikkoSound.dispose();
        hitSound.dispose();
        jamppaJyraa.dispose();
    }
}