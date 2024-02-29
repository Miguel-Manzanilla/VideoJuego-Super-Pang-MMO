package es.riberadeltajo.superpang_mmo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

public class Disparo {
    public float coordenada_x, coordenada_y; //coordenadas donde se dibuja el control
    private Juego juego;
    private float velocidad;
    private MediaPlayer mediaPlayer; //para reproducir el sonido de disparo
    private final float MAX_SEGUNDOS_EN_CRUZAR_PANTALLA=3;

    /*Constructor con coordenadas iniciales y número de disparo*/
    public Disparo(Juego j,float x, float y){
        juego=j;
        coordenada_x=x+(j.jug.personaje_ancho/2);
        coordenada_y=y-j.disparoBM.getHeight();
        velocidad=j.maximosPantalla[j.x] /MAX_SEGUNDOS_EN_CRUZAR_PANTALLA/BucleJuego.MAX_FPS;
        Log.i(Juego.class.getSimpleName(),"Velocidad de disparo: " + velocidad);
        mediaPlayer=MediaPlayer.create(j.getContext(), R.raw.disparo);
        mediaPlayer.start();
    }



    //se actualiza la coordenada y nada más
    public void actualizaCoordenadas(){
        coordenada_y-=velocidad;
    }

    public void Dibujar(Canvas c, Paint p) {
        c.drawBitmap(juego.disparoBM, coordenada_x, coordenada_y, p);
    }

    public int ancho(){
        return juego.disparoBM.getWidth();
    }

    public int alto(){
        return juego.disparoBM.getHeight();
    }

    public boolean fueraDePantalla() {
        return coordenada_y < juego.maximosPantalla[juego.y]*0.245;
    }
}
