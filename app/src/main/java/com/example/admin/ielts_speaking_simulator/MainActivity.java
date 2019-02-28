package com.example.admin.ielts_speaking_simulator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements OnInitListener {
    TextView myquestion,editText ;
    ImageView ballimg;
    public static final int RequestPermissionCode = 1;
    Random random ;
    ArrayList<String> dsQuest = new ArrayList<String>();
    ArrayList<String> dsresultQuest = new ArrayList<String>();
    ArrayList<String> dsRecord = new ArrayList<String>();
    int curIndex=0;

    MediaRecorder mediaRecorder ;
    //String outputfile = null;
    Button buttonStart, buttonStop, speak, answeractivity, btnRecognizer;
    String AudioSavePathInDevice = null;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    MediaPlayer mediaPlayer ;

    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;

    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById(R.id.ghiam);
        buttonStop = (Button) findViewById(R.id.dungghiam);
        buttonStop.setEnabled(false);

        /////
        btnRecognizer = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        /////////

        ballimg = findViewById(R.id.imgball);
        ballimg.setBackgroundResource(R.drawable.hoatcanhball);

        random = new Random();

        myquestion = findViewById(R.id.txtquestion);
        speak = findViewById(R.id.btnspeak);
        answeractivity = findViewById(R.id.btnActivityResult);

        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        // set volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.getStreamVolume(am.STREAM_MUSIC);

        // ------------ : RECOGNIZER : ----------------//
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

       // final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
                ButtonStopRecording();
                buttonStop.performClick();
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    editText.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        // ------------ : AUTO TALK : ---------------//
        ButtonQuestionSpeak();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speak.performClick();
            }
        },300);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        AnimationDrawable hh1=(AnimationDrawable)ballimg.getBackground();
        if(hasFocus==true)
            hh1.start();
        else
            hh1.stop();
    }

   /* public void ButtonSpeechRecognizer()
    {
        btnRecognizer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        editText.setHint("You will see input here");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        editText.setText("");
                        editText.setHint("Listening...");
                        ButtonStartRecording();
                        break;
                }
                return false;
            }
        });
    }*/

    public void ButtonQuestionSpeak()
    {
        speak.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputStream in = null;
                try {
                    in = getAssets().open("forecastquest.txt");
                    int size = in.available();
                    byte[] buffet = new byte[size];
                    in.read(buffet);
                    String chuoi = new String(buffet);
                    String[] finalchuoi = chuoi.split("\n");
                    dsQuest.clear();

                    for (int k=0; k<finalchuoi.length; k++)
                    {
                        dsQuest.add(finalchuoi[k]);
                    }
                    for (int v = 0; v < 5 ; v ++)
                    {
                        int ranQuest = (int)(Math.random()*(dsQuest.size()-1));
                        dsresultQuest.add(dsQuest.get(ranQuest));
                      //  Log.i("questions", dsresultQuest.toString());
                    }

                        String quest2 = dsresultQuest.get(curIndex);
                        myquestion.setText(quest2);
                        speakWords(quest2);
                        Log.i("speakquest", quest2);

                        String mysplit[] = quest2.split(" ");
                        int k = mysplit.length;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ButtonStartRecording();
                                buttonStart.performClick();
                                ButtonStopRecording();
                                buttonStop.setEnabled(true);
                            }
                        }, k*450);

            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        });
    }

    public void ButtonStartRecording()
    {
        buttonStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                if(checkPermission()) {

                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                    dsRecord.add(AudioSavePathInDevice);

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Say Something", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestPermission();
                }
            }
        });

    }

    public void ButtonStopRecording()
    {
        buttonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                curIndex++;
                Log.i("index", curIndex+"");

                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);

                Toast.makeText(MainActivity.this, "Stop Recording", Toast.LENGTH_SHORT).show();

                if (curIndex ==5)
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ButtonAnswer();
                            answeractivity.performClick();
                        }
                    }, 2000);

                }
                else
                {
                    String quest2 = dsresultQuest.get(curIndex);
                    myquestion.setText(quest2);
                    speakWords(quest2);
                    Log.i("speakquest", quest2);

                    String mysplit[] = quest2.split(" ");
                    int k = mysplit.length;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ButtonStartRecording();
                            buttonStart.performClick();
                            ButtonStopRecording();
                            buttonStop.setEnabled(true);
                        }
                    }, k*450);
                }
            }
        });
    }

    public void ButtonAnswer()
    {
        answeractivity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                i.putExtra("question", dsresultQuest);
                i.putExtra("record", dsRecord);
                startActivity(i);
                finish();
            }
        });
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);

    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));
            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    ///////////////////////////////////%%%%%%%%%%%%%%%%%%%%%%%%%%%%


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
                myTTS = new TextToSpeech(MainActivity.this, (TextToSpeech.OnInitListener) this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
    //setup TTS
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
