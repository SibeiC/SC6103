package com.chencraft.ntu.model.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class IntResponse extends GenericResponse {
    private int value;
}
