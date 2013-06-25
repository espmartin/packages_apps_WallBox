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

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.WallpaperFragment;
import com.lithidsw.wallbox.app.wallpapers.db.LocalDataSource;
import com.lithidsw.wallbox.utils.Utils;

public class DownloadReceiver extends BroadcastReceiver {

	private static final String EXTRA_DOWNLOAD_ID = DownloadManager.EXTRA_DOWNLOAD_ID;
	private static final String COLUMN_STATUS = DownloadManager.COLUMN_STATUS;
	private static final int STATUS_SUCCESSFUL = DownloadManager.STATUS_SUCCESSFUL;

	private DownloadManager dm = null;
	private LocalDataSource mDataSource;
	long downloadId;

	private DownloadManager.Query query = null;
	private Cursor cursor = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			downloadId = intent.getLongExtra(EXTRA_DOWNLOAD_ID, 0);
			try {
				mDataSource = new LocalDataSource(context);
				mDataSource.open();
				if (mDataSource.isQueueItem(Long.toString(downloadId))) {
					query = new DownloadManager.Query();
					query.setFilterById(downloadId);
					cursor = dm.query(query);
					if (cursor.moveToFirst()) {
						int columnIndex = cursor.getColumnIndex(COLUMN_STATUS);
						if (STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
							String[] items = mDataSource
									.getItemsFromQueueId(Long
											.toString(downloadId));
							if (items[0] != null && items[1] != null && items[2] != null) {
								showNoti(context, Integer.parseInt(items[0]), items[1], items[2]);
							} else {
								errorToast(context,
										R.string.toast_notification_error);
							}
						}
					}
				} else {
					errorToast(context, R.string.toast_download_unsuccessful);
				}
			} finally {
				cursor.close();
				mDataSource.close();
			}
		}
	}

	private void showNoti(Context c, int id, String name, String uri) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(name)
				.setContentText(c.getString(R.string.app_name))
				.addAction(R.drawable.ic_action_picture,
						c.getString(R.string.apply),
						getPendingIntent(c, name, uri, id));

		NotificationCompat.BigPictureStyle bigStyle = new NotificationCompat.BigPictureStyle()
				.bigPicture(BitmapFactory.decodeFile(uri));
		mBuilder.setAutoCancel(true);
		mBuilder.setStyle(bigStyle);

		Intent intent = new Intent(c, WallpaperFragment.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(c, 0, intent, 0);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) 
				c.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, mBuilder.build());
	}

	private void errorToast(Context context, int i) {
		Toast.makeText(context, context.getString(i), Toast.LENGTH_LONG).show();
	}

	public PendingIntent getPendingIntent(Context c, String name, String path, int id) {
		Intent intent = new Utils(c).getWallpaperApplyIntent(name, path, id);
		return PendingIntent.getActivity(c, id, intent, 0);
	}
}
