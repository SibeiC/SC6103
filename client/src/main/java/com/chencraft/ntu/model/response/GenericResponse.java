package com.chencraft.ntu.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * A standard generic response object used for acknowledging operations that do not return specific data.
 */
@Data
@Schema(description = "Generic response")
public class GenericResponse {
    /**
     * HTTP-like status code indicating the outcome of the operation.
     */
    @Schema(description = "Status code", example = "200", requiredMode = Schema.RequiredMode.REQUIRED)
    private int statusCode = 200;

    /**
     * Descriptive message about the operation result.
     */
    @Schema(description = "Response message", example = "Operation successful", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message = "Operation successful";
}
