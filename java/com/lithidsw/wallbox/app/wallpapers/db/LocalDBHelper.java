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

package com.lithidsw.wallbox.app.wallpapers.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lithidsw.wallbox.utils.C;

public class LocalDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wallpapers.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Downloaded databases for when papers are downloaded *
     */
    public static final String TABLE_DOWNLOADED = C.TAG_DOWNLOADED;
    public static final String COLUMN_D_ID = C.TAG_WID;
    public static final String COLUMN_D_NAME = C.TAG_NAME;
    public static final String COLUMN_D_AUTHOR = C.TAG_AUTHOR;
    public static final String COLUMN_D_PATH = C.TAG_PATH;
    public static final String COLUMN_D_QUEUE = C.TAG_QUEUE;
    public static final String COLUMN_D_COMPLETE = C.TAG_COMPLETE;
    public static final String COLUMN_D_TYPE = C.TAG_TYPE;

    private static final String DATABASE_CREATE_DOWNLOADED = "create table "
            + TABLE_DOWNLOADED + "(" + COLUMN_D_ID + " integer not null, "
            + COLUMN_D_NAME + " text not null, " + COLUMN_D_AUTHOR
            + " text not null, " + COLUMN_D_PATH + " text not null, "
            + COLUMN_D_QUEUE + " long not null, " + COLUMN_D_COMPLETE
            + " integer not null, " + COLUMN_D_TYPE + " text not null);";

    /**
     * Wallpapers database to populate from the public db server *
     */
    public static final String TABLE_WALLPAPERS = C.TAG_WALLPAPERS;
    public static final String COLUMN_ID = C.TAG_WID;
    public static final String COLUMN_NAME = C.TAG_NAME;
    public static final String COLUMN_AUTHOR = C.TAG_AUTHOR;
    public static final String COLUMN_DESC = C.TAG_DESC;
    public static final String COLUMN_PREVIEW = C.TAG_PREVIEW;
    public static final String COLUMN_SIZE_XLARGE = C.TAG_SIZE_XLARGE;
    public static final String COLUMN_SIZE_LARGE = C.TAG_SIZE_LARGE;
    public static final String COLUMN_SIZE_NORMAL = C.TAG_SIZE_NORMAL;
    public static final String COLUMN_DATE = C.TAG_DATE;

    private static final String DATABASE_CREATE_WALLPAPERS = "create table "
            + TABLE_WALLPAPERS + "(" + COLUMN_ID + " integer not null, "
            + COLUMN_NAME + " text not null, " + COLUMN_AUTHOR
            + " text not null, " + COLUMN_DESC + " text not null, "
            + COLUMN_PREVIEW + " text not null, " + COLUMN_SIZE_XLARGE
            + " text not null, " + COLUMN_SIZE_LARGE + " text not null, "
            + COLUMN_SIZE_NORMAL + " text not null, " + COLUMN_DATE
            + " text not null);";


    public LocalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_DOWNLOADED);
        database.execSQL(DATABASE_CREATE_WALLPAPERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLPAPERS);
        onCreate(db);
    }

}
