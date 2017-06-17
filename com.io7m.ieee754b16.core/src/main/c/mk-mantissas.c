/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static unsigned int
convertMantissa(
  unsigned int i)
{
  // Zero pad mantissa bits
  unsigned int m = i << 13;

  // Zero exponent
  unsigned int e = 0;

  // While not normalized
  while (!(m & 0x00800000)) {
    // Decrement exponent (1<<23)
    e -= 0x00800000;
    // Shift mantissa
    m <<= 1;
  }

  // Clear leading 1 bit
  m &= ~0x00800000;
  // Adjust bias ((127 - 14) << 23)
  e += 0x38800000;
  // Return combined number
  return m | e;
}

typedef enum {
  TARGET_JAVA,
  TARGET_C
} target_t;

int
main (int argc, char *argv[])
{
  if (argc != 2) {
    fprintf(stderr, "usage: c|java\n");
    exit(1);
  }

  target_t target;
  const char *type = argv[1];
  if (strcmp(type, "c") == 0) {
    target = TARGET_C;
  } else if (strcmp(type, "java") == 0) {
    target = TARGET_JAVA;
  } else {
    fprintf(stderr, "error: unknown target type\n");
    exit(1);
  }

  unsigned int mantissas[2048];

  mantissas[0] = 0;
  for (unsigned int index = 1; index < 1023; ++index) {
    mantissas[index] = convertMantissa(index);
  }
  for (unsigned int index = 1024; index < 2047; ++index) {
    mantissas[index] = 0x38000000 + ((index - 1024) << 13);
  }

  switch (target) {
    case TARGET_JAVA:
      printf("package com.io7m.ieee754b16;\n");
      printf("\n");
      printf("final class MantissaTable\n");
      printf("{\n");
      printf("  private MantissaTable() { }\n");
      printf("\n");
      printf("  static final int[] MANTISSA_TABLE = {\n");
      break;
    case TARGET_C:
      printf("#include <ieee754b16/mantissa.h>\n");
      printf("\n");
      printf("const unsigned int MANTISSA_TABLE[] = {\n");
      break;
  }

  for (unsigned int index = 0; index < sizeof(mantissas) / sizeof(int); ++index) {
    printf("  0x%08x,\n", mantissas[index]);
  }
  printf("};\n");
  printf("\n");

  switch (target) {
    case TARGET_JAVA:
      printf("  static {\n");
      printf("    assert MANTISSA_TABLE.length == 2048;\n");
      printf("  };\n");
      printf("}\n");
      break;
    case TARGET_C:
      break;
  }

  return 0;
}

