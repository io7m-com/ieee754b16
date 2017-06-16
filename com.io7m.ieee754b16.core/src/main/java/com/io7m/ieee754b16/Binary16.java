/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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
 * Functions to convert values to/from the {@code binary16} format
 * specified in {@code IEEE 754 2008}.
 * </p>
 */

public final class Binary16
{
  /**
   * The encoded form of negative infinity {@code -∞}.
   */

  public static final char NEGATIVE_INFINITY;

  /**
   * The encoded form of positive infinity {@code ∞}.
   */

  public static final char POSITIVE_INFINITY;

  /**
   * The encoded form of positive zero {@code 0}.
   */

  public static final char POSITIVE_ZERO;

  /**
   * The encoded form of negative zero {@code -0}.
   */

  public static final char NEGATIVE_ZERO;

  /**
   * The <i>bias</i> value used to offset the encoded exponent. A given
   * exponent {@code e} is encoded as <code>{@link #BIAS} + e</code>.
   */

  public static final int BIAS;

  private static final int MASK_SIGN;
  private static final int MASK_EXPONENT;
  private static final int MASK_SIGNIFICAND;

  static {
    NEGATIVE_INFINITY = (char) 0xFC00;
    POSITIVE_INFINITY = (char) 0x7C00;
    POSITIVE_ZERO = (char) 0x0000;
    NEGATIVE_ZERO = (char) 0x8000;
    BIAS = 15;
  }

  static {
    MASK_SIGN = 0x8000;
    MASK_EXPONENT = 0x7C00;
    MASK_SIGNIFICAND = 0x03FF;
  }

  private Binary16()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @return One possible not-a-number value.
   */

  public static char exampleNaN()
  {
    final int n =
      (int) packSetExponentUnbiasedUnchecked(16)
        | (int) packSetSignificandUnchecked(1);
    final char c = (char) n;
    return c;
  }

  /**
   * @param k A packed {@code binary16} value
   *
   * @return {@code true} if the given packed {@code binary16} value is
   * infinite.
   */

