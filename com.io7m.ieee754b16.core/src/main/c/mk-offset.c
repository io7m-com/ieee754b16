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

  char *offset_type;
  switch (target) {
    case TARGET_JAVA:
      offset_type = "char";
      printf("package com.io7m.ieee754b16;\n");
      printf("\n");
      printf("final class OffsetTable\n");
      printf("{\n");
      printf("  private OffsetTable() { }\n");
      printf("\n");
      printf("  static final char[] OFFSET_TABLE = {\n");
      break;
    case TARGET_C:
      offset_type = "uint16_t";
      printf("#include <ieee754b16/offset.h>\n");
      printf("\n");
      printf("const uint16_t OFFSET_TABLE[] = {\n");
      break;
  }

  for (int index = 0; index < 64; ++index) {
    if (index == 0) {
      printf("  // %d\n", index);
      printf("  (%s) %d,\n", offset_type, 0);
    } else if (index == 32) {
      printf("  // %d\n", index);
      printf("  (%s) %d,\n", offset_type, 0);
    } else {
      printf("  (%s) %d,\n", offset_type, 1024);
    }
  }
  printf("};");
  printf("\n");

  switch (target) {
    case TARGET_JAVA:
      printf("static {\n");
      printf("  assert OFFSET_TABLE.length == 64;\n");
      printf("}\n");
      break;
    case TARGET_C:
      break;
  }

  printf("\n");
  return 0;
}