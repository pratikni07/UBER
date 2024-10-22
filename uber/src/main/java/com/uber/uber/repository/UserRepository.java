package com.uber.uber.repository;

import com.uber.uber.model.UserSchema;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserSchema, ObjectId> {
    UserSchema findByEmail(String email);
    UserSchema findByEmailAndPassword(String email, String password);

    Optional<UserSchema> findById(String id);
}