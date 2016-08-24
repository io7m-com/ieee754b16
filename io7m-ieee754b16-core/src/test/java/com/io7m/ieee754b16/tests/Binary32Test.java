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

import com.io7m.ieee754b16.Binary32;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for Binary32.
 */

public final class Binary32Test
{
  @Test
  public void testInfinityExponent()
  {
    Assert.assertEquals(
      128L,
      (long) Binary32.unpackGetExponentUnbiased(Float.POSITIVE_INFINITY));
  }

  @Test
  public void testInfinityNegativeExponent()
  {
    Assert.assertEquals(
      128L,
      (long) Binary32.unpackGetExponentUnbiased(Float.NEGATIVE_INFINITY));
  }

  @Test
  public void testInfinityNegativeSign()
  {
    Assert.assertEquals(
      1L,
      (long) Binary32.unpackGetSign(Float.NEGATIVE_INFINITY));
  }

  @Test
  public void testInfinityNegativeSignificand()
  {
    Assert.assertEquals(
      0L,
      (long) Binary32.unpackGetSignificand(Float.NEGATIVE_INFINITY));
  }

  @Test
  public void testInfinitySign()
  {
    Assert.assertEquals(
      0L,
      (long) Binary32.unpackGetSign(Float.POSITIVE_INFINITY));
  }

  @Test
  public void testInfinitySignificand()
  {
    Assert.assertEquals(
      0L,
      (long) Binary32.unpackGetSignificand(Float.POSITIVE_INFINITY));
  }

  @Test
  public void testNaNExponent()
  {
    Assert.assertEquals(
      128L,
      (long) Binary32.unpackGetExponentUnbiased(Float.NaN));
  }

  @Test
  public void testNaNSignificand()
  {
    Assert.assertTrue(Binary32.unpackGetSignificand(Float.NaN) > 0);
  }
}
