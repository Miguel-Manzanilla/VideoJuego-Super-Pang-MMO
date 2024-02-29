package es.riberadeltajo.superpang_mmo;

import android.graphics.Bitmap;

public class Jugador {

    public float personaje_ancho = 0;
    public float personaje_alto = 0;
    public float portada_alto = 0;
    public float portada_ancho = 0;
    public int puntos=0;
    public int estadoPortada=0;
    public int estadoPersonaje = 5;
    public int estadoPersonajeder = 2;
    public int vidas=3;
    public String tipo;

    //vectores

    public float posicionPersonaje[] = new float[2];
    public int jugadorElegido[] = new int[2];
    public float posicionInicialPersonaje[] = new float[2];
    public float velocidadpersonaje[] = new float[2];


}
