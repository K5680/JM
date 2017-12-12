package com.lonestones.jamppamaalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Vesada on 12.12.2017.
 **/

public class LoppuRuutu implements Screen {

    private final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture ruutu;
    private Stage stage;
    private int keratytKolikot;
    private Preferences pref;
    private int palkka;
    private int taskurahat;
    private Label title;
    private GlyphLayout tekstiLayout = new GlyphLayout(); // tarvitaan fonttien säätämiseen
    private Ruohonleikkuri leikkuri;
    private Jamppa jamppa;
    private ParticleEffect pe;  // savu- ym efektit
    private long ajastin;
    private Music loppuMusic;

    public LoppuRuutu(final JamppaMaalla peli) {
        this.game = peli;
        stage = new Stage(new ScreenViewport());    // ruudun "näyttämö"
        ruutu = new Texture(Gdx.files.internal("loppukuva.png"));

        pref =  Gdx.app.getPreferences("JamppaMaallaPrefs");  // haetaan tallennetut arvot
        haePrefs();                                                 //

        jamppa = new Jamppa(7);
        jamppa.setXmax(2000);
        jamppa.setXmin(-500);
        jamppa.setX(-500);
        leikkuri = new Ruohonleikkuri();
        leikkuri.setTaso(pref.getInteger("leikkuri"));

        loppuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/dreams_on_prairie_by_vesada.mp3"));

        // partikkeliefektit
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("tuliefekti3.p"),Gdx.files.internal(""));
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        pe.start();

        JamppaMaalla.gameSkin.getFont("milkshake2").getData().setScale(stage.getWidth()/1400);   // skaalataan fonttia hiukan ruutuun sopivaksi

        int enkka = pref.getInteger("enkka");
        // lisätään "gameSkin":istä tyyli, jolla tekstit ja napit tehdään ruutuun
        Label rahat = new Label("Kaikkien aikojen suurin taskurahapotti:" + " " + enkka, JamppaMaalla.gameSkin, "default");
        rahat.setAlignment(Align.left);
        rahat.setX((stage.getWidth()*1/10));
        rahat.setY((stage.getHeight()*6/10));
        stage.addActor(rahat);  // lisätään "näyttämölle" "näyttelijä"

        Label omatRahat = new Label("Sinun tienestisi: " + " " + taskurahat, JamppaMaalla.gameSkin,"black");
        omatRahat.setAlignment(Align.left);
        omatRahat.setX((stage.getWidth()*1/10));
        omatRahat.setY((stage.getHeight()*5/10));
        stage.addActor(omatRahat);  // lisätään "näyttämölle" "näyttelijä"


        // gameSkin nappi
        ImageTextButton playButton = new ImageTextButton("Alkuun",JamppaMaalla.gameSkin);
        playButton.setWidth(stage.getWidth()/4);
        playButton.setHeight(stage.getHeight()/7);
        playButton.setPosition((stage.getWidth()-playButton.getWidth()-20),20);
        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuRuutu(game));    // takaisin hommiin
                dispose();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(playButton); // lisätään "näyttämölle" "näyttelijä"

        ajastin = TimeUtils.nanoTime();

        // otetaan "back" -nappula normikäyttöön (peliruudussa napataan sen toiminto)
        Gdx.input.setCatchBackKey(true);   // ulos pelistä back-napilla
        loppuMusic.play();
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();    // updates all of the Actions connected to an Actor

        // iso jamppa kulkee ruudun ohi
        if (TimeUtils.nanoTime() > ajastin + TimeUtils.millisToNanos(12000)) {
            jamppa.setX(jamppa.getX() + 15f);
            if (jamppa.getX() > (stage.getWidth() + 100)) {
                ajastin = TimeUtils.nanoTime();
                jamppa.setX(-500);
            }
        }

        stage.getBatch().begin();
            stage.getBatch().draw(ruutu, 0, 0, stage.getWidth(),stage.getHeight());

            stage.getBatch().draw(leikkuri.getLeikkuriKuvaKaanto(), stage.getWidth()*2/4, stage.getHeight()*1/5, stage.getWidth()*4/10, stage.getHeight()*5/10);

            pe.update(Gdx.graphics.getDeltaTime());
            pe.setPosition(stage.getWidth()*2/4+stage.getWidth()*1/10, stage.getHeight()*1/5+stage.getHeight()*2/10);  // set the position
            pe.draw(stage.getBatch(), Gdx.graphics.getDeltaTime()); // draw it

            stage.getBatch().draw(jamppa.getJamppaKuva(), jamppa.getX(),-300);
        stage.getBatch().end();
        stage.draw();

        if(pe.isComplete()) pe.reset();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            // back-napilla takaisin main menuun
            //  jamppaMusic.stop();

            game.setScreen(new MainMenuRuutu(game));
            loppuMusic.stop();
            dispose();
        }
    }


    private void haePrefs() {
        keratytKolikot = pref.getInteger("kolikot");
        palkka = (int)(pref.getFloat("leikkaustarkkuus")*3);
        taskurahat = (pref.getInteger("taskurahat"))+ palkka + keratytKolikot*100;

        pref.putInteger("taskurahat", taskurahat);
        pref.putInteger("kolikot", 0);
        int enkka = (pref.getInteger("enkka"));
        if (taskurahat > enkka) pref.putInteger("enkka", taskurahat); // jos paras rahatilanne koskaan -> tallennus
        pref.flush();
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
        loppuMusic.dispose();
        ruutu.dispose();
        stage.dispose();
    }
}
