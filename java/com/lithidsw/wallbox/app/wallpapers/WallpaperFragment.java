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

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.adapters.WallpaperAdapter;
import com.lithidsw.wallbox.app.wallpapers.db.LocalDBHelper;
import com.lithidsw.wallbox.app.wallpapers.loader.SyncHelper;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.CustomDialogs;
import com.lithidsw.wallbox.utils.Utils;

public class WallpaperFragment extends Fragment {
	WallpaperAdapter wallAdapter;
    WallpaperAdapter searchAdapter;
	SharedPreferences prefs;

	private WallpaperLoader wallLoader;
    private Utils mUtils;
	private SyncHelper mSyncHelper;

	private FragmentActivity fa;

	GridView wallGrid;
    GridView searchGrid;
    TextView mTextView;

	ArrayList<String[]> aWall = new ArrayList<String[]>();
    ArrayList<String[]> aSearch = new ArrayList<String[]>();

	MenuItem mRefresh;
    MenuItem mSearch;


	String[] sortList;
	String[] sortListdesc;

    private boolean simpleFirstRun = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.wallpaper_frag, container, false);
        if (rl != null) {
            fa = super.getActivity();
            mSyncHelper = new SyncHelper(fa);
            mUtils = new Utils(fa);
            wallGrid = (GridView) rl.findViewById(R.id.grid_view);
            prefs = fa.getSharedPreferences(C.PREF, Context.MODE_PRIVATE);

            sortList = getResources().getStringArray(R.array.wallpaper_sort_entries);
            sortListdesc = getResources().getStringArray(R.array.wallpaper_sort_desc);

            if (!prefs.getBoolean(C.PREF_WALLPAPER_CHECK_SAVE_SORT, false)) {
                prefs.edit().putInt(C.PREF_WALLPAPER_CURRENT_SORT, 0).commit();
            }

            wallGrid = (GridView) rl.findViewById(R.id.grid_view);
            searchGrid = (GridView) rl.findViewById(R.id.search_grid_view);
            mTextView = (TextView) rl.findViewById(R.id.no_content);

            wallAdapter = new WallpaperAdapter(fa, aWall);
            searchAdapter = new WallpaperAdapter(fa, aSearch);

            wallGrid.setAdapter(wallAdapter);
            searchGrid.setAdapter(searchAdapter);
            setHasOptionsMenu(true);

            if (!prefs.getBoolean(C.PREF_WALLPAPER_FIRST_RUN_MAIN, false)) {
                prefs.edit().putBoolean(C.PREF_WALLPAPER_FIRST_RUN_MAIN, true).commit();
                String title = getResources().getString(R.string.main_title_wallpapers);
                String message = getResources().getString(R.string.wallpaper_description);
                new CustomDialogs().openFirstDialog(fa, title, message);
            }
        }
		return rl;
	}

	/**
	 * Stop the async tasks
	 */

	private void stopWallLoader() {
		if (wallLoader != null
				&& wallLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
			wallLoader.cancel(true);
			wallLoader = null;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wallpaper_menu, menu);
        if (menu.size() > 0) {
            mRefresh = menu.findItem(R.id.menu_refresh);
            if (!simpleFirstRun) {
                runReload();
                simpleFirstRun = true;
            }
            mSearch = menu.findItem(R.id.menu_search);
            if (mSearch != null) {
                setupSearch(mSearch);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			runReload();
			return true;
		case R.id.menu_sort:
			// Nothing right now
			return true;
		case R.id.menu_downloads:
			startActivity(new Intent(fa, WallpaperDownloadedActivity.class));
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(fa, WallpaperSettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    private void setupSearch(MenuItem searchItem) {
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                wallGrid.setVisibility(View.GONE);
                searchGrid.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                wallGrid.setVisibility(View.VISIBLE);
                searchGrid.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                return true;
            }
        });
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setQueryHint("Search: ");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    new SearchLoader().execute(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (s.length() < 2) {
                        new SearchLoader().execute("");
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		wallAdapter.notifyDataSetChanged();
	}

	private void runReload() {
		stopWallLoader();
		wallLoader = (WallpaperLoader) new WallpaperLoader().execute("reload");
	}

	private void runRefresh() {
		stopWallLoader();
		wallLoader = (WallpaperLoader) new WallpaperLoader().execute("refresh");
	}

	@Override
	public void onPause() {
		super.onPause();
		stopWallLoader();
	}

    private String[] getWallpaperItem(Cursor c) {
        String url = C.URL_WALL_WALLPAPERS;

        int col_pre = c.getColumnIndex(LocalDBHelper.COLUMN_PREVIEW);
        int col_id = c.getColumnIndex(LocalDBHelper.COLUMN_ID);
        int col_xl = c.getColumnIndex(LocalDBHelper.COLUMN_SIZE_XLARGE);
        int col_l = c.getColumnIndex(LocalDBHelper.COLUMN_SIZE_LARGE);
        int col_n = c.getColumnIndex(LocalDBHelper.COLUMN_SIZE_NORMAL);
        int col_date = c.getColumnIndex(LocalDBHelper.COLUMN_DATE);
        int col_name = c.getColumnIndex(LocalDBHelper.COLUMN_NAME);
        int col_auth = c.getColumnIndex(LocalDBHelper.COLUMN_AUTHOR);

        String preitem = c.getString(col_pre);
        String widitem = c.getString(col_id);
        String xlitem = c.getString(col_xl);
        String litem = c.getString(col_l);
        String nitem = c.getString(col_n);
        String dateitem = c.getString(col_date);
        String nameitem = c.getString(col_name);
        String authitem = c.getString(col_auth);

        String[] item = new String[8];
        item[0] = url+preitem;
        item[1] = widitem;
        item[2] = url+xlitem;
        item[3] = url+litem;
        item[4] = url+nitem;
        item[5] = dateitem;
        item[6] = nameitem;
        item[7] = authitem;

        return item;
    }

    class SearchLoader extends AsyncTask<String, String, Integer> {
        @Override
        protected void onPreExecute() {
            aSearch.clear();
            searchGrid.invalidateViews();
        }

        @Override
        protected Integer doInBackground(String... arg) {
            int count = 0;
            Cursor c = null;
            LocalDBHelper helper = new LocalDBHelper(fa);

            /**
             * The search sqlite command:
             *
             * SELECT * FROM wallpapers WHERE name LIKE '%item%' \
             * OR author LIKE '%item%' OR description LIKE '%item%'
             */

            String raw_cmd = C.SELECT_WALLPAPERS + " WHERE "
                    + LocalDBHelper.COLUMN_D_NAME + " LIKE " + "'%" + arg[0]
                    + "%'" + " OR " + LocalDBHelper.COLUMN_AUTHOR + " LIKE "
                    + "'%" + arg[0] + "%'" + " OR "
                    + LocalDBHelper.COLUMN_DESC + " LIKE " + "'%" + arg[0]
                    + "%';";

            SQLiteDatabase database = helper.getWritableDatabase();
            try {
                if (database != null) {
                    c = database.rawQuery(raw_cmd, null);
                    count = c.getCount();
                    if (c.moveToFirst()) {
                        for (int i = 0; i < c.getCount(); i++) {
                            String[] item = getWallpaperItem(c);
                            aSearch.add(item);
                            //mUtils.checkFiles(fa, wallSizeNormal.get(i), wallWid.get(i));
                            c.moveToNext();
                        }
                    }
                }
            } finally {
                if (c != null) {
                    c.close();
                }

                if (database != null) {
                    database.close();
                }
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            searchAdapter.notifyDataSetChanged();
            if (count > 0) {
                searchGrid.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
            } else {
                searchGrid.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }
        }
    }

	class WallpaperLoader extends AsyncTask<String, String, Integer> {

		@Override
		protected void onPreExecute() {
			mRefresh.setVisible(false);
            fa.setProgressBarIndeterminateVisibility(true);
            aWall.clear();
            wallGrid.invalidateViews();
		}

		@Override
		protected Integer doInBackground(String... arg) {

			Cursor c = null;
            int count = 0;
			if ("reload".equals(arg[0])) {
				mSyncHelper.getWallpapers();
			}

            LocalDBHelper helper = new LocalDBHelper(fa);
			String[] cs = { C.QUERY_LATEST_PAPER, C.QUERY_EARLIEST_PAPER,
					C.QUERY_NAME_AZ_PAPER, C.QUERY_NAME_ZA_PAPER,
					C.QUERY_AUTHOR_AZ_PAPER, C.QUERY_AUTHOR_ZA_PAPER };
			String raw_cmd = cs[mUtils.sortPref(prefs, C.PREF_WALLPAPER_CURRENT_SORT)];

            SQLiteDatabase database = helper.getWritableDatabase();
			try {
                if (database != null) {
                    c = database.rawQuery(raw_cmd, null);
                    count = c.getCount();
                    if (c.moveToFirst()) {
                        for (int i = 0; i < c.getCount(); i++) {
                            String[] item = getWallpaperItem(c);
                            aWall.add(item);
                            //mUtils.checkFiles(fa, wallSizeNormal.get(i), wallWid.get(i));
                            c.moveToNext();
                        }
                    }
                }
			} finally {
                if (c != null) {
                    c.close();
                }

                if (database != null) {
                    database.close();
                }
			}
			return count;
		}

		@Override
		protected void onPostExecute(Integer count) {
			fa.setProgressBarIndeterminateVisibility(false);
			wallAdapter.notifyDataSetChanged();

            if (count > 0) {
                wallGrid.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
            } else {
                wallGrid.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }

            mRefresh.setVisible(true);
		}
	}
}
