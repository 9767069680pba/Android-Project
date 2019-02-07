package com.example.issc.rajshri;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
public class activity_registration extends AppCompatActivity implements
        TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    SQLiteDatabase db;
    int cnt=0;
    private Button register,speak;
    private TextView username,mobile,email;


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
        setContentView(R.layout.activity_registration);

        tts = new TextToSpeech(this, this);

        speak = (Button) findViewById(R.id.speak);
        register=(Button) findViewById(R.id.register);

        username = (TextView) findViewById(R.id.username);
        mobile=(TextView)findViewById(R.id.mobile);
        email=(TextView)findViewById(R.id.email);



        db = openOrCreateDatabase("dlvi11", Context.MODE_PRIVATE, null);

        speakOut("Speak User Name....");
        cnt=1;

        speak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });



        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(username.getText().toString().length()==0)
                {
                    Toast.makeText( activity_registration.this, "Please Enter The User Name", Toast.LENGTH_SHORT ).show();
                }
                else if(mobile.getText().toString().length()==0)
                {
                    Toast.makeText( activity_registration.this, "Please Enter The Password", Toast.LENGTH_SHORT ).show();
                }
                else if(email.getText().toString().length()==0)
                {
                    Toast.makeText( activity_registration.this, "Please Enter The Email Address", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    String user=username.getText().toString();
                    String mobile_number=mobile.getText().toString();
                    String email_address=email.getText().toString();

                    db.execSQL("INSERT INTO USER VALUES(7,'"+user+"',NULL,'"+email_address+"','"+mobile_number+"',NULL,NULL);");

                    Intent in=new Intent(activity_registration.this,MainActivity.class);
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

                    if((result.get(0)).equals("help"))
                    {
                        speakOut("");
                    }

                    if((result.get(0)).equals("register"))
                    {
                        if(username.getText().toString().length()==0)
                        {
                           speakOut("Please Enter The User Name");
                        }
                        else if(mobile.getText().toString().length()==0)
                        {
                            speakOut("Please Enter The Mobile Number");
                        }
                        else if(email.getText().toString().length()==0)
                        {
                            speakOut("Please Enter The Email Id");
                        }
                        else
                        {
                            String user=username.getText().toString();
                            String mobile_number=mobile.getText().toString();
                            String email_address=email.getText().toString();
                            db.execSQL("INSERT INTO USER VALUES(1,'"+user+"',NULL,'"+email_address+"','"+mobile_number+"',NULL,NULL);");
                            Intent in=new Intent(activity_registration.this,MainActivity.class);
                            startActivity(in);
                        }
                    }

                    else
                    {
                        if(cnt==1)
                        {
                            username.setText(result.get(0).toString());
                            speakOut("Enter Mobile No");
                            cnt=2;
                        }
                        if(cnt==2)
                        {
                            mobile.setText(result.get(0).toString());
                            cnt=3;
                        }
                        if(cnt==3)
                        {
                            email.setText(result.get(0).toString());
                        }
                    }
                }
                break;
            }
        }
    }
}