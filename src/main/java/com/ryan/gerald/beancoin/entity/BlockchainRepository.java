package com.ryan.gerald.beancoin.entity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BlockchainRepository extends CrudRepository<Blockchain, Integer> {

    @Query("SELECT b from Blockchain b where b.name = :name")
    Blockchain getBlockchainByName(String name);

}
