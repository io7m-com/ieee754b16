/*
 * Copyright © 2015 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
  private static final int MASK_MANTISSA;

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
    MASK_MANTISSA = 0x03FF;
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
    return (char) n;
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
    return packFloat((float) k);
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
    return (double) unpackFloat(k);
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

  public static float unpackFloat(
    final char k)
  {
    final int f16_mantissa = (int) k & MASK_MANTISSA;
    final int f16_exponent = (int) k & MASK_EXPONENT;
    final int f16_sign = (int) k & MASK_SIGN;

    /*
     * If the exponent is zero, and the mantissa is zero, the number is zero.
     * The sign is preserved.
     */

    if (f16_exponent == 0 && f16_mantissa == 0) {
      return unpackFloatZero(f16_sign);
    }

    /*
     * If the exponent indicates that the number is infinite or NaN,
     * then return a similar infinite or NaN.
     */

    if (f16_exponent == MASK_EXPONENT) {
      return unpackFloatInfiniteNaN(f16_mantissa, f16_sign);
    }

    /*
     * If the exponent is nonzero, then the number is normal in 16 bits and can
     * therefore be translated to a normal value in 32 bits.
     */

    if (f16_exponent != 0) {
      return unpackFloatNormal(f16_mantissa, f16_exponent, f16_sign);
    }

    /*
     * If the exponent is zero, and the mantissa not zero, the number is
     * a 16-bit subnormal but can be transformed to a 32-bit normal.
     */

    return unpackFloatSubnormal(f16_mantissa, f16_sign);
  }

  private static float unpackFloatSubnormal(
    final int f16_mantissa,
    final int f16_sign)
  {
    // Try to convert the subnormal 16-bit value to a 32-bit normal value.
    // Repeatedly scale the mantissa and exponent while the mantissa is subnormal.
    int r_mantissa = f16_mantissa;
    int r_exponent = 0x1c400;
    do {
      r_mantissa <<= 1;
      r_exponent -= 0x400;
    } while ((r_mantissa & 0x400) == 0);

    // Discard the subnormal bit
    r_mantissa &= MASK_MANTISSA;

    final int f32_exponent = r_exponent << 13;
    final int f32_mantissa = r_mantissa << 13;
    final int f32_sign = f16_sign << 16;
    return Float.intBitsToFloat(f32_sign | f32_exponent | f32_mantissa);
  }

  private static float unpackFloatZero(
    final int f16_sign)
  {
    return Float.intBitsToFloat(f16_sign << 16);
  }

  private static float unpackFloatNormal(
    final int f16_mantissa,
    final int f16_exponent,
    final int f16_sign)
  {
    // Floating point numbers in the normal range of the type size adopt the
    // exponent and thus the precision to the magnitude of the value. But this
    // is not a smooth adoption, it happens in steps: switching to the next
    // higher exponent results in half the precision. The precision now remains
    // the same for all values of the mantissa until the next jump to the next
    // higher exponent. The extension code above makes these transitions
    // smoother by returning a value that is in the geographical center of the
    // covered 32 bit float range for this particular half float value. Every
    // normal half float value maps to exactly 8192 32 bit float values. The
    // returned value is supposed to be exactly in the middle of these values.
    // But at the transition of the half float exponent the lower 4096 values
    // have twice the precision as the upper 4096 values and thus cover a
    // number space that is only half as large as on the other side. All these
    // 8192 32 bit float values map to the same half float value, so converting
    // a half float to 32 bit and back results in the same half float value
    // regardless of which of the 8192 intermediate 32 bit values was chosen.

    final int r_exponent = f16_exponent + 0x1c000;
    final int f32_mantissa;
    if (f16_mantissa == 0 && r_exponent > 0x1c400) {
      f32_mantissa = MASK_MANTISSA;
    } else {
      f32_mantissa = f16_mantissa << 13;
    }

    final int f32_exponent = r_exponent << 13;
    final int f32_sign = f16_sign << 16;
    return Float.intBitsToFloat(f32_sign | f32_exponent | f32_mantissa);
  }

  private static float unpackFloatInfiniteNaN(
    final int f16_mantissa,
    final int f16_sign)
  {
    final int r_sign = f16_sign << 16;
    final int r_exponent = 0x3fc00 << 13;
    final int r_mantissa = f16_mantissa << 13;
    return Float.intBitsToFloat(r_sign | r_exponent | r_mantissa);
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
    final int f32_bits = Float.floatToIntBits(k);
    final int f16_sign = (f32_bits >>> 16) & 0x8000;
    final int f32_unrounded = f32_bits & 0x7fffffff;
    final int f32_rounded = f32_unrounded + 0x1000;

    /*
     * The 32-bit float might be large enough to become NaN or Infinity.
     */

    if (f32_rounded >= 0x47800000) {
      return packFloatMaybeNaNInfinity(
        f32_bits, f16_sign, f32_unrounded, f32_rounded);
    }

    /*
     * The 32-bit float is a normal value, and is of a size that would allow
     * it to remain a normal value as a 16-bit float.
     */

    if (f32_rounded >= 0x38800000) {
      return packFloatNormal(f16_sign, f32_rounded);
    }

    /*
     * The 32-bit float value is subnormal and would be too small
     * to even become a subnormal 16-bit float. Instead, simply return a signed
     * zero value.
     */

    if (f32_rounded < 0x33000000) {
      return (char) f16_sign;
    }

    /*
     * The 32-bit float value is subnormal, but would fit in a 16-bit float.
     */

    return packFloatSubnormal(f32_bits, f16_sign, f32_unrounded);
  }

  private static char packFloatSubnormal(
    final int f32_bits,
    final int f16_sign,
    final int f32_unrounded)
  {
    final int f16_rounded =
      f32_unrounded >>> 23;

    // Add subnormal bit
    final int f16_with_subnormal =
      (f32_bits & 0x7fffff) | 0x800000;

    // Round depending on cut off
    final int f16_rounded_cutoff =
      0x800000 >>> (f16_rounded - 102);

    // Divide by 2^(1-(exp-127+15)) and >> 13 | exp = 0
    final int f16_divided =
      (f16_with_subnormal + f16_rounded_cutoff) >>> (126 - f16_rounded);

    return (char) (f16_sign | f16_divided);
  }

  private static char packFloatNormal(
    final int f16_sign,
    final int f32_rounded)
  {
    return (char) (f16_sign | f32_rounded - 0x38000000 >>> 13);
  }

  private static char packFloatMaybeNaNInfinity(
    final int f32_bits,
    final int f16_sign,
    final int f32_unrounded,
    final int f32_rounded)
  {
    // This extension slightly extends the number range of the half float
    // format by saving some 32 bit values form getting promoted to Infinity.
    // The affected values are those that would have been smaller than
    // Infinity without rounding and would become Infinity only due to the
    // rounding.

    if (f32_unrounded >= 0x47800000) {
      if (f32_rounded < 0x7f800000) {
        return (char) (f16_sign | 0x7c00);
      }
      return (char) (f16_sign | 0x7c00 | (f32_bits & 0x007fffff) >>> 13);
    }

    return (char) (f16_sign | 0x7bff);
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
    final int sm = s & MASK_MANTISSA;
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
    final StringBuilder b = new StringBuilder(16);
    int z = (int) k;
    for (int i = 0; i < 16; ++i) {
      if ((z & 1) == 1) {
        b.insert(0, "1");
      } else {
        b.insert(0, "0");
      }
      z >>= 1;
    }

    return b.toString();
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
    return (int) k & MASK_MANTISSA;
  }
}
