package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.MySerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data transfer object for a service that allows a user to monitor updates made to all bank accounts.
 */
@Data
@Schema(description = "Request to register for monitoring account updates")
public class MonitorRequest implements MySerializable {
    /**
     * The designated time period (in seconds) during which the client will receive callbacks.
     */
    @Schema(description = "Length of monitor interval in seconds", example = "60", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer monitorInterval;
}
