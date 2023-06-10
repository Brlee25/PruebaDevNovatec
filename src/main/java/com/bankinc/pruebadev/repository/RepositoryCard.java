package com.bankinc.pruebadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bankinc.pruebadev.model.Card;

public interface RepositoryCard extends JpaRepository<Card, String> {

}
