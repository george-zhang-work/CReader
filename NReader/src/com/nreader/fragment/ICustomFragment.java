
package com.nreader.fragment;

import android.app.Activity;

public interface ICustomFragment {
    /**
     * Called when the fragment is visible to the user is shown, and actively
     * running. This is generally tied to Activity.onResume of the containing
     * Activity's lifecycle.
     */
    public void onResumeFragment();

    /**
     * Called when the fragment has detected the user's press of the back key.
     * The default implementation simply invoke the current activity's
     * {@link Activity#onBackPressed()} but you can override this to do whatever
     * you want. <b>Remember not cycle-reference.</b>
     */
    public void onBackPressed();
}
