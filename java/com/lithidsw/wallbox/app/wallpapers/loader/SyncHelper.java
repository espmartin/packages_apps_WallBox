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

package com.lithidsw.wallbox.app.wallpapers.loader;

import android.content.Context;

import com.lithidsw.wallbox.app.wallpapers.db.ContentDataSource;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncHelper {

    Context mContext;

    public SyncHelper(Context context) {
        mContext = context;
    }

    public boolean getWallpapers() {
        return getAllWallpapers(mContext);
    }

    private boolean getAllWallpapers(Context context) {
        String json = new JsonHelper().getJsonUrl(C.URL_WALL_JSON);
        new ContentDataSource(context).deleteAllTable("wallpapers");

        JSONObject jsonObject;
        JSONArray mArray;
        int success;
        try {
            jsonObject = new JSONObject(json);
            mArray = jsonObject.getJSONArray(C.TAG_WALLPAPERS);
            success = jsonObject.getInt(C.TAG_SUCCESS);
            switch (success) {
                case 1:
                    for (int i = 0; i < mArray.length(); i++) {
                        JSONObject c = mArray.getJSONObject(i);
                        new ContentDataSource(context).putWallItem(
                                "" + c.getInt(C.TAG_WID),
                                c.getString(C.TAG_NAME),
                                c.getString(C.TAG_AUTHOR),
                                c.getString(C.TAG_DESC),
                                c.getString(C.TAG_PREVIEW),
                                c.getString(C.TAG_SIZE_XLARGE),
                                c.getString(C.TAG_SIZE_LARGE),
                                c.getString(C.TAG_SIZE_NORMAL),
                                "" + c.getInt(C.TAG_DATE));
                    }
                    break;
                case 0:
                    return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
