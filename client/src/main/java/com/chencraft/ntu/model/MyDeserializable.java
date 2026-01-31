package com.chencraft.ntu.model;

/**
 * Interface for defining objects capable of being deserialized from a byte array representation.
 * Classes implementing this interface should provide the logic to reconstruct an object
 * of type {@code T} from a given byte array.
 * <p>
 * The default implementation of the {@code unmarshal} method throws an
 * {@code UnsupportedOperationException} and must be overridden in implementing classes.
 */
public interface MyDeserializable {
    static <T extends MyDeserializable> T unmarshal(byte[] bytes, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }
}
