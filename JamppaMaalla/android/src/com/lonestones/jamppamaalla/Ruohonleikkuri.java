package com.lonestones.jamppamaalla;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Omistaja on 29.11.2017.
 */

public class Ruohonleikkuri {
    Rectangle leikkuri;
    private float x;
    private float y;

    private Rectangle leikkuriRect;
    public boolean leikkuriTormaa = false;
    public long leikkuriMaissa;

    //animaatio
    private float stateTime;    // animaation nopeus
    private static final int FRAME_COLS = 2, FRAME_ROWS = 2;
    Animation<TextureRegion> walkAnimation; // frame type = textureregion
    private Texture leikkuriLiikeKuva;
    TextureRegion jamppaKuva;


    public Ruohonleikkuri() {
        // lataa leikkurin kuva

        // Load the sprite sheet as a Texture
        leikkuriLiikeKuva = new Texture(Gdx.files.internal("leikkuri_anim.png"));

        TextureRegion[][] tmp = TextureRegion.split(leikkuriLiikeKuva,
                leikkuriLiikeKuva.getWidth() / FRAME_COLS,
                leikkuriLiikeKuva.getHeight() / FRAME_ROWS);

        TextureRegion[] leikkuriFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                leikkuriFrames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation<TextureRegion>(0.1f, leikkuriFrames);
        TextureRegion leikkuriKuva = walkAnimation.getKeyFrame(stateTime, true);

        leikkuriRect = new Rectangle();               // rect, jonka törmäyksiä esteisiin tarkkaillaan
        x =  150;
        y = 20; // bottom left corner of the jamppa is 20 pixels above the bottom screen edge
        leikkuriRect.width = 20;                // rect koko, säädettävä kohdalleen
        leikkuriRect.height =  20;              // rect koko, säädettävä kohdalleen

        stateTime = 0f; // Instantiate a SpriteBatch for drawing and reset the elapsed animation time to 0
    }

    public Rectangle getleikkuriRect() {
        return leikkuriRect;
    }

    public void setX(float xi) {
        x = xi;
        leikkuriRect.x = xi+35;
    }

    public void setY(float yi) {
        y = yi;
        leikkuriRect.y = yi+10;
    }


    public void leikkuriCrash() {
        leikkuriTormaa = true;
        leikkuriMaissa = TimeUtils.nanoTime();

    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public TextureRegion getLeikkuriKuva() {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
        TextureRegion leikkuriKuva = walkAnimation.getKeyFrame(stateTime, true);
        return leikkuriKuva;

    }

}
