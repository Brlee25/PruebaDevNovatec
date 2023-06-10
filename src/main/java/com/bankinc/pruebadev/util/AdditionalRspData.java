package com.bankinc.pruebadev.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdditionalRspData {

    EXPIRED_CARD("TARJETA EXPIRADA"),
    INACTIVE_CARD("TARJETA INACTIVA"),
    INSUFFICIENT_FUNDS("FONDOS INSUFICIENTES"),
    INVALID_CURRENCY("TIPO DE MONEDA INVALIDO"),
    SUCCESFUL_TRANSACTION("TRANSACCIÓN EXITOSA"),
    TRANSACTION_NOT_FOUND("TRANSACCIÓN ORIGINAL NO ENCONTRADA"),
    EXPIRED_TIME("NO SE PUDO ANULAR DEBIDO A QUE ES SUPERIOR A 24 HRS");

    private String value;

}
