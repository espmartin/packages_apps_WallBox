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

package com.lithidsw.wallbox.db;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lithidsw.wallbox.utils.C;

import java.util.ArrayList;
import java.util.Random;

public class TableHelper {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    final private static String TABLE = DBHelper.TABLE_CURRENT_PAPERS;
    SharedPreferences prefs;
    Context mContext;

    private static final Random rgenerator = new Random();

    public TableHelper(Context context) {
        dbHelper = new DBHelper(context);
        mContext = context;
        prefs = context.getSharedPreferences(C.PREF, Context.MODE_PRIVATE);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void writePaperItem(String path, String md5) {
        ContentValues value = new ContentValues();
        value.put(DBHelper.C_PATH, path);
        value.put(DBHelper.C_MD5, md5);
        open();
        database.insert(TABLE, null, value);
        close();
    }

    public ArrayList<String[]> getAllPapers() {
        ArrayList<String[]> list = new ArrayList<String[]>();
        open();
        Cursor c;
        c = database.query(TABLE, new String[]{"*"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String[] items = new String[2];
                items[0] = c.getString(c
                        .getColumnIndex(DBHelper.C_PATH));
                items[1] = c.getString(c
                        .getColumnIndex(DBHelper.C_MD5));
                list.add(items);
                c.moveToNext();
            }
        }
        close();
        return list;
    }

    public String[] getRandPaper() {
        WallpaperManager wm = WallpaperManager.getInstance(mContext);
        ArrayList<String[]> list = new ArrayList<String[]>();
        open();
        Cursor c;
        c = database.query(TABLE, new String[]{"*"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String[] items = new String[2];
                items[0] = c.getString(c
                        .getColumnIndex(DBHelper.C_PATH));
                items[1] = c.getString(c
                        .getColumnIndex(DBHelper.C_MD5));
                list.add(items);
                c.moveToNext();
            }
        }
        close();

        String[] path = new String[2];
        int m_size = list.size();

        while (true) {
            if (m_size < 1) {
                break;
            }
            int rand = rgenerator.nextInt(m_size);

            if (!prefs.getString(C.PREF_LAST_RANDOMIZER_MD5, "").equals(list.get(rand)[1])) {
                path[0] = list.get(rand)[0];
                path[1] = list.get(rand)[1];
                break;
            }
        }

        return path;
    }

    public void deleteAllPapers() {
        open();
        database.execSQL(DBHelper.DROP_TABLE + TABLE);
        close();
    }

    public void deleteSinglePaper(String md5) {
        open();
        database.delete(TABLE, DBHelper.C_MD5 + " = '" + md5 + "'", null);
        close();
    }

    public boolean isExist(String md5) {
        boolean is = false;
        open();
        Cursor c;
        c = database.query(TABLE, new String[]{"*"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String dbid = c.getString(c.getColumnIndex(DBHelper.C_MD5));
                if (dbid.equalsIgnoreCase(md5)) {
                    is = true;
                    break;
                }
                c.moveToNext();
            }
        }
        close();
        return is;
    }
}
