package net.ichigotake.pyazing.media

import net.ichigotake.pyazing.UploadMedia;

class UploadModeDetector {

    static UploadMode detect(UploadMedia media) {
        return media.isImage() ? new ImageUploadMode() : new VideoUploadMode();
    }

}

interface UploadMode {

    String getRequestParameter()

}

class ImageUploadMode implements UploadMode {

    @Override
    String getRequestParameter() {
        return 'imagedata'
    }
}

class VideoUploadMode implements UploadMode {

    @Override
    String getRequestParameter() {
        return "data"
    }
}
