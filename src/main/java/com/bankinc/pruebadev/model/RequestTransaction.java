package com.bankinc.pruebadev.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestTransaction {

    @NotNull
    @NotBlank
    private String cardId;
    @NotNull
    @NotBlank
    private String currencyCode;
    @NotNull
    private float  price;
    private int    transactionId;
}
