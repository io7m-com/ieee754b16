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

  // h=basetable[(f>>23)&0x1ff]+((f&0x007fffff)>>shifttable[(f>>23)&0x1ff])
  const unsigned int base_index = (b.i >> 23) & 0x1ff;
  const unsigned int mask       = b.i & 0x007fffff;
  const unsigned int base       = BASE_TABLE [base_index];
  const unsigned int shift      = SHIFT_TABLE [base_index];
  const unsigned int shifted    = mask >> shift;

  return (ieee754b16_half_t) (base + shifted);
}

double
ieee754b16_unpack(
  const ieee754b16_half_t h)
{
  bits_t b;

  // f=mantissatable[offsettable[h>>10]+(h&0x3ff)]+exponenttable[h>>10]
  const unsigned int index    = h >> 10;
  const unsigned int masked   = h & 0x3ff;
  const unsigned int offset   = OFFSET_TABLE [index];
  const unsigned int exponent = EXPONENT_TABLE [index];
  const unsigned int mantissa = MANTISSA_TABLE [offset + masked];

  b.i = mantissa + exponent;
  return (double) b.f;
}

