
package com.nreader;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class Home extends FragmentActivity {

    private DrawerLayout mDrawerLayout;

    @SuppressWarnings("unused")
    private FrameLayout mDrawer;
    @SuppressWarnings("unused")
    private FrameLayout mContent;

    private ActionBarHelper mAbHelper;
    private ActionBarDrawerToggle mAbDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (FrameLayout) findViewById(R.id.drawer_flayout);
        mContent = (FrameLayout) findViewById(R.id.content_flayout);

        mDrawerLayout.setDrawerListener(new DrawerListener());
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mAbHelper = createActionBarHelper();
        mAbHelper.init();

        mAbDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mAbDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * The action bar home/up action should open or close the drawer.
         * mAbDrawerToggle will take care of this.
         */
        if (mAbDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mAbDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * A drawer listener can be used to respond to drawer events such as
     * becoming fully opened or closed. You should always prefer to perform
     * expensive operations such as drastic relayout when no animation is
     * currently in progress, either before or after the drawer animates.
     * 
     * When using ActionBarDrawerToggle, all DrawerLayout listener methods
     * should be forwarded if the ActionBarDrawerToggle is not used as the
     * DrawerLayout listener directly.
     */
    private class DrawerListener implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerOpened(View drawerView) {
            mAbDrawerToggle.onDrawerOpened(drawerView);
            mAbHelper.onDrawerOpened();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mAbDrawerToggle.onDrawerClosed(drawerView);
            mAbHelper.onDrawerClosed();
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mAbDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mAbDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    /**
     * Create a compatible helper that will manipulate the action bar if
     * available.
     */
    private ActionBarHelper createActionBarHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new ActionBarHelperICS();
        } else {
            return new ActionBarHelper();
        }
    }

    /**
     * Stub action bar helper; this does nothing.
     */
    private class ActionBarHelper {
        public void init() {
        }

        public void onDrawerClosed() {
        }

        public void onDrawerOpened() {
        }

        @SuppressWarnings("unused")
        public void setTitle(CharSequence title) {
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private class ActionBarHelperICS extends ActionBarHelper {
        private final ActionBar mActionBar;
        private CharSequence mDrawerTitle;
        private CharSequence mTitle;

        ActionBarHelperICS() {
            mActionBar = getActionBar();
        }

        @Override
        public void init() {
            super.init();
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mDrawerTitle = mTitle = getTitle();
        }

        /**
         * When the drawer is closed we restore the action bar state reflecting
         * the specific contents in view.
         */
        @Override
        public void onDrawerClosed() {
            super.onDrawerClosed();
            mActionBar.setTitle(mTitle);
        }

        /**
         * When the drawer is open we set the action bar to a generic title. The
         * action bar should only contain data relevant at the top level of the
         * nav hierarchy represented by the drawer, as the rest of your content
         * will be dimmed down and non-interactive.
         */
        @Override
        public void onDrawerOpened() {
            super.onDrawerOpened();
            mActionBar.setTitle(mDrawerTitle);
        }

        @Override
        public void setTitle(CharSequence title) {
            mTitle = title;
        }
    }
}
