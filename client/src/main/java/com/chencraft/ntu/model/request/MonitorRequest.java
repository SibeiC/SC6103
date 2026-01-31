package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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

    @Override
    public OpCode getOpCode() {
        return OpCode.OpMonitor;
    }

    @Override
    public List<FieldDefn> getFieldDefs() {
        return List.of(FieldDefn.MONITOR_INTERVAL);
    }
}
