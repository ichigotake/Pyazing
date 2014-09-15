package net.ichigotake.pyazing;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.IOException;
import java.io.InputStream;

public final class UploadMediaService extends Service {

    public static final String NOTIFICATION_TAG_COPY_TO_CLIPBOARD = "copy_to_clipboard";
    private static final String EXTRA_MEDIA_URI = "media_uri";
    private final String NOTIFICATION_PROGRESS = "progress";

    public static Intent createIntent(Context context, Uri data, String mimeType) {
        Intent intent = new Intent(context, UploadMediaService.class);
        intent.setType(mimeType);
        intent.putExtra(EXTRA_MEDIA_URI, data);
        return intent;
    }

    public UploadMediaService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Uri data = intent.getParcelableExtra(EXTRA_MEDIA_URI);
            upload(new UploadMedia(data, intent.getType()));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    protected void upload(final UploadMedia media) {
        try {
            String filename;
            if (media.isVideo() && !media.hasFilenameExtension()) {
                filename = media.getData().getLastPathSegment() + ".mp4";
            } else {
                filename = media.getData().getLastPathSegment();
            }
            InputStream inputStream = getContentResolver().openInputStream(media.getData());
            RequestParams params = new RequestParams();
            params.put(media.getUploadMode().getParameter(), inputStream, filename, media.getMimeType());
            AsyncHttpClient client = new AsyncHttpClient();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    startProgressBar();
                    Toast.makeText(getApplicationContext(),
                            R.string.app_upload_progress_start, Toast.LENGTH_SHORT).show();
                }
            });
            client.post(this, getString(R.string.app_server_url), params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    stopProgressBar();
                    notifyToSuccess(new String(responseBody));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    stopProgressBar();
                    notifyToFailure(media);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startProgressBar() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_upload_progress))
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(100, 0, true)
                .setAutoCancel(false)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_PROGRESS, R.string.app_name, notification);
    }

    private void stopProgressBar() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_PROGRESS, R.string.app_name);
    }

    private void notifyToSuccess(String url) {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_upload_succeed))
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_launcher,
                        getString(R.string.app_copy_to_clipboard),
                        createCopyToClipboardIntent(url))
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_TAG_COPY_TO_CLIPBOARD, R.string.app_name, notification);
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.app_upload_succeed),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private PendingIntent createCopyToClipboardIntent(String url) {
        return PendingIntent.getService(this, 0, CopyToClipboardService.createIntent(
                        this,
                        url,
                        getString(R.string.app_copy_to_clipboard)), PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private void notifyToFailure(UploadMedia media) {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_upload_failure))
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_launcher,
                        getString(R.string.app_upload_retry), createRetryUploadIntent(media))
                .build();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, notification);
    }

    private PendingIntent createRetryUploadIntent(UploadMedia media) {
        Intent intent = createIntent(getApplicationContext(), media.getData(), media.getMimeType());
        return PendingIntent.getService(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
