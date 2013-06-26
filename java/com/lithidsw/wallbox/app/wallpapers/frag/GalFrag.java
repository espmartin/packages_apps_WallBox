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

package com.lithidsw.wallbox.app.wallpapers.frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.db.LocalDataSource;
import com.lithidsw.wallbox.loader.ImageLoader;
import com.lithidsw.wallbox.utils.Utils;

public class GalFrag extends Fragment {

    private LocalDataSource downloadDataSource;
    private FragmentActivity fa;
    Utils mUtils;

    ImageView image;

    private String url;
    private String author;
    private String name;
    private String title;
    private String preview;

    private int id;
    private int current;
    private int total;
    private int color;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fa = super.getActivity();
        mUtils = new Utils(fa);
        downloadDataSource = new LocalDataSource(fa);

        ImageLoader imageLoader = new ImageLoader(fa.getApplicationContext(), 325);

        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.wallpaper_gallery_image, container, false);

        if (rl != null) {
            image = (ImageView) rl.findViewById(R.id.frag_image);
        }

        author = getArguments().getString("author", null);
        name = getArguments().getString("name", null);
        title = getArguments().getString("title", null);
        preview = getArguments().getString("preview", null);
        id = Integer.parseInt(getArguments().getString("id", null));
        current = getArguments().getInt("current", 0);
        total = getArguments().getInt("total", 0);

        imageLoader.DisplayImage(preview, image);
        color = getResources().getColor(R.color.dark_light);

        setHasOptionsMenu(true);
        return rl;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        fa.setTitle(Html.fromHtml(title + " <b><font color='" + color + "'>" + (current + 1)
                + "</font>" + "/<font color='" + color + "'>" + total + "</font></b>"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_apply:
                Intent intent = mUtils.getWallpaperApplyIntent(name, downloadDataSource.getLocalPath(name, "" + id), 0);
                fa.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
