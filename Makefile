CC=cc -g -W -Wall -Werror -pedantic -std=c99 -Icom.io7m.ieee754b16.core/src/main/c

all:\
target/obj\
target/obj/ieee754b16.a\
target/obj/test

target/obj:
	mkdir -p target/obj

target/obj/convert.o: com.io7m.ieee754b16.core/src/main/c/convert.c
	${CC} -c -o target/obj/convert.o com.io7m.ieee754b16.core/src/main/c/convert.c

target/obj/mk-exponents: com.io7m.ieee754b16.core/src/main/c/mk-exponents.c
	${CC} -o target/obj/mk-exponents com.io7m.ieee754b16.core/src/main/c/mk-exponents.c

target/obj/mk-offset: com.io7m.ieee754b16.core/src/main/c/mk-offset.c
	${CC} -o target/obj/mk-offset com.io7m.ieee754b16.core/src/main/c/mk-offset.c

target/obj/mk-shiftbase: com.io7m.ieee754b16.core/src/main/c/mk-shiftbase.c
	${CC} -o target/obj/mk-shiftbase com.io7m.ieee754b16.core/src/main/c/mk-shiftbase.c

target/obj/mk-mantissas: com.io7m.ieee754b16.core/src/main/c/mk-mantissas.c
	${CC} -o target/obj/mk-mantissas com.io7m.ieee754b16.core/src/main/c/mk-mantissas.c

target/obj/exponents.c: target/obj/mk-exponents
	target/obj/mk-exponents c > target/obj/exponents.c

target/obj/exponents.o: target/obj/exponents.c
	${CC} -c -o target/obj/exponents.o target/obj/exponents.c

target/obj/mantissas.c: target/obj/mk-mantissas
	target/obj/mk-mantissas c > target/obj/mantissas.c

target/obj/mantissas.o: target/obj/mantissas.c
	${CC} -c -o target/obj/mantissas.o target/obj/mantissas.c

target/obj/shiftbase.c: target/obj/mk-shiftbase
	target/obj/mk-shiftbase c > target/obj/shiftbase.c

target/obj/shiftbase.o: target/obj/shiftbase.c
	${CC} -c -o target/obj/shiftbase.o target/obj/shiftbase.c

target/obj/offset.c: target/obj/mk-offset
	target/obj/mk-offset c > target/obj/offset.c

target/obj/offset.o: target/obj/offset.c
	${CC} -c -o target/obj/offset.o target/obj/offset.c

target/obj/ieee754b16.a: target/obj/offset.o target/obj/shiftbase.o target/obj/mantissas.o target/obj/exponents.o target/obj/convert.o
	ar rc target/obj/ieee754b16.a target/obj/offset.o target/obj/shiftbase.o target/obj/mantissas.o target/obj/exponents.o target/obj/convert.o
	ranlib target/obj/ieee754b16.a

target/obj/test: target/obj/ieee754b16.a com.io7m.ieee754b16.core/src/main/c/test.c
	${CC} -o target/obj/test com.io7m.ieee754b16.core/src/main/c/test.c target/obj/ieee754b16.a

clean:
	rm -rf target/obj

