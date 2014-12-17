package com.intangibleobject.pfsensewol;

import android.os.Bundle;

/**
 * Created by czuares on 10/5/14.
 */
public class Model {

    public String userName;
    public String password;
    public String serverAddress;
    public String macAddress;
    public String serverInterface;
    public boolean trustAnySSLCertificate;

    public Model(Bundle bundle) {
        this.userName = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_SERVER_USERNAME);
        this.password = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_SERVER_PASSWORD);
        this.serverAddress = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_SERVER_ADDRESS);
        this.macAddress = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_MAC_ADDRESS);
        this.serverInterface = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_INTERFACE);
        this.trustAnySSLCertificate = bundle.getBoolean(PluginBundleManager.BUNDLE_EXTRA_TRUST_ANY_CERT);
    }

    public Model(String serverAddress, String userName, String password, String macAddress, String serverInterface, Boolean trustAnySSLCertificate) {
        this.userName = userName;
        this.password = password;
        this.serverAddress = serverAddress;
        this.macAddress = macAddress;
        this.serverInterface = serverInterface;
        this.trustAnySSLCertificate = trustAnySSLCertificate;
    }

    public String getBlurb() {
        return String.format("%s", this.serverAddress);
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PluginBundleManager.BUNDLE_EXTRA_SERVER_USERNAME, this.userName);
        bundle.putString(PluginBundleManager.BUNDLE_EXTRA_SERVER_PASSWORD, this.password);
        bundle.putString(PluginBundleManager.BUNDLE_EXTRA_SERVER_ADDRESS, this.serverAddress);
        bundle.putString(PluginBundleManager.BUNDLE_EXTRA_MAC_ADDRESS, this.macAddress);
        bundle.putString(PluginBundleManager.BUNDLE_EXTRA_INTERFACE, this.serverInterface);
        bundle.putBoolean(PluginBundleManager.BUNDLE_EXTRA_TRUST_ANY_CERT,this.trustAnySSLCertificate);
        return bundle;
    }

    public String getLoginPage() {
        return this.serverAddress + "/index.php";
    }

    public String getWolPage() {
        return this.serverAddress + "/services_wol.php";
    }
}
