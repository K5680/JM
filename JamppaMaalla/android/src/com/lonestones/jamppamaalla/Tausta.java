package com.lonestones.jamppamaalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Vesada on 30.11.2017.
 */

// periytetään taustan objektit Esteestä, koska toimivat samaan tapaan, mutta ilman törmäystä
public class Tausta extends Este {

    private int tasot;

    public Tausta(int taso) {
        super();
        tasot = taso;
        tyyppi = "tausta";

        // aseta x ja y (puuraja = yläreuna), peliä luodessa saa laittaa mihin vain x-arvoon
        if (PeliRuutu.peliAlkaaNyt)
            x = MathUtils.random(0, 800);
        else
            x = 800;

        // onko puurivistöä vai tausta pilviä
        if (tasot == 0) {
            // lataa taustan "este" -kuva, arpomalla tyyppi
            esteTyyppi = MathUtils.random(0, 7);
            switch (esteTyyppi) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    esteKuva = new Texture(Gdx.files.internal("kuusi_iso.png"));
                    break;
                case 5:
                case 6:
                    esteKuva = new Texture(Gdx.files.internal("kivi.png"));
                    break;
                case 7:
                    esteKuva = new Texture(Gdx.files.internal("talo.png"));
                    break;
            }
            y = JamppaMaalla.Puuraja - 32;

        } else {
            esteTyyppi = MathUtils.random(0, 2);
            switch (esteTyyppi) {
                case 0:
                    esteKuva = new Texture(Gdx.files.internal("pilvi1.png"));
                    break;
                case 1: case 2:
                    esteKuva = new Texture(Gdx.files.internal("pilvi2.png"));
                break;
            }
            y = JamppaMaalla.Puuraja-15;
        }
    }
}
