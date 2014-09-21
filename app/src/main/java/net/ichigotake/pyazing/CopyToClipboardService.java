package net.ichigotake.pyazing;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

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
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            copyToClipboard(
                    text,
                    intent.getStringExtra(EXTRA_LABEL)
            );
            Toasts.completeCopingText(getApplicationContext(), text);
        }
        stopSelf();
    }

    private void copyToClipboard(String text, String label) {
        ClipData.Item item = new ClipData.Item(text);
        String[] mimeType = new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData clipData = new ClipData(new ClipDescription(label, mimeType), item);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }
}
