package com.lonestones.jamppamaalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Omistaja on 29.11.2017.
 */

public class Jamppa {

    Rectangle jamppa;
    private float x;
    private float y;
    private float xMax;
    private float yMax;
    private float xMin;
    private float yMin;
    private Rectangle jamppaRect;
    public boolean jamppaTormaa = false;
    public long jamppaMaissa;

    //animaatio
    private float stateTime;    // animaation nopeus
    private static final int FRAME_COLS = 3, FRAME_ROWS = 2;
    private Animation<TextureRegion> walkAnimation; // frame type = textureregion
    Texture jampanJuoksu;



    // TODO Jampan rypäsy

    public Jamppa(int skaalaus) {
        lataaFreimit(skaalaus);     // skaalataanko jamppakuvaa ja minkä verran, menosuunta

        jamppaRect = new Rectangle();               // rect, jonka törmäyksiä esteisiin tarkkaillaan
        x = 100;
        y = 20; // bottom left corner of the jamppa is 20 pixels above the bottom screen edge
        jamppaRect.width = 30; // rect koko, säädettävä kohdalleen
        jamppaRect.height =  30;                    // rect koko, säädettävä kohdalleen
        xMin = 100;
        yMin = 0;
        xMax = 200;
        yMax = JamppaMaalla.Puuraja;    // yläreuna jampan liikkumisessa

        stateTime = 0f; // Instantiate a SpriteBatch for drawing and reset the elapsed animation time to 0
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
       // Log.d("X", "setX: "+x+"   xMax"+xMax);
        x = xi;
        jamppaRect.x = xi+30;
    }

    public void setY(float yi) {
        // make sure jamppa stays within the screen bounds
        if (yi < yMin)
            yi = yMin;
        if (yi > yMax - 64)
            yi = yMax - 64;

       // Log.d("Y", "setY: "+y);
        y = yi;
        jamppaRect.y = yi+10;
    }


    public void jamppaCrash() {
        jamppaTormaa = true;
        jamppaMaissa = TimeUtils.nanoTime();

    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setXmax(float xmax) {   // pitää olla muutettavissa, jotta jamppa voi liikkua muuallakin kuin peliruudussa
        xMax = xmax;
    }

    public void setXmin(float xmin){  // pitää olla muutettavissa, jotta jamppa voi liikkua muuallakin kuin peliruudussa
        xMin = xmin;
    }


    public TextureRegion getJamppaKuva() {
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
        TextureRegion jamppaKuva = walkAnimation.getKeyFrame(stateTime, true);

        return jamppaKuva;
    }

    public TextureRegion getJamppaKuvaKaanto() {    // käänteinen jamppa, flipataan freimit
        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
        TextureRegion jamppaKuva = walkAnimation.getKeyFrame(stateTime, true);

        if (!jamppaKuva.isFlipX())
            jamppaKuva.flip(true, false);

        return jamppaKuva;
    }

    public void lataaFreimit(int skaalaus) {
        // lataa jampan kuva, "leikataan" kuvasta framet
        jampanJuoksu = new Texture(Gdx.files.internal("jamppa_anim.png"));

        if (skaalaus > 0) jampanJuoksu = skaalaaJamppaKuva(skaalaus);   // skaalataan tarvittaessa jamppaa

        TextureRegion[][] tmp = TextureRegion.split(jampanJuoksu,
                jampanJuoksu.getWidth() / FRAME_COLS,
                jampanJuoksu.getHeight() / FRAME_ROWS);

        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);
        TextureRegion jamppaKuva = walkAnimation.getKeyFrame(stateTime, true);
    }


    public Texture skaalaaJamppaKuva(int skaalaus) {    // skaalataan kuva suuremmaksi, koska resoluutio on eri kaupparuudussa
        int kerroin = skaalaus;

        if (!jampanJuoksu.getTextureData().isPrepared()) {
            jampanJuoksu.getTextureData().prepare();
        }
        Pixmap pixmap200 = jampanJuoksu.getTextureData().consumePixmap();
        Pixmap pixmap100 = new Pixmap(pixmap200.getWidth() * kerroin, pixmap200.getHeight() * kerroin, pixmap200.getFormat());

        pixmap100.drawPixmap(pixmap200,
                0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
                0, 0, pixmap100.getWidth(), pixmap100.getHeight()
        );
        Texture skaalattuJamppa = new Texture(pixmap100);

        pixmap200.dispose();
        pixmap100.dispose();

        return skaalattuJamppa;
    }
}
