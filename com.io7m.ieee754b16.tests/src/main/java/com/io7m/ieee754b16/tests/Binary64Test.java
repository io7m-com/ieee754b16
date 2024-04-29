/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.ieee754b16.tests;

import com.io7m.ieee754b16.Binary64;
import com.io7m.junreachable.UnreachableCodeException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Tests for Binary64.
 */

public final class Binary64Test
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  @Test
  public void testInfinityExponent()
  {
    Assert.assertEquals(
      1024L,
      Binary64.unpackGetExponentUnbiased(Double.POSITIVE_INFINITY));
  }

  @Test
  public void testInfinityNegativeExponent()
  {
    Assert.assertEquals(
      1024L,
      Binary64.unpackGetExponentUnbiased(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void testInfinityNegativeSign()
  {
    Assert.assertEquals(1L, Binary64.unpackGetSign(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void testInfinityNegativeSignificand()
  {
    Assert.assertEquals(
      0L,
      Binary64.unpackGetSignificand(Double.NEGATIVE_INFINITY));
  }

  @Test
  public void testInfinitySign()
  {
    Assert.assertEquals(0L, Binary64.unpackGetSign(Double.POSITIVE_INFINITY));
  }

  @Test
  public void testInfinitySignificand()
  {
    Assert.assertEquals(
      0L,
      Binary64.unpackGetSignificand(Double.POSITIVE_INFINITY));
  }

  @Test
  public void testNaNExponent()
  {
    Assert.assertEquals(1024L, Binary64.unpackGetExponentUnbiased(Double.NaN));
  }

  @Test
  public void testNaNSignificand()
  {
    Assert.assertTrue(Binary64.unpackGetSignificand(Double.NaN) > 0L);
  }

  /**
   * The constructor is unreachable.
   */

  @Test
  public void testUnreachable()
    throws Exception
  {
    final Constructor<Binary64> c = Binary64.class.getDeclaredConstructor();
    c.setAccessible(true);

    this.expected.expect(InvocationTargetException.class);
    this.expected.expectCause(Is.isA(UnreachableCodeException.class));
    c.newInstance();
  }
}
