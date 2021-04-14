package com.armin_mehraeen.speech;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton language, copy, speaker, mic, clear;
    EditText input;
    TextView language_show;
    TextToSpeech textToSpeech;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                        Toast.makeText(MainActivity.this, "This language not supported ...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Init failed ...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        language_show = findViewById(R.id.language_show);
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(this);
        language = findViewById(R.id.language);
        language.setOnClickListener(this);
        mic = findViewById(R.id.mic);
        mic.setOnClickListener(this);
        copy = findViewById(R.id.copy);
        copy.setOnClickListener(this);
        speaker = findViewById(R.id.speaker);
        speaker.setOnClickListener(this);
        input = findViewById(R.id.input);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if (resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                input.setText(result.get(0));
            }
        }
    }

    @Override
    public void onClick(View v) {
        input.setBackgroundResource(R.drawable.ed_style);
        String txt = input.getText().toString().trim();
        if (v.getId() == R.id.language) {
            String lan = "";
            if (flag == 0){
                flag = 1;
                lan = "Persian";
            }else{
                flag = 0;
                lan = "English";
            }
            language_show.setText("Microphone Language : " + lan);
            Toast.makeText(this, "Language Changed ...", Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.copy) {
            if (txt.isEmpty())
                input.setBackgroundResource(R.drawable.ed_error_style);
            else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(this.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", txt);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.speaker) {
            if (txt.isEmpty())
                input.setBackgroundResource(R.drawable.ed_error_style);
            else {
                textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(this, "Speaker Clicked", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.mic) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            if (flag == 0){
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.US);
            }else {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"fa");
            }
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech-App");
            try{
                startActivityForResult(intent,100);
            }catch (ActivityNotFoundException e){
                Toast.makeText(this, "Your device not supported ...", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "Mic Clicked", Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.clear) {
            input.setText("");
            Toast.makeText(this, "Clear Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}