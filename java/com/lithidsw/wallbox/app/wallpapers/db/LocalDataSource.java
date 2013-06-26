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

import java.io.File;

public class LocalDataSource {

    private SQLiteDatabase database;
    private LocalDBHelper dbHelper;
    private String TABLE = LocalDBHelper.TABLE_DOWNLOADED;

    public LocalDataSource(Context context) {
        dbHelper = new LocalDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean createItem(Integer num, String name, String author,
                              String path, long queue, int complete, String type) {
        open();
        ContentValues values = new ContentValues();
        values.put(LocalDBHelper.COLUMN_D_ID, num);
        values.put(LocalDBHelper.COLUMN_D_NAME, name);
        values.put(LocalDBHelper.COLUMN_D_AUTHOR, author);
        values.put(LocalDBHelper.COLUMN_D_PATH, path);
        values.put(LocalDBHelper.COLUMN_D_QUEUE, queue);
        values.put(LocalDBHelper.COLUMN_D_COMPLETE, complete);
        values.put(LocalDBHelper.COLUMN_D_TYPE, type);
        database.insert(TABLE, null, values);
        close();
        return false;
    }

    public void deleteItem(long id) {
        open();
        database.delete(TABLE, LocalDBHelper.COLUMN_D_ID + " = " + id, null);
        close();
    }

    public boolean isSingleItem(String name, String type) {
        boolean exist = false;
        open();
        Cursor all;
        all = database.rawQuery("select * from " + TABLE + " where "
                + LocalDBHelper.COLUMN_D_NAME + " = '" + name + "' and "
                + LocalDBHelper.COLUMN_D_TYPE + " = '" + type + "'", null);
        exist = all.moveToFirst();
        close();
        return exist;
    }

    public boolean isQueueItem(String queue) {
        Cursor all;
        all = database.rawQuery("select * from " + TABLE + " where "
                + LocalDBHelper.COLUMN_D_QUEUE + " = '" + queue + "'", null);
        return all.moveToFirst();
    }

    public String[] getItemsFromQueueId(String queue) {
        String[] item = new String[3];
        String itemId = null;
        String itemName = null;
        String itemUri = null;
        open();
        Cursor cur = database.rawQuery("select * from " + TABLE + " where "
                + LocalDBHelper.COLUMN_D_QUEUE + " = '" + queue + "'", null);
        if (cur.moveToFirst()) {
            itemId = cur.getString(cur
                    .getColumnIndex(LocalDBHelper.COLUMN_D_ID));
            itemName = cur.getString(cur
                    .getColumnIndex(LocalDBHelper.COLUMN_D_NAME));
            itemUri = cur.getString(cur
                    .getColumnIndex(LocalDBHelper.COLUMN_D_PATH));
        }
        close();
        item[0] = itemId;
        item[1] = itemName;
        item[2] = itemUri;
        return item;
    }

    public String[] getItemsFromId(int id) {
        String[] item = new String[2];
        String itemName = null;
        String itemUri = null;
        open();
        Cursor cur = database.rawQuery("select * from " + TABLE + " where "
                + LocalDBHelper.COLUMN_D_ID + " = '" + id + "'", null);
        if (cur.moveToFirst()) {
            itemName = cur.getString(cur
                    .getColumnIndex(LocalDBHelper.COLUMN_D_NAME));
            itemUri = cur.getString(cur
                    .getColumnIndex(LocalDBHelper.COLUMN_D_PATH));
        }
        close();
        item[0] = itemName;
        item[1] = itemUri;
        return item;
    }

    public String getLocalPath(String name, String id) {
        String path[] = null;
        try {
            open();
            path = getItemsFromId(Integer.parseInt(id));
        } finally {
            close();
        }
        return path[1];
    }

    public void checkItem(String path, String id) {
        File f = new File(path);
        if (!f.exists()) {
            open();
            deleteItem(Long.parseLong(id));
            close();
        }
    }

    public void cleanTable() {
        open();
        Cursor cur;
        cur = database.rawQuery("select * from " + TABLE, null);
        while (cur.moveToNext()) {
            File filePath = new File(cur.getString(cur
                    .getColumnIndex(LocalDBHelper.COLUMN_D_PATH)));
            if (!filePath.exists()) {
                deleteItem(Integer.parseInt(cur.getString(cur
                        .getColumnIndex(LocalDBHelper.COLUMN_D_ID))));
            }
        }
        cur.close();
        close();
    }

}
