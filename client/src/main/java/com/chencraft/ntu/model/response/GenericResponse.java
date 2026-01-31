package com.chencraft.ntu.model.response;

import com.chencraft.ntu.model.MessageType;
import com.chencraft.ntu.model.OpCode;
import lombok.Data;

/**
 * A standard generic response object
 */
@Data
public abstract class GenericResponse {
    private MessageType messageType;

    private int requestId;

    private OpCode operationCode;
}
