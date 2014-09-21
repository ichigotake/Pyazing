package net.ichigotake.pyazing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class UploadMediaService extends Service {

    private static final String EXTRA_MEDIA_URI = "media_uri";
    private final String LOG_TAG = UploadMediaService.class.getSimpleName();

    public static Intent createIntent(Context context, Uri data, String mimeType) {
        Intent intent = new Intent(context, UploadMediaService.class);
        intent.setType(mimeType);
        intent.putExtra(EXTRA_MEDIA_URI, data);
        return intent;
    }

    private UploadingNotification uploadingNotification;

    public UploadMediaService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        uploadingNotification = new UploadingNotification(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Uri data = intent.getParcelableExtra(EXTRA_MEDIA_URI);
            try {
                upload(new UploadMedia(data, intent.getType()));
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "", e);
                Toasts.ignoreFile(getApplicationContext());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void upload(final UploadMedia media) throws FileNotFoundException {
        String filename;
        if (media.isVideo() && !media.hasFilenameExtension()) {
            filename = media.getData().getLastPathSegment() + ".mp4";
        } else {
            filename = media.getData().getLastPathSegment();
        }
        InputStream inputStream = getContentResolver().openInputStream(media.getData());
        RequestParams params = new RequestParams();
        UploadMode uploadMode = media.isImage() ? UploadMode.IMAGE : UploadMode.VIDEO;
        params.put(uploadMode.getParameter(), inputStream, filename, media.getMimeType());
        AsyncHttpClient client = new AsyncHttpClient();
        Toasts.startUploading(getApplicationContext());
        client.post(this, getString(R.string.app_server_url), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                uploadingNotification.stopProgress();
                uploadingNotification.notifyToSuccess(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                uploadingNotification.stopProgress();
                uploadingNotification.notifyToFailure(media);
            }
        });
    }

}
