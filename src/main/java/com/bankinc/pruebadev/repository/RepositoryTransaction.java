package com.bankinc.pruebadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bankinc.pruebadev.model.Transaction;

public interface RepositoryTransaction extends JpaRepository<Transaction, Integer> {

}
