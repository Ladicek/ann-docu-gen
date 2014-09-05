package cz.ladicek.annDocuGen.api;

public class Optional<T> {
    private final T value;

    private Optional(T value) {
        this.value = value;
    }

    public static <T> Optional<T> absent() {
        return new Optional<T>(null);
    }

    public static <T> Optional<T> of(T value) {
        assert value != null;
        return new Optional<T>(value);
    }
}
