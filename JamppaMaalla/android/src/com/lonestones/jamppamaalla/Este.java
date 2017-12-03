package com.lonestones.jamppamaalla;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

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
    public String tyyppi;
    private boolean leikattu;
    public Texture ruohoLeikattu;

    public Este() {

        // lataa estekuva, arpomalla tyyppi
        esteTyyppi = MathUtils.random(0,2);
        switch (esteTyyppi) {
            case 0:
                esteKuva = new Texture(Gdx.files.internal("kivi.png"));
                tyyppi = "kivi";
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                esteKuva = new Texture(Gdx.files.internal("ruoho.png"));
                tyyppi = "ruoho";
                break;
            case 12:
                esteKuva = new Texture(Gdx.files.internal("kolikko.png"));
                tyyppi = "kolikko";
                break;
        }

        // tähän ehkä parempi systeemi? taulukko? samasta kuvasta framet?
        ruohoLeikattu = new Texture(Gdx.files.internal("ruoho_leikattu.png"));


        // aseta x ja y (puuraja = yläreuna)
        y = MathUtils.random(0, JamppaMaalla.Puuraja-100);
        x = 800;
        xMin = -100;

        // tee esteen rectangle
        esteRect = new Rectangle();
        esteRect.width = esteKuva.getWidth();
        esteRect.height = esteKuva.getHeight();
        esteRect.x = x;
        esteRect.y = y;

        leikattu = false;
    }

    public Rectangle getEsteRect() {
        return esteRect;
    }

    public String getTyyppi() { return tyyppi; }

    public Boolean getLeikattu() { return leikattu; }

    public void setLeikattu(boolean leik){
        leikattu = leik;
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
        if ((tyyppi == "ruoho") && (leikattu)){
            return ruohoLeikattu;
        }
        else
            return esteKuva;
    }

}
