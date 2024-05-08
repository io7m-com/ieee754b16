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

import com.io7m.ieee754b16.Binary16;
import com.io7m.junreachable.UnreachableCodeException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Tests for Binary16.
 */

public final class Binary16Test
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  /**
   * Exponents in the range [-15, 16] are encoded and decoded correctly.
   */

  @Test
  public void testExponentIdentity()
  {
    System.out.println("-- Exponent identities");
    for (int e = -15; e <= 16; ++e) {
      final char p = Binary16.packSetExponentUnbiasedUnchecked(e);
      final int u = Binary16.unpackGetExponentUnbiased(p);
      System.out.println("e: " + e);
      System.out.println("p: " + Integer.toHexString((int) p));
      System.out.println("u: " + u);
      Assert.assertEquals((long) e, (long) u);
    }
  }

  /**
   * Infinities are infinite.
   */

  @Test
  public void testInfinite()
  {
    Assert.assertTrue(Binary16.isInfinite(Binary16.POSITIVE_INFINITY));
    Assert.assertTrue(Binary16.isInfinite(Binary16.NEGATIVE_INFINITY));
    Assert.assertFalse(Binary16.isInfinite(Binary16.exampleNaN()));

    for (int i = 0; i <= 65535; ++i) {
      Assert.assertFalse(Binary16.isInfinite(Binary16.packDouble((double) i)));
    }
  }

  /**
   * The unencoded exponent of infinity is 16.
   */

  @Test
  public void testInfinityExponent()
  {
    Assert.assertEquals(
      16L,
      (long) Binary16.unpackGetExponentUnbiased(Binary16.POSITIVE_INFINITY));
  }

  /**
   * The unencoded exponent of infinity is 16.
   */

  @Test
  public void
  testInfinityNegativeExponent()
  {
    Assert.assertEquals(
      16L,
      (long) Binary16.unpackGetExponentUnbiased(Binary16.NEGATIVE_INFINITY));
  }

  /**
   * The sign of negative infinity is 1.
   */

  @Test
  public void
  testInfinityNegativeSign()
  {
    Assert
      .assertEquals(
        1L,
        (long) Binary16.unpackGetSign(Binary16.NEGATIVE_INFINITY));
  }

  /**
   * The significand of infinity is 0.
   */

  @Test
  public void
  testInfinityNegativeSignificand()
  {
    Assert.assertEquals(
      0L,
      (long) Binary16.unpackGetSignificand(Binary16.NEGATIVE_INFINITY));
  }

  /**
   * The sign of positive infinity is 0.
   */

  @Test
  public void testInfinitySign()
  {
    Assert
      .assertEquals(
        0L,
        (long) Binary16.unpackGetSign(Binary16.POSITIVE_INFINITY));
  }

  /**
   * The significand of infinity is 0.
   */

  @Test
  public void
  testInfinitySignificand()
  {
    Assert.assertEquals(
      0L,
      (long) Binary16.unpackGetSignificand(Binary16.POSITIVE_INFINITY));
  }

  /**
   * NaN is NaN.
   */

  @Test
  public void testNaN()
  {
    final int n =
      (int) Binary16.packSetExponentUnbiasedUnchecked(16)
        | (int) Binary16.packSetSignificandUnchecked(1);
    final char c = (char) n;
    Assert.assertEquals(16L, (long) Binary16.unpackGetExponentUnbiased(c));
    Assert.assertEquals(1L, (long) Binary16.unpackGetSignificand(c));
    Assert.assertEquals(
      16L,
      (long) Binary16.unpackGetExponentUnbiased(Binary16.exampleNaN()));
    Assert.assertEquals(
      1L,
      (long) Binary16.unpackGetSignificand(Binary16.exampleNaN()));
    Assert.assertTrue(Binary16.isNaN(c));
    Assert.assertTrue(Binary16.isNaN(Binary16.exampleNaN()));
  }

  /**
   * Packing NaN results in NaN.
   */

  @Test
  public void testPackDoubleNaN()
  {
    final double k = Double.NaN;
    final char r = Binary16.packDouble(k);
    Assert.assertTrue(Binary16.isNaN(r));
  }

  /**
   * Packing negative infinity results in negative infinity.
   */

  @Test
  public void
  testPackDoubleNegativeInfinity()
  {
    Assert.assertTrue((int) Binary16.NEGATIVE_INFINITY == (int) Binary16
      .packDouble(Double.NEGATIVE_INFINITY));
  }

  /**
   * Packing negative zero results in negative zero.
   */

  @Test
  public void
  testPackDoubleNegativeZero()
  {
    Assert.assertTrue((int) Binary16.NEGATIVE_ZERO == (int) Binary16.packDouble(
      -0.0));
  }

  /**
   * Packing positive infinity results in positive infinity.
   */

  @Test
  public void
  testPackDoublePositiveInfinity()
  {
    Assert.assertTrue((int) Binary16.POSITIVE_INFINITY == (int) Binary16
      .packDouble(Double.POSITIVE_INFINITY));
  }

  /**
   * Packing positive zero results in positive zero.
   */

  @Test
  public void
  testPackDoublePositiveZero()
  {
    Assert.assertTrue((int) Binary16.POSITIVE_ZERO == (int) Binary16.packDouble(
      0.0));
  }

  /**
   * Integers in the range [0, 65520] should be representable.
   */

  @Test
  public void
  testPackDoubleUnpackFloat()
  {
    for (int i = 0; i <= 65536; ++i) {
      final double in = (double) i;
      final char packed = Binary16.packDouble(in);
      final float r = Binary16.unpackFloat(packed);

      double delta = 0.001;
      if (i >= 15) {
        delta = 0.01;
      }
      if (i >= 127) {
        delta = 0.1;
      }
      if (i >= 1023) {
        delta = 0.2;
      }
      if (i >= 2047) {
        delta = 1.0;
      }
      if (i >= 4095) {
        delta = 2.0;
      }
      if (i >= 8190) {
        delta = 4.0;
      }
      if (i >= 16380) {
        delta = 8.0;
      }
      if (i >= 32760) {
        delta = 16.0;
      }
      if (i >= 65520) {
        delta = 32.0;
      }

      System.out.println(String.format(
        "packed: 0x%04x 0b%s in: %f unpacked: %f diff: %f delta: %f",
        Integer.valueOf((int) packed),
        Binary16.toRawBinaryString(packed),
        Double.valueOf(in),
        Double.valueOf(r),
        Math.abs(in - r),
        delta));

      if (i < 65536) {
        Assert.assertEquals(in, r, delta);
      }

      if (i == 65536) {
        Assert.assertTrue(Double.isInfinite(r));
      }
    }
  }

  /**
   * Integers in the range [0, 65520] should be representable.
   */

  @Test
  public void
  testPackFloatDoubleEquivalent()
  {
    for (int i = 0; i <= 65536; ++i) {
      final float f_in = (float) i;
      final double d_in = (double) i;
      final char pf = Binary16.packFloat(f_in);
      final char pd = Binary16.packDouble(d_in);

      System.out.println("i: " + i);
      System.out.println(String.format(
        "pack_f: 0x%04x 0b%s",
        Integer.valueOf((int) pf),
        Binary16.toRawBinaryString(pf)));
      System.out.println(String.format(
        "pack_d: 0x%04x 0b%s",
        Integer.valueOf((int) pd),
        Binary16.toRawBinaryString(pd)));

      Assert.assertEquals((long) pf, (long) pd);
    }
  }

  /**
   * Packing NaN results in NaN.
   */

  @Test
  public void testPackFloatNaN()
  {
    final float k = Float.NaN;
    final char r = Binary16.packFloat(k);
    Assert.assertTrue(Binary16.isNaN(r));
  }

  /**
   * Packing negative infinity results in negative infinity.
   */

  @Test
  public void
  testPackFloatNegativeInfinity()
  {
    Assert.assertTrue((int) Binary16.NEGATIVE_INFINITY == (int) Binary16
      .packFloat(Float.NEGATIVE_INFINITY));
  }

  /**
   * Packing negative zero results in negative zero.
   */

  @Test
  public void
  testPackFloatNegativeZero()
  {
    Assert.assertTrue((int) Binary16.NEGATIVE_ZERO == (int) Binary16.packFloat(-0.0f));
  }

  /**
   * Packing positive infinity results in positive infinity.
   */

  @Test
  public void
  testPackFloatPositiveInfinity()
  {
    Assert.assertTrue((int) Binary16.POSITIVE_INFINITY == (int) Binary16
      .packFloat(Float.POSITIVE_INFINITY));
  }

  /**
   * Packing positive zero results in positive zero.
   */

  @Test
  public void
  testPackFloatPositiveZero()
  {
    Assert.assertTrue((int) Binary16.POSITIVE_ZERO == (int) Binary16.packFloat(
      0.0f));
  }

  /**
   * Integers in the range [0, 65520] should be representable.
   */

  @Test
  public void
  testPackFloatUnpackDouble()
  {
    for (int i = 0; i <= 65536; ++i) {
      final float in = (float) i;
      final char packed = Binary16.packFloat(in);
      final double r = Binary16.unpackDouble(packed);

      double delta = 0.001;
      if (i >= 15) {
        delta = 0.01;
      }
      if (i >= 127) {
        delta = 0.1;
      }
      if (i >= 1023) {
        delta = 0.2;
      }
      if (i >= 2047) {
        delta = 1.0;
      }
      if (i >= 4095) {
        delta = 2.0;
      }
      if (i >= 8190) {
        delta = 4.0;
      }
      if (i >= 16380) {
        delta = 8.0;
      }
      if (i >= 32760) {
        delta = 16.0;
      }
      if (i >= 65520) {
        delta = 32.0;
      }

      System.out.println(String.format(
        "packed: 0x%04x 0b%s in: %f unpacked: %f diff: %f delta: %f",
        Integer.valueOf((int) packed),
        Binary16.toRawBinaryString(packed),
        Double.valueOf(in),
        Double.valueOf(r),
        Math.abs(in - r),
        delta));

      if (i < 65536) {
        Assert.assertEquals(in, r, delta);
      }

      if (i == 65536) {
        Assert.assertTrue(Double.isInfinite(r));
      }
    }
  }

  /**
   * Integers in the range [1, 65520] should be representable.
   */

  @Test
  public void
  testPackUnpackDouble()
  {
    for (int i = 1; i <= 65536; ++i) {
      final double in = (double) i;
      final char packed = Binary16.packDouble(in);
      final double r = Binary16.unpackDouble(packed);

      double delta = 0.001;
      if (i >= 15) {
        delta = 0.01;
      }
      if (i >= 127) {
        delta = 0.1;
      }
      if (i >= 1023) {
        delta = 0.2;
      }
      if (i >= 2047) {
        delta = 1.0;
      }
      if (i >= 4095) {
        delta = 2.0;
      }
      if (i >= 8190) {
        delta = 4.0;
      }
      if (i >= 16380) {
        delta = 8.0;
      }
      if (i >= 32760) {
        delta = 16.0;
      }
      if (i >= 65520) {
        delta = 32.0;
      }

      System.out.println(String.format(
        "packed: 0x%04x 0b%s in: %f unpacked: %f diff: %f delta: %f",
        Integer.valueOf((int) packed),
        Binary16.toRawBinaryString(packed),
        Double.valueOf(in),
        Double.valueOf(r),
        Math.abs(in - r),
        delta));

      if (i < 65536) {
        Assert.assertEquals(in, r, delta);
      }

      if (i == 65536) {
        Assert.assertTrue(Double.isInfinite(r));
      }
    }
  }

  /**
   * Show that packing/unpacking is an identity operation for all packed values.
   */

  @Test
  public void
  testPackUnpackCompleteDouble()
  {
    for (char index = (char) 0; index < 65535; ++index) {
      final double r0 = Binary16.unpackDouble(index);
      final char p0 = Binary16.packDouble(r0);
      final double r1 = Binary16.unpackDouble(p0);

      System.out.println(String.format(
        "index: %04x unpack0: %.8f packed: %04x unpack1: %.8f diff: %.8f",
        (int) index,
        r0,
        (int) p0,
        r1,
        Math.abs(r1 - r0)));

      Assert.assertEquals(r0, r1, 0.0);
    }
  }

  /**
   * Integers in the range [0, 65520] should be representable.
   */

  @Test
  public void
  testPackUnpackFloat()
  {
    for (int i = 0; i <= 65536; ++i) {
      final float in = (float) i;
      final char packed = Binary16.packFloat(in);
      final float r = Binary16.unpackFloat(packed);

      double delta = 0.001;
      if (i >= 15) {
        delta = 0.01;
      }
      if (i >= 127) {
        delta = 0.1;
      }
      if (i >= 1023) {
        delta = 0.2;
      }
      if (i >= 2047) {
        delta = 1.0;
      }
      if (i >= 4095) {
        delta = 2.0;
      }
      if (i >= 8190) {
        delta = 4.0;
      }
      if (i >= 16380) {
        delta = 8.0;
      }
      if (i >= 32760) {
        delta = 16.0;
      }
      if (i >= 65520) {
        delta = 32.0;
      }

      System.out.println(String.format(
        "packed: 0x%04x 0b%s in: %f unpacked: %f diff: %f delta: %f",
        Integer.valueOf((int) packed),
        Binary16.toRawBinaryString(packed),
        Double.valueOf(in),
        Double.valueOf(r),
        Math.abs(in - r),
        delta));

      if (i < 65536) {
        Assert.assertEquals(in, r, delta);
      }

      if (i == 65536) {
        Assert.assertTrue(Double.isInfinite(r));
      }
    }
  }

  /**
   * Signs in the range [0, 1] are encoded and decoded correctly.
   */

  @Test
  public void testSignIdentity()
  {
    System.out.println("-- Sign identities");
    for (int e = 0; e <= 1; ++e) {
      final char p = Binary16.packSetSignUnchecked(e);
      final int u = Binary16.unpackGetSign(p);
      System.out.println("e: " + e);
      System.out.println("p: " + Integer.toHexString((int) p));
      System.out.println("u: " + u);
      Assert.assertEquals((long) e, (long) u);
    }
  }

  /**
   * Significands in the range [0, 1023] are encoded and decoded correctly.
   */

  @Test
  public void
  testSignificandIdentity()
  {
    System.out.println("-- Significand identities");
    for (int e = 0; e <= 1023; ++e) {
      final char p = Binary16.packSetSignificandUnchecked(e);
      final int u = Binary16.unpackGetSignificand(p);
      System.out.println("e: " + e);
      System.out.println("p: " + Integer.toHexString((int) p));
      System.out.println("u: " + u);
      Assert.assertEquals((long) e, (long) u);
    }
  }

  /**
   * Unpacking NaN results in NaN.
   */

  @Test
  public void testUnpackDoubleNaN()
  {
    final double k = Binary16.unpackDouble(Binary16.exampleNaN());
    Assert.assertTrue(Double.isNaN(k));
  }

  /**
   * Unpacking negative infinity results in negative infinity.
   */

  @Test
  public void
  testUnpackDoubleNegativeInfinity()
  {
    Assert.assertTrue(Double.NEGATIVE_INFINITY == Binary16
      .unpackDouble(Binary16.NEGATIVE_INFINITY));
  }

  /**
   * Unpacking negative zero results in negative zero.
   */

  @Test
  public void
  testUnpackDoubleNegativeZero()
  {
    Assert.assertTrue(-0.0 == Binary16.unpackDouble(Binary16.NEGATIVE_ZERO));
  }

  /**
   * Unpacking 1.0 results in 1.0.
   */

  @Test
  public void
  testUnpackDoubleOne()
  {
    final char one = (char) 0x3C00;
    final double r = Binary16.unpackDouble(one);
    System.out.println(String.format("0x%04x -> %f", Integer.valueOf((int) one),
                                     Double.valueOf(r)));
    Assert.assertEquals(r, 1.0, 0.001);
  }

  /**
   * Unpacking -1.0 results in -1.0.
   */

  @Test
  public void
  testUnpackDoubleOneNegative()
  {
    final char one = (char) 0xBC00;
    final double r = Binary16.unpackDouble(one);
    System.out.println(String.format("0x%04x -> %f", Integer.valueOf((int) one),
                                     Double.valueOf(r)));
    Assert.assertEquals(r, -1.0, 0.001);
  }

  /**
   * Unpacking positive infinity results in positive infinity.
   */

  @Test
  public void
  testUnpackDoublePositiveInfinity()
  {
    Assert.assertTrue(
      Double.POSITIVE_INFINITY == Binary16.unpackDouble(Binary16.POSITIVE_INFINITY));
  }

  /**
   * Unpacking positive zero results in positive zero.
   */

  @Test
  public void
  testUnpackDoublePositiveZero()
  {
    Assert.assertTrue(
      0.0 == Binary16.unpackDouble(Binary16.POSITIVE_ZERO));
  }

  /**
   * Unpacking 2.0 results in 2.0.
   */

  @Test
  public void
  testUnpackDoubleTwo()
  {
    final char one = (char) 0x4000;
    final double r = Binary16.unpackDouble(one);
    System.out.println(String.format(
      "%04x -> %f",
      Integer.valueOf((int) one),
      Double.valueOf(r)));
    Assert.assertEquals(r, 2.0, 0.001);
  }

  /**
   * Unpacking -2.0 results in -2.0.
   */

  @Test
  public void testUnpackDoubleTwoNegative()
  {
    final char one = (char) 0xC000;
    final double r = Binary16.unpackDouble(one);
    System.out.println(String.format(
      "%04x -> %f",
      Integer.valueOf((int) one),
      Double.valueOf(r)));
    Assert.assertEquals(r, -2.0, 0.001);
  }

  /**
   * Unpacking NaN results in NaN.
   */

  @Test
  public void testUnpackFloatNaN()
  {
    final float k = Binary16.unpackFloat(Binary16.exampleNaN());
    Assert.assertTrue(Float.isNaN(k));
  }

  /**
   * Unpacking negative infinity results in negative infinity.
   */

  @Test
  public void testUnpackFloatNegativeInfinity()
  {
    Assert.assertTrue(Float.NEGATIVE_INFINITY == Binary16
      .unpackFloat(Binary16.NEGATIVE_INFINITY));
  }

  /**
   * Unpacking negative zero results in negative zero.
   */

  @Test
  public void testUnpackFloatNegativeZero()
  {
    Assert.assertTrue(-0.0 == (double) Binary16.unpackFloat(Binary16.NEGATIVE_ZERO));
  }

  /**
   * Unpacking 1.0 results in 1.0.
   */

  @Test
  public void testUnpackFloatOne()
  {
    final char one = (char) 0x3C00;
    final float r = Binary16.unpackFloat(one);
    System.out.println(String.format("0x%04x -> %f", Integer.valueOf((int) one),
                                     Float.valueOf(r)));
    Assert.assertEquals((double) r, 1.0, 0.001);
  }

  /**
   * Unpacking -1.0 results in -1.0.
   */

  @Test
  public void testUnpackFloatOneNegative()
  {
    final char one = (char) 0xBC00;
    final float r = Binary16.unpackFloat(one);
    System.out.println(String.format("0x%04x -> %f", Integer.valueOf((int) one),
                                     Float.valueOf(r)));
    Assert.assertEquals((double) r, -1.0, 0.001);
  }

  /**
   * The constructor is unreachable.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUnreachable()
    throws Exception
  {
    final Constructor<Binary16> c = Binary16.class.getDeclaredConstructor();
    c.setAccessible(true);

    this.expected.expect(InvocationTargetException.class);
    this.expected.expectCause(Is.isA(UnreachableCodeException.class));
    c.newInstance();
  }
}
