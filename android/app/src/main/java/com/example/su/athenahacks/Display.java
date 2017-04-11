package com.example.su.athenahacks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Display extends AppCompatActivity {
    EditText redVal, greenVal, blueVal, tempUnit;
    Button submit, temp, time;
    TextView response;
    Boolean isOnTemp = false, isOnTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        redVal = (EditText)findViewById(R.id.red_val);
        greenVal = (EditText)findViewById(R.id.green_val);
        blueVal = (EditText)findViewById(R.id.blue_val);
        submit = (Button)findViewById(R.id.changeColors);
        response = (TextView)findViewById(R.id.response);
        temp = (Button)findViewById(R.id.tempButton);
        time = (Button)findViewById(R.id.timeButton);
        tempUnit = (EditText)findViewById(R.id.tempUnit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(redVal.getText().toString().equals("") || greenVal.getText().toString().equals("")
                        || blueVal.getText().toString().equals("")){
                    Toast notification = Toast.makeText(getApplicationContext(), "You must enter all of the values!", Toast.LENGTH_LONG);
                    notification.show();
                }
                else{
                    StringBuilder command = new StringBuilder();
                    command.append("RGB_" + redVal.getText().toString());
                    command.append("," + greenVal.getText().toString());
                    command.append(("," + blueVal.getText().toString()));

                    Client myClient = new Client("207.151.58.166", 5001, command.toString(), response);
                    myClient.execute();
                }
            }
        });

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnTemp){
                    isOnTemp = true;
                    temp.setText("HIDE TEMP");
                    if(tempUnit.getText().toString().equals("F")) {
                        Client myClient = new Client("207.151.58.166", 5001, "TEMP_F", response);
                        myClient.execute();
                    }
                    else if (tempUnit.getText().toString().equals("C")){
                        Client myClient = new Client("207.151.58.166", 5001, "TEMP_C", response);
                        myClient.execute();
                    }
                    else{
                        Toast notification = Toast.makeText(getApplicationContext(), "You must set a unit of measure! Look to the manual on the home page for help.", Toast.LENGTH_LONG);
                        notification.show();
                    }
                }
                else{
                    isOnTemp = false;
                    temp.setText("SHOW TEMP");
                    Client myClient = new Client("207.151.58.166", 5001, "TEMP_OFF", response);
                    myClient.execute();
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnTime){
                    isOnTime = true;
                    time.setText("HIDE TIME");
                    Client myClient = new Client("207.151.58.166", 5001, "TIME_ON", response);
                    myClient.execute();
                }
                else{
                    isOnTime = false;
                    time.setText("SHOW TIME");
                    Client myClient = new Client("207.151.58.166", 5001, "TIME_OFF", response);
                    myClient.execute();
                }
            }
        });


    }
}
