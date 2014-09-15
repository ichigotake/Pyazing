package net.ichigotake.pyazing;

enum UploadMode {

    IMAGE("imagedata"),
    VIDEO("data"),
    ;

    private final String parameter;

    private UploadMode(String parameter) {
        this.parameter = parameter;
    }

    String getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return parameter;
    }

}
