package com.profiling.profiling_project.repository;

import com.profiling.profiling_project.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {}