package com.bankinc.pruebadev.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
public class Transaction {

    @Column(nullable = false)
    private float  amount;
    @Column(nullable = false)
    private String currencyCode;
    @Column(nullable = false)
    private Date   dateTime;
    @Id
    @Column(length = 6)
    private int    transactionId;
    @Column(nullable = false)
    private String rspData;
    private String state;
    @Column(length = 6)
    private int    originalTransactionId;
    private String transactionType;
    @Column(length = 6)
    private int    annulledBy;

}
