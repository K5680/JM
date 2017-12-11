package com.lonestones.jamppamaalla;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

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
    public String tyyppi = " ";
    private boolean leikattu;
    public Texture ruohoLeikattu;


    public Este(int tyyp) {

        // aseta x ja y (puuraja = yläreuna)
        y = MathUtils.random(0, JamppaMaalla.Puuraja-100);
        x = 800;
        xMin = -300;

        // lataa estekuvat, arpomalla tyyppi
        if (tyyp != 0)
            esteTyyppi = tyyp;
        else
            esteTyyppi = MathUtils.random(0,17);

        switch (esteTyyppi) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                esteKuva = new Texture(Gdx.files.internal("ruoho2.png"));
                tyyppi = "ruoho";
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                esteKuva = new Texture(Gdx.files.internal("ruoho.png"));
                tyyppi = "ruoho";
                break;
            case 13:
            case 14:
                esteKuva = new Texture(Gdx.files.internal("latakko.png"));
                tyyppi = "latakko";
                break;
            case 15:
            case 16:
                esteKuva = new Texture(Gdx.files.internal("kivi.png"));
                tyyppi = "kivi";
                break;
            case 17:
                esteKuva = new Texture(Gdx.files.internal("kolikko.png"));
                tyyppi = "kolikko";
                break;
            case 18:
            // "maaliin" tullessa talli esille
                esteKuva = new Texture(Gdx.files.internal("talli.png"));        // talli
                x = 800;
                y = 10;
                tyyppi ="talli";
                break;
        }

        // TODO: LISÄÄ NITROT!


        // tähän ehkä parempi systeemi? taulukko? samasta kuvasta framet?
        ruohoLeikattu = new Texture(Gdx.files.internal("ruoho_leikattu.png"));

        // tee esteen rectangle saman kokoiseksi kuin kuva
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
