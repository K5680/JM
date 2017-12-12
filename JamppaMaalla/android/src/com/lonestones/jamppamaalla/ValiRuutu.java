package com.lonestones.jamppamaalla;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;


/**
 * Created by Vesada on 7.12.2017.
 */

public class ValiRuutu implements Screen{
    private final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture alkuruutu;
    private Stage stage;
    private int keratytKolikot;
    private Preferences pref;
    private int palkka;
    private int taskurahat;
    private Label title;

    public ValiRuutu(final JamppaMaalla peli) {
        this.game = peli;
        stage = new Stage(new ScreenViewport());    // ruudun "näyttämö"
        alkuruutu = new Texture(Gdx.files.internal("valiruutu.png"));

        pref =  Gdx.app.getPreferences("JamppaMaallaPrefs");  // haetaan tallennetut arvot
        haePrefs();                                                 //

        // lisätään "gameSkin":istä tyyli, jolla tekstit ja napit tehdään ruutuun
        if (keratytKolikot > 0) {   // jos on kerätty kolikoita, lisätään niiden summa ruutuun myös, muutoin pelkkä palkka
            title = new Label("" + palkka + " + " + keratytKolikot * 100, JamppaMaalla.gameSkin, "default");
        }else {
            title = new Label("" + palkka + " + " + keratytKolikot * 100, JamppaMaalla.gameSkin, "default");
        }
        title.setAlignment(Align.left);
        title.setX((stage.getWidth()*17/40));
        title.setY((stage.getHeight()*19/36));
        title.setWidth(stage.getWidth());
        stage.addActor(title);  // lisätään "näyttämölle" "näyttelijä"

        JamppaMaalla.gameSkin.getFont("milkshake2").getData().setScale(stage.getWidth()/1500);   // skaalataan fonttia hiukan ruutuun sopivaksi

        // lisätään "gameSkin":istä tyyli, jolla tekstit ja napit tehdään ruutuun
        Label rahat = new Label(""+taskurahat, JamppaMaalla.gameSkin,"default");
        rahat.setAlignment(Align.left);
        rahat.setX((stage.getWidth()*19/40));
        rahat.setY((stage.getHeight()*17/48));
        rahat.setWidth(stage.getWidth());
        stage.addActor(rahat);  // lisätään "näyttämölle" "näyttelijä"

        // gameSkin napit
        ImageTextButton kauppaButton = new ImageTextButton("Kauppaan",JamppaMaalla.gameSkin);
        kauppaButton.setWidth(stage.getWidth()/4);
        kauppaButton.setHeight(stage.getHeight()/7);
        kauppaButton.setPosition(stage.getWidth()/4-kauppaButton.getWidth()/3,stage.getHeight()/3-kauppaButton.getHeight());
        kauppaButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
               game.setScreen(new KauppaRuutu(game));   // kauppaan
               dispose();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(kauppaButton); // lisätään "näyttämölle" "näyttelijä"
        // gameSkin nappi
        ImageTextButton playButton = new ImageTextButton("Hommiin",JamppaMaalla.gameSkin);
        playButton.setWidth(stage.getWidth()/4);
        playButton.setHeight(stage.getHeight()/7);
        playButton.setPosition((stage.getWidth()/4+playButton.getWidth()),stage.getHeight()/3-playButton.getHeight());
        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new PeliRuutu(game));    // takaisin hommiin
                dispose();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(playButton); // lisätään "näyttämölle" "näyttelijä"

        Array<Actor> kolikot = stage.getActors();

        // kerätyt kolikot ruutuun
        for (int i = 0; i < keratytKolikot; i++) {
            Image kolikko = new Image(new Texture(Gdx.files.internal("kolikko.png")));
            kolikot.add(kolikko);
            kolikko.setX(1600f);
            kolikko.setY(960f);

            MoveToAction action = new MoveToAction();
            action.setPosition((stage.getWidth() * 5 / 7) + (i*(stage.getWidth()/5))/ keratytKolikot, stage.getHeight() * 8 / 12); // kolikot asettuu riviin, enemmän kolikoita -> pienemmät välit -> mahtuu ruutuun
            action.setDuration(2f);
            kolikko.addAction(action);
            stage.addActor(kolikko);
        }

        // otetaan "back" -nappula normikäyttöön (peliruudussa napataan sen toiminto)
        Gdx.input.setCatchBackKey(true);   // ulos pelistä back-napilla
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();    // updates all of the Actions connected to an Actor


        stage.getBatch().begin();
            stage.getBatch().draw(alkuruutu, 0, 0, stage.getWidth(),stage.getHeight());
            stage.getBatch().end();
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            // back-napilla takaisin main menuun
            //  jamppaMusic.stop();

            game.setScreen(new MainMenuRuutu(game));
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
        alkuruutu.dispose();
        stage.dispose();
    }
}