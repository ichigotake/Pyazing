package net.ichigotake.pyazing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class UploadMediaActivity extends Activity {

    private final String LOG_TAG = UploadMediaActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Uri imageUri = Uri.parse(getIntent().getExtras().get(Intent.EXTRA_STREAM).toString());
            String mimeType = getIntent().getType();
            startService(UploadMediaService.createIntent(this, imageUri, mimeType));
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "", e);
            Toast.makeText(this, R.string.app_upload_failure, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
