package com.ryan.gerald.beancoin.repository;

import com.ryan.gerald.beancoin.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, String> {


//    @Query("select u from USER u")
//    List<Transaction> getListOfUsers();
}
