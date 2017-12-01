package com.lonestones.jamppamaalla;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Omistaja on 29.11.2017.
 */

public class Este {

    public Texture esteKuva;
    public int esteTyyppi;
    public float x;
    public float y;
    private float yMax;
    private float xMin;
    private float yMin;
    private Rectangle esteRect;


    public Este() {

        // lataa estekuva, arpomalla tyyppi
        esteTyyppi = MathUtils.random(0,1);
        switch (esteTyyppi) {
            case 0:
                esteKuva = new Texture(Gdx.files.internal("kivi.png"));
                break;
            case 1:
                esteKuva = new Texture(Gdx.files.internal("kolikko.png"));
                break;
        }

        // aseta x ja y (puuraja = yläreuna)
        y = MathUtils.random(0, JamppaMaalla.Puuraja-100);
        x = 800;
        xMin = -100;

        // tee esteen rectangle
        esteRect = new Rectangle();
        esteRect.width = 64;
        esteRect.height = 64;
        esteRect.x = x;
        esteRect.y = y;
    }

    public Rectangle getEsteRect() {
        return esteRect;
    }

    public void setX(float xi) {

        // pysy rajoissa
        if (x < xMin)
            x = xMin;
        x = xi;
        esteRect.x = xi;
    }

    public void setY(float yi) {
        // pysy rajoissa
        // ...
        y = yi;
        esteRect.y = yi;
    }


    public float getX() {
        return x;
    }

    public float getXMin() {return xMin; }

    public float getY() {
        return y;
    }

    public Texture getEsteKuva() {
        return esteKuva;
    }

}
