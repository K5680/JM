package com.lonestones.jamppamaalla;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


/**
 * Created by Vesada on 29.11.2017.
 * */


public class MainMenuRuutu implements Screen {  // Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).

    private final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture alkuruutu;
    private Preferences pref;   // Sovelluskohtaiset tallennettavat tiedot

    // TODO äänet pois/päälle


    public MainMenuRuutu(final JamppaMaalla peli) {
        game = peli;
        pref =  Gdx.app.getPreferences("JamppaMaallaPrefs");   // pref:in nimi
        haePrefs();                                                   // haetaan tiedot

        // alkukuva ruutuun
        alkuruutu = new Texture(Gdx.files.internal("alkuruutu_valmis.png"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 960);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 150, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
            game.batch.draw(alkuruutu,0,0,1600,960);
            game.isofont.draw(game.batch, "Täpsäytä ruutua aloittaaksesi ", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/6,300f,0,false);
        game.batch.end();


        Gdx.input.setCatchBackKey(false);           // otetaan "back" -nappula normikäyttöön (peliruudussa napataan sen toiminto)

        if (Gdx.input.isTouched()) {
            game.setScreen(new PeliRuutu(game));    // ruutua painettaessa käynnistetään peli
            dispose();
        }

    }


    private void haePrefs() {
        if (pref == null) {
            pref.putInteger("enkka", 0);
            pref.putBoolean("soundOn", true);
        }
        // Nollataan arvot pelin alkuun
        pref.putInteger("kolikot", 0);
        pref.putInteger("taskurahat", 0);
        pref.putFloat("leikkaustarkkuus", 0);
        pref.putInteger("leikkuri", 0);
        pref.flush();
 }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
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
        // Huom, Game.dispose() kutsutaan automaattisesti
    }
}