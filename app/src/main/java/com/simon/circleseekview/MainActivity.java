package com.simon.circleseekview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CircleSeekView color_picker;
    private RelativeLayout layout;
    private TextView value_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        color_picker = findViewById(R.id.k2_light_color_picker);
        value_tv = findViewById(R.id.value_tv);
        layout = findViewById(R.id.layout);
        color_picker.setColorValue(35);
        color_picker.setOnSelectColorListener(new CircleSeekView.OnSelectColorListener() {
            @Override
            public void onSelectColor(int[] rgb, int colorValue,boolean isFromUser) {
                layout.setBackgroundColor(Color.argb(255,rgb[0],rgb[1],rgb[2]));
                value_tv.setText(colorValue+"");
            }

            @Override
            public void onSelectColorFinish(int colorValue) {

            }
        });

    }


    @Override
    protected void onDestroy() {
        color_picker.recycleBitMap();
        super.onDestroy();
    }
}
