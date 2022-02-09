
class FieldGetter {

    public int getNumberOfFieldsClassDeclares(Class<?> clazz) {
        return clazz.getDeclaredFields().length;
    }

}