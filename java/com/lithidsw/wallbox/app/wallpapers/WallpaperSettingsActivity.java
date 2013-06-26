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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.widget.Toast;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.db.ContentDataSource;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.Utils;

public class WallpaperSettingsActivity extends PreferenceActivity {

    static CheckBoxPreference mCheckInterval;
    Activity a;
    Utils mUtils;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wallpaper_preferences);
        a = this;
        mUtils = new Utils(a);

        final ActionBar mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        mCheckInterval = (CheckBoxPreference) findPreference(C.PREF_WALLPAPER_CHECK_INTERVAL);
        mCheckInterval.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                if (arg1.equals(true)) {
                    mUtils.setWallpaperAlarms(true);
                } else {
                    mUtils.setWallpaperAlarms(false);
                }
                return true;
            }
        });

        Preference prefFav = findPreference(C.PREF_WALLPAPER_CLEAR_FAVORITES);
        if (prefFav != null) {
            prefFav.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    new ContentDataSource(a).deleteAllTable("favorites");
                    CharSequence cha = a.getString(R.string.favorites_title);
                    Toast.makeText(a, cha, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

        Preference prefDown = findPreference(C.PREF_WALLPAPER_CLEAR_DOWNLOADED);
        if (prefDown != null) {
            prefDown.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    new ContentDataSource(a).deleteAllTable("downloaded");
                    mUtils.removeDir(mUtils.getExternalDir());
                    mUtils.sendToast(a.getString(R.string.downloaded_removed));
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + mUtils.getExternalDir())));
                    return false;
                }
            });
        }

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        Preference prefInfoScreen = findPreference(C.PREF_WALLPAPER_INFO_SCREEN);
        if (prefInfoScreen != null) {
            prefInfoScreen.setSummary("Width: " + dpWidth + "dp" + " Height:" + dpHeight + "dp");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
