package com.example.soundboardproject;

import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech mTTS;
    private EditText mEditText;
    private Button mButtonSpeak;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSpeak = findViewById(R.id.button_speak);

        mRadioGroup = findViewById(R.id.radio_group);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale("ru"));

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mButtonSpeak.setEnabled(true);

                        Log.d("Voice", mTTS.getVoice().toString());

                        int i = 0;
                        for(Voice v: mTTS.getVoices()) {
                            if (v.getName().startsWith("ru")) {
                                RadioButton rb = new RadioButton(getApplicationContext());
                                rb.setId(i);
                                rb.setText(v.getName());
                                mRadioGroup.addView(rb);
                                if (v.getName().equals(mTTS.getVoice().getName()))
                                    mRadioGroup.check(i);
                                i++;
                            }
                        }
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mEditText = findViewById(R.id.edit_text);
        mSeekBarPitch = findViewById(R.id.pitch);
        mSeekBarSpeed = findViewById(R.id.speed);

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int ID = mRadioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(ID);
                for (Voice v : mTTS.getVoices()) {
                    if (v.getName().equals(rb.getText().toString())) {
                        mTTS.setVoice(v);
                        Log.d("Change", "Voice changed");
                    }
                }
                Log.d("Change", "Voice not changed");
            }
        });

    }

    private void speak() {
        String text = mEditText.getText().toString();
        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}