  public static boolean isInfinite(
    final char k)
  {
    if (unpackGetExponentUnbiased(k) == 16) {
      if (unpackGetSignificand(k) == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param k A packed {@code binary16} value
   *
   * @return {@code true} if the given packed {@code binary16} value is not a
   * number ({@code NaN}).
   */

  public static boolean isNaN(
    final char k)
  {
    final int e = unpackGetExponentUnbiased(k);
    final int s = unpackGetSignificand(k);
    return (e == 16) && (s > 0);
  }

  /**
   * <p>
   * Convert a double precision floating point value to a packed
   * {@code binary16} value.
   * </p>
   * <p>
   * For the following specific cases, the function returns:
   * </p>
   * <ul>
   * <li>{@code NaN} iff {@code isNaN(k)}</li>
   * <li>{@link #POSITIVE_INFINITY} iff
   * <code>k == {@link Double#POSITIVE_INFINITY}</code></li>
   * <li>{@link #NEGATIVE_INFINITY} iff
   * <code>k == {@link Double#NEGATIVE_INFINITY}</code></li>
   * <li>{@link #NEGATIVE_ZERO} iff {@code k == -0.0}</li>
   * <li>{@link #POSITIVE_ZERO} iff {@code k == 0.0}</li>
   * </ul>
   * <p>
   * Otherwise, the {@code binary16} value that most closely represents
   * {@code k} is returned. This may obviously be an infinite value as
   * the interval of double precision values is far larger than that of the
   * {@code binary16} type.
   * </p>
   *
   * @param k A floating point value
   *
   * @return A packed {@code binary16} value
   *
   * @see #unpackDouble(char)
   */

  public static char packDouble(
    final double k)
  {
    if (Double.isNaN(k)) {
      return exampleNaN();
    }
    if (k == Double.POSITIVE_INFINITY) {
      return POSITIVE_INFINITY;
    }
    if (k == Double.NEGATIVE_INFINITY) {
      return NEGATIVE_INFINITY;
    }
    if (Double.doubleToLongBits(k) == Binary64.NEGATIVE_ZERO_BITS) {
      return NEGATIVE_ZERO;
    }
    if (k == 0.0) {
      return POSITIVE_ZERO;
    }

    final long de = Binary64.unpackGetExponentUnbiased(k);
    final long ds = Binary64.unpackGetSign(k);
    final long dn = Binary64.unpackGetSignificand(k);
    final char rsr = packSetSignUnchecked((int) ds);

    /*
     * Extract the 5 least-significant bits of the exponent.
     */

    final int rem = (int) (de & 0x001FL);
    final char rer = packSetExponentUnbiasedUnchecked(rem);

    /*
     * Extract the 10 most-significant bits of the significand.
     */

    final long rnm = dn & 0xFFC0000000000L;
    final long rns = rnm >> 42;
    final char rnr = packSetSignificandUnchecked((int) rns);

    /*
     * Combine the results.
     */

    return (char) ((int) rsr | (int) rer | (int) rnr);
  }

  /**
   * <p>
   * Convert a single precision floating point value to a packed
   * {@code binary16} value.
   * </p>
   * <p>
   * For the following specific cases, the function returns:
   * </p>
   * <ul>
   * <li>{@code NaN} iff {@code isNaN(k)}</li>
   * <li>{@link #POSITIVE_INFINITY} iff
   * <code>k == {@link Float#POSITIVE_INFINITY}</code></li>
   * <li>{@link #NEGATIVE_INFINITY} iff
   * <code>k == {@link Float#NEGATIVE_INFINITY}</code></li>
   * <li>{@link #NEGATIVE_ZERO} iff {@code k == -0.0}</li>
   * <li>{@link #POSITIVE_ZERO} iff {@code k == 0.0}</li>
   * </ul>
   * <p>
   * Otherwise, the {@code binary16} value that most closely represents
   * {@code k} is returned. This may obviously be an infinite value as
   * the interval of single precision values is far larger than that of the
   * {@code binary16} type.
   * </p>
   *
   * @param k A floating point value
   *
   * @return A packed {@code binary16} value
   *
   * @see #unpackFloat(char)
   */

  public static char packFloat(
    final float k)
  {
    if (Float.isNaN(k)) {
      return exampleNaN();
    }
    if (k == Float.POSITIVE_INFINITY) {
      return POSITIVE_INFINITY;
    }
    if (k == Float.NEGATIVE_INFINITY) {
      return NEGATIVE_INFINITY;
    }
    if (Float.floatToIntBits(k) == Binary32.NEGATIVE_ZERO_BITS) {
      return NEGATIVE_ZERO;
    }
    if ((double) k == 0.0) {
      return POSITIVE_ZERO;
    }

    final long de = (long) Binary32.unpackGetExponentUnbiased(k);
    final long ds = (long) Binary32.unpackGetSign(k);
    final long dn = (long) Binary32.unpackGetSignificand(k);
    final char rsr = packSetSignUnchecked((int) ds);

    /*
     * Extract the 5 least-significant bits of the exponent.
     */

    final int rem = (int) (de & 0x001FL);
    final char rer = packSetExponentUnbiasedUnchecked(rem);

    /*
     * Extract the 10 most-significant bits of the significand.
     */

    final long rnm = dn & 0x7FE000L;
    final long rns = rnm >> 13;
    final char rnr = packSetSignificandUnchecked((int) rns);

    /*
     * Combine the results.
     */

    return (char) ((int) rsr | (int) rer | (int) rnr);
  }

  /**
   * <p>
   * Encode the unbiased exponent {@code e}. Values should be in the
   * range {@code [-15, 16]} - values outside of this range will be
   * truncated.
   * </p>
   *
   * @param e An exponent
   *
   * @return A packed exponent
   *
   * @see #unpackGetExponentUnbiased(char)
   */

  public static char packSetExponentUnbiasedUnchecked(
    final int e)
  {
    final int eb = e + BIAS;
    final int es = eb << 10;
    final int em = es & MASK_EXPONENT;
    return (char) em;
  }

  /**
   * <p>
   * Encode the significand {@code s}. Values should be in the range
   * {@code [0, 1023]}. Values outside of this range will be truncated.
   * </p>
   *
   * @param s A significand
   *
   * @return A packed significand
   *
   * @see #unpackGetSignificand(char)
   */

  public static char packSetSignificandUnchecked(
    final int s)
  {
    final int sm = s & MASK_SIGNIFICAND;
    return (char) sm;
  }

  /**
   * <p>
   * Encode the sign bit {@code s}. Values should be in the range
   * {@code [0, 1]}, with {@code 0} ironically denoting a positive
   * value. Values outside of this range will be truncated.
   * </p>
   *
   * @param s A sign bit
   *
   * @return A packed sign bit
   *
   * @see #unpackGetSign(char)
   */

  public static char packSetSignUnchecked(
    final int s)
  {
    final int ss = s << 15;
    final int sm = ss & MASK_SIGN;
    return (char) sm;
  }

  /**
   * Show the given raw packed {@code binary16} value as a string of
   * binary digits.
   *
   * @param k A packed {@code binary16} value
   *
   * @return A string representation
   */

  public static String toRawBinaryString(
    final char k)
  {
    final StringBuilder b = new StringBuilder();
    int z = (int) k;
    for (int i = 0; i < 16; ++i) {
      if ((z & 1) == 1) {
        b.insert(0, "1");
      } else {
        b.insert(0, "0");
      }
      z >>= 1;
    }

    final String r = b.toString();
    assert r != null;
    return r;
  }

  /**
   * <p>
   * Convert a packed {@code binary16} value {@code k} to a
   * double-precision floating point value.
   * </p>
   * <p>
   * The function returns:
   * </p>
   * <ul>
   * <li>{@code NaN} iff {@code isNaN(k)}</li>
   * <li>{@link Double#POSITIVE_INFINITY} iff
   * <code>k == {@link #POSITIVE_INFINITY}</code></li>
   * <li>{@link Double#NEGATIVE_INFINITY} iff
   * <code>k == {@link #NEGATIVE_INFINITY}</code></li>
   * <li>{@code -0.0} iff <code>k == {@link #NEGATIVE_ZERO}</code></li>
   * <li>{@code 0.0} iff <code>k == {@link #POSITIVE_ZERO}</code></li>
   * <li>{@code (-1.0 * n) * (2 ^ e) * 1.s}, for the decoded sign
   * {@code n} of {@code k}, the decoded exponent {@code e} of
   * {@code k}, and the decoded significand {@code s} of
   * {@code k}.</li>
   * </ul>
   *
   * @param k A packed {@code binary16} value
   *
   * @return A floating point value
   *
   * @see #packDouble(double)
   */

  public static double unpackDouble(
    final char k)
  {
    if (isNaN(k)) {
      return Double.NaN;
    }
    if ((int) k == (int) POSITIVE_INFINITY) {
      return Double.POSITIVE_INFINITY;
    }
    if ((int) k == (int) NEGATIVE_INFINITY) {
      return Double.NEGATIVE_INFINITY;
    }
    if ((int) k == (int) NEGATIVE_ZERO) {
      return -0.0;
    }
    if ((int) k == (int) POSITIVE_ZERO) {
      return 0.0;
    }

    final long e = (long) unpackGetExponentUnbiased(k);
    final long s = (long) unpackGetSign(k);
    final long n = (long) unpackGetSignificand(k);

    /*
     * Shift the sign bit to the position at which it will appear in the
     * resulting value.
     */

    final long rsr = s << 63;

    /*
     * 1. Bias the exponent.
     *
     * 2. Shift the result left to the position at which it will appear in the
     * resulting value.
     */

    final long reb = (e + Binary64.BIAS);
    final long rer = reb << 52;

    /*
     * Shift the significand left to the position at which it will appear in
     * the resulting value.
     */

    final long rnr = n << 42;
    return Double.longBitsToDouble(rsr | rer | rnr);
  }

  /**
   * <p>
   * Convert a packed {@code binary16} value {@code k} to a
   * single-precision floating point value.
   * </p>
   * <p>
   * The function returns:
   * </p>
   * <ul>
   * <li>{@code NaN} iff {@code isNaN(k)}</li>
   * <li>{@link Float#POSITIVE_INFINITY} iff
   * <code>k == {@link #POSITIVE_INFINITY}</code></li>
   * <li>{@link Float#NEGATIVE_INFINITY} iff
   * <code>k == {@link #NEGATIVE_INFINITY}</code></li>
   * <li>{@code -0.0} iff <code>k == {@link #NEGATIVE_ZERO}</code></li>
   * <li>{@code 0.0} iff <code>k == {@link #POSITIVE_ZERO}</code></li>
   * <li>{@code (-1.0 * n) * (2 ^ e) * 1.s}, for the decoded sign
   * {@code n} of {@code k}, the decoded exponent {@code e} of
   * {@code k}, and the decoded significand {@code s} of
   * {@code k}.</li>
   * </ul>
   *
   * @param k A packed {@code binary16} value
   *
   * @return A floating point value
   *
   * @see #packFloat(float)
   */

  public static float unpackFloat(
    final char k)
  {
    if (isNaN(k)) {
      return Float.NaN;
    }
    if ((int) k == (int) POSITIVE_INFINITY) {
      return Float.POSITIVE_INFINITY;
    }
    if ((int) k == (int) NEGATIVE_INFINITY) {
      return Float.NEGATIVE_INFINITY;
    }
    if ((int) k == (int) NEGATIVE_ZERO) {
      return -0.0f;
    }
    if ((int) k == (int) POSITIVE_ZERO) {
      return 0.0f;
    }

    final int e = unpackGetExponentUnbiased(k);
    final int s = unpackGetSign(k);
    final int n = unpackGetSignificand(k);

    /*
     * Shift the sign bit to the position at which it will appear in the
     * resulting value.
     */

    final int rsr = s << 31;

    /*
     * 1. Bias the exponent.
     *
     * 2. Shift the result left to the position at which it will appear in the
     * resulting value.
     */

    final int reb = (e + Binary32.BIAS);
    final int rer = reb << 23;

    /*
     * Shift the significand left to the position at which it will appear in
     * the resulting value.
     */

    final int rnr = n << 13;
    return Float.intBitsToFloat(rsr | rer | rnr);
  }

  /**
   * <p>
   * Extract and unbias the exponent of the given packed {@code binary16}
   * value.
   * </p>
   * <p>
   * The exponent is encoded <i>biased</i> as a number in the range
   * {@code [0, 31]}, with {@code 0} indicating that the number is
   * <i>subnormal</i> and {@code [1, 30]} denoting the actual exponent
   * plus {@link #BIAS}. Infinite and {@code NaN} values always have an
   * exponent of {@code 31}.
   * </p>
   * <p>
   * This function will therefore return:
   * </p>
   * <ul>
   * <li>
   * <code>0 - {@link #BIAS} = -15</code> iff the input is a <i>subnormal</i>
   * number.</li>
   * <li>An integer in the range
   * <code>[1 - {@link #BIAS}, 30 - {@link #BIAS}] = [-14, 15]</code> iff the
   * input is a <i>normal</i> number.</li>
   * <li>
   * {@code 16} iff the input is {@link #POSITIVE_INFINITY},
   * {@link #NEGATIVE_INFINITY}, or {@code NaN}.</li>
   * </ul>
   *
   * @param k A packed {@code binary16} value
   *
   * @return The unbiased exponent
   *
   * @see #packSetExponentUnbiasedUnchecked(int)
   */

  public static int unpackGetExponentUnbiased(
    final char k)
  {
    final int em = (int) k & MASK_EXPONENT;
    final int es = em >> 10;
    return es - BIAS;
  }

  /**
   * Retrieve the sign bit of the given packed {@code binary16} value, as
   * an integer in the range {@code [0, 1]}.
   *
   * @param k A packed {@code binary16} value
   *
   * @return An unpacked sign bit
   *
   * @see Binary16#packSetSignUnchecked(int)
   */

  public static int unpackGetSign(
    final char k)
  {
    return ((int) k & MASK_SIGN) >> 15;
  }

  /**
   * <p>
   * Return the significand of the given packed {@code binary16} value as
   * an integer in the range {@code [0, 1023]}.
   * </p>
   *
   * @param k A packed {@code binary16} value
   *
   * @return An unpacked significand
   *
   * @see Binary16#packSetSignificandUnchecked(int)
   */

  public static int unpackGetSignificand(
    final char k)
  {
    return (int) k & MASK_SIGNIFICAND;
  }
}
