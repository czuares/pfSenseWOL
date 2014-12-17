package com.intangibleobject.pfsensewol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Locale;

public class FireReceiver extends BroadcastReceiver {
    private static final String TAG= FireReceiver.class.getSimpleName();
    public FireReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!LocaleIntent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
            Log.e(TAG,
                    String.format(Locale.US, "Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
            return;
        }

        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(LocaleIntent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        if (PluginBundleManager.isBundleValid(bundle))
        {
           WakeOnLanService.startAction(context, bundle);
        }
    }
}
