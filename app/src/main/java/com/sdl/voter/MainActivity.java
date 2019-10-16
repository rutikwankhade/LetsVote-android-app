package com.sdl.voter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    private Button joinNowButton, Loginbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        joinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        Loginbutton = (Button) findViewById(R.id.main_login_btn);


        Loginbutton.setOnClickListener(new View. OnClickListener()
        {

            public  void  onClick  (View view )
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        } );


        joinNowButton.setOnClickListener(new View. OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        } );

    }
}
