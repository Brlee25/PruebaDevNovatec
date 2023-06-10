package com.bankinc.pruebadev.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StateTransaction {

    ANNULLED("ANULADA"), APPROVED("APROBADA"), REJECTED("RECHAZADA");

    private String value;

}
