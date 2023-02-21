package com.cesarvaldivia.banktransactions.controllers;

import com.cesarvaldivia.banktransactions.models.Transaction;
import com.cesarvaldivia.banktransactions.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping(path = "/transaction")
public class TransactionController {

    @Autowired
    TransactionRepository repository;

    @GetMapping(path = "/list")
    public @ResponseBody Iterable<Transaction> listTransaction(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        PageRequest sortedByPriceDesc = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());
        return repository.findByDateBetween(startDate, endDate, sortedByPriceDesc);
    }
}
