package com.bankinc.pruebadev.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bankinc.pruebadev.model.RequestTransaction;
import com.bankinc.pruebadev.model.Transaction;
import com.bankinc.pruebadev.service.TransactionService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/transaction")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/purchase")
    public ResponseEntity<Transaction> processPurchase(@RequestBody RequestTransaction request) {
        return (this.transactionService.processPurchaseTransaction(request));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> processInquiryTransaction(@PathVariable(name = "transactionId") int transactionId) {
        return (this.transactionService.processInquiryTransaction(transactionId));
    }

    @PostMapping("/anulation")
    public ResponseEntity<Transaction> processAnulationTransaction(@RequestBody RequestTransaction request) {
        return (this.transactionService.processAnulationTransaction(request));
    }
}
