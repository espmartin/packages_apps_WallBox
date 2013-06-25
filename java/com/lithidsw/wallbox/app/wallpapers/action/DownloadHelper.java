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

package com.lithidsw.wallbox.app.wallpapers.action;

import java.io.File;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.lithidsw.wallbox.R;
import com.lithidsw.wallbox.app.wallpapers.db.LocalDataSource;
import com.lithidsw.wallbox.utils.C;
import com.lithidsw.wallbox.utils.Utils;

public class DownloadHelper {

	Activity a;
	private LocalDataSource datasource;
	Utils mUtils;
	
	public DownloadHelper(Activity act) {
		a = act;
        mUtils = new Utils(a);
	}

	public void startDownload(String url, String name, String author, int id) {
		datasource = new LocalDataSource(a);

		final String filename = url.substring(url.lastIndexOf('/') + 1, url.length());

		final File cache = mUtils.getExternalDir();
		if (!cache.exists()) {
			cache.mkdirs();
		}

		final String path = cache + "/" + filename;
		File check = new File(path);

		if (!check.exists()) {
			DownloadManager.Request request;
			request = new DownloadManager.Request(Uri.parse(url));
			request.setTitle(name);
			request.setDescription("Downloading...");
			request.setDestinationInExternalPublicDir(a.getString(R.string.app_name),filename);
			request.setVisibleInDownloadsUi(true);
			DownloadManager manager = (DownloadManager) a.getSystemService(Context.DOWNLOAD_SERVICE);
			final long en = manager.enqueue(request);

			datasource.createItem(id, name, author, path, en, 0, C.TAG_WALLPAPERS);

			mUtils.sendToast("Download started");
		} else {
			mUtils.sendToast("File: " + filename + " Exists, not downloading");
		}
	}
}
