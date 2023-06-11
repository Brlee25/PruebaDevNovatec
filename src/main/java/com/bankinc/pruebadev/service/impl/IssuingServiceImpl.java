package com.bankinc.pruebadev.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.bankinc.pruebadev.exception.ResourceNotFound;
import com.bankinc.pruebadev.model.Card;
import com.bankinc.pruebadev.repository.RepositoryCard;
import com.bankinc.pruebadev.service.IssuingService;
import com.bankinc.pruebadev.util.Utils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IssuingServiceImpl implements IssuingService {

    @Autowired
    private final RepositoryCard repositoryCard;

    @Override
    public ResponseEntity<Card> processActivateCard(String cardId) {
        return (new ResponseEntity<>(this.updateStateCard(cardId, true), HttpStatus.OK));
    }

    @Override
    public ResponseEntity<Card> processBalanceInquiry(String cardId) {
        return (new ResponseEntity<>(this.findCard(cardId), HttpStatus.OK));
    }

    @Override
    public ResponseEntity<Card> processCardBlocking(String cardId) {
        return (new ResponseEntity<>(this.CardBlocking(cardId), HttpStatus.OK));
    }

    @Override
    public ResponseEntity<Card> processGenerateCard(int productId) {
        String issuerId = String.valueOf(productId);
        if (issuerId.length() != 6) {
            return (new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }
        return (new ResponseEntity<>(this.saveCard(this.getNewCard(issuerId)), HttpStatus.OK));
    }

    @Override
    public ResponseEntity<Card> processLoadBalance(String cardId, float amount) {
        Card foundCard = this.findCard(cardId);
        foundCard.setBalance(foundCard.getBalance() + amount);
        return (new ResponseEntity<>((this.saveCard(foundCard)), HttpStatus.OK));
    }

    private Card CardBlocking(String cardId) {
        Card foundCard = this.findCard(cardId);
        foundCard.setBlocked(true);
        return (this.saveCard(foundCard));
    }

    private Card findCard(String cardId) {
        Card foundCard = this.repositoryCard.findById(cardId).orElseThrow(() -> {
            throw (new ResourceNotFound());
        });
        return (foundCard);
    }

    private Card getNewCard(String issuerId) {
        Card card = new Card();
        card.setCardId(issuerId + Utils.generateRandomNum(10));
        card.setCurrencyCode("840");
        card.setCardholderName("Customer Name");
        card.setExpirationDate(Utils.getFormatedDate(3, 0));
        return (card);
    }

    private Card saveCard(Card card) {
        Card cardSave = this.repositoryCard.save(card);
        return (cardSave);
    }

    private Card updateStateCard(String cardId, boolean isActive) {
        Card foundCard = this.findCard(cardId);
        foundCard.setActive(isActive);
        return (this.saveCard(foundCard));
    }

}
