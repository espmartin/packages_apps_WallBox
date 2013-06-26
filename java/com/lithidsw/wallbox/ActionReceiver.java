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

package com.lithidsw.wallbox;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lithidsw.wallbox.app.wallpapers.receiver.UpdateChecker;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.DateBuilder;
import com.lithidsw.wallbox.utils.Utils;

import java.util.Date;

public class ActionReceiver extends BroadcastReceiver {

    SharedPreferences prefs;
    Utils mUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            mUtils = new Utils(context);
            prefs = context.getSharedPreferences(C.PREF, Context.MODE_PRIVATE);
            if (String.valueOf(intent.getAction()).equals("com.lithidsw.wallbox.UPDATE_RANDOMIZER_WALL")) {
                int interval = prefs.getInt(C.PREF_RANDOMIZER_INTERVAL, 0);

                if (interval > 0) {
                    long curTime = new Date().getTime();
                    String date = new DateBuilder().getFullDate(context, String.valueOf(curTime));
                    System.out.println("PapersRand, got alarm for update wallpaper: " + date);

                    final Context con = context;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            String item = new Utils(con).setRandomizerWallpaperFromFile();
                            if (item != null) {
                                prefs.edit().putString(C.PREF_LAST_RANDOMIZER_MD5, item).commit();
                            }
                        }
                    };

                    thread.start();
                }
            }

            if (String.valueOf(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {
                int interval = prefs.getInt(C.PREF_RANDOMIZER_INTERVAL, 0);
                if (interval > 0) {
                    new Utils(context).stopRandomizerAlarms();
                    new Utils(context).setRandomizerAlarms(interval);
                }

                if (prefs.getBoolean(C.PREF_SATURATE_START, false)) {
                    mUtils.stopSaturatedAlarms();
                    mUtils.setSaturatedAlarms();
                    mUtils.sendToast(context.getResources().getString(R.string.started_sat));
                }

                if (prefs.getBoolean(C.PREF_WALLPAPER_CHECK_INTERVAL, false)) {
                    mUtils.setWallpaperAlarms(false);
                    mUtils.setWallpaperAlarms(true);
                }

                if (prefs.getBoolean(C.PREF_WALLPAPER_CHECK_BOOT, false)) {
                    new UpdateChecker(context).runUpdateCheck();
                }
            }

            if (String.valueOf(intent.getAction()).equals("com.lithidsw.wallbox.UPDATE_SATURATE_WALL")) {
                WallpaperManager wm = WallpaperManager.getInstance(context);
                try {
                    WallpaperInfo info = wm.getWallpaperInfo();
                    info.toString();
                    mUtils.stopSaturatedAlarms();
                    prefs.edit().putBoolean(C.PREF_SATURATE_START, false).commit();
                } catch (NullPointerException e) {
                    int randInt = prefs.getInt(C.PREF_RANDOMIZER_INTERVAL, 0);
                    if (randInt <= 0) {
                        mUtils.setSaturatedWallpaperFromFile(false);
                    } else {
                        mUtils.setSaturatedWallpaperFromFile(true);
                    }
                }
            }

            if (String.valueOf(intent.getAction()).equals("com.lithidsw.wallbox.CHECK_WALLPAPER_UPDATES")) {
                if (prefs.getBoolean(C.PREF_WALLPAPER_CHECK_INTERVAL, false)) {
                    new UpdateChecker(context).runUpdateCheck();
                }
            }
        }
    }
}
