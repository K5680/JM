package com.lonestones.jamppamaalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;


/**
 * Created by Vesada on 29.11.2017.
 * */

// Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).
public class MainMenuRuutu implements Screen {

    private final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture alkuruutu;
    private Label title;


    public MainMenuRuutu(final JamppaMaalla peli) {
        game = peli;

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

        // otetaan "back" -nappula normikäyttöön (peliruudussa napataan sen toiminto)
        Gdx.input.setCatchBackKey(false);

        if (Gdx.input.isTouched()) {
            game.setScreen(new PeliRuutu(game));
            dispose();
        }

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
    }
}