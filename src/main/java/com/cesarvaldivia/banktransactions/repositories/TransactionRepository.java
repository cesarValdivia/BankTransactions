package com.cesarvaldivia.banktransactions.repositories;

import com.cesarvaldivia.banktransactions.models.Transaction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Integer>, CrudRepository<Transaction, Integer> {

    Iterable<Transaction> findByDateBetween(LocalDate start, LocalDate end, PageRequest sortedByPriceDesc);
}
