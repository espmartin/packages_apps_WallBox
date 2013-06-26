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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.db.LocalDBHelper;
import com.lithidsw.wallbox.app.wallpapers.db.LocalDataSource;
import com.lithidsw.wallbox.app.wallpapers.frag.GalFrag;
import com.lithidsw.wallbox.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class WallpaperDownloadedActivity extends FragmentActivity {

    Activity a;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    ArrayList<String> wallPreview = new ArrayList<String>();
    ArrayList<String> wallWid = new ArrayList<String>();
    ArrayList<String> wallName = new ArrayList<String>();
    ArrayList<String> wallAuthor = new ArrayList<String>();

    Utils mUtils;
    private WallpaperLoader wallLoader;

    private static final String TABLE_DOWNLOADED = LocalDBHelper.TABLE_DOWNLOADED;
    private static final String SELECT_WALLPAPERS = "SELECT * FROM ";
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = this;
        mUtils = new Utils(a);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        title = a.getString(R.string.downloaded);
        wallLoader = (WallpaperLoader) new WallpaperLoader().execute(
                TABLE_DOWNLOADED, LocalDBHelper.COLUMN_D_PATH);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopWallLoader();
        finish();
    }

    private void stopWallLoader() {
        if (wallLoader != null
                && wallLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            wallLoader.cancel(true);
            wallLoader = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wallpaper_gallery_menu, menu);
        return true;
    }

    private void removeItem(int i) {
        String message = "Removed "+wallName.get(i);

        File filePath = new File(wallPreview.get(i));
        if (filePath.exists()) {
            if (filePath.delete()) {
                new LocalDataSource(WallpaperDownloadedActivity.this)
                        .deleteItem(Integer.parseInt(wallWid.get(i)));
                mUtils.sendToast(message);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(filePath));
                sendBroadcast(mediaScanIntent);
            }
        }

        stopWallLoader();
        Intent intent = getIntent();
        finish();

        if (wallPreview.size() > 1) {
            startActivity(intent);
        }
    }

    private void updateViews() {
        if (wallWid.size() > 0) {
            setContentView(R.layout.wallpaper_gallery_main);
            mSectionsPagerAdapter = new SectionsPagerAdapter(
                    getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(0);
        } else {
            setTitle(title);
            setContentView(R.layout.wallpaper_empty);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_delete:
                removeItem(mViewPager.getCurrentItem());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return wallWid.size();
        }

        @Override
        public Fragment getItem(int num) {
            Bundle args = new Bundle();
            args.putString("author", wallAuthor.get(num));
            args.putString("name", wallName.get(num));
            args.putString("preview", wallPreview.get(num));
            args.putString("id", wallWid.get(num));
            args.putInt("total", wallWid.size());
            args.putString("title", title);
            args.putInt("current", num);
            Fragment frag = new GalFrag();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public CharSequence getPageTitle(int num) {
            return Html.fromHtml("<b>"
                    + (wallAuthor.get(num) + "</b>" + " " + wallName.get(num)));
        }
    }

    class WallpaperLoader extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            wallName.clear();
            wallPreview.clear();
            wallAuthor.clear();
            wallWid.clear();
        }

        @Override
        protected String doInBackground(String... arg) {
            if (arg[0] == null) {
                return null;
            }
            Cursor c = null;
            LocalDBHelper helper = new LocalDBHelper(a);

            SQLiteDatabase database = helper.getWritableDatabase();
            try {
                c = database.rawQuery(SELECT_WALLPAPERS + arg[0], null);
                if (c != null) {
                    if (c.moveToFirst()) {
                        for (int i = 0; i < c.getCount(); i++) {
                            wallPreview.add(c.getString(c.getColumnIndex(arg[1])));
                            wallWid.add(c.getString(c
                                    .getColumnIndex(LocalDBHelper.COLUMN_ID)));
                            wallName.add(c.getString(c
                                    .getColumnIndex(LocalDBHelper.COLUMN_NAME)));
                            wallAuthor.add(c.getString(c
                                    .getColumnIndex(LocalDBHelper.COLUMN_AUTHOR)));
                            c.moveToNext();
                        }
                    }
                }
            } finally {
                c.close();
                database.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String str) {
            updateViews();
        }
    }

}
