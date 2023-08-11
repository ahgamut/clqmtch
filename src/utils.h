#ifndef UTILS_H
#define UTILS_H

#include <stdint.h>
#include <stdlib.h>

#ifdef _MSC_VER
#include <intrin.h>
#endif

#ifndef NDEBUG
#include <assert.h>
#include <stdio.h>
#define ASSERT(x)   assert((x))
#define DEBUGF(...) fprintf(stderr, "<DEBUG> " __VA_ARGS__)
#else
#define ASSERT(x)
#define DEBUGF(...)
#endif

#define ARRAYLEN(X) (sizeof((X)) / sizeof(*(X)))

typedef uint8_t u8;
typedef uint16_t u16;
typedef uint32_t u32;
typedef uint64_t u64;

#endif /* UTILS_H */
