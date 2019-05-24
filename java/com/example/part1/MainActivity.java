package com.example.part1;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech mTTS;
    private SeekBar mSeekbarpitch;
    private SeekBar mSeekbarspeed;
    private Button mButtonspeak;
    private TextView txvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonspeak = findViewById(R.id.button_speak);

        mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.ENGLISH);
                mButtonspeak.setEnabled(true);
            }
        });

        mSeekbarpitch = findViewById(R.id.seek_bar_pitch);
        mSeekbarspeed = findViewById(R.id.seek_bar_speed);
        txvResult = findViewById(R.id.txvResult);
        mButtonspeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }

    private void speak(){
        float pitch = (float)mSeekbarpitch.getProgress() / 50;
        if(pitch<0.1) pitch = 0.1f;
        float speed = (float)mSeekbarspeed.getProgress() / 50;
        if(speed<0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        try{
            InputStream is = this.getResources().openRawResource(R.raw.sample);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            String jsontext = new String(buffer);

            mTTS.speak(jsontext,TextToSpeech.QUEUE_FLUSH,null);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                int a = 0;
                String crt = "correct answer";
                String wrn = "wrong answer";
                String jsontext;
                try {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    InputStream is = this.getResources().openRawResource(R.raw.sample1);
                    byte[] buffer = new byte[is.available()];
                    while (is.read(buffer) != -1) ;
                    jsontext = new String(buffer);
                    if (jsontext.equals(result)) {
                        txvResult.setText(crt);
                    }
                    else{
                        txvResult.setText(wrn);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

