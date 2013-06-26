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

package com.lithidsw.wallbox.app.wallpapers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.loader.ImageLoader;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.Utils;

public class ApplyWallpaperActivity extends Activity {

    Utils mUtils;
    private ImageLoader imageLoader;
    Activity a;
    NotificationManager nm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_apply);
        a = this;
        mUtils = new Utils(a);

        Intent intent = getIntent();
        String uri = intent.getStringExtra(C.EXTRA_URI_PATH);
        String name = intent.getStringExtra(C.EXTRA_URI_NAME);
        int id = intent.getIntExtra(C.EXTRA_ID, 0);

        setTitle(name);

        ImageView img = (ImageView) findViewById(R.id.image);
        imageLoader = new ImageLoader(a, 325);
        imageLoader.DisplayImage(uri, img);

        if (id != 0) {
            nm = (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(id);
        }
        new wallpaperRun().execute(uri);
    }

    class wallpaperRun extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            mUtils.sendToast("Applying");
        }

        @Override
        protected Boolean doInBackground(String... args) {
            return setLocalBitmap(args[0]);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                mUtils.sendToast(a.getString(R.string.wallpaper_applied_toast));
            } else {
                mUtils.sendToast(a.getString(R.string.wallpaper_not_applied_toast));
            }

            finish();
        }
    }

    private boolean setLocalBitmap(String string) {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(a);
            Bitmap myBitmap = BitmapFactory.decodeFile(string);
            wm.setBitmap(myBitmap);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
