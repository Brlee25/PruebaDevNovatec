package com.bankinc.pruebadev.service;

import org.springframework.http.ResponseEntity;
import com.bankinc.pruebadev.model.RequestTransaction;
import com.bankinc.pruebadev.model.Transaction;

public interface TransactionService {

    ResponseEntity<Transaction> processAnulationTransaction(RequestTransaction reqTransaction);

    ResponseEntity<Transaction> processInquiryTransaction(int transactionId);

    ResponseEntity<Transaction> processPurchaseTransaction(RequestTransaction transaction);
}
