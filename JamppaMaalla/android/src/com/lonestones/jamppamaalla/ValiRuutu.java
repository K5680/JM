package com.lonestones.jamppamaalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Omistaja on 7.12.2017.
 */

public class ValiRuutu implements Screen{

   //final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture alkuruutu;
    private Stage stage;
    private Game game;


    public ValiRuutu(Game peli) {
        game = peli;
        stage = new Stage(new ScreenViewport());

        alkuruutu = new Texture(Gdx.files.internal("valiruutu.png"));

        Label title = new Label("Title Screen", JamppaMaalla.gameSkin,"font");
        title.setAlignment(Align.center);
        title.setY(Gdx.graphics.getHeight()*2/3);
        title.setWidth(Gdx.graphics.getWidth());
        stage.addActor(title);

        ImageTextButton kauppaButton = new ImageTextButton("Kauppaan",JamppaMaalla.gameSkin);
        kauppaButton.setWidth(Gdx.graphics.getWidth()/2);
        kauppaButton.setPosition(Gdx.graphics.getWidth()/4-kauppaButton.getWidth()/3,Gdx.graphics.getHeight()/3-kauppaButton.getHeight()/2);
        kauppaButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
               // game.setScreen(new PeliRuutu(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(kauppaButton);

        ImageTextButton playButton = new ImageTextButton("Hommiin",JamppaMaalla.gameSkin);
        playButton.setWidth(Gdx.graphics.getWidth()/2);
        playButton.setPosition((Gdx.graphics.getWidth()/2-playButton.getWidth()/2),Gdx.graphics.getHeight()/3-playButton.getHeight()/2);
        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                // game.setScreen(new PeliRuutu(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(playButton);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();

        stage.getBatch().begin();
        stage.getBatch().draw(alkuruutu, 0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.draw();
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
        stage.dispose();
    }
}