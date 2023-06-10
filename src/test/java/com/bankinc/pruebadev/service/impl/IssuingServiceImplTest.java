package com.bankinc.pruebadev.service.impl;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import com.bankinc.pruebadev.exception.ResourceNotFound;
import com.bankinc.pruebadev.model.Card;
import com.bankinc.pruebadev.repository.RepositoryCard;

@RunWith(SpringRunner.class)
@DataJpaTest
class IssuingServiceImplTest {

    @Autowired
    private RepositoryCard     repositoryCard;

    @InjectMocks
    private IssuingServiceImpl issuingServiceImpl;

    @AfterEach
    void tearDown() {
        this.issuingServiceImpl = null;
    }

    @BeforeEach
    void setUp() {
        this.issuingServiceImpl = new IssuingServiceImpl(this.repositoryCard);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessGenerateCardCorrectly() {
        ResponseEntity<?> response = this.issuingServiceImpl.processGenerateCard(999999);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void processGenerateCardWithProductIdIncorrect() {
        ResponseEntity<?> response = this.issuingServiceImpl.processGenerateCard(9999);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void processActiveCardCorrectly() {
        Card card = this.issuingServiceImpl.processGenerateCard(999999).getBody();
        Assertions.assertEquals(HttpStatus.OK, this.issuingServiceImpl.processActivateCard(card.getCardId()).getStatusCode());
    }

    @Test
    void processLoadBalanceCorrectly() {
        Card card = this.issuingServiceImpl.processGenerateCard(999999).getBody();
        ResponseEntity<Card> response = this.issuingServiceImpl.processLoadBalance(card.getCardId(), 10000);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(10000, response.getBody().getBalance());
    }

    @Test
    void processActiveCardWithCardNoExists() {
        Assertions.assertThrows(ResourceNotFound.class, () -> {
            this.issuingServiceImpl.processActivateCard("9999990000000000");
        });
    }

    @Test
    void processCardBlockingCorrectly() {
        Card card = this.issuingServiceImpl.processGenerateCard(999999).getBody();
        ResponseEntity<Card> response = this.issuingServiceImpl.processCardBlocking(card.getCardId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().isBlocked());
    }

    @Test
    void processBalanceInquiryCorrectly() {
        Card card = this.issuingServiceImpl.processGenerateCard(999999).getBody();
        this.issuingServiceImpl.processLoadBalance(card.getCardId(), 10000);
        ResponseEntity<Card> response = this.issuingServiceImpl.processBalanceInquiry(card.getCardId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(10000, response.getBody().getBalance());
    }

}
