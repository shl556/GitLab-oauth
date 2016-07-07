package com.shl.mongodb.spring;

import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.WriteConcernResolver;

import com.mongodb.WriteConcern;

/**
 * 可以根据mongo操作的collection、operation类型、java实体类型等确立不同的WriteConcern策略
 * @author Administrator
 *
 */
public class MyAppWriteConcernResolver implements WriteConcernResolver {

  public WriteConcern resolve(MongoAction action) {
    if (action.getEntityType().getSimpleName().contains("Audit")) {
      return WriteConcern.UNACKNOWLEDGED;
    } else if (action.getEntityType().getSimpleName().contains("Metadata")) {
      return WriteConcern.JOURNALED;
    }
    return action.getDefaultWriteConcern();
  }
}


