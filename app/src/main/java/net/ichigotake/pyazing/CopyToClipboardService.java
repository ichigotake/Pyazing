package net.ichigotake.pyazing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public final class CopyToClipboardService extends IntentService {

    private final static String EXTRA_LABEL = "label";

    public static Intent createIntent(Context context, String text, String label) {
        Intent intent = new Intent(context, CopyToClipboardService.class);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(EXTRA_LABEL, label);
        return intent;
    }

    public CopyToClipboardService() {
        this(CopyToClipboardService.class.getSimpleName());
    }

    public CopyToClipboardService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            copyToClipboard(
                    text,
                    intent.getStringExtra(EXTRA_LABEL)
            );
            Handler mainThread = new Handler(Looper.getMainLooper());
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.app_copy_to_clipboard_complete, text),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
            cancelNotify();
        }
        stopSelf();
    }

    private void cancelNotify() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(UploadMediaService.NOTIFICATION_TAG_COPY_TO_CLIPBOARD, R.string.app_name);
    }

    private void copyToClipboard(String text, String label) {
        ClipData.Item item = new ClipData.Item(text);
        String[] mimeType = new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData clipData = new ClipData(new ClipDescription(label, mimeType), item);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }
}
