package com.bankinc.pruebadev.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
public class Card {

    @Column(nullable = false)
    private float         balance;
    private String        cardholderName;
    @Id
    @Column(length = 16)
    private String        cardId;
    @Column(nullable = false)
    private String        currencyCode;
    @Column(nullable = false)
    private Date          expirationDate;
    private boolean       isActive;
    private boolean       isBlocked;
    @OneToMany
    private Transaction[] transactions;

}
