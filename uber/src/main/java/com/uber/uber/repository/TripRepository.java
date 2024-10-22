package com.uber.uber.repository;

import com.uber.uber.model.TripSchema;
import com.uber.uber.model.UserSchema;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TripRepository extends MongoRepository<TripSchema, ObjectId> {


}
