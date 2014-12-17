package com.intangibleobject.pfsensewol;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;


public class EditActivity extends AbstractPluginActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(LocaleIntent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        setContentView(R.layout.activity_edit);

        if (null == savedInstanceState) {
            if (PluginBundleManager.isBundleValid(localeBundle)) {
                final Model model = new Model(localeBundle);

                ((EditText) findViewById(android.R.id.text1)).setText(model.serverAddress);
                ((EditText) findViewById(android.R.id.text2)).setText(model.userName);
                ((EditText) findViewById(R.id.editText1)).setText(model.password);
                ((EditText) findViewById(R.id.editText2)).setText(model.macAddress);
                ((EditText) findViewById(R.id.editText3)).setText(model.serverInterface);
                ((CheckBox) findViewById(android.R.id.checkbox)).setChecked(model.trustAnySSLCertificate);
            }
        }
    }

    @Override
    public void finish() {
        if (!isCanceled()) {

            final String serverAddress = ((EditText) findViewById(android.R.id.text1)).getText().toString();
            final String username = ((EditText) findViewById(android.R.id.text2)).getText().toString();
            final String password = ((EditText) findViewById(R.id.editText1)).getText().toString();
            final String macAddress = ((EditText) findViewById(R.id.editText2)).getText().toString();
            final String serverInterface = ((EditText) findViewById(R.id.editText3)).getText().toString();
            final boolean trustAnyCert = ((CheckBox) findViewById(android.R.id.checkbox)).isChecked();

            Model model = new Model(serverAddress, username, password, macAddress, serverInterface, trustAnyCert);

            final Intent resultIntent = new Intent();

            final Bundle resultBundle =
                    PluginBundleManager.generateBundle(getApplicationContext(), model);
            resultIntent.putExtra(LocaleIntent.EXTRA_BUNDLE, resultBundle);

            final String blurb = model.getBlurb();
            resultIntent.putExtra(LocaleIntent.EXTRA_STRING_BLURB, blurb);

            setResult(RESULT_OK, resultIntent);
        }

        super.finish();
    }
}
