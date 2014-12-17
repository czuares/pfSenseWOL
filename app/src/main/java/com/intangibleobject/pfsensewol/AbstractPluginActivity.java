package com.intangibleobject.pfsensewol;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public abstract class AbstractPluginActivity extends Activity
{
private static final String TAG = AbstractPluginActivity.class.getSimpleName();
    private boolean mIsCancelled = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        try
        {
            getActionBar().setIcon(getPackageManager().getApplicationIcon(getCallingPackage()));
        }
        catch (final PackageManager.NameNotFoundException e)
        {
            Log.w(TAG, "An error occurred loading the host's icon", e);
        }

        return true;
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item)
    {
        final int id = item.getItemId();

        if (android.R.id.home == id)
        {
            finish();
            return true;
        }
        else if (R.id.twofortyfouram_locale_menu_dontsave == id)
        {
            mIsCancelled = true;
            finish();
            return true;
        }
        else if (R.id.twofortyfouram_locale_menu_save == id)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean isCanceled()
    {
        return mIsCancelled;
    }
}