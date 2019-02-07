package com.example.issc.rajshri;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class activity_login extends AppCompatActivity implements
        TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    SQLiteDatabase db;

    private Button login,speak;
    private TextView user;


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                speakOut("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tts = new TextToSpeech(this, this);
        user = (TextView) findViewById(R.id.user);
        speak = (Button) findViewById(R.id.speak);
        login=(Button) findViewById(R.id.login);


        db = openOrCreateDatabase("dlvi11", Context.MODE_PRIVATE, null);

        speak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String str=user.getText().toString();
                // db.execSQL("select U_ID from user where name="+str);
                if(str.length()==0)
                {
                    Toast.makeText( activity_login.this, "Please Enter The User Name", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    //select Query

                    Intent in=new Intent(activity_login.this, activity_homepage.class);
                    in.putExtra("username",user.getText().toString());
                    startActivity(in);
                }
            }
        });


    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if((result.get(0)).equals("login"))
                    {
                        String str=user.getText().toString();
                        if(str.length()==0)
                        {
                            speakOut("please speak the user name");
                        }
                        else {
                                db.execSQL("select * from user where name='"+user.getText().toString()+"'");
                                Intent in=new Intent(activity_login.this,activity_homepage.class);
                                in.putExtra("username",user.getText().toString());
                                startActivity(in);
                        }
                    }
                    else
                    {
                        user.setText(result.get(0));
                    }
                }
                break;
            }
        }
    }
}