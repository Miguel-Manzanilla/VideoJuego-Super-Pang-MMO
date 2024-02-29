package es.riberadeltajo.superpang_mmo;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;

public class BucleJuego extends Thread {

    // Frames por segundo deseados
    public final static int 	MAX_FPS = 30;
    // Máximo número de frames saltados
    private final static int	MAX_FRAMES_SALTADOS = 5;
    // El periodo de frames
    private final static int	TIEMPO_FRAME = 1000 / MAX_FPS;

    private Juego juego;

    public int iteraciones;
    public long tiempoTotal;

    public boolean JuegoEnEjecucion=true;
    private static final String TAG = Juego.class.getSimpleName();
    private SurfaceHolder surfaceHolder;
    Paint p=new Paint();
    public int maxX,maxY; //altura y anchura de la pantalla

    BucleJuego(SurfaceHolder sh, Juego s){
        juego=s;
        surfaceHolder=sh;

        //Obtener Dimensiones del Canvas
        Canvas c=sh.lockCanvas();
        maxX = c.getWidth();
        maxY = c.getHeight();
        sh.unlockCanvasAndPost(c);
        Typeface typeface = Typeface.createFromAsset(s.getContext().getAssets(), "font/pixel.ttf");
        p.setTypeface(typeface);
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Comienza el game loop");


        long tiempoComienzo;		// Tiempo en el que el ciclo comenzó
        long tiempoDiferencia;		// Tiempo que duró el ciclo
        int tiempoDormir;		// Tiempo que el thread debe dormir (<0 si vamos mal de tiempo)
        int framesASaltar;	// número de frames saltados

        tiempoDormir = 0;

        while (JuegoEnEjecucion) {
            canvas = null;
            // bloquear el canvas para que nadie más escriba en el
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {

                    tiempoComienzo = System.currentTimeMillis();
                    framesASaltar = 0;	// resetear los frames saltados
                    // Actualizar estado del juego
                    juego.actualizar();
                    // renderizar la imagen
                    juego.renderizar(canvas,p);
                    iteraciones++;
                    // Calcular cuánto tardó el ciclo
                    tiempoDiferencia = System.currentTimeMillis() - tiempoComienzo;

                    // Calcular cuánto debe dormir el thread antes de la siguiente iteración
                    tiempoDormir = (int)(TIEMPO_FRAME - tiempoDiferencia);

                    tiempoTotal+=tiempoDiferencia+tiempoDormir;

                    if (tiempoDormir > 0) {
                        // si sleepTime > 0 vamos bien de tiempo
                        try {
                            // Enviar el thread a dormir
                            // Algo de batería ahorramos
                            Thread.sleep(tiempoDormir);
                        } catch (InterruptedException e) {}
                    }

                    while (tiempoDormir < 0 && framesASaltar < MAX_FRAMES_SALTADOS) {
                        // Vamos mal de tiempo: Necesitamos ponernos al día
                        juego.actualizar(); // actualizar si rendering
                        tiempoDormir += TIEMPO_FRAME;	// actualizar el tiempo de dormir
                        framesASaltar++;
                    }


                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            } finally {
                // si hay excepción desbloqueamos el canvas
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            Log.d(TAG, "Nueva iteración!");
        }
    }


}
