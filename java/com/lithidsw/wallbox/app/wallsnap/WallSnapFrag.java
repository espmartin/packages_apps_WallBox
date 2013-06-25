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

package com.lithidsw.wallbox.app.wallsnap;

import android.app.ProgressDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.CustomDialogs;
import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.utils.Utils;

public class WallSnapFrag extends Fragment implements View.OnClickListener{

    LinearLayout ll;
    FragmentActivity fa;
    private WallpaperManager wm;
    private ImageView image;
    SharedPreferences prefs;
    Utils mUtils;
    RadioButton mRadioSat;
    RadioButton mRadioHue;
    SeekBar mSeekSat;
    SeekBar mSeekHue;
    Bitmap mBitmap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fa = super.getActivity();
        mUtils = new Utils(fa);
        prefs = fa.getSharedPreferences(C.PREF, Context.MODE_PRIVATE);
        ll = (LinearLayout)inflater.inflate(R.layout.wallsave_frag, container, false);
        wm = WallpaperManager.getInstance(fa);
        image = (ImageView) ll.findViewById(R.id.main_image);
        try {
            WallpaperInfo info = wm.getWallpaperInfo();
            info.toString();
        } catch (NullPointerException ignore){}
        image.setImageDrawable(wm.getDrawable());

        mRadioSat = (RadioButton) ll.findViewById(R.id.radio_sat);
        mRadioSat.setOnClickListener(this);
        mRadioHue = (RadioButton) ll.findViewById(R.id.radio_hue);
        mRadioHue.setOnClickListener(this);

        ll.findViewById(R.id.button_set).setOnClickListener(this);

        mSeekSat = (SeekBar) ll.findViewById(R.id.sat_seeker);
        mSeekSat.setMax(11);
        mSeekSat.setProgress(11);
        mSeekHue = (SeekBar) ll.findViewById(R.id.hue_seeker);
        mSeekHue.setMax(180);

        mSeekSat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                image.setImageDrawable(mUtils.convertToGrayscale(image.getDrawable(), mUtils.getFloat(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mSeekHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                image.getDrawable().setColorFilter(ColorFilterGenerator.adjustHue(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if (!prefs.getBoolean(C.PREF_WALLSNAP_FIRST_RUN_MAIN, false)) {
            prefs.edit().putBoolean(C.PREF_WALLSNAP_FIRST_RUN_MAIN, true).commit();
            String title = getResources().getString(R.string.main_title_wallsnap);
            String message = getResources().getString(R.string.wallsnap_description);
            new CustomDialogs().openFirstDialog(fa, title, message);
        }
        return ll;
    }

    private void updateView() {
        if (mRadioSat.isChecked()) {
            mSeekHue.setVisibility(View.GONE);
            mSeekSat.setVisibility(View.VISIBLE);
            mSeekSat.setProgress(11);
        } else {
            mSeekHue.setVisibility(View.VISIBLE);
            mSeekSat.setVisibility(View.GONE);
            mSeekHue.setProgress(0);
        }
    }

    private Bitmap loadBitmapFromView(View v) {
        final int w = v.getWidth();
        final int h = v.getHeight();
        final Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas c = new  Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_set:
                mBitmap = Utils.drawableToBitmap(image.getDrawable());
                new ImageSaver().execute();
                break;
            default:
                updateView();
                break;
        }
    }

    class ImageSaver extends AsyncTask<String, String, Boolean> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(fa, "", fa.getResources().getString(R.string.save_wallpaper));
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return mUtils.saveWallSnap(loadBitmapFromView(image));
        }

        @Override
        protected void onPostExecute(Boolean count) {
            progressDialog.dismiss();
            if (count) {
                mUtils.sendToast(fa.getResources().getString(R.string.wallpaper_saved));
            } else {
                mUtils.sendToast(fa.getResources().getString(R.string.wallpaper_error));
            }
        }
    }
}
