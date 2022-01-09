package com.ryan.gerald.beancoin.repository;

import com.ryan.gerald.beancoin.entity.Block;
import org.springframework.data.repository.CrudRepository;

public interface BlockRepository extends CrudRepository<Block, Long> {
}
