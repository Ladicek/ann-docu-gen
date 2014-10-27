package cz.ladicek.annDocuGen.annotationProcessor.gson;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class OptionalTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        if (typeToken.getRawType() != Optional.class || !(type instanceof ParameterizedType)) {
            return null;
        }

        Type valueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(valueType));
        return new OptionalTypeAdapter(valueAdapter);
    }

    private static final class OptionalTypeAdapter<T> extends TypeAdapter<Optional<T>> {
        private final TypeAdapter<T> valueAdapter;

        private OptionalTypeAdapter(TypeAdapter<T> valueAdapter) {
            this.valueAdapter = valueAdapter;
        }

        public void write(JsonWriter out, Optional<T> value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginArray();
            if (value.isPresent()) {
                valueAdapter.write(out, value.get());
            }
            out.endArray();
        }

        public Optional<T> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            T element = null;
            in.beginArray();
            if (in.hasNext()) {
                element = valueAdapter.read(in);
            }
            in.endArray();

            return Optional.fromNullable(element);
        }
    }
}
