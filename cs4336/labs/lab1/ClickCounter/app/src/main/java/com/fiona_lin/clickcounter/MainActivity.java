package com.fiona_lin.clickcounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button clickme = (Button) findViewById(R.id.clickme);

        final Button clear = (Button) findViewById(R.id.clear);

        clickme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                count++;
                myClick(v); /* my method to call new intent or activity */
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myClear(v); /* my method to call new intent or activity */
            }
        });

    }

    public void myClick(View v) {
        TextView txCounter = (TextView) findViewById(R.id.textView2);
        String display = "You clicked " + this.count + " times";
        txCounter.setText(display);
    }

    public void myClear(View v) {
        TextView txCounter = (TextView) findViewById(R.id.textView2);
        String display = "You clicked " + this.count + " times, and reset to 0";
        txCounter.setText(display);
        this.count = 0;
    }
}
