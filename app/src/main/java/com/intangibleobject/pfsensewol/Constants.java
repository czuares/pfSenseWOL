package com.intangibleobject.pfsensewol;


import android.content.Context;

public class Constants {
    public static String PackageName = "com.intangibleobject.pfsensewol";

    public static final String ExtraPrefix = ".extra.";
    public static final String BundlePrefix = PackageName + ExtraPrefix;

    private Constants()
    {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
    public static int getVersionCode(final Context context)
    {
        try
        {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }
        catch (final UnsupportedOperationException e)
        {
            return 1;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
