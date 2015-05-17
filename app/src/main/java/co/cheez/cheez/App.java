package co.cheez.cheez;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jiho on 5/10/15.
 */
public class App extends Application {
    private static Context mContext;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mRequestQueue = Volley.newRequestQueue(this);


        disableSSLCertificateChecking();
    }

    public static Context getContext() {
        return mContext;
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


    private static void disableSSLCertificateChecking() {
        // https://gist.github.com/aembleton/889392
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // http://www.nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
