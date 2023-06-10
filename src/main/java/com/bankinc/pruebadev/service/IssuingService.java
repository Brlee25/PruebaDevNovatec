package com.bankinc.pruebadev.service;

import org.springframework.http.ResponseEntity;
import com.bankinc.pruebadev.model.Card;

public interface IssuingService {

    ResponseEntity<Card> processActivateCard(String cardId);

    ResponseEntity<Card> processBalanceInquiry(String cardId);

    ResponseEntity<Card> processCardBlocking(String cardId);

    ResponseEntity<Card> processGenerateCard(int productId);

    ResponseEntity<Card> processLoadBalance(String cardId, float balance);

}
