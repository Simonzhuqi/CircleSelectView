package com.simon.circleseekview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private CircleSeekView k2_light_color_picker;
    private ImageView k2_light_small_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        k2_light_color_picker = findViewById(R.id.k2_light_color_picker);
        k2_light_small_icon = findViewById(R.id.k2_light_small_icon);
        k2_light_color_picker.setColorValue(35);
        k2_light_color_picker.setOnSelectColorListener(new CircleSeekView.OnSelectColorListener() {
            @Override
            public void onSelectColor(int[] rgb, int colorValue,boolean isFromUser) {
                k2_light_small_icon.setBackgroundColor(Color.argb(255,rgb[0],rgb[1],rgb[2]));
            }

            @Override
            public void onSelectColorFinish(int colorValue) {

            }
        });

    }


    @Override
    protected void onDestroy() {
        k2_light_color_picker.recycleBitMap();
        super.onDestroy();
    }
}
