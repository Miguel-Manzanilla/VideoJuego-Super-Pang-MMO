package es.riberadeltajo.superpang_mmo;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Juego extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private SurfaceHolder holder;
    private BucleJuego bucle;
    public SQLiteDatabase db;
    public Jugador jug=new Jugador();
    public Mapa m=new Mapa();
    public Bitmap disparoBM;
    public Bitmap bolaBM;
    public Bitmap bolaX2;
    public Bitmap bolaTiempo;
    public Bitmap bola_vida;
    public  Bitmap personaje;
    public  Bitmap personajeder;
    public Bitmap portadaPersonaje;
    private ArrayList<Disparo> lista_disparos=new ArrayList<Disparo>();
    private int frames_para_nuevo_disparo=0;
    private final int MAX_FRAMES_ENTRE_DISPARO= (int) (BucleJuego.MAX_FPS/1.5);
    private boolean nuevo_disparo=false;
    public final int x = 0;
    public final int y = 1;
    private final int IZQUIERDA = 0;
    private final int DERECHA = 1;
    private final int UP = 2;
    private final int rojo = 0;
    private final int azul = 1;
    private boolean hayToque;
    private boolean elegirJugador = false;
    private float cargar = 0;
    private float suma = 0;
    //vectores
    public float maximosPantalla[] = new float[2];
    public Bitmap personajes[] = new Bitmap[2];
    private String texto="";
    public String textos[] = {"NUNCA TE RINDAS","FROM THE BOTTOM!!","WHAT THE FUCK","DONT TOUCH MY BACK!!","WAWAWAWAWAWAWA","MANZANNILLAAA¿?¿","MONDO CLUB !!!",
            "¿ESTO ES POLLO?","ALI KEBAB","INFRASTRUCTURE 30K €","NO MEWING ⊙﹏⊙","ME HE ENCIDIDO UNO",
            "NAVALCÁN SHIT","SI HOMBRE","CHALECO REFLECTANTE","NO VA SUBIR PARA ARRIBA","TOMCAT=ZORROTOM","JEROMADERO",
            "DAVID JUDAS","XIKISSSS CQCQCQ"};
    private Control[] controles = new Control[3];
    private ArrayList<Toque> toques = new ArrayList<>();
    private int tiempoCrucePantalla = 2;
    private float deltaT = 0;
    public boolean orientacionder = false;
    public int clasificacion = 0;
    private int cuentaAtras=0;
    private int cogerCuenta=0;
    private boolean entrarCuenta=true;
    private int cogerTiempoJuego=0;
    private  boolean primeraVez=true;
    private  boolean primeraCancion=true;
    private  boolean segundaCancion=true;
    public  boolean mostrarMensaje=false;
    public  boolean mostrarMensajeRamdon=true;
    public boolean derrota=false;
    private MediaPlayer mpIncio=new MediaPlayer();
    private MediaPlayer mpJuego=new MediaPlayer();
    private MediaPlayer mpHablar=new MediaPlayer();
    private MediaPlayer mpDerrota=new MediaPlayer();
    private MediaPlayer mpBola=new MediaPlayer();
    private int nivel =0;
    private int PUNTOS_CAMBIO_NIVEL=2000;

    //bolas
    private int bolas_x_min=20;
    private int frames_para_nueva_bola=0;
    private int bolas_explotadas=0;
    private int bolas_creadas=0;
    private ArrayList<Bola> lista_bolas=new ArrayList<Bola>();
    public Bitmap BolaNormal,BolaX2,Bola_Tiempo,Bola_vida;
    int minutos;
    int seg;
    boolean insertar=true;
    boolean reinicio=false;

    private static final String TAG = Juego.class.getSimpleName();

    public Juego(Activity context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // se crea la superficie, creamos el game loop

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);
        inicializarVariables();
        // creamos el game loop
        bucle = new BucleJuego(getHolder(), this);

        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);

        //comenzar el bucle
        bucle.start();
        setOnTouchListener(this);
    }

    private void inicializarVariables() {
        //coger maximo de x y de y
        Canvas c = getHolder().lockCanvas();
        maximosPantalla[y] = c.getHeight();
        maximosPantalla[x] = c.getWidth();
        getHolder().unlockCanvasAndPost(c);
        m.mapa = m.sacarBitmapRamdon(getContext());
        m.mapa_ancho = m.mapa.getWidth();
        m.mapa_alto = m.mapa.getHeight();
        //cargar mapa en las posiciones de x y de y
        m.posicionMapa[x] = 0;
        m.posicionMapa[y] = 0;
        float margen = (float) (maximosPantalla[y] * 0.15);
        //flecha_izda
        controles[IZQUIERDA] = new Control(getContext(), 50, maximosPantalla[y] - margen);
        controles[IZQUIERDA].cargar(R.drawable.flecha_izda);
        controles[IZQUIERDA].nombre = "IZQUIERDA";
        //flecha_derecha
        controles[DERECHA] = new Control(getContext(),
                controles[0].ancho() + controles[0].coordenada_x + 85, controles[0].coordenada_y);
        controles[DERECHA].cargar(R.drawable.flecha_dcha);
        controles[DERECHA].nombre = "DERECHA";
        //fecha_up
        controles[UP] = new Control(getContext(),
                maximosPantalla[x] - controles[0].ancho() - 50, controles[0].coordenada_y);
        controles[UP].cargar(R.drawable.disparo);
        controles[UP].nombre = "ARRIBA";
        suma = (maximosPantalla[x] - 100) / 380;
        Bitmap projo = BitmapFactory.decodeResource(getResources(), R.drawable.rojo);
        Bitmap rojofin = Bitmap.createScaledBitmap(projo, (int) ((int) maximosPantalla[x]*0.4), (int) ((int) (maximosPantalla[y] / 2) - maximosPantalla[y] * 0.2), false);
        personajes[rojo] = rojofin;
        Bitmap pazul = BitmapFactory.decodeResource(getResources(), R.drawable.azul);
        Bitmap azulfin = Bitmap.createScaledBitmap(pazul, (int) ((int) maximosPantalla[x]*0.4), (int) ((int) (maximosPantalla[y] / 2) - maximosPantalla[y] * 0.2), false);
        personajes[azul] = azulfin;
        jug.posicionInicialPersonaje[x] = maximosPantalla[x] / 2 + jug.personaje_ancho / 9;
        jug.posicionInicialPersonaje[y] = (float) (maximosPantalla[y] * 0.73);
        jug.posicionPersonaje[x] = jug.posicionInicialPersonaje[x];
        jug.posicionPersonaje[y] = jug.posicionInicialPersonaje[y];
        deltaT = 1.0f / BucleJuego.MAX_FPS;
        jug.velocidadpersonaje[x] = maximosPantalla[x] / tiempoCrucePantalla;
        mpIncio=MediaPlayer.create(getContext(),R.raw.cancionprincipio);
        mpJuego=MediaPlayer.create(getContext(),R.raw.cancionjuego);
        mpHablar=MediaPlayer.create(getContext(),R.raw.hablar);
        mpDerrota=MediaPlayer.create(getContext(),R.raw.derrota);
        mpBola=MediaPlayer.create(getContext(),R.raw.bloob);
        crearBD();
        cargarBolas();
    }

    private void cargarBolas() {
        frames_para_nueva_bola=bucle.MAX_FPS*60/bolas_x_min;
        Random r=new Random();
        int size=170;
        switch (r.nextInt(3)){
            case 0:
                Bitmap azul=BitmapFactory.decodeResource(getResources(),R.drawable.bolaazul);
                BolaNormal= Bitmap.createScaledBitmap(azul, size, size, false);
                break;
            case 1:
                Bitmap roja=BitmapFactory.decodeResource(getResources(),R.drawable.bolaroja);
                BolaNormal= Bitmap.createScaledBitmap(roja, size, size, false);
                break;
            case 2:
                Bitmap negra=BitmapFactory.decodeResource(getResources(),R.drawable.bolanegra);
                BolaNormal= Bitmap.createScaledBitmap(negra, size, size, false);
                break;
        }
        BolaX2=BitmapFactory.decodeResource(getResources(),R.drawable.bolax2);
        Bola_vida=BitmapFactory.decodeResource(getResources(),R.drawable.bolavida);
    }

    private void crearBD() {
        Context context = getContext();
        db = context.openOrCreateDatabase("BDPuntuacion", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS MisPuntuaciones(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TIPO_JUGADOR VARCHAR(100)," +
                "TIEMPO INTEGER," +
                "PUNTUACION INTEGER);");
    }

    /**
     * Este método actualiza el estado del juego. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */
    public void actualizar() throws InterruptedException {
        if(!derrota) {
            cargar = cargar + suma;
            if (!controles[DERECHA].pulsado && !controles[IZQUIERDA].pulsado) {
                jug.estadoPersonaje = 4;
                jug.estadoPersonajeder=1;
                if (controles[UP].pulsado){
                    jug.estadoPersonaje=5;
                    jug.estadoPersonajeder=0;
                    nuevo_disparo = true;
                }
            }

            if (jug.posicionPersonaje[x] >= maximosPantalla[x]*0.03) {
                if (controles[IZQUIERDA].pulsado) {
                    jug.posicionPersonaje[x] -= deltaT * jug.velocidadpersonaje[x];
                    jug.estadoPersonaje--;
                    orientacionder = false;
                    if (jug.estadoPersonaje < 0){
                        jug.estadoPersonaje = 3;
                    }

                }
            }
            if (jug.posicionPersonaje[x] <= maximosPantalla[x] - (jug.personaje_ancho / 5) -maximosPantalla[x]*0.08) {
                if (controles[DERECHA].pulsado) {
                    jug.posicionPersonaje[x] += deltaT * jug.velocidadpersonaje[x];
                    jug.estadoPersonajeder++;
                    orientacionder = true;
                    if (jug.estadoPersonajeder>4){
                        jug.estadoPersonajeder=2;
                    }
                }
            }
            if ((bucle.tiempoTotal/1000)%2==0){
                jug.estadoPortada++;
                if (mostrarMensajeRamdon){
                    mostrarMensajeRamdon=false;
                    Random r=new Random();
                    texto=textos[r.nextInt(20)];
                }
                if (jug.estadoPortada==3){
                    jug.estadoPortada=0;
                    mostrarMensaje=true;
                }
            }else {
                jug.estadoPortada=0;
                mostrarMensaje=false;
                mostrarMensajeRamdon=true;
            }
        }
        if (frames_para_nuevo_disparo == 0) {
            if (nuevo_disparo) {
                crearDisparo();
                nuevo_disparo = false;
            }
            frames_para_nuevo_disparo = MAX_FRAMES_ENTRE_DISPARO;
        }
        frames_para_nuevo_disparo--;
        //Los disparos se mueven
        for(Iterator<Disparo> it_disparos = lista_disparos.iterator(); it_disparos.hasNext();) {
            Disparo d=it_disparos.next();
            d.actualizaCoordenadas();
            if(d.fueraDePantalla()) {
                it_disparos.remove();
            }
        }
        /*BOLAS*/
        if (cuentaAtras>=3){
            if(frames_para_nueva_bola==0){
                crearBola();
                frames_para_nueva_bola=bucle.MAX_FPS*60/bolas_x_min;
            }
            frames_para_nueva_bola--;

            for(Bola e: lista_bolas){
                e.actualizaCoordenadas();
            }
            //colisiones
            for(Iterator<Bola> bolas= lista_bolas.iterator();bolas.hasNext();) {
                Bola b = bolas.next();
                for(Iterator<Disparo> it_disparos=lista_disparos.iterator();it_disparos.hasNext();) {
                    Disparo d=it_disparos.next();
                    if (colision(b, d)) {
                        try {
                            bolas.remove();
                            it_disparos.remove();
                            mpBola.start();
                        }
                        catch(Exception ex){}
                        bolas_explotadas++;
                        /*Puntos*/
                        if(b.tipo_bola==b.BOLA_X2)
                            jug.puntos +=100;
                        if (b.tipo_bola==b.BOLA_NORMAL)
                            jug.puntos +=50;
                        if (b.tipo_bola==b.BOLA_VIDA)
                            if (jug.vidas==5){
                                jug.puntos +=100;
                            }else{
                                jug.vidas++;
                            }
                    }
                }
                if (colisionquitar(b, jug)) {
                    jug.vidas--;
                    bolas.remove();
                }
            }
            if (jug.vidas==0){
                derrota=true;
                mpDerrota.start();
                mpJuego.stop();
            }
            if(nivel != jug.puntos /PUNTOS_CAMBIO_NIVEL) {
                nivel = jug.puntos / PUNTOS_CAMBIO_NIVEL;
                bolas_x_min += (5 * nivel);
            }
        }

    }
    public boolean colisionquitar(Bola e, Jugador j){
        Bitmap bm=e.bitmap();
        if (personaje!=null){
            Bitmap jugados=BitmapFactory.decodeResource(getResources(),R.drawable.jugadorparado);;
            if (!orientacionder){
                jugados = Bitmap.createScaledBitmap(personaje, (int) (j.personaje_ancho/6)-20, (int) j.personaje_alto-20, false);
            }
            if (orientacionder){
                jugados =BitmapFactory.decodeResource(getResources(),R.drawable.jugadorlder);
            }
            return Colision.hayColision(bm,(int) e.coordenada_x,(int)e.coordenada_y,
                    jugados,(int)j.posicionPersonaje[x],(int)j.posicionPersonaje[y]);
        }
        return false;
    }
    public boolean colision(Bola e, Disparo d){
        Bitmap bm=e.bitmap();
        Bitmap disparo=this.disparoBM;
        return Colision.hayColision(bm,(int) e.coordenada_x,(int)e.coordenada_y,
                disparo,(int)d.coordenada_x,(int)d.coordenada_y);
    }
    private void crearBola() {
        Bola b =new Bola(this,1);
        bolaBM=BolaNormal;
        bolaX2=BolaX2;
        bola_vida=Bola_vida;
        bolaTiempo=Bola_Tiempo;
        lista_bolas.add(b);
        bolas_creadas++;
    }

    private void crearDisparo() {
        lista_disparos.add(new Disparo(this,jug.posicionPersonaje[x]-jug.personaje_ancho/5-jug.personaje_ancho/5/2-20,jug.posicionPersonaje[y]));
    }
    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */
    public void renderizar(Canvas canvas, Paint p) {
            if (elegirJugador) {
                    if (jug.jugadorElegido[y] <= maximosPantalla[y] / 2) {
                        Log.d("JUGADOR", "ROJO");
                        personaje = BitmapFactory.decodeResource(getResources(), R.drawable.jugadorrojo);
                        personajeder=BitmapFactory.decodeResource(getResources(),R.drawable.jugadorrojoder);
                        portadaPersonaje=BitmapFactory.decodeResource(getResources(),R.drawable.portadaroja);
                        disparoBM=BitmapFactory.decodeResource(getResources(),R.drawable.shootred);
                        jug.tipo="rojo";
                    } else {
                        Log.d("JUGADOR", "AZUL");
                        jug.tipo="azul";
                        personaje = BitmapFactory.decodeResource(getResources(), R.drawable.jugadorazul);
                        personajeder=BitmapFactory.decodeResource(getResources(),R.drawable.jugadorazulder);
                        portadaPersonaje=BitmapFactory.decodeResource(getResources(),R.drawable.portadaazul);
                        disparoBM=BitmapFactory.decodeResource(getResources(),R.drawable.shotblue);
                    }
                    jug.personaje_ancho = personaje.getWidth();
                    jug.personaje_alto = personaje.getHeight();
                    jug.portada_ancho=portadaPersonaje.getWidth();
                    jug.portada_alto=portadaPersonaje.getHeight();
                if (clasificacion < 2) {
                    generarPantallaClasificacion(canvas, p);
                } else {
                    //4
                    if (cuentaAtras<4){
                        pantallacargajuego(canvas,p);
                    }else{
                        generarJuego(canvas, p);
                    }
                }
            } else {
                cargarPantallaeleccionJugador(canvas, p);
            }

    }
    private void pantallacargajuego(Canvas canvas, Paint p) {
        mpIncio.stop();
        if (segundaCancion){
            segundaCancion=false;
            mpJuego.start();
            mpJuego.isLooping();
        }
        if (entrarCuenta){
            cogerCuenta= (int) (bucle.tiempoTotal/1000);
            entrarCuenta=false;
        }
        cuentaAtras= (int) ((bucle.tiempoTotal/1000)-cogerCuenta);
        if (cuentaAtras%2==0){
            canvas.drawColor(Color.parseColor("#DFD8CB"));
        }else{
            //#00F080
            canvas.drawColor(Color.parseColor("#00F080"));
        }
        p.setTextSize(750);
        if (3-cuentaAtras==1){
            canvas.drawText((3-cuentaAtras)+"", (float) (maximosPantalla[x]*0.4), (float) (maximosPantalla[y]*0.6),p);
        }else if (3-cuentaAtras>=0){
            canvas.drawText((3-cuentaAtras)+"", (float) (maximosPantalla[x]*0.3), (float) (maximosPantalla[y]*0.6),p);
        }
    }

    private void generarPantallaClasificacion(Canvas canvas, Paint p) {
        canvas.drawColor(Color.parseColor("#00F080"));
        p.setTextSize(107);
        p.setStrokeWidth(1);
        canvas.drawText("CLASIFICACIÓN", (float) (maximosPantalla[x] * 0.15), (float) (maximosPantalla[y] * 0.10), p);
        p.setStrokeWidth(10);
        canvas.drawLine((float) (maximosPantalla[x] * 0.10), (float) (maximosPantalla[y] * 0.11), (float) (maximosPantalla[x] * 0.92), (float) (maximosPantalla[y] * 0.11), p);
        p.setTextSize(35);
        p.setStrokeWidth(1);
        canvas.drawText("POSICIÓN", (float) (maximosPantalla[x] * 0.15), (float) (maximosPantalla[y] * 0.15), p);
        canvas.drawText("TIEMPO", (float) (maximosPantalla[x] * 0.33), (float) (maximosPantalla[y] * 0.15), p);
        canvas.drawText("PUNTUACIÓN", (float) (maximosPantalla[x] * 0.66), (float) (maximosPantalla[y] * 0.15), p);
        p.setStrokeWidth(1);
        Cursor c = db.rawQuery("SELECT * FROM MisPuntuaciones order by PUNTUACION desc", null);
        for (int i = 0; i < 10; i++) {
            if (c.moveToNext()){
                canvas.drawText(i + 1 + "", (float) (maximosPantalla[x] * 0.20), (float) (maximosPantalla[y] * 0.20) + maximosPantalla[y] / 12 * i, p);
                canvas.drawText(c.getString(2)+" ", (float) (maximosPantalla[x] * 0.33), (float) (maximosPantalla[y] * 0.20) + maximosPantalla[y] / 12 * i, p);
                canvas.drawText(c.getInt(3)+" ", (float) (maximosPantalla[x] * 0.66), (float) (maximosPantalla[y] * 0.20) + maximosPantalla[y] / 12 * i, p);
                canvas.drawLine((float) (maximosPantalla[x] * 0.15), (float) (maximosPantalla[y] * 0.21) + maximosPantalla[y] / 12 * i, (float) (maximosPantalla[x] * 0.87), (float) (maximosPantalla[y] * 0.21) + maximosPantalla[y] / 12 * i, p);
            }

        }
        p.setTextSize(45);
        canvas.drawText("TOCA PARA INICIAR EL JUEGO", (float) (maximosPantalla[x] * 0.20), (float) (maximosPantalla[y] * 0.99), p);
    }

    private void generarPersonaje(Canvas canvas, Paint p) {
        if (orientacionder) {
            canvas.drawBitmap(personajeder,
                    new Rect((int) (jug.estadoPersonajeder * jug.personaje_ancho / 6), 0, (int) (jug.estadoPersonajeder * jug.personaje_ancho / 6+ jug.personaje_ancho / 6)-12, (int) (jug.personaje_alto)),
                    new Rect((int) jug.posicionPersonaje[x], (int) jug.posicionPersonaje[y],
                            (int) (jug.posicionPersonaje[x] + jug.personaje_ancho / 6 * 2), (int) (jug.posicionPersonaje[y] + jug.personaje_alto *1.8)), p);
        } else {
            canvas.drawBitmap(personaje,
                    new Rect((int) (jug.estadoPersonaje * jug.personaje_ancho / 6)+12, 0, (int) (jug.estadoPersonaje * jug.personaje_ancho / 6+ jug.personaje_ancho / 6), (int) (jug.personaje_alto)),
                    new Rect((int) jug.posicionPersonaje[x], (int) jug.posicionPersonaje[y],
                            (int) (jug.posicionPersonaje[x] + jug.personaje_ancho / 6 * 2), (int) (jug.posicionPersonaje[y] + jug.personaje_alto *1.8)), p);
        }

    }

    private void cargarPantallaeleccionJugador(Canvas canvas, Paint p) {
        if (primeraCancion){
            primeraCancion=false;
            mpIncio.start();
            mpIncio.isLooping();
        }
        canvas.drawColor(Color.parseColor("#00A352"));
        p.setColor(Color.parseColor("#007D3E"));
        p.setStrokeWidth(720);
        Bitmap bm=BitmapFactory.decodeResource(getResources(),R.drawable.florpixel);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, (int) ((int) maximosPantalla[x]*0.45), (int) ((int) maximosPantalla[x]*0.45), false);
        canvas.drawBitmap(resizedBitmap,0,0,p);
        canvas.drawBitmap(resizedBitmap,maximosPantalla[x]-resizedBitmap.getWidth(),maximosPantalla[y]-resizedBitmap.getWidth(),p);
        canvas.drawLine((float) (maximosPantalla[x]*0.18),(float) (maximosPantalla[y]*0.25), (float) (maximosPantalla[x]*0.82),(float) (maximosPantalla[y]*0.25), p);
        canvas.drawLine((float) (maximosPantalla[x]*0.18),(float) (maximosPantalla[y]*0.75), (float) (maximosPantalla[x]*0.82),(float) (maximosPantalla[y]*0.75), p);
        p.setStrokeWidth(190);
        canvas.drawLine((float) (maximosPantalla[x]*0.03),(maximosPantalla[y] / 2 - 15), (float) (maximosPantalla[x]*0.97), (float) (maximosPantalla[y] / 2 - 15), p);
        p.setColor(Color.parseColor("#00BE5F"));
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(670);
        canvas.drawLine((float) (maximosPantalla[x]*0.2),(float) (maximosPantalla[y]*0.25), (float) (maximosPantalla[x]*0.8),(float) (maximosPantalla[y]*0.25), p);
        canvas.drawBitmap(personajes[rojo], (maximosPantalla[x] / 2) - (personajes[rojo].getWidth() / 2), (float) (maximosPantalla[y]*0.1), null);
        canvas.drawLine((float) (maximosPantalla[x]*0.2),(float) (maximosPantalla[y]*0.75), (float) (maximosPantalla[x]*0.8),(float) (maximosPantalla[y]*0.75), p);
        canvas.drawBitmap(personajes[azul], (maximosPantalla[x] / 2) - (personajes[azul].getWidth() / 2), (float) (maximosPantalla[y]*0.9 - personajes[azul].getHeight()), null);
        p.setStrokeWidth(150);
        canvas.drawLine((float) (maximosPantalla[x]*0.05),(maximosPantalla[y] / 2 - 15), (float) (maximosPantalla[x]*0.95), (float) (maximosPantalla[y] / 2 - 15), p);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(1);
        p.setTextSize(45);
        canvas.drawText("TOCA PARA ELEGIR JUGADOR", (float) (maximosPantalla[x] * 0.20), (float) (maximosPantalla[y] / 2), p);

    }

    private void generarJuego(Canvas canvas, Paint p) {
        canvas.drawColor(Color.parseColor("#bafeca"));
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(Color.BLACK);
        p.setTextSize(50);
        Bitmap mapafin = Bitmap.createScaledBitmap(m.mapa, (int) maximosPantalla[x], (int) (maximosPantalla[y]*0.6), false);
        canvas.drawBitmap(mapafin, 0, (float) (maximosPantalla[y]*0.225), p);
        for (int i = 0; i < controles.length; i++) {
            controles[i].dibujar(canvas, p);
        }
        int cambio= (int) (jug.portada_ancho/3)*jug.estadoPortada;
        Rect origen = new Rect((int) (cambio+jug.portada_ancho*0.01), 0, (int) ((int) (jug.portada_ancho/3)+cambio-jug.portada_ancho*0.01), (int) jug.portada_alto);
        Rect destino = new Rect((int) maximosPantalla[x]-350,30, (int) maximosPantalla[x]-30,350);
        canvas.drawBitmap(portadaPersonaje,origen,destino,p);
        if (mostrarMensaje){
            p.setStrokeWidth(60);
            p.setColor(Color.parseColor("#00F080"));
            canvas.drawLine((float) (maximosPantalla[x] -350),355, (float) (maximosPantalla[x] -30), 355, p);
            p.setStrokeWidth(1);
            p.setColor(Color.BLACK);
            mpHablar.start();
            if (texto.contains("30K") || texto.contains("BACK") || texto.contains("CHALECO") || texto.contains("ARRIBA")){
                p.setTextSize(23);
            }else{
                p.setTextSize(30);
            }
            canvas.drawText(texto,maximosPantalla[x]-345,360,p);
        }
        p.setStrokeWidth(440);
        p.setColor(Color.parseColor("#00BE5F"));
        canvas.drawLine(30F, (float) 0, (float) (maximosPantalla[x] * 0.66), 0, p);
        p.setStrokeWidth(420);
        p.setColor(Color.parseColor("#00F080"));
        canvas.drawLine(40F, (float) 0, (float) (maximosPantalla[x] * 0.65), 0, p);
        p.setStrokeWidth(1);
        p.setTextSize(100);
        p.setColor(Color.BLACK);
        canvas.drawText("SCORE",50,90,p);
        p.setStrokeWidth(10);
        canvas.drawLine((float) (maximosPantalla[x] * 0.05), (float) (maximosPantalla[y]*0.05), (float) (maximosPantalla[x] * 0.35), (float) (maximosPantalla[y]*0.05), p);
        p.setStrokeWidth(1);
        String formato = "%05d";
        String resultado = String.format(formato, jug.puntos);
        p.setTextSize(90);
        canvas.drawText(resultado+"",70,(float) (maximosPantalla[y]*0.09),p);
        p.setStrokeWidth(200);
        p.setColor(Color.parseColor("#00BE5F"));
        canvas.drawLine(80, (float) (maximosPantalla[y]*0.185), 510, (float) (maximosPantalla[y]*0.185), p);
        p.setStrokeWidth(180);
        p.setColor(Color.parseColor("#00F080"));
        canvas.drawLine(90, (float) (maximosPantalla[y]*0.185), 500, (float) (maximosPantalla[y]*0.185), p);
        p.setStrokeWidth(1);
        p.setColor(Color.BLACK);
        p.setTextSize(75);
        canvas.drawText("PLAYER 01", 100F, (float) (maximosPantalla[y]*0.18),p);
        canvas.drawText("NIVEL: "+nivel, 150f, (float) (maximosPantalla[y]*0.22),p);
        p.setTextSize(100);
        canvas.drawText("TIME", (float) ((int) maximosPantalla[x]*0.42),90,p);
        if(!derrota) {
            if (primeraVez){
                primeraVez=false;
                cogerTiempoJuego= (int) (bucle.tiempoTotal/1000);
            }
            minutos= (int) ((bucle.tiempoTotal/1000-cogerTiempoJuego)/60);
            seg=(int) ((bucle.tiempoTotal/1000-cogerTiempoJuego)%60);

        }
        String segundosFormateados = String.format("%02d", seg);
        p.setStrokeWidth(10);
        canvas.drawLine((float) (maximosPantalla[x] * 0.4), (float) (maximosPantalla[y]*0.05), (float) (maximosPantalla[x] * 0.65), (float) (maximosPantalla[y]*0.05), p);
        p.setStrokeWidth(1);
        p.setTextSize(90);
        canvas.drawText(minutos+":"+segundosFormateados, (float) ((int) maximosPantalla[x]*0.45), (float) (maximosPantalla[y]*0.09),p);
        generarPersonaje(canvas, p);
        for(Disparo d:lista_disparos){
            d.Dibujar(canvas,p);
        }
        for(Bola b: lista_bolas){
            b.dibujar(canvas,p);
        }

        for (int i = 0; i < jug.vidas; i++) {
            Bitmap bm=BitmapFactory.decodeResource(getResources(),R.drawable.cora);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, 70, 70, false);
            canvas.drawBitmap(resizedBitmap,450+(100*(i+1)), (float) (maximosPantalla[y]*0.18),p);
        }
        if(derrota) {
            mpDerrota.start();
            p.setAlpha(128);
            canvas.drawRect(0,0,maximosPantalla[x],maximosPantalla[y],p);
            p.setAlpha(255);
            p.setStrokeWidth(650);
            p.setColor(Color.parseColor("#00BE5F"));
            canvas.drawLine((float) 50, (float) maximosPantalla[y] /2+100, (float) maximosPantalla[x]-50, (float) maximosPantalla[y] /2+100, p);
            p.setStrokeWidth(630);
            p.setColor(Color.parseColor("#00F080"));
            canvas.drawLine((float) 60, (float) maximosPantalla[y] /2+100, (float) maximosPantalla[x]-60, (float) maximosPantalla[y] /2+100, p);
            p.setStrokeWidth(1);
            p.setAlpha(0);
            p.setColor(Color.BLACK);
            p.setTextSize(maximosPantalla[x] /10);
            canvas.drawText("DERROTA!!", 250, maximosPantalla[y] /2-50, p);
            p.setTextSize(maximosPantalla[x] /20);
            canvas.drawText("PUNTUACIÓN: "+jug.puntos, 200, maximosPantalla[y] /2+100, p);
            canvas.drawText("HAS DURADO: "+minutos+":"+segundosFormateados, 200, maximosPantalla[y] /2+200, p);
            canvas.drawText("TOCA PARA REINICIAR EL JUEGO", 120F, (float) (maximosPantalla[y] *0.67), p);
            String tiempo=minutos+":"+segundosFormateados;
            if (insertar){
                insertar=false;
                db.execSQL("INSERT INTO MisPuntuaciones (TIPO_JUGADOR,TIEMPO, PUNTUACION) VALUES ('"+jug.tipo+"','"+tiempo+"', " +jug.puntos+ ");");
            }
            mpHablar.stop();
            mpJuego.stop();
            mpBola.stop();
            mpIncio.stop();
            if (reinicio){
                reiniciarVariables();
            }
        }

    }

    private void reiniciarVariables() {
        clasificacion = 0;
        cuentaAtras=0;
        cogerCuenta=0;
        entrarCuenta=true;
        cogerTiempoJuego=0;
        primeraVez=true;
        primeraCancion=true;
        segundaCancion=true;
        mostrarMensaje=false;
        mostrarMensajeRamdon=true;
        derrota=false;
        reinicio=false;
        jug.vidas=3;
        elegirJugador=false;
        jug.puntos=0;
        nivel=0;
        for(Iterator<Bola> bolas= lista_bolas.iterator();bolas.hasNext();) {
            Bola b = bolas.next();
            bolas.remove();
            b.velocidad=0;
        }
        m.mapa=null;
        m.mapa=m.sacarBitmapRamdon(getContext());
        bolas_x_min=20;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Juego destruido!");
        // cerrar el thread y esperar que acabe
        boolean retry = true;
        while (retry) {
            try {
                bucle.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int index;
        int x, y;

        // Obtener el pointer asociado con la acción
        index = event.getActionIndex();


        x = (int) event.getX(index);
        y = (int) event.getY(index);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                hayToque = true;
                clasificacion++;
                elegirJugador = true;
                jug.jugadorElegido[0] = x;
                jug.jugadorElegido[1] = y;
                synchronized (this) {
                    toques.add(index, new Toque(index, x, y));
                }
                if (derrota){
                    reinicio=true;
                }
                //se comprueba si se ha pulsado
                for (int i = 0; i < controles.length; i++)
                    controles[i].compruebaPulsado(x, y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                synchronized (this) {
                    toques.remove(index);
                }

                //se comprueba si se ha soltado el botón
                for (int i = 0; i < controles.length; i++)
                    controles[i].compruebaSoltado(toques);
                break;

            case MotionEvent.ACTION_UP:
                synchronized (this) {
                    toques.clear();
                }
                hayToque = false;
                //se comprueba si se ha soltado el botón
                for (int i = 0; i < controles.length; i++)
                    controles[i].compruebaSoltado(toques);
                break;
        }
        return true;
    }

}
