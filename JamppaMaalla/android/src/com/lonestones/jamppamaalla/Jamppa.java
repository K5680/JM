package com.lonestones.jamppamaalla;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Omistaja on 29.11.2017.
 */

public class Jamppa {

    Texture jamppaKuva;
    Rectangle jamppa;
    private float x;
    private float y;
    private float xMax;
    private float yMax;
    private float xMin;
    private float yMin;
    private Rectangle jamppaRect;

    public Jamppa() {
        // lataa jampan kuva
        jamppaKuva = new Texture(Gdx.files.internal("jamppa.png"));

        // create a Rectangle to logically represent the jamppa
        jamppaRect = new Rectangle();
        x = 800 / 2 - 64 / 2; // center the jamppa horizontally
        y = 20; // bottom left corner of the jamppa is 20 pixels above the bottom screen edge
        jamppaRect.width = 64;
        jamppaRect.height = 64;

        xMin = -64;
        yMin = 0;
        xMax = 200;
        yMax = 300;    // yläreuna jampan liikkumisessa
    }

    public Rectangle getJamppaRect() {
        return jamppaRect;
    }

    public void setX(float xi) {

        // make sure jamppa stays within the screen bounds
        if (xi < xMin)
            xi = xMin;
        if (xi > xMax - 64)
            xi = xMax - 64;
        Log.d("X", "setX: "+x+"   xMax"+xMax);
        x = xi;
        jamppaRect.x = xi;
    }

    public void setY(float yi) {
        // make sure jamppa stays within the screen bounds
        if (yi < yMin)
            yi = yMin;
        if (yi > yMax - 64)
            yi = yMax - 64;

        Log.d("Y", "setY: "+y);
        y = yi;
        jamppaRect.y = yi;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Texture getJamppaKuva() {
        return jamppaKuva;
    }

}
