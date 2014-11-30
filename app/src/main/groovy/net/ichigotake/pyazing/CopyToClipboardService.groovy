package net.ichigotake.pyazing;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

final class CopyToClipboardService extends IntentService {

    private final static def EXTRA_LABEL = "label";

    static Intent createIntent(Context context, String text, String label) {
        Intent intent = new Intent(context, CopyToClipboardService.class);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(EXTRA_LABEL, label);
        return intent;
    }

    CopyToClipboardService() {
        this(CopyToClipboardService.class.getSimpleName());
    }

    CopyToClipboardService(String name) {
        super(name);
    }

    @Override
    void onHandleIntent(Intent intent) {
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

    private def copyToClipboard(String text, String label) {
        ClipData.Item item = new ClipData.Item(text);
        def mimeType = [ClipDescription.MIMETYPE_TEXT_PLAIN];
        ClipData clipData = new ClipData(new ClipDescription(label, mimeType.toArray()), item);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }
}
