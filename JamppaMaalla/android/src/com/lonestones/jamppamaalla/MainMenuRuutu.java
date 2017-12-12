package com.lonestones.jamppamaalla;


import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;


/**
 * Created by Vesada on 29.11.2017.
 * */


public class MainMenuRuutu implements Screen {  // Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).

    private final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture alkuruutu;
    private Preferences pref;   // Sovelluskohtaiset tallennettavat tiedot
    private Texture nuottiKuva;
    private TextureRegion[] nuottiFrame;
    private GlyphLayout tekstiLayout = new GlyphLayout(); // tarvitaan fonttien säätämiseen
    private long ajastin = TimeUtils.nanoTime();

    // TODO äänet pois/päälle


    public MainMenuRuutu(final JamppaMaalla peli) {
        this.game = peli;
        pref =  Gdx.app.getPreferences("JamppaMaallaPrefs");   // pref:in nimi
        haePrefs();                                                   // haetaan tiedot

        game.elamat = 3;    // nollataan elämät

        // alkukuva ruutuun
        alkuruutu = new Texture(Gdx.files.internal("alkuruutu_valmis.png"));
        lataaNuotti();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 960);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 150, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        tekstiLayout.setText(game.isofont, "Täpsäytä ruutua aloittaaksesi");    // asetetaan teksti "GlyphLayoutiin"

        float nuotinX = 1400, nuotinY = 20, nuotinKorkeus = 960/5; // "nuotin" sijainti

        game.batch.begin();
            game.batch.draw(alkuruutu,0,0,1600,960);

            if (game.musiikkiOn){   // äänet päällä / pois
                game.batch.draw(nuottiFrame[0],nuotinX, nuotinY,1600/9,nuotinKorkeus);
            } else {
                game.batch.draw(nuottiFrame[1],nuotinX, nuotinY,1600/9,nuotinKorkeus);
            }

            game.font.draw(game.batch, "JamppaMaalla v" + game.versio + " / 2017 (C) Vesada",20,30);

            game.isofont.draw(game.batch, tekstiLayout, tekstiLayout.width / 2, tekstiLayout.height*3);
        game.batch.end();


        Gdx.input.setCatchBackKey(false);   // otetaan "back" -nappula normikäyttöön (peliruudussa napataan sen toiminto)

        if (Gdx.input.isTouched()) {        // kosketus
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if ((touchPos.x > nuotinX) && (touchPos.y < nuotinY+nuotinKorkeus)) {   // nuotin klikkaus, ajastin jottei vaihdu eestaas
                if (TimeUtils.nanoTime() > ajastin + TimeUtils.millisToNanos(300)) {
                    ajastin = TimeUtils.nanoTime();

                    if (game.musiikkiOn) {
                        game.musiikkiOn = false;
                    } else {
                        game.musiikkiOn = true;
                    }
                }
            } else {
                game.setScreen(new PeliRuutu(game));    // ruutua painettaessa käynnistetään peli
                dispose();
            }
        }
    }


    private void haePrefs() {
        if (pref == null) {
            pref.putInteger("enkka", 0);
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
        nuottiKuva.dispose();
        // Huom, Game.dispose() kutsutaan automaattisesti
    }

    public void lataaNuotti() {
        // lataa nuottikuva
        nuottiKuva= new Texture(Gdx.files.internal("nuotti.png"));
        // otetaan erilleen freimit = päällä pois
        TextureRegion[][] tmp = TextureRegion.split(nuottiKuva, nuottiKuva.getWidth()/2, nuottiKuva.getHeight());
        nuottiFrame = new TextureRegion[nuottiKuva.getWidth()*nuottiKuva.getHeight()];
        nuottiFrame[0] = tmp[0][0];
        nuottiFrame[1] = tmp[0][1];
        }

}
