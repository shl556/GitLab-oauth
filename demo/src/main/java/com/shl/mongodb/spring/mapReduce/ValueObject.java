package com.shl.mongodb.spring.mapReduce;



public class ValueObject {

  private String id;
  private String value;

  public String getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "ValueObject [id=" + id + ", value=" + value + "]";
  }
}

