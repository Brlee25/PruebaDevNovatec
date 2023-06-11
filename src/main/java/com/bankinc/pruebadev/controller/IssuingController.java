package com.bankinc.pruebadev.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bankinc.pruebadev.exception.ResourceNotFound;
import com.bankinc.pruebadev.model.Card;
import com.bankinc.pruebadev.service.IssuingService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/card")
@AllArgsConstructor
public class IssuingController {

    private final IssuingService issuingService;

    @PostMapping(path = "/enroll")
    public ResponseEntity<Card> processActiveCard(@RequestBody Card card) {
        ResponseEntity<Card> response = null;
        try {
            response = this.issuingService.processActivateCard(card.getCardId());
        }
        catch (ResourceNotFound rne) {
            return (new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return (response);
    }

    @GetMapping(path = "/balance/{cardId}")
    public ResponseEntity<Card> processBalanceInquiry(@PathVariable(name = "cardId") String cardId) {
        ResponseEntity<Card> response = null;
        try {
            response = this.issuingService.processBalanceInquiry(cardId);
        }
        catch (ResourceNotFound rne) {
            return (new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return (response);
    }

    @GetMapping(path = "/{productId}/number")
    public ResponseEntity<Card> processGenerateCard(@PathVariable(name = "productId") int productId) {
        return (this.issuingService.processGenerateCard(productId));
    }

    @DeleteMapping(path = "/{cardId}")
    public ResponseEntity<Card> processInactiveCard(@PathVariable(name = "cardId") String cardId) {
        ResponseEntity<Card> response = null;
        try {
            response = this.issuingService.processCardBlocking(cardId);
        }
        catch (ResourceNotFound rne) {
            return (new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return (response);
    }

    @PostMapping(path = "/balance")
    public ResponseEntity<Card> processLoadBalance(@RequestBody Card card) {
        ResponseEntity<Card> response = null;
        try {
            response = this.issuingService.processLoadBalance(card.getCardId(), card.getBalance());
        }
        catch (ResourceNotFound rne) {
            return (new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return (response);
    }
}
