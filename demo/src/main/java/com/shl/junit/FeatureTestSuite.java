package com.shl.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
 * 通过Suite注解包装，同时运行多个Test Class
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  AssertTests.class
})

public class FeatureTestSuite {
  
}
