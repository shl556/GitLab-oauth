package com.shl.mongodb.spring;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.shl.mongodb.User;

public interface UserRepository extends QueryByExampleExecutor<User> {

}
