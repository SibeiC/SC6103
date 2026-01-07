package com.chencraft.ntu.model;

import com.chencraft.ntu.util.Converter;

import java.lang.reflect.Field;

/**
 * Interface for defining serializable objects that can be converted into
 * a byte array representation. Implementing classes must contain fields
 * that support serialization to ensure proper functionality.
 * <p>
 * The default implementation of the {@code marshall} method uses
 * reflection to inspect the fields of the implementing class and convert
 * their values into byte arrays. Supported field types include:
 * - {@code String}
 * - {@code Integer} (both primitive and wrapper types)
 * - {@code Double} (both primitive and wrapper types)
 * - Other classes implementing the {@code Serializable} interface
 * <p>
 * Unsupported field types will result in an {@code UnsupportedOperationException}.
 * <p>
 * Reflection-based access ensures that private, protected, or package-private
 * fields are also processed by the serialization logic.
 * <p>
 * Any {@code IllegalAccessException} encountered during the serialization
 * process will result in a {@code RuntimeException}.
 * <p>
 * Classes implementing this interface should ensure that fields expected
 * to be serialized are properly initialized to prevent unintended behavior.
 */
public interface MySerializable {
    default byte[] marshall() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();

            try {
                if (type.equals(String.class)) {
                    byte[] value = Converter.toByteArray((String) field.get(this));
                } else if (double.class.equals(type) || Double.class.equals(type)) {
                    byte[] value = Converter.toByteArray((Double) field.get(this));
                } else if (int.class.equals(type) || Integer.class.equals(type)) {
                    byte[] value = Converter.toByteArray((Integer) field.get(this));
                } else if (MySerializable.class.isAssignableFrom(type)) {
                    byte[] value = ((MySerializable) field.get(this)).marshall();
                } else {
                    throw new UnsupportedOperationException("Unsupported type: " + type.getSimpleName());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to marshall field: " + field.getName()
                                                   + " for class: " + this.getClass()
                                                                          .getSimpleName(), e);
            }
        }

        throw new UnsupportedOperationException("To be implemented.");
    }
}
