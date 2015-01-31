package net.ichigotake.pyazing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams
import net.ichigotake.pyazing.media.UploadMode
import net.ichigotake.pyazing.media.UploadModeDetector
import org.apache.http.Header;

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
    IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    def void onCreate() {
        super.onCreate();
        uploadingNotification = new UploadingNotification(this);
    }

    @Override
    def int onStartCommand(Intent intent, int flags, int startId) {
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
        UploadMode uploadMode = UploadModeDetector.detect(media);
        params.put(uploadMode.getRequestParameter(), inputStream, filename, media.getMimeType());
        // TODO: アップロード前に UploadMediaActivity で設定出来るようにする
        if (media.isImage()) {
            params.put("auto_resize", 1);
            params.put("width", 1024);
            params.put("height", 1024);
        }
        AsyncHttpClient client = new AsyncHttpClient();
        Toasts.startUploading(getApplicationContext());
        uploadingNotification.startProgress();
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
