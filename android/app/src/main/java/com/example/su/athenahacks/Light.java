package com.example.su.athenahacks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Light extends AppCompatActivity {
    Button lightswitch;
    TextView response;
    ImageView bulb;

    Boolean isOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        lightswitch = (Button)findViewById(R.id.on_off);
        response = (TextView)findViewById(R.id.response);
        bulb = (ImageView)findViewById(R.id.bulb);

        lightswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOn) {
                    Client myClient = new Client("207.151.58.166", 5001, "LIGHT_ON", response);
                    myClient.execute();
                    bulb.setImageResource(R.drawable.onbulb);
                    lightswitch.setText("turn off");
                    isOn = true;
                }
                else {
                    Client myClient = new Client("207.151.58.166", 5001, "LIGHT_OFF", response);
                    myClient.execute();
                    bulb.setImageResource(R.drawable.offbulb);
                    lightswitch.setText("turn on");
                    isOn = false;
                }
            }
        });

    }
}
