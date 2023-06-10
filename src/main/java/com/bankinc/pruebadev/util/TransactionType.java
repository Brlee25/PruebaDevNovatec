package com.bankinc.pruebadev.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {

    PURCHASE("Compra"), ANULATION("Anulación");

    private String value;
}
