#include <ieee754b16/convert.h>
#include <ieee754b16/exponent.h>
#include <ieee754b16/offset.h>
#include <ieee754b16/mantissa.h>
#include <ieee754b16/shiftbase.h>

#include <stdint.h>

typedef union {
  uint32_t i;
  float    f;
} bits_t;

ieee754b16_half_t
ieee754b16_pack(
  const double x)
{
  bits_t b;
  b.f = (float) x;

  return BASE_TABLE [(b.i >> 23) & 0x1ff] + ((b.i & 0x007fffff) >> SHIFT_TABLE [(b.i >> 23) & 0x1ff]);
}

double
ieee754b16_unpack(
  const ieee754b16_half_t h)
{
  bits_t b;

  const unsigned int e = h >> 10u;
  const unsigned int m = h & 0x3ffu;
  const unsigned int o = OFFSET_TABLE[e];

  b.i = MANTISSA_TABLE[o + m] + EXPONENT_TABLE[e];
  return (double) b.f;
}

