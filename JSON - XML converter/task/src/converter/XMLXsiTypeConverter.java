package converter;

public interface XMLXsiTypeConverter<T> {
    T convert(String value);
}