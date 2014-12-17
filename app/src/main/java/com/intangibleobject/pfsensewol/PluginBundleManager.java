package com.intangibleobject.pfsensewol;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public final class PluginBundleManager {

    public static final String BUNDLE_EXTRA_SERVER_USERNAME = Constants.BundlePrefix + "SERVER_USERNAME";
    public static final String BUNDLE_EXTRA_SERVER_PASSWORD = Constants.BundlePrefix + "SERVER_PASSWORD";
    public static final String BUNDLE_EXTRA_SERVER_ADDRESS = Constants.BundlePrefix + "SERVER_ADDRESS";
    public static final String BUNDLE_EXTRA_INTERFACE = Constants.BundlePrefix + "INTERFACE";
    public static final String BUNDLE_EXTRA_MAC_ADDRESS = Constants.BundlePrefix + "MAC_ADDRESS";
    public static final String BUNDLE_EXTRA_TRUST_ANY_CERT = Constants.BundlePrefix + "TRUST_ANY_CERT";
    public static final String BUNDLE_EXTRA_INT_VERSION_CODE =
            Constants.BundlePrefix + "INT_VERSION_CODE";

    public static String[] Extras = new String[]
            {
                    BUNDLE_EXTRA_INT_VERSION_CODE, BUNDLE_EXTRA_INTERFACE, BUNDLE_EXTRA_MAC_ADDRESS,
                    BUNDLE_EXTRA_SERVER_ADDRESS, BUNDLE_EXTRA_SERVER_PASSWORD, BUNDLE_EXTRA_SERVER_USERNAME,
                    BUNDLE_EXTRA_TRUST_ANY_CERT
            };

    private static final String TAG = PluginBundleManager.class.getSimpleName();
    ;

    private PluginBundleManager() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }

    public static boolean isBundleValid(final Bundle bundle) {
        if (null == bundle) {
            return false;
        }

        for (String extra : Extras) {
            if (!bundle.containsKey(extra)) {
                Log.e(TAG,
                        String.format("bundle must contain extra %s", extra));

                return false;
            }
        }

        if (Extras.length != bundle.keySet().size()) {

            Log.e(TAG,
                    String.format("bundle must contain %s keys, but currently contains %d keys: %s",
                            Extras.length, bundle.keySet().size(), bundle.keySet())); //$NON-NLS-1$

            return false;
        }

        if (bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 0) != bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 1)) {
            Log.e(TAG,
                    String.format("bundle extra %s appears to be the wrong type.  It must be an int", BUNDLE_EXTRA_INT_VERSION_CODE)); //$NON-NLS-1$

            return false;
        }

        return true;
    }

    public static Bundle generateBundle(final Context context, final Model model) {
        final Bundle result = model.getBundle();
        result.putInt(BUNDLE_EXTRA_INT_VERSION_CODE, Constants.getVersionCode(context));
        return result;
    }
}
