package com.example.su.athenahacks;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {


        // ===========================================================

        // Fields

        // ===========================================================



        private final int SPLASH_DISPLAY_LENGTH = 3000;



        // ===========================================================

        // "Constructors"

        // ===========================================================



        /** Called when the activity is first created. */

        @Override

        public void onCreate(Bundle icicle) {

            super.onCreate(icicle);

            setContentView(R.layout.activity_splash);



                /* New Handler to start the Menu-Activity

                 * and close this Splash-Screen after some seconds.*/

            new Handler().postDelayed(new Runnable(){

                @Override

                public void run() {

                    Intent mainIntent = new Intent(Splash.this, MainActivity.class);

                    Splash.this.startActivity(mainIntent);

                    Splash.this.finish();

                }

            }, SPLASH_DISPLAY_LENGTH);

        }
}
