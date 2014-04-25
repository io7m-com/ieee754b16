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

package com.io7m.ieee754b16;

import com.io7m.junreachable.UnreachableCodeException;

/**
 * <p>
 * Utility functions related to the <code>binary64</code> format specified in
 * <code>IEEE 754 2008</code>.
 * </p>
 */

public final class Binary64
{
  static final long BIAS;
  static final long MASK_EXPONENT;
  static final long MASK_SIGN;
  static final long MASK_SIGNIFICAND;
  static final long NEGATIVE_ZERO_BITS;

  static {
    NEGATIVE_ZERO_BITS = 0x8000000000000000L;
    MASK_SIGN = 0x8000000000000000L;
    MASK_EXPONENT = 0x7ff0000000000000L;
    MASK_SIGNIFICAND = 0x000fffffffffffffL;
    BIAS = 1023;
  }

  /**
   * <p>
   * Extract and unbias the exponent of the given packed <code>double</code>
   * value.
   * </p>
   * <p>
   * The exponent is encoded <i>biased</i> as a number in the range
   * <code>[0, 2047]</code>, with <code>0</code> indicating that the number is
   * <i>subnormal</i> and <code>[1, 2046]</code> denoting the actual exponent
   * plus {@link #BIAS}. Infinite and <code>NaN</code> values always have a
   * biased exponent of <code>2047</code>.
   * </p>
   * <p>
   * This function will therefore return:
   * </p>
   * <ul>
   * <li>
   * <code>0 - {@link #BIAS} = -1023</code> iff the input is a
   * <i>subnormal</i> number.</li>
   * <li>An integer in the range
   * <code>[1 - {@link #BIAS}, 2046 - {@link #BIAS}] = [-1022, 1023]</code>
   * iff the input is a <i>normal</i> number.</li>
   * <li>
   * <code>2047 - {@link #BIAS} = 1024</code> iff the input is
   * {@link Double#POSITIVE_INFINITY}, {@link Double#NEGATIVE_INFINITY}, or
   * <code>NaN</code>.</li>
   * </ul>
   * 
   * @param d
   *          A floating point value
   * @return An unbiased exponent
   */

  public static long unpackGetExponentUnbiased(
    final double d)
  {
    final long b = Double.doubleToRawLongBits(d);
    final long em = b & Binary64.MASK_EXPONENT;
    final long es = em >> 52;
    return es - Binary64.BIAS;
  }

  /**
   * Retrieve the sign bit of the given floating point value, as an integer.
   * 
   * @param d
   *          A floating point value
   * @return An unpacked sign bit
   */

  public static long unpackGetSign(
    final double d)
  {
    final long b = Double.doubleToRawLongBits(d);
    return ((b & Binary64.MASK_SIGN) >> 63) & 1;
  }

  /**
   * <p>
   * Return the significand of the given floating point value as an integer.
   * </p>
   * 
   * @see Binary16#packSetSignificandUnchecked(int)
   * @param d
   *          A floating point value
   * @return An unpacked significand
   */

  public static long unpackGetSignificand(
    final double d)
  {
    final long b = Double.doubleToRawLongBits(d);
    return b & Binary64.MASK_SIGNIFICAND;
  }

  private Binary64()
  {
    throw new UnreachableCodeException();
  }
}
