package com.example.su.athenahacks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ScrollView;
import android.widget.TextView;

public class Manual extends AppCompatActivity {
    ScrollView scrollbar;
    TextView info;

    String sourceString = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        scrollbar = (ScrollView)findViewById(R.id.scroller);
        info = (TextView)findViewById(R.id.man);

        sourceString = "<h1><b>" + "Temperature" + "</b></h1>"
        + "<p>Here, you can display the temperature of your dolls' new home. You must enter either 'C' or 'F' as the temperature unit.</p>" +
                "<p>More info about what these values mean:</p>" +
                "<p>Celsius is a temperature scale where 0 °C indicates the melting point of ice and 100 °C  indicates the steam point of water.\n" +
                "Fahrenheit is a temperature scale used mostly in the USA, where  the freezing point of water is 32 °F and the boiling point of water is 212 °F.\n" +
                "To convert from Celsius to Fahrenheit, use formula:  F = (C x 2) + 30.</p>"
        +"<h1><b>Light</b></h1>"
        +"<p>This is one's easy! Just like an on/off switch that you would find in your own home</p>"
        +"<p>More info about light:</p>"
        +"<p>A light bulb, or electric light or electric lamp is a device that produces light from electricity.\n" +
                "Thomas Edison is credited with inventing the light bulb.</p>"
        +"<h1><b>RGB Values</b></h1>"
        +"<p>A particular RGB color space is defined by the three chromaticities of the red, green, and blue additive primaries. By combining different scale of these three, you can produce any color! This is like mixing paint in art class</p>";
        info.setText(Html.fromHtml(sourceString));

    }
}
