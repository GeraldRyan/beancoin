package com.ryan.gerald.beancoin.repository;

import com.ryan.gerald.beancoin.entity.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, String> {

    @Query("select t from Transaction t")
    List<Transaction> getUnminedTransactionList();

    /**
     * IMPLEMENT ME - check syntax and arrange by oldest so add timestamp
     */
    @Query(value = "select t from Transaction t LIMIT ?1", nativeQuery = true)
    List<Transaction> getNOldestTransactions();

    @Modifying
    @Query("DELETE from Transaction t where t.uuid = :uuid")
    void delete(String uuid);

}