package net.ichigotake.pyazing;

import android.net.Uri;

class UploadMedia {

    private final Uri data;
    private final String mimeType;

    UploadMedia(Uri data, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
    }

    Uri getData() {
        return data;
    }

    String getMimeType() {
        return mimeType;
    }

    boolean isImage() {
        return mimeType.startsWith("image/");
    }

    boolean isVideo() {
        return mimeType.startsWith("video/");
    }

    boolean hasFilenameExtension() {
        return data.getLastPathSegment().contains(".");
    }

}
