package com.ryan.gerald.beancoin.repository;

import com.ryan.gerald.beancoin.entity.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, String> {
}
