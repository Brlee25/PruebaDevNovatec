package com.bankinc.pruebadev.service.impl;

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
import com.bankinc.pruebadev.model.RequestTransaction;
import com.bankinc.pruebadev.model.Transaction;
import com.bankinc.pruebadev.repository.RepositoryCard;
import com.bankinc.pruebadev.repository.RepositoryTransaction;
import com.bankinc.pruebadev.util.AdditionalRspData;
import com.bankinc.pruebadev.util.StateTransaction;

@RunWith(SpringRunner.class)
@DataJpaTest
class TransactionServiceImplTest {

    @Autowired
    private RepositoryCard         repositoryCard;
    @Autowired
    private RepositoryTransaction  repositoryTransaction;
    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;
    @InjectMocks
    private IssuingServiceImpl     issuingServiceImpl;
    private RequestTransaction     requestTransaction;
    private RequestTransaction     requestAnulationTransaction;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        this.transactionServiceImpl = new TransactionServiceImpl(this.repositoryCard, this.repositoryTransaction);
        this.issuingServiceImpl = new IssuingServiceImpl(this.repositoryCard);
    }

    private void activeCardAndLoadBalance(Card card) {
        this.issuingServiceImpl.processActivateCard(card.getCardId());
        this.issuingServiceImpl.processLoadBalance(card.getCardId(), 10000);
        this.requestTransaction = new RequestTransaction();
        this.requestTransaction.setCardId(card.getCardId());
        this.requestTransaction.setPrice(3000);
        this.requestTransaction.setCurrencyCode("840");
    }

    private void setRequestAnulationTransaction() {
        Card card = this.issuingServiceImpl.processGenerateCard(999999).getBody();
        this.activeCardAndLoadBalance(card);
        ResponseEntity<Transaction> response = this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        this.requestAnulationTransaction = new RequestTransaction();
        this.requestAnulationTransaction.setCardId(card.getCardId());
        this.requestAnulationTransaction.setTransactionId(response.getBody().getTransactionId());
    }

    @AfterEach
    void tearDown() throws Exception {
        this.transactionServiceImpl = null;
        this.issuingServiceImpl = null;
        this.requestTransaction = null;
    }

    @Test
    void processPurchaseTransactionCorrectly() {
        this.activeCardAndLoadBalance(this.issuingServiceImpl.processGenerateCard(999999).getBody());
        ResponseEntity<Transaction> response = this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void processPurchaseTransactionWithCardIncorrect() {
        this.activeCardAndLoadBalance(this.issuingServiceImpl.processGenerateCard(999999).getBody());
        this.requestTransaction.setCardId("9999990000000000");
        Assertions.assertThrows(ResourceNotFound.class, () -> {
            this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        });
    }

    @Test
    void processPurchaseTransactionWithCurrencyCodeIncorrect() {
        this.activeCardAndLoadBalance(this.issuingServiceImpl.processGenerateCard(999999).getBody());
        this.requestTransaction.setCurrencyCode("170");
        ResponseEntity<Transaction> response = this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        Transaction transaction = response.getBody();
        Assertions.assertEquals(StateTransaction.REJECTED.getValue(), transaction.getState());
        Assertions.assertEquals(AdditionalRspData.INVALID_CURRENCY.getValue(), transaction.getRspData());
    }

    @Test
    void processPurchaseTransactionWithCardInactive() {
        Card card = this.issuingServiceImpl.processGenerateCard(999999).getBody();
        this.issuingServiceImpl.processLoadBalance(card.getCardId(), 10000);
        this.requestTransaction = new RequestTransaction();
        this.requestTransaction.setCardId(card.getCardId());
        this.requestTransaction.setPrice(3000);
        this.requestTransaction.setCurrencyCode("840");
        ResponseEntity<Transaction> response = this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        Transaction transaction = response.getBody();
        Assertions.assertEquals(StateTransaction.REJECTED.getValue(), transaction.getState());
        Assertions.assertEquals(AdditionalRspData.INACTIVE_CARD.getValue(), transaction.getRspData());
    }

    @Test
    void processPurchaseTransactionWithInsufficientFounds() {
        this.activeCardAndLoadBalance(this.issuingServiceImpl.processGenerateCard(999999).getBody());
        this.requestTransaction.setPrice(20000);
        ResponseEntity<Transaction> response = this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        Transaction transaction = response.getBody();
        Assertions.assertEquals(StateTransaction.REJECTED.getValue(), transaction.getState());
        Assertions.assertEquals(AdditionalRspData.INSUFFICIENT_FUNDS.getValue(), transaction.getRspData());
    }

    @Test
    void processInquiryTransactionCorrectly() {
        this.activeCardAndLoadBalance(this.issuingServiceImpl.processGenerateCard(999999).getBody());
        ResponseEntity<Transaction> response = this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        Assertions.assertEquals(HttpStatus.OK,
            this.transactionServiceImpl.processInquiryTransaction(response.getBody().getTransactionId()).getStatusCode());
    }

    @Test
    void processInquiryTransactionWithTransactionIdIncorrect() {
        this.activeCardAndLoadBalance(this.issuingServiceImpl.processGenerateCard(999999).getBody());
        this.transactionServiceImpl.processPurchaseTransaction(this.requestTransaction);
        Assertions.assertThrows(ResourceNotFound.class, () -> {
            this.transactionServiceImpl.processInquiryTransaction(123456);
        });
    }

    @Test
    void processAnulationTransactionCorrectly() {
        this.setRequestAnulationTransaction();
        Assertions.assertEquals(HttpStatus.OK,
            this.transactionServiceImpl.processAnulationTransaction(this.requestAnulationTransaction).getStatusCode());
    }

    @Test
    void processAnulationTransactionWithCardIncorrect() {
        this.setRequestAnulationTransaction();
        this.requestAnulationTransaction.setCardId("9999990000000000");
        Assertions.assertThrows(ResourceNotFound.class, () -> {
            this.transactionServiceImpl.processAnulationTransaction(this.requestAnulationTransaction);
        });
    }

    @Test
    void processAnulationTransactionWithTransactionIdIncorrect() {
        this.setRequestAnulationTransaction();
        this.requestAnulationTransaction.setTransactionId(123456);
        Assertions.assertThrows(ResourceNotFound.class, () -> {
            this.transactionServiceImpl.processAnulationTransaction(this.requestAnulationTransaction);
        });
    }

    @Test
    void processAnulationTransactionWithTransactionIdNotFromCard() {
        this.setRequestAnulationTransaction();
        this.requestAnulationTransaction.setTransactionId(123456);
        Assertions.assertThrows(ResourceNotFound.class, () -> {
            this.transactionServiceImpl.processAnulationTransaction(this.requestAnulationTransaction);
        });
    }

}
