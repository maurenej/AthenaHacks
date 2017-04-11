package com.example.su.athenahacks;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Sound extends AppCompatActivity {
    RadioGroup radioSound;
    RadioButton sound1, sound2, sound3;
    TextView response;
    Button play;
    Boolean isSelected = true;
    String command = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        radioSound = (RadioGroup) findViewById(R.id.radioSound);
        sound1 = (RadioButton)findViewById(R.id.SOUND_1);
        sound2 = (RadioButton)findViewById(R.id.SOUND_2);
        sound3 = (RadioButton)findViewById(R.id.SOUND_3);
        play = (Button) findViewById(R.id.play_button);
        response = (TextView)findViewById(R.id.response);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sound1.isChecked()){
                    command = "ALARM";
                    isSelected = true;
                }
                else if (sound2.isChecked()){
                    command = "SOUND_2";
                    isSelected = true;
                }
                else if (sound3.isChecked()){
                    command = "SOUND_3";
                    isSelected = true;
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "You must check a sound track!", Toast.LENGTH_LONG);
                    toast.show();
                    isSelected = false;
                }
                if (isSelected) {
                    Client myClient = new Client("207.151.58.166", 5001, command, response);
                    myClient.execute();
                }
            }
        });

    }

}
