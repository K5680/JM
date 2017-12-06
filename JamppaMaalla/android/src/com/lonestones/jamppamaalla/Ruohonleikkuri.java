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
    private int taso;

    public int getHienosaatoX() {
        return hienosaatoX;
    }

    public int getHienosaatoY() {
        return hienosaatoY;
    }

    public int getRectxW() {
        return rectxW;
    }

    public int getRectyW() {
        return rectyW;
    }

    private int hienosaatoX = 35;
    private int hienosaatoY = 10;
    private int rectxW;
    private int rectyW;



    public Ruohonleikkuri() {
        leikkuriRect = new Rectangle(); // Leikkurin rect, jonka törmäyksiä esteisiin tarkkaillaan
        setTaso(3);     // Ladataan leikkurin freimit, samaa funktiota käytetään kun myöhemmin päivitetään leikkurin malli

        x =  100;
        y = 20;         // bottom left corner of the jamppa is 20 pixels above the bottom screen edge

        stateTime = 0f; // Instantiate a SpriteBatch for drawing and reset the elapsed animation time to 0
    }



    public Rectangle getleikkuriRect() {
        return leikkuriRect;
    }

    public void setX(float xi) {
        x = xi+hienosaatoX;
        leikkuriRect.x = xi;    // säädetään rect leikkurin mukaan paremmin kohdalleen
    }

    public void setY(float yi) {
        y = yi+hienosaatoY;
        leikkuriRect.y = yi;
    }


    public void leikkuriCrash() {
        leikkuriTormaa = true;
        leikkuriMaissa = TimeUtils.nanoTime();

    }

    public void setTaso(int leikkurinTyyppi) {
        taso = leikkurinTyyppi;

        // Load the sprite sheet as a Texture. Ruohonleikkurin tason mukaan ladataan eri kuvat
        switch (taso) {
            case 0:
                leikkuriLiikeKuva = new Texture(Gdx.files.internal("leikkuri_anim.png"));
                rectxW = 20;    // rect koko
                rectyW = 15;    //
                hienosaatoX = 35;
                break;
            case 1:
                leikkuriLiikeKuva = new Texture(Gdx.files.internal("leikkuri2_anim.png"));
                rectxW = 40;    // rect koko
                rectyW = 15;    //
                hienosaatoX = 35;
                break;
            case 2:
                leikkuriLiikeKuva = new Texture(Gdx.files.internal("leikkuri3_anim.png"));
                rectxW = 40;    // rect koko
                rectyW = 20;    //
                hienosaatoX = 35;
                break;
            case 3:
                leikkuriLiikeKuva = new Texture(Gdx.files.internal("leikkuri4_anim.png"));
                rectxW = 40;    // rect koko
                rectyW = 20;    //
                hienosaatoX = 0;
                break;
        }

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

        leikkuriRect.width = rectxW;                // asetetaan rect koko
        leikkuriRect.height =  rectyW;              // leikkurin tyypin mukaan
    }


    public TextureRegion getLeikkuriKuva() {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
        TextureRegion leikkuriKuva = walkAnimation.getKeyFrame(stateTime, true);
        return leikkuriKuva;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
