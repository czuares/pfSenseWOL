package com.intangibleobject.pfsensewol;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WakeOnLanService extends IntentService {

    private static final String TAG = WakeOnLanService.class.getSimpleName();
    private static final String ACTION_WOL = Constants.PackageName + ".action.WOL";

    private DefaultHttpClient mHttpClient;

    public WakeOnLanService() {
        super(TAG);
    }

    public static void startAction(Context context, Bundle modelBundle) {
        Intent intent = new Intent(context, WakeOnLanService.class);
        intent.setAction(ACTION_WOL);
        intent.putExtra(LocaleIntent.EXTRA_BUNDLE, modelBundle);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WOL.equals(action)) {
                final Bundle bundle = intent.getBundleExtra(LocaleIntent.EXTRA_BUNDLE);
                handleAction(bundle);
            }
        }
    }

    private void handleAction(Bundle modelBundle) {
        Model model = new Model(modelBundle);
        if (model.trustAnySSLCertificate) {
            mHttpClient = getUnsafeHttpClient(); //initHttpClient();
        }else{
            mHttpClient = new DefaultHttpClient();
        }
        try {
            String CSRFToken = getCSRFToken(model);
            if (TextUtils.isEmpty(CSRFToken)) {
                Log.e(TAG, "Unable to get CSRF token");
                return;
            }
            boolean authenticated = authenticate(model, CSRFToken);
            if (!authenticated) {
                Log.e(TAG, "Unable to authenticate!");
                return;
            }
            Log.i(TAG, "Authenticated! Sending WOL");
            boolean success = sendWolRequest(model);
            if (!success) {
                Log.e(TAG, "Failure sending WOL");
                return;
            }
            Log.i(TAG, "WOL sent successfully");
            //TODO: post message
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (mHttpClient != null) {
                mHttpClient.getConnectionManager().shutdown();
            }
        }
    }

    private DefaultHttpClient getUnsafeHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

//    private void initHttpClient() {
//        HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//
//        DefaultHttpClient client = new DefaultHttpClient();
//
//        SchemeRegistry registry = new SchemeRegistry();
//        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
//        registry.register(new Scheme("https", socketFactory, 443));
//        SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
//        mHttpClient = new DefaultHttpClient(mgr, client.getParams());
//
//// Set verifier
//        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//
//// Example send http request
//    }

    private boolean sendWolRequest(Model model) throws IOException {
        Uri.Builder b = Uri.parse(model.getWolPage()).buildUpon();
        b.appendQueryParameter("if", model.serverInterface)
                .appendQueryParameter("mac", model.macAddress);

        String url = b.build().toString();

        HttpGet httpget = new HttpGet(url);
        HttpResponse response = mHttpClient.execute(httpget);
        HttpEntity entity = response.getEntity();

        Log.d(TAG, "Login form get: " + response.getStatusLine());

        String content = EntityUtils.toString(entity);
        return !content.toLowerCase().contains(String.format("Sent magic packet to %s", model.macAddress.toLowerCase()));
    }

//    private void logCookies() {
//        Log.d(TAG, "Cookies:");
//        List<Cookie> cookies = mHttpClient.getCookieStore().getCookies();
//        if (cookies.isEmpty()) {
//            Log.d(TAG, "No cookies");
//        } else {
//            for (int i = 0; i < cookies.size(); i++) {
//                Log.d(TAG, "- " + cookies.get(i).toString());
//            }
//        }
//    }

    private String getCSRFToken(Model model) throws IOException {
        HttpGet httpget = new HttpGet(model.getLoginPage());
        HttpResponse response = mHttpClient.execute(httpget);
        HttpEntity entity = response.getEntity();

        Log.d(TAG, "Login form get: " + response.getStatusLine());

        String content = EntityUtils.toString(entity);
        // Log.d(TAG, content);
        Document doc = Jsoup.parse(content);
        Element element = doc.select("input[name=__csrf_magic]").first();
        String csrfToken = element.val();
        Log.d(TAG, "CSRF Token: " + csrfToken);

        return csrfToken;
    }

    private boolean authenticate(Model model, String CSRFToken) throws IOException {

        HttpPost httpPost = new HttpPost(model.getLoginPage());

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("login", "Login"));
        nvps.add(new BasicNameValuePair("__csrf_magic", CSRFToken));
        nvps.add(new BasicNameValuePair("usernamefld", model.userName));
        nvps.add(new BasicNameValuePair("passwordfld", model.password));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = mHttpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        Log.d(TAG, "Login form get: " + response.getStatusLine());

        //logCookies();

        String content = EntityUtils.toString(entity);
        return !content.toLowerCase().contains("username or password incorrect");
    }

    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore trustStore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(trustStore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
