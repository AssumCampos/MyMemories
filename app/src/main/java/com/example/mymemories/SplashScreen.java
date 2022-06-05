package com.example.mymemories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIMER = 2000;
    private ImageView backgroundImage;
    private TextView logo, slogan;
    private Animation sideAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        backgroundImage = findViewById(R.id.background_image);
        logo = findViewById(R.id.poweredByLine);
        slogan = findViewById(R.id.textView2);

        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        backgroundImage.setAnimation(sideAnim);
        logo.setAnimation(bottomAnim);

        // Gradient color
        TextView textViewColecciones = (TextView) findViewById(R.id.poweredByLine);
        TextPaint paint = textViewColecciones.getPaint();
        float width = paint.measureText("MyMemories");
        Shader textShader = new LinearGradient(0, 0, width, textViewColecciones.getTextSize(),
                new int[]{
                        Color.parseColor("#ffcc80"),
                        Color.parseColor("#f57c00")
                }, null, Shader.TileMode.CLAMP);
        textViewColecciones.getPaint().setShader(textShader);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);
    }
}