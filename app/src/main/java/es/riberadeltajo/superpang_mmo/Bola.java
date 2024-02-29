package es.riberadeltajo.superpang_mmo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Bola {

    public final int BOLA_NORMAL = 0;
    public final int BOLA_X2 = 1;
    public final int BOLA_TIEMPO = 2;
    public final int BOLA_VIDA = 3;
    public float velocidad;
    public float coordenada_x, coordenada_y;
    public int tipo_bola;
    public float direccion_vertical = 1;
    public float direccion_horizontal = 1;
    public int nivel;
    private Juego juego;


    public Bola(Juego j, int n) {
        juego = j;
        nivel = n;
        calcularVelocidad(j);
        double r = Math.random();
        if (r < 0.9) {
            tipo_bola = BOLA_NORMAL;
        }
        if (r >= 0.9 && r < 0.95) {
            tipo_bola = BOLA_X2;
        }
        if (r >= 0.95) {
            tipo_bola = BOLA_VIDA;
        }
        if (Math.random() > 0.5)
            direccion_horizontal = 1; //derecha
        else
            direccion_horizontal = -1; //izquierda

        if (Math.random() > 0.5)
            direccion_vertical = 1; //abajo
        else
            direccion_vertical = -1; //arriba
        double limiteIzq = j.maximosPantalla[j.x] * 0.20;
        double limiteDer = j.maximosPantalla[j.x] * 0.80;
        coordenada_x = (float) (limiteIzq + (limiteDer - limiteIzq) * new Random().nextDouble());
        coordenada_y = (float) (j.maximosPantalla[j.y] * 0.245);
    }

    public void calcularVelocidad(Juego j) {
        float VELOCIDAD_BOLA = (float) (j.maximosPantalla[j.y] / 20f / BucleJuego.MAX_FPS);
        if (tipo_bola==BOLA_X2 || tipo_bola==BOLA_VIDA){
            velocidad = nivel * VELOCIDAD_BOLA+3;
        }else{
            velocidad = nivel * VELOCIDAD_BOLA;
        }


    }

    public void actualizaCoordenadas() {
        coordenada_x += direccion_horizontal * velocidad;
        coordenada_y += direccion_vertical * velocidad;
        if (coordenada_x <= juego.maximosPantalla[juego.x] * 0.03 && direccion_horizontal == -1)
            direccion_horizontal = 1;
        if (coordenada_x > juego.maximosPantalla[juego.x] - juego.bolaBM.getWidth() - juego.maximosPantalla[juego.x] * 0.03 && direccion_horizontal == 1)
            direccion_horizontal = -1;
        if (tipo_bola == BOLA_NORMAL) {
            if (coordenada_y >= juego.maximosPantalla[juego.y] * 0.725 && direccion_vertical == 1)
                direccion_vertical = -1;
        } else {
            if (tipo_bola == BOLA_X2) {
                if (coordenada_y >= juego.maximosPantalla[juego.y] * 0.745 && direccion_vertical == 1)
                    direccion_vertical = -1;
            } else {
                if (coordenada_y >= juego.maximosPantalla[juego.y] * 0.745 && direccion_vertical == 1)
                    direccion_vertical = -1;
            }

        }
        if (coordenada_y <= juego.maximosPantalla[juego.y] * 0.245 && direccion_vertical == -1)
            direccion_vertical = 1;
    }

    public void dibujar(Canvas c, Paint p) {
        switch (tipo_bola) {
            case 0:
                c.drawBitmap(juego.bolaBM, coordenada_x, coordenada_y, p);
                break;
            case 1:
                c.drawBitmap(juego.bolaX2, coordenada_x, coordenada_y, p);
                break;
            case 3:
                c.drawBitmap(juego.bola_vida, coordenada_x, coordenada_y, p);
                break;
        }

    }

    public int ancho() {
        switch (tipo_bola) {
            case 0:
                return juego.bolaBM.getWidth();
            case 1:
                return juego.bolaX2.getWidth();
            case 2:
                return juego.bolaTiempo.getWidth();
            case 3:
                return juego.bola_vida.getWidth();
        }
        return 0;
    }

    public int alto() {
        switch (tipo_bola) {
            case 0:
                return juego.getHeight();
            case 1:
                return juego.bolaX2.getHeight();
            case 2:
                return juego.bolaTiempo.getHeight();
            case 3:
                return juego.bola_vida.getHeight();
        }
        return 0;
    }

    public Bitmap bitmap() {
        switch (tipo_bola) {
            case 0:
                return juego.bolaBM;
            case 1:
                return juego.bolaX2;
            case 2:
                return juego.bolaTiempo;
            case 3:
                return juego.bola_vida;
        }
        return null;
    }


}

