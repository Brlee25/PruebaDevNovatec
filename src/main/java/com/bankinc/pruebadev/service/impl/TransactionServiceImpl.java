package com.bankinc.pruebadev.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.bankinc.pruebadev.exception.ResourceNotFound;
import com.bankinc.pruebadev.model.Card;
import com.bankinc.pruebadev.model.RequestTransaction;
import com.bankinc.pruebadev.model.Transaction;
import com.bankinc.pruebadev.repository.RepositoryCard;
import com.bankinc.pruebadev.repository.RepositoryTransaction;
import com.bankinc.pruebadev.service.TransactionService;
import com.bankinc.pruebadev.util.AdditionalRspData;
import com.bankinc.pruebadev.util.StateTransaction;
import com.bankinc.pruebadev.util.TransactionType;
import com.bankinc.pruebadev.util.Utils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private final RepositoryCard        repositoryCard;
    @Autowired
    private final RepositoryTransaction repositoryTransaction;

    @Override
    public ResponseEntity<Transaction> processAnulationTransaction(RequestTransaction reqTransaction) {
        Card card = this.repositoryCard.findById(reqTransaction.getCardId()).orElseThrow(() -> {
            throw (new ResourceNotFound());
        });
        Transaction originalTransaction = this.repositoryTransaction.findById(reqTransaction.getTransactionId()).orElseThrow(
            () -> {
                throw (new ResourceNotFound());
            });
        Transaction transaction = this.getTransactionFromRequest(reqTransaction);
        if (!this.isTransactionFromCard(card, reqTransaction.getTransactionId())) {
            transaction.setState(StateTransaction.REJECTED.getValue());
            transaction.setRspData(AdditionalRspData.TRANSACTION_NOT_FOUND.getValue());
            return (new ResponseEntity<>(transaction, HttpStatus.BAD_REQUEST));
        }
        transaction.setTransactionType(TransactionType.ANULATION.getValue());
        transaction.setCurrencyCode(originalTransaction.getCurrencyCode());
        transaction.setAmount(originalTransaction.getAmount());
        if (!originalTransaction.getDateTime().after(Utils.getFormatedDate(0, -24))) {
            transaction.setState(StateTransaction.REJECTED.getValue());
            transaction.setRspData(AdditionalRspData.EXPIRED_TIME.getValue());
            this.SetNewTransactionInCard(card, transaction);
            return (new ResponseEntity<>(this.saveTransactionInBd(transaction, card), HttpStatus.OK));
        }
        originalTransaction.setState(StateTransaction.ANNULLED.getValue());
        originalTransaction.setAnnulledBy(transaction.getTransactionId());
        this.repositoryTransaction.save(originalTransaction);
        transaction.setState(StateTransaction.APPROVED.getValue());
        transaction.setRspData(AdditionalRspData.SUCCESFUL_TRANSACTION.getValue());
        card.setBalance(card.getBalance() + originalTransaction.getAmount());
        this.SetNewTransactionInCard(card, transaction);
        return (new ResponseEntity<>(this.saveTransactionInBd(transaction, card), HttpStatus.OK));
    }

    private boolean isTransactionFromCard(Card card, int transactionId) {
        for (Transaction transaction : card.getTransactions()) {
            if (transaction.getTransactionId() == transactionId) {
                return (true);
            }
        }
        return (false);
    }

    @Override
    public ResponseEntity<Transaction> processInquiryTransaction(int transactionId) {
        return (new ResponseEntity<>(this.repositoryTransaction.findById(transactionId).orElseThrow(() -> {
            throw (new ResourceNotFound());
        }), HttpStatus.OK));
    }

    @Override
    public ResponseEntity<Transaction> processPurchaseTransaction(RequestTransaction reqTransaction) {
        Card card = this.repositoryCard.findById(reqTransaction.getCardId()).orElseThrow(() -> {
            throw (new ResourceNotFound());
        });
        Transaction transaction = this.getTransactionFromRequest(reqTransaction);
        transaction.setTransactionType(TransactionType.PURCHASE.getValue());
        if (!reqTransaction.getCurrencyCode().equals(card.getCurrencyCode())) {
            transaction.setState(StateTransaction.REJECTED.getValue());
            transaction.setRspData(AdditionalRspData.INVALID_CURRENCY.getValue());
            this.SetNewTransactionInCard(card, transaction);
            return (new ResponseEntity<>(this.saveTransactionInBd(transaction, card), HttpStatus.OK));
        }
        if (card.getBalance() < transaction.getAmount()) {
            transaction.setState(StateTransaction.REJECTED.getValue());
            transaction.setRspData(AdditionalRspData.INSUFFICIENT_FUNDS.getValue());
            this.SetNewTransactionInCard(card, transaction);
            return (new ResponseEntity<>(this.saveTransactionInBd(transaction, card), HttpStatus.OK));
        }
        if (transaction.getDateTime().after(card.getExpirationDate())) {
            transaction.setState(StateTransaction.REJECTED.getValue());
            transaction.setRspData(AdditionalRspData.EXPIRED_CARD.getValue());
            this.SetNewTransactionInCard(card, transaction);
            return (new ResponseEntity<>(this.saveTransactionInBd(transaction, card), HttpStatus.OK));
        }
        if (!card.isActive()) {
            transaction.setState(StateTransaction.REJECTED.getValue());
            transaction.setRspData(AdditionalRspData.INACTIVE_CARD.getValue());
            this.SetNewTransactionInCard(card, transaction);
            return (new ResponseEntity<>(this.saveTransactionInBd(transaction, card), HttpStatus.OK));
        }
        card.setBalance(card.getBalance() - transaction.getAmount());
        transaction.setState(StateTransaction.APPROVED.getValue());
        transaction.setRspData(AdditionalRspData.SUCCESFUL_TRANSACTION.getValue());
        this.SetNewTransactionInCard(card, transaction);
        return (new ResponseEntity<>(this.saveTransactionInBd(transaction, card), HttpStatus.OK));
    }

    private Transaction getTransactionFromRequest(RequestTransaction reqTransaction) {
        Transaction transaction = new Transaction();
        transaction.setAmount(reqTransaction.getPrice());
        transaction.setCurrencyCode(reqTransaction.getCurrencyCode());
        transaction.setDateTime(Utils.getFormatedDate());
        transaction.setTransactionId(Integer.parseInt(Utils.generateRandomNum(6)));
        transaction.setOriginalTransactionId(reqTransaction.getTransactionId());
        return (transaction);
    }

    private Transaction saveTransactionInBd(Transaction transaction, Card card) {
        Transaction transactionSaved = this.repositoryTransaction.save(transaction);
        this.repositoryCard.save(card);
        return (transactionSaved);
    }

    private void SetNewTransactionInCard(Card card, Transaction newTransaction) {
        int length = card.getTransactions() == null ? 0 : card.getTransactions().length;
        Transaction[] newTransactions = new Transaction[length + 1];
        for (int i = 0; i < length; i++) {
            newTransactions[i] = card.getTransactions()[i];
        }
        newTransactions[newTransactions.length - 1] = newTransaction;
        card.setTransactions(newTransactions);
    }

}
