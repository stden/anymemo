/*
Copyright (C) 2013 Haowen Ning

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/

package org.liberty.android.fantastischmemo.ui.loader;

import java.util.HashMap;
import java.util.Map;

import org.liberty.android.fantastischmemo.AMActivity;
import org.liberty.android.fantastischmemo.ui.LoadingProgressFragment;

import roboguice.util.Ln;

import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public class MultipleLoaderManager {

    private int runningLoaderCount = 0;

    private Handler handler = new Handler();

    private Runnable onAllLoaderCompletedRunnable = null;
    
    private Map<Integer, LoaderCallbacks<?>> loaderCallbackMap
        = new HashMap<Integer, LoaderCallbacks<?>>();

    private Map<Integer, Boolean> loaderReloadOnStartMap
        = new HashMap<Integer, Boolean>();

    public void registerLoaderCallbacks(int id, LoaderCallbacks<?> callbacks, boolean reloadOnStart) {
        loaderCallbackMap.put(id, callbacks);
        loaderReloadOnStartMap.put(id, reloadOnStart);
    }

    public void setOnAllLoaderCompletedRunnable(Runnable onAllLoaderCompletedRunnable) {
        this.onAllLoaderCompletedRunnable = onAllLoaderCompletedRunnable;
    }

    public void startLoading(AMActivity activity) {
        DialogFragment df = new LoadingProgressFragment();
        df.show(activity.getSupportFragmentManager(), LoadingProgressFragment.class.toString());

        LoaderManager.enableDebugLogging(true);
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        for (int id : loaderCallbackMap.keySet()) {
            if (loaderReloadOnStartMap.get(id)) {
                loaderManager.restartLoader(id, null, loaderCallbackMap.get(id));
            } else {
                loaderManager.initLoader(id, null, loaderCallbackMap.get(id));
            }
        }
        runningLoaderCount = loaderCallbackMap.size();
    }

    public synchronized void checkAllLoadersCompleted() {
        Ln.v("Finished loader");
        runningLoaderCount--;
        // The onPostInit is running on UI thread.
        if (runningLoaderCount <= 0 && onAllLoaderCompletedRunnable != null) {
            handler.post(onAllLoaderCompletedRunnable);
        }
    }

    public void destroy() {
        // The handler needs to remove the callbacks to avoid the race condition
        // that onPostInitRunnable is running after onDestroy.
        if (onAllLoaderCompletedRunnable != null) {
            handler.removeCallbacks(onAllLoaderCompletedRunnable);
        }
    }
}
