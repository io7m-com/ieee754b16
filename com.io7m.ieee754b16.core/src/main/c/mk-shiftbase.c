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
#include <stdint.h>

static void
generateTables(
  uint16_t *base_table,
  unsigned int *shift_table)
{
  unsigned int index;
  int e;
  for (index = 0; index < 256; ++index) {
    e = index - 127;
    unsigned int index0 = index | 0x000;
    unsigned int index1 = index | 0x100;
    if (e < -24) {
      // Very small numbers map to zero
      base_table[index0] = (uint16_t) 0x0000;
      base_table[index1] = (uint16_t) 0x8000;
      shift_table[index0] = 24;
      shift_table[index1] = 24;
    } else if (e < -14) {
      // Small numbers map to denorms
      unsigned int shift = -e - 14;
      base_table[index0] = (uint16_t) (0x0400 >> shift);
      base_table[index1] = (uint16_t) ((0x0400 >> shift) | 0x8000);
      unsigned int table_shift = -e - 1;
      shift_table[index0] = table_shift;
      shift_table[index1] = table_shift;
    } else if (e <= 15) {
      // Normal numbers just lose precision
      base_table[index0] = (uint16_t) ((e + 15) << 10);
      base_table[index1] = (uint16_t) (((e + 15) << 10) | 0x8000);
      shift_table[index0] = 13;
      shift_table[index1] = 13;
    } else if (e < 128) {
      // Large numbers map to Infinity
      base_table[index0] = (uint16_t) 0x7C00;
      base_table[index1] = (uint16_t) 0xFC00;
      shift_table[index0] = 24;
      shift_table[index1] = 24;
    } else {
      // Infinity and NaN's stay Infinity and NaN's
      base_table[index0] = (uint16_t) 0x7C00;
      base_table[index1] = (uint16_t) 0xFC00;
      shift_table[index0] = 13;
      shift_table[index1] = 13;
    }
  }
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

  uint16_t base_table[512];
  unsigned int shift_table[512];

  generateTables(base_table, shift_table);

  switch (target) {
    case TARGET_JAVA:
      printf("package com.io7m.ieee754b16;\n");
      printf("\n");
      printf("final class ShiftBaseTable\n");
      printf("{\n");
      printf("  private ShiftBaseTable() { }\n");
      printf("\n");
      printf("  static final char[] BASE_TABLE = {\n");
      for (unsigned int index = 0; index < 512; ++index) {
        printf("  (char) 0x%04x,\n", base_table[index]);
      }
      break;
    case TARGET_C:
      printf("#include <ieee754b16/shiftbase.h>\n");
      printf("\n");
      printf("const uint16_t BASE_TABLE[] = {\n");
      for (unsigned int index = 0; index < 512; ++index) {
        printf("  0x%04x,\n", base_table[index]);
      }
      break;
  }

  printf("};\n");
  printf("\n");

  switch (target) {
    case TARGET_JAVA:
      printf("  static final int[] SHIFT_TABLE = {\n");
      break;
    case TARGET_C:
      printf("const unsigned int SHIFT_TABLE[] = {\n");
      break;
  }

  for (unsigned int index = 0; index < 512; ++index) {
    printf("  %u,\n", shift_table[index]);
  }
  printf("};\n");
  printf("\n");

  switch (target) {
    case TARGET_JAVA:
      printf("static {\n");
      printf("  assert BASE_TABLE.length == 512;\n");
      printf("  assert SHIFT_TABLE.length == 512;\n");
      printf("}\n");
      break;
    case TARGET_C:
      break;
  }

  printf("\n");
  return 0;
}