package com.shl.junit.category;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Categories.class)
@IncludeCategory({SlowTests.class,FastTests.class})
//@IncludeCategory(SlowTests.class)
@ExcludeCategory( FastTests.class)
@SuiteClasses( { A.class, B.class,C.class}) // Note that Categories is a kind of Suite
public class SlowTestSuite {
  // Will run A.b and B.c, but not A.a
}
