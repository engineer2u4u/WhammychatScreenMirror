package screenAlike;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;

//import com.google.firebase.crash.FirebaseCrash;

import com.example.screenalike.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.content.Context.WIFI_SERVICE;
//import java.util.concurrent.ConcurrentLinkedQueue;

//
//import info.dvkr.screenstream.R;
//import info.dvkr.screenstream.ScreenStreamApplication;
//
//import static info.dvkr.screenstream.ScreenStreamApplication.getAppPreference;
//import static info.dvkr.screenstream.ScreenStreamApplication.getMainActivityViewModel;

public final class AppData {
    private final WindowManager mWindowManager;
    private final WifiManager mWifiManager;
    private final int mDensityDpi;
    private final float mScale;
    private String mIndexHtmlPage;
//    private final String mPinRequestHtmlPage;
//    private final String mPinRequestErrorMsg;
    private final byte[] mIconBytes;
    private int mClients;
    private final ConcurrentLinkedDeque<byte[]> mImageQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedQueue<Client> mClientQueue = new ConcurrentLinkedQueue<>();
    private volatile int mClientTimeout;
    private static final String DEFAULT_CLIENT_TIMEOUT = "3000";
    private volatile boolean isActivityRunning;
    private volatile boolean isStreamRunning;
    private final Context mContext;
    private final SharedPreferences mSharedPreferences;
    private volatile int mServerPort;
    private String DEFAULT_SERVER_PORT = "8080";
    public AppData(final Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mDensityDpi = getDensityDpi();
        mScale = getScale(context);
       // mPinRequestHtmlPage = getPinRequestHtmlPage(context);
//        mPinRequestErrorMsg = context.getString(R.string.html_wrong_pin);
        mIconBytes = getFavicon(context);
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mServerPort = Integer.parseInt(mSharedPreferences.getString(mContext.getString(R.string.pref_key_server_port), DEFAULT_SERVER_PORT));
        mClientTimeout = Integer.parseInt(mSharedPreferences.getString(mContext.getString(R.string.pref_key_client_con_timeout), DEFAULT_CLIENT_TIMEOUT));

    }

    public void setActivityRunning(final boolean activityRunning) {
        isActivityRunning = activityRunning;
    }

    public void setStreamRunning(final boolean streamRunning) {
        isStreamRunning = streamRunning;
//        getMainActivityViewModel().setStreaming(streamRunning);
    }

    public ConcurrentLinkedDeque<byte[]> getImageQueue() {
        return mImageQueue;
    }

    public ConcurrentLinkedQueue<Client> getClientQueue() {
        return mClientQueue;
    }

    public boolean isActivityRunning() {
        return isActivityRunning;
    }

    public boolean isStreamRunning() {
        return isStreamRunning;
    }

    public WindowManager getWindowsManager() {
        return mWindowManager;
    }

    public int getScreenDensity() {
        return mDensityDpi;
    }

    public float getDisplayScale() {
        return mScale;
    }

    public Point getScreenSize() {
        final Point screenSize = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(screenSize);
        return screenSize;
    }

    public void initIndexHtmlPage(final Context context) {
        mIndexHtmlPage = getHtml(context, "index.html");
//        if (ScreenStreamApplication.getAppPreference().isDisableMJPEGCheck()) {
//            mIndexHtmlPage = mIndexHtmlPage.replaceFirst("id=mj", "").replaceFirst("id=pmj", "");
//        }
    }

    public String getIndexHtml(final String streamAddress) {
        return mIndexHtmlPage.replaceFirst("SCREEN_STREAM_ADDRESS", streamAddress);
    }

//    public String getPinRequestHtml(final boolean isError) {
//        return mPinRequestHtmlPage.replaceFirst("wrong_pin", (isError) ? mPinRequestErrorMsg : "&nbsp");
//    }

    public byte[] getIcon() {
        return mIconBytes;
    }
    public void setClients(final int clients) {
        mClients = clients;
    }
    @Nullable
    public InetAddress getIpAddress() {
        try {
            final int ipInt = mWifiManager.getConnectionInfo().getIpAddress();
            return InetAddress.getByAddress(new byte[]{
                    (byte) (ipInt & 0xff),
                    (byte) (ipInt >> 8 & 0xff),
                    (byte) (ipInt >> 16 & 0xff),
                    (byte) (ipInt >> 24 & 0xff)});
        } catch (UnknownHostException e) {
           // FirebaseCrash.report(e);
        }
        return null;
    }


    public int getServerPort(){
        return mServerPort;
    }

    public String getServerAddress() {
        return "http:/" + getIpAddress() + ":" + getServerPort();
    }

    public int getClientTimeout() {
        return mClientTimeout;
    }

//    public String getServerAddress() {
//        return "http:/" + getIpAddress() + ":" + getServerPort();
//    }
//
//    public String getIpAddress() {
//        WifiManager wm = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
//        String ipInt = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//        return ipInt;
//    }


    public boolean isWiFiConnected() {
        return mWifiManager.getConnectionInfo().getIpAddress() != 0;
    }

    //Private
    private int getDensityDpi() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.densityDpi;
    }

    private float getScale(final Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

//    private String getPinRequestHtmlPage(final Context context) {
//        return getHtml(context, "pinrequest.html")
//                .replaceFirst("stream_require_pin", context.getString(R.string.html_stream_require_pin))
//                .replaceFirst("enter_pin", context.getString(R.string.html_enter_pin))
//                .replaceFirst("four_digits", context.getString(R.string.html_four_digits))
//                .replaceFirst("submit_text", context.getString(R.string.html_submit_text));
//    }

    private String getHtml(final Context context, final String fileName) {
        final StringBuilder sb = new StringBuilder();
        String line;
        try (final BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(context.getAssets().open(fileName), StandardCharsets.UTF_8)
                     )) {
            while ((line = reader.readLine()) != null) sb.append(line.toCharArray());
        } catch (IOException e) {
           // FirebaseCrash.report(e);
        }
        final String html = sb.toString();
        sb.setLength(0);
        return html;
    }

    private byte[] getFavicon(final Context context) {
        try (final InputStream inputStream = context.getAssets().open("favicon.png")) {
            final byte[] iconBytes = new byte[inputStream.available()];
            int count = inputStream.read(iconBytes);
            if (count != 353) throw new IOException();
            return iconBytes;
        } catch (IOException e) {
           // FirebaseCrash.report(e);
        }
        return null;
    }
}