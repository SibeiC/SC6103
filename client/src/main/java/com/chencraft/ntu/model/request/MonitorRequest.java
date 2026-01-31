package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for a service that allows a user to monitor updates made to all bank accounts.
 */
@Data
public class MonitorRequest implements MySerializable {
    /**
     * The designated time period (in seconds) during which the client will receive callbacks.
     */
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
