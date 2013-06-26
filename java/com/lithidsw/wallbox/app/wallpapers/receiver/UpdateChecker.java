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

package com.lithidsw.wallbox.app.wallpapers.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.WallpaperFragment;
import com.lithidsw.wallbox.app.wallpapers.db.ContentDataSource;
import com.lithidsw.wallbox.app.wallpapers.loader.SyncHelper;

public class UpdateChecker {

    private int before;
    private int after;

    Context c;
    private SyncHelper mSyncHelper;

    public UpdateChecker(Context context) {
        c = context;
        mSyncHelper = new SyncHelper(c);
    }

    public void runUpdateCheck() {
        new checkUpdates().execute();
    }

    class checkUpdates extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {

            before = 0;
            after = 0;
            before = new ContentDataSource(c).getWallCount();
            boolean checked = mSyncHelper.getWallpapers();
            if (checked) {
                after = new ContentDataSource(c).getWallCount();
            }

            if (after > before) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                showUpdateNoti();
            }
        }

    }

    public void showUpdateNoti() {
        int math = (after - before);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(c.getString(R.string.app_name))
                .setContentText(math + " new wallpapers available!");
        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(c, WallpaperFragment.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(c, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager nm = (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, mBuilder.build());
    }
}
