package com.ryan.gerald.beancoin.tutorials.datamodelworkingexample;

import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserExampleRepository extends CrudRepository<UserExample, Integer> {

}