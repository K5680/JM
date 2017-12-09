package com.lonestones.jamppamaalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/**
 * Created by Vesada on 9.12.2017.
 */


public class KauppaRuutu implements Screen {
    private final JamppaMaalla game;
    private OrthographicCamera camera;
    private Texture kaupparuutu;
    private Stage stage;
    private Image kolikko;


    public KauppaRuutu(final JamppaMaalla peli) {
        game = peli;

        stage = new Stage(new ScreenViewport());    // ruudun "näyttämö"

        kaupparuutu = new Texture(Gdx.files.internal("ruutu.png"));


        // lisätään "gameSkin":istä tyyli, jolla tekstit ja napit tehdään ruutuun
        Label title = new Label("Mitäpä sais olla?", JamppaMaalla.gameSkin,"default");
        title.setAlignment(Align.left);
        title.setX((Gdx.graphics.getWidth()*17/40));
        title.setY((Gdx.graphics.getHeight()*19/36));
        title.setWidth(Gdx.graphics.getWidth());
        stage.addActor(title);  // lisätään "näyttämölle" "näyttelijä"

        int taskurahat = 200;
        // lisätään "gameSkin":istä tyyli, jolla tekstit ja napit tehdään ruutuun
        Label rahat = new Label(""+taskurahat, JamppaMaalla.gameSkin,"default");
        rahat.setAlignment(Align.left);
        rahat.setX((Gdx.graphics.getWidth()*19/40));
        rahat.setY((Gdx.graphics.getHeight()*17/48));
        rahat.setWidth(Gdx.graphics.getWidth());
        stage.addActor(rahat);  // lisätään "näyttämölle" "näyttelijä"

        // gameSkin napit
        ImageTextButton kauppaButton = new ImageTextButton("Kauppaan",JamppaMaalla.gameSkin);
        kauppaButton.setWidth(Gdx.graphics.getWidth()/4);
        kauppaButton.setHeight(Gdx.graphics.getHeight()/7);
        kauppaButton.setPosition(Gdx.graphics.getWidth()/4-kauppaButton.getWidth()/3,Gdx.graphics.getHeight()/3-kauppaButton.getHeight());
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
        stage.addActor(kauppaButton); // lisätään "näyttämölle" "näyttelijä"
        // gameSkin nappi
        ImageTextButton playButton = new ImageTextButton("Hommiin",JamppaMaalla.gameSkin);
        playButton.setWidth(Gdx.graphics.getWidth()/4);
        playButton.setHeight(Gdx.graphics.getHeight()/7);
        playButton.setPosition((Gdx.graphics.getWidth()/4+playButton.getWidth()),Gdx.graphics.getHeight()/3-playButton.getHeight());
        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new PeliRuutu(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(playButton); // lisätään "näyttämölle" "näyttelijä"



        // otetaan "back" -nappula normikäyttöön (peliruudussa napataan sen toiminto)
        Gdx.input.setCatchBackKey(true);   // ulos pelistä back-napilla
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();    // updates all of the Actions connected to an Actor

        stage.getBatch().begin();
        stage.getBatch().draw(kaupparuutu, 0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        stage.getBatch().end();
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            // back-napilla takaisin main menuun
            //  jamppaMusic.stop();

            game.setScreen(new MainMenuRuutu(game));
        }
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