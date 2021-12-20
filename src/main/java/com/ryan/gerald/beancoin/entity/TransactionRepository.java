package com.ryan.gerald.beancoin.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, String> {

    @Query("select t from Transaction t")
    List<Transaction> getListOfTransactions();

    @Modifying
    @Query("DELETE from Transaction t where t.uuid = :uuid")
    void delete(String uuid);

}
