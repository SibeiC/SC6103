package com.chencraft.ntu.model;

import com.chencraft.ntu.service.IdGenerator;
import com.chencraft.ntu.util.Converter;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
        List<byte[]> pieces = new ArrayList<>();
        int totalLength = 0;

        // Message Type value
        byte[] typeBytes = Converter.toByteArray(MessageType.MsgRequest);
        pieces.add(typeBytes);
        totalLength += typeBytes.length;

        // Request Id
        byte[] idBytes = Converter.toByteArray(IdGenerator.getNextId());
        pieces.add(idBytes);
        totalLength += idBytes.length;

        // Operation Code
        byte[] opCodeBytes = new byte[]{getOpCode().getValue()};
        pieces.add(opCodeBytes);
        totalLength += opCodeBytes.length;

        // Body
        for (FieldDefn fieldDefn : getFieldDefs()) {
            try {
                String fieldName = fieldDefn.getFieldName();
                Class<?> fieldType = fieldDefn.getFieldType();
                Field field = this.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                byte[] fieldBytes;
                if (fieldType.equals(String.class)) {
                    fieldBytes = Converter.toByteArray((String) field.get(this));
                } else if (fieldType.equals(Double.class)) {
                    fieldBytes = Converter.toByteArray((Double) field.get(this));
                } else if (fieldType.equals(Integer.class)) {
                    fieldBytes = Converter.toByteArray((Integer) field.get(this));
                } else if (fieldType.equals(Currency.class)) {
                    fieldBytes = Converter.toByteArray((Currency) field.get(this));
                } else {
                    throw new UnsupportedOperationException("Unsupported type: " + fieldType.getSimpleName());
                }
                pieces.add(fieldBytes);
                totalLength += fieldBytes.length;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to marshall field: " + fieldDefn.getFieldName()
                                                   + " for class: " + this.getClass()
                                                                          .getSimpleName(), e);
            }
        }

        byte[] result = new byte[totalLength];
        int currentPos = 0;
        for (byte[] piece : pieces) {
            System.arraycopy(piece, 0, result, currentPos, piece.length);
            currentPos += piece.length;
        }
        return result;
    }
}
