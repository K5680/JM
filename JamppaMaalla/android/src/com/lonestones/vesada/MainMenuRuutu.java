package com.lonestones.vesada;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by Vesada on 29.11.2017.
 * */

// Screens contain methods from ApplicationListener objects + new methods like show and hide (lose focus).
public class MainMenuRuutu implements Screen {

    final JamppaMaalla game;
    OrthographicCamera camera;

    public MainMenuRuutu(final JamppaMaalla peli) {
        game = peli;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 150, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
            game.font.draw(game.batch, "Jamppa Maalla ", 100, 150);
            game.font.draw(game.batch, "Tap tap!", 100, 100);
        game.batch.end();

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
