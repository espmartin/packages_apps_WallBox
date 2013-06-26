/*
 * Copyright 2013 Jeremie Long
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lithidsw.wallbox.app.colorwall;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.colorpicker.ColorPicker;
import com.lithidsw.wallbox.colorpicker.SVBar;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.CustomDialogs;

import java.io.IOException;

public class ColorWallFrag extends Fragment implements View.OnClickListener {

    LinearLayout ll;
    FragmentActivity fa;
    private ColorPicker picker;
    private SVBar svBar;

    SharedPreferences prefs;

    private WallpaperManager wm;
    private int mColor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fa = super.getActivity();
        prefs = fa.getSharedPreferences(C.PREF, Context.MODE_PRIVATE);
        ll = (LinearLayout) inflater.inflate(R.layout.colorwall_frag, container, false);
        wm = WallpaperManager.getInstance(fa);
        picker = (ColorPicker) ll.findViewById(R.id.picker);
        picker.setOldCenterColor(getResources().getColor(R.color.black));
        svBar = (SVBar) ll.findViewById(R.id.svbar);
        picker.addSVBar(svBar);
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mColor = color;
            }
        });
        ll.findViewById(R.id.color_white).setOnClickListener(this);
        ll.findViewById(R.id.color_blue).setOnClickListener(this);
        ll.findViewById(R.id.color_green).setOnClickListener(this);
        ll.findViewById(R.id.color_purple).setOnClickListener(this);
        ll.findViewById(R.id.color_yellow).setOnClickListener(this);
        ll.findViewById(R.id.color_red).setOnClickListener(this);

        ll.findViewById(R.id.button1).setOnClickListener(this);

        if (!prefs.getBoolean(C.PREF_COLORWALL_FIRST_RUN_MAIN, false)) {
            prefs.edit().putBoolean(C.PREF_COLORWALL_FIRST_RUN_MAIN, true).commit();
            String title = getResources().getString(R.string.main_title_colorwall);
            String message = getResources().getString(R.string.colorwall_description);
            new CustomDialogs().openFirstDialog(fa, title, message);
        }
        return ll;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.color_white:
                picker.setColor(getResources().getColor(R.color.white));
                break;
            case R.id.color_blue:
                picker.setColor(getResources().getColor(R.color.blue));
                break;
            case R.id.color_purple:
                picker.setColor(getResources().getColor(R.color.purple));
                break;
            case R.id.color_green:
                picker.setColor(getResources().getColor(R.color.green));
                break;
            case R.id.color_yellow:
                picker.setColor(getResources().getColor(R.color.yellow));
                break;
            case R.id.color_red:
                picker.setColor(getResources().getColor(R.color.red));
                break;
            case R.id.button1:
                try {
                    Bitmap bit = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                    Canvas can = new Canvas(bit);
                    can.drawColor(mColor);
                    wm.setBitmap(bit);
                    Toast.makeText(fa, "Current color wallpaper applied!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(fa, "Current color wallpaper had an error!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                picker.setColor(getResources().getColor(R.color.black));
                break;
        }
    }
}
