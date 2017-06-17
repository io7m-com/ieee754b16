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

  unsigned int exponents[64];

  exponents[0] = 0;
  for (unsigned int index = 1; index <= 30; ++index) {
    exponents[index] = index << 23u;
  }
  exponents[31] = 0x47800000u;
  exponents[32] = 0x80000000u;
  for (unsigned int index = 33; index <= 62; ++index) {
    exponents[index] = 0x80000000u + ((index - 32u) << 23u);
  }
  exponents[63] = 0xC7800000u;

  switch (target) {
    case TARGET_JAVA:
      printf("package com.io7m.ieee754b16;\n");
      printf("\n");
      printf("final class ExponentTable\n");
      printf("{\n");
      printf("  private ExponentTable() { }\n");
      printf("\n");
      printf("  static final int[] EXPONENT_TABLE = {\n");
      break;
    case TARGET_C:
      printf("#include <ieee754b16/exponent.h>\n");
      printf("\n");
      printf("const unsigned int EXPONENT_TABLE[] = {\n");
      break;
  }

  for (unsigned int index = 0; index < sizeof(exponents) / sizeof(int); ++index) {
    printf("  0x%08x, // [%u]\n", exponents[index], index);
  }

  printf("};\n");
  printf("\n");

  switch (target) {
    case TARGET_JAVA:
      printf("  static {\n");
      printf("    assert EXPONENT_TABLE.length == 64;\n");
      printf("  };\n");
      printf("}\n");
      break;
    case TARGET_C:
      break;
  }

  return 0;
}