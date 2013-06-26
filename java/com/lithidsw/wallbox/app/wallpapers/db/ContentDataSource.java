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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContentDataSource {

    // Database fields
    private SQLiteDatabase database;
    private LocalDBHelper dbHelper;
    private static final String TABLE_WALLPAPERS = LocalDBHelper.TABLE_WALLPAPERS;
    private static final String TABLE_DOWNLOADED = LocalDBHelper.TABLE_DOWNLOADED;

    public ContentDataSource(Context context) {
        dbHelper = new LocalDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void putWallItem(String id, String name, String author,
                            String desc, String preview, String large, String normal,
                            String small, String created) {

        System.out.println("Id: " + id + " Name: " + name);
        open();
        ContentValues values = new ContentValues();
        values.put(LocalDBHelper.COLUMN_ID, id);
        values.put(LocalDBHelper.COLUMN_NAME, name);
        values.put(LocalDBHelper.COLUMN_AUTHOR, author);
        values.put(LocalDBHelper.COLUMN_DESC, desc);
        values.put(LocalDBHelper.COLUMN_PREVIEW, preview);
        values.put(LocalDBHelper.COLUMN_SIZE_XLARGE, large);
        values.put(LocalDBHelper.COLUMN_SIZE_LARGE, normal);
        values.put(LocalDBHelper.COLUMN_SIZE_NORMAL, small);
        values.put(LocalDBHelper.COLUMN_DATE, created);
        database.insert(TABLE_WALLPAPERS, null, values);
        close();
    }

    public int getWallCount() {
        int r = 0;
        open();
        Cursor c = database.rawQuery("select count(*) from wallpapers", null);
        if (c.moveToFirst()) {
            r = c.getInt(0);
        }
        close();
        return r;
    }

    public void deleteAllTable(String type) {
        open();
        String THIS_TABLE = null;
        if (type.equals("downloaded")) {
            THIS_TABLE = TABLE_DOWNLOADED;
        }
        if (type.equals("wallpapers")) {
            THIS_TABLE = TABLE_WALLPAPERS;
        }
        database.delete(THIS_TABLE, null, null);
        close();
    }
}
