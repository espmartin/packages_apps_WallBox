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

package com.lithidsw.wallbox.utils;

public class C {
    public static final String TAG = "MrPaperMaker";

    /* Saturate static strings */
    public static final int ALARM_ID = 5697;
    public static final String CACHEIMG = "image-cached";
    public static final String PREF = "com.lithidsw.mrpapermaker_preferences";
    public static final String PREF_SATURATE_START = "pref_saturate_start";
    public static final String PREF_SATURATE_FIRST_RUN_MAIN = "pref_saturate_first_run_main";

    /* Randomizer static strings */
    public static final String PREF_RANDOMIZER_FIRST_RUN_MAIN = "pref_randomizer_first_run_main";
    public static final String PREF_RANDOMIZER_INTERVAL = "pref_randomizer_interval";
    public static final String PREF_LAST_RANDOMIZER_MD5 = "pref_randomizer_last_md5";

    /* ColorWall static strings */
    public static final String PREF_COLORWALL_FIRST_RUN_MAIN = "pref_colorwall_first_run_main";

    /* WallSnap static strings */
    public static final String PREF_WALLSNAP_FIRST_RUN_MAIN = "pref_wallsnap_first_run_main";

    /* Wallpaper static strings */
    public static final String URL_WALL_JSON = "https://raw.github.com/lithid/WallBoxWallpapers/master/mrwallpapers.json";
    public static final String URL_WALL = "https://raw.github.com/lithid/WallBoxWallpapers/master/";
    public static final String PREF_WALLPAPER_FIRST_RUN_MAIN = "pref_wallpaper_first_run_main";
    public static final String PREF_WALLPAPER_CHECK_INTERVAL = "key_wallpaper_check_interval";
    public static final String PREF_WALLPAPER_CURRENT_SORT = "key_wallpaper_current_sort_wall";
    public static final String PREF_WALLPAPER_CHECK_BOOT = "key_wallpaper_check_boot";
    public static final String PREF_WALLPAPER_CHECK_SAVE_SORT = "key_wallpaper_check_save_sort";
    public static final String PREF_WALLPAPER_CLEAR_FAVORITES = "key_wallpaper_clear_favorites";
    public static final String PREF_WALLPAPER_CLEAR_DOWNLOADED = "key_wallpaper_clear_downloaded";
    public static final String PREF_WALLPAPER_INFO_SCREEN = "key_wallpaper_info_screen";
    public static final String EXTRA_URI_PATH = "extra_uri_path";
    public static final String EXTRA_URI_NAME = "extra_uri_name";
    public static final String EXTRA_ID = "extra_id";

    public static final String TAG_DOWNLOADED = "downloaded";
    public static final String TAG_WALLPAPERS = "wallpapers";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_COMPLETE = "complete";
    public static final String TAG_TYPE = "type";
    public static final String TAG_PREVIEW = "size_preview";
    public static final String TAG_WID = "id";
    public static final String TAG_SIZE_XLARGE = "size_xlarge";
    public static final String TAG_SIZE_LARGE = "size_large";
    public static final String TAG_SIZE_NORMAL = "size_normal";
    public static final String TAG_DATE = "date";
    public static final String TAG_NAME = "name";
    public static final String TAG_DESC = "description";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_PATH = "path";
    public static final String TAG_QUEUE = "queue";

    public static final String TABLE_WALLPAPERS = TAG_WALLPAPERS;
    public static final String COLUMN_ID = " ORDER BY " + TAG_WID;
    public static final String COLUMN_NAME = " ORDER BY " + TAG_NAME;
    public static final String COLUMN_AUTHOR = " ORDER BY " + TAG_AUTHOR;
    public static final String URL_WALL_WALLPAPERS = URL_WALL;
    public static final String SELECT_WALLPAPERS = "SELECT * FROM " + TABLE_WALLPAPERS;
    public static final String QUERY_LATEST_PAPER = SELECT_WALLPAPERS + COLUMN_ID + " DESC";
    public static final String QUERY_EARLIEST_PAPER = SELECT_WALLPAPERS + COLUMN_ID + " ASC";
    public static final String QUERY_NAME_AZ_PAPER = SELECT_WALLPAPERS + COLUMN_NAME + " COLLATE NOCASE ASC";
    public static final String QUERY_NAME_ZA_PAPER = SELECT_WALLPAPERS + COLUMN_NAME + " COLLATE NOCASE DESC";
    public static final String QUERY_AUTHOR_AZ_PAPER = SELECT_WALLPAPERS + COLUMN_AUTHOR + " COLLATE NOCASE ASC";
    public static final String QUERY_AUTHOR_ZA_PAPER = SELECT_WALLPAPERS + COLUMN_AUTHOR + " COLLATE NOCASE DESC";

    /* About */
    public static final String URL_CONTRIB = "https://raw.github.com/lithid/WallBox/master/contributers.htm";
    public static final String URL_CHANGELOG = "https://raw.github.com/lithid/WallBox/master/changelog.htm";
}
