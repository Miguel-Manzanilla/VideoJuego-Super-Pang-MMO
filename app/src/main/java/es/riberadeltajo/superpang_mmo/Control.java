package es.riberadeltajo.superpang_mmo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;


    public class Control {
        public boolean pulsado=false; //indica si el control está pulsado o no
        public float coordenada_x, coordenada_y; //coordenadas donde se dibuja el control
        private Bitmap imagen; //imagen del control
        private Context mContexto;
        public String nombre;


        public Control(Context c, float x, float y){
            coordenada_x=x;
            coordenada_y=y;
            mContexto=c;
        }

        //carga su bitmap
        public void cargar(int recurso){
            imagen=BitmapFactory.decodeResource(mContexto.getResources(), recurso);
        }

        //se dibuja en un canvas con un pincel (incluso transparente)
        public void dibujar(Canvas c, Paint p){
            c.drawBitmap(imagen,coordenada_x,coordenada_y,p);
        }

        //se comprueba si se ha pulsado con el pointer i
        public void compruebaPulsado(int x, int y){
            if(x>coordenada_x && x<coordenada_x+ ancho() && y>coordenada_y && y<coordenada_y+ alto()){
                pulsado=true;
            }
        }


        public void compruebaSoltado(ArrayList<Toque> lista){
            boolean aux=false;
            for(Toque t:lista){
                if(t.x>coordenada_x && t.x<coordenada_x+ ancho() && t.y>coordenada_y && t.y<coordenada_y+ alto()) {
                    aux = true;
                }
            }
            if(!aux){
                pulsado=false;
            }
        }

        public int ancho(){
            return imagen.getWidth();
        }

        public int alto(){
            return imagen.getHeight();
        }
    }
