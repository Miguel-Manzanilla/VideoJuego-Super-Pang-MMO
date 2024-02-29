package es.riberadeltajo.superpang_mmo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Mapa {
    public  Bitmap mapa;
    public float mapa_ancho = 0;
    public float mapa_alto = 0;
    public float posicionMapa[] = new float[2];
    public Bitmap sacarBitmapRamdon(Context c) {
        Random r=new Random();
        int randomNumber = r.nextInt(8) + 1;
        switch (randomNumber) {
            case 1:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo1);
            case 2:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo2);
            case 3:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo3);
            case 4:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo4);
            case 5:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo5);
            case 6:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo6);
            case 7:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo7);
            case 8:
                return BitmapFactory.decodeResource(c.getResources(), R.drawable.fondo8);
        }
        return null;
    }
}
