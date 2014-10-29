package cz.ladicek.annDocuGen.annotationProcessor.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonHolder {
    private GsonHolder() {} // avoid instantiation

    public static final Gson instance = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
            .create();
}
