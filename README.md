ieee754b16
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.ieee754b16/com.io7m.ieee754b16.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.ieee754b16%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.ieee754b16/com.io7m.ieee754b16?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/ieee754b16/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/ieee754b16.svg?style=flat-square)](https://codecov.io/gh/io7m-com/ieee754b16)

![com.io7m.ieee754b16](./src/site/resources/ieee754b16.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/ieee754b16/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/ieee754b16/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/ieee754b16/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/ieee754b16/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/ieee754b16/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/ieee754b16/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/ieee754b16/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/ieee754b16/actions?query=workflow%3Amain.windows.temurin.lts)|

## ieee754b16

Java functions to convert to and from the [IEEE754](https://en.wikipedia.org/wiki/IEEE_754)
`binary16` type.

## Features

* Conversion between `double`/`float` and `binary16` values.
* High coverage test suite (100%, minus an unreachable private constructor).
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## Usage

The package uses the `char` type to store values of the `binary16` type.
To convert a double-precision value `32.0` to `binary16` format: 

```
final char k = Binary16.packDouble(32.0);
```

To unpack values from the `binary16` format:

```
final double r = Binary16.unpackDouble(k);
```


