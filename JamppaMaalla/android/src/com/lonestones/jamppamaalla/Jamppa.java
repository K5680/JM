package com.lonestones.jamppamaalla;

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

        setX(x);
        setY(y);
    }

    public Rectangle getJamppaRect() {
        return jamppaRect;
    }

    public void setX(float xi) {

        // make sure the jamppa stays within the screen bounds
        if (x < 0)
            x = 0;
        if (x > 800 - 64)
            x = 800 - 64;
        x = xi;
        jamppaRect.x = xi;
    }

    public void setY(float yi) {
        // make sure the jamppa stays within the screen bounds
        if (y < 0)
            y = 0;
        if (y > 480 - 64)
            y = 800 - 64;

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
