package es.riberadeltajo.superpang_mmo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int altoPantalla;
    int anchoPantalla;
    private Button btn;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_main);
        calculaPantalla();
        btn = findViewById(R.id.btnEmpezar);
        btn.setBackgroundColor(Color.parseColor("#D5A4B4"));
        cargarMusicaIntro();
        animacionBoton();
        animacionJuegoCargaCangrejo();
        animacionTitulo();
        try {
            animacionBolas();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActividadJuego.class));
                mp.stop();
                mp.release();
            }
        });

    }

    private void animacionTitulo() {
        ImageView img = findViewById(R.id.imageView);
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        AlphaAnimation fadeAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation.setDuration(3000);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(fadeAnimation);
        img.startAnimation(animationSet);
    }

    private void animacionBolas() throws InterruptedException {
        //bola roja
        ImageView roja=findViewById(R.id.bolaroja);
        roja.setVisibility(ImageView.VISIBLE);
        Animation bolaRoja = AnimationUtils.loadAnimation(this, R.anim.bolas);
        roja.startAnimation(bolaRoja);
        Thread.sleep(1000);
        //bola azul
        ImageView azul=findViewById(R.id.bolaAzul);
        AnimatorSet animadorAzul = new AnimatorSet();
        ObjectAnimator trasladar = ObjectAnimator.ofFloat(azul, "translationX", -200f, 0f);
        trasladar.setDuration(3000);
        trasladar.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator fade = ObjectAnimator.ofFloat(azul, "alpha", 0f, 1f);
        fade.setDuration(3000);
        fade.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator escalarX = ObjectAnimator.ofFloat(azul, "scaleX", 0.8f, 1f);
        ObjectAnimator escalarY = ObjectAnimator.ofFloat(azul, "scaleY", 0.8f, 1f);
        AnimatorSet escala = new AnimatorSet();
        escala.playTogether(escalarX, escalarY);
        escala.setDuration(3000);
        ObjectAnimator rotacion = ObjectAnimator.ofFloat(azul, "rotation", 0f, 360f);
        rotacion.setDuration(3000);
        animadorAzul.playTogether(trasladar, fade, escala, rotacion);
        animadorAzul.start();
        //bola negra
        ImageView negra=findViewById(R.id.bolanegra);
        AnimatorSet animadorNegro = new AnimatorSet();
        ObjectAnimator trasladarNegro = ObjectAnimator.ofFloat(negra, "translationX", 200f, 0f);
        trasladarNegro.setDuration(3000);
        trasladarNegro.setInterpolator(new AccelerateInterpolator());
        animadorNegro.playTogether(trasladarNegro, fade, escala, rotacion);
        animadorNegro.start();
    }

    private void animacionJuegoCargaCangrejo() {
        ImageView imageView = findViewById(R.id.cangrejos);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        AnimatorSet animadorCangrejo = new AnimatorSet();
        // Animación de fade
        ObjectAnimator fade = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
        fade.setDuration(7000);
        fade.setInterpolator(new AccelerateInterpolator());
        // Animación de translación
        float desdeX = -180;
        float hastaX = 180;
        TranslateAnimation animation = new TranslateAnimation(desdeX, hastaX, 0, 1);
        animation.setDuration(5000); // duración de la animación
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        imageView.startAnimation(animation);
        animadorCangrejo.playTogether(fade);
        animadorCangrejo.start();

        animationDrawable.start();
    }

    public void calculaPantalla() {
        if (Build.VERSION.SDK_INT > 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            anchoPantalla = size.x;
            altoPantalla = size.y;
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            anchoPantalla = display.getWidth();  // deprecated
            altoPantalla = display.getHeight();  // deprecated
        }

        Log.i(Juego.class.getSimpleName(), "alto:" + altoPantalla + "," + "ancho:" + anchoPantalla);
    }

    private void hideSystemUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Configure the behavior of the hidden system bars.
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

    }

    private void cargarMusicaIntro() {
        mp = MediaPlayer.create(getApplicationContext(), R.raw.intro);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.isLooping();
            }
        });
    }

    public void animacionBoton() {
        if (Build.VERSION.SDK_INT > 10) {
            AnimatorSet animadorBoton = new AnimatorSet();
            ObjectAnimator trasladar = ObjectAnimator.ofFloat(btn, "translationY", 200f, 0f);
            trasladar.setDuration(3000);
            trasladar.setInterpolator(new AccelerateInterpolator());
            ObjectAnimator fade = ObjectAnimator.ofFloat(btn, "alpha", 0f, 1f);
            fade.setDuration(3000);
            fade.setInterpolator(new AccelerateInterpolator());
            ObjectAnimator escalarX = ObjectAnimator.ofFloat(btn, "scaleX", 0.8f, 1f);
            ObjectAnimator escalarY = ObjectAnimator.ofFloat(btn, "scaleY", 0.8f, 1f);
            AnimatorSet escala = new AnimatorSet();
            escala.playTogether(escalarX, escalarY);
            escala.setDuration(3000);
            ObjectAnimator rotacion = ObjectAnimator.ofFloat(btn, "rotation", 0f, 360f);
            rotacion.setDuration(3000);
            animadorBoton.playTogether(trasladar, fade, escala, rotacion);
            animadorBoton.start();
        }
    }
}