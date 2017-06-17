#ifndef IEEE754B16_CONVERT_H
#define IEEE754B16_CONVERT_H

/// \file convert.h
/// \brief Functions for converting values to IEEE 754 Binary16 values

#include <stdint.h>

/// The type of packed floating point values.

typedef uint16_t ieee754b16_half_t;

/// Pack a double precision floating point value to a Binary16 value.
///
/// @param x The input value
///
/// @return A packed Binary16 value

ieee754b16_half_t ieee754b16_pack(double x);

/// Unpack a double precision floating point value from a Binary16 value.
///
/// @param x The input value
///
/// @return A double precision float

double ieee754b16_unpack(ieee754b16_half_t x);

#endif // IEEE754B16_CONVERT_H