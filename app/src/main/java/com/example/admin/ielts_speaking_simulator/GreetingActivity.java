package com.example.admin.ielts_speaking_simulator;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class GreetingActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Button btnhi;
    ImageView iv;
    TextView txt;

    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        // set volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.getStreamVolume(am.STREAM_MUSIC);

        btnhi = findViewById(R.id.btnhello);
        iv=findViewById(R.id.imgview);
        iv.setBackgroundResource(R.drawable.hoatcanh);
        txt = findViewById(R.id.txtgreeting);

        Buttonclick();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnhi.performClick();
                txt.setVisibility(View.VISIBLE);
                txt.setText("Hello, I'm Zombie and I'm your speaking examiner today. I'm going to ask you 5 questions, then I'll show you my and your answers. Let's get started!");
            }
        }, 1000);

        Thread splash= new Thread(){
            public void run()
            {
                try {
                    Thread.sleep(12000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finally{
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                }finish();
            }
        };
        splash.start();


    }

    public void Buttonclick()
    {
        btnhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakWords("Hello, I'm Zombie, and I'm your speaking examiner today. I'm going to ask you 5 questions, then I'll show you my and your answers. Let's get started!");
               // speakWords("Hello");
            }
        });
    }

    //speak the user text
    public void speakWords(String speech) {
        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onInit(int i) {
        //check for successful instantiation
        if (i == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (i == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        AnimationDrawable hh=(AnimationDrawable)iv.getBackground();
        if(hasFocus==true)
            hh.start();
        else
            hh.stop();
    }

}
