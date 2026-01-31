package com.chencraft.ntu.model;

import com.chencraft.ntu.service.IdGenerator;
import com.chencraft.ntu.util.Converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

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
 * - Other classes implementing the {@code MySerializable} interface
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
    OpCode getOpCode();

    List<FieldDefn> getFieldDefs();

    default byte[] marshall() {
        try {
            // TODO: Replace ByteArrayOutputStream
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            // Message Type value
            output.write(Converter.toByteArray(MessageType.MsgRequest));

            // Request Id
            output.write(Converter.toByteArray(IdGenerator.getNextId()));

            // Operation Code
            output.write(getOpCode().getValue());

            // Body
            for (FieldDefn fieldDefn : getFieldDefs()) {
                try {
                    String fieldName = fieldDefn.getFieldName();
                    Class<?> fieldType = fieldDefn.getFieldType();
                    Field field = this.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);

                    if (fieldType.equals(String.class)) {
                        output.write(Converter.toByteArray((String) field.get(this)));
                    } else if (fieldType.equals(Double.class)) {
                        output.write(Converter.toByteArray((Double) field.get(this)));
                    } else if (fieldType.equals(Integer.class)) {
                        output.write(Converter.toByteArray((Integer) field.get(this)));
                    } else if (fieldType.equals(Currency.class)) {
                        output.write(Converter.toByteArray((Currency) field.get(this)));
                    } else {
                        throw new UnsupportedOperationException("Unsupported type: " + fieldType.getSimpleName());
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to marshall field: " + fieldDefn.getFieldName()
                                                       + " for class: " + this.getClass()
                                                                              .getSimpleName(), e);
                }
            }

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to marshall object: " + this.getClass().getSimpleName(), e);
        }
    }
}
