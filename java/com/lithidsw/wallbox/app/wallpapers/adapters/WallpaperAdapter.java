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

package com.lithidsw.wallbox.app.wallpapers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.action.DownloadHelper;
import com.lithidsw.wallbox.app.wallpapers.db.ContentDataSource;
import com.lithidsw.wallbox.app.wallpapers.db.LocalDataSource;
import com.lithidsw.wallbox.loader.ImageLoader;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WallpaperAdapter extends BaseAdapter {

	/*
	 * This adapter handles the wallpaper views and search views
	 */

    private Activity activity;
    private List<String[]> mWallpapers = new ArrayList<String[]>();
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private DownloadHelper mDownload;
    private LocalDataSource downloadDataSource;
    private ContentDataSource wallpaperDataSource;
    Utils mUtils;
    View vi;

    public WallpaperAdapter(Activity a, List<String[]> b) {
        activity = a;
        mWallpapers = b;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext(), 500);
        downloadDataSource = new LocalDataSource(activity);
        mDownload = new DownloadHelper(activity);
        wallpaperDataSource = new ContentDataSource(activity);
        mUtils = new Utils(activity);
    }

    @Override
    public int getCount() {
        try {
            return mWallpapers.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.wallpaper_item, null);
        }

        final String FinalPreview = mWallpapers.get(position)[0];
        final String FinalWid = mWallpapers.get(position)[1];
        final String FinalTxtLarge = mWallpapers.get(position)[2];
        final String FinalTxtNormal = mWallpapers.get(position)[3];
        final String FinalTxtSmall = mWallpapers.get(position)[4];
        final String FinalTxtCreated = mWallpapers.get(position)[5];
        final String FinalName = mWallpapers.get(position)[6];
        final String FinalAuthor = mWallpapers.get(position)[7];

        final String url = mUtils.getScreenSize(FinalTxtSmall, FinalTxtNormal, FinalTxtLarge);

        final ImageView imgpreview = (ImageView) vi.findViewById(R.id.preview);
        imageLoader.DisplayImage(FinalPreview, imgpreview);

       final TextView txtname = (TextView) vi.findViewById(R.id.name);
        txtname.setText(FinalName);

        final TextView txtauthor = (TextView) vi.findViewById(R.id.author);
        txtauthor.setText(FinalAuthor);

        final LinearLayout over_view = (LinearLayout) vi.findViewById(R.id.over_view);

        imgpreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDownload.startDownload(url, FinalName, FinalAuthor, Integer.parseInt(FinalWid));
                resetDownloadData(FinalName, over_view);
            }
        });

        over_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = mUtils.getWallpaperApplyIntent(FinalName, downloadDataSource.getLocalPath(FinalName, FinalWid), 0);
                activity.startActivity(intent);
            }
        });

        resetDownloadData(FinalName, over_view);
        return vi;
    }

    private void resetDownloadData(String name, LinearLayout view) {
        boolean is = downloadDataSource.isSingleItem(name, C.TAG_WALLPAPERS);
        view.setVisibility(is ? View.VISIBLE : View.GONE);
    }
}
