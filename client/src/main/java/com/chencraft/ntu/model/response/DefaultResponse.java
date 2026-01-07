package com.chencraft.ntu.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Default response object used as a baseline implementation for generic operations.
 * Extends {@code GenericResponse} to inherit standard response attributes such as status
 * and message. This class serves as a versatile, reusable response model for operations
 * that do not require any additional attributes beyond the generic response structure.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Default response")
public class DefaultResponse extends GenericResponse {
}
