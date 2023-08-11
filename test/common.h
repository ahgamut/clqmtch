#ifndef TEST_COMMON_H
#define TEST_COMMON_H
#undef NDEBUG
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>

#define ASSERT_TRUE(X)                     \
  if (!(X))                                \
  {                                        \
    fprintf(stderr, "assert true failed\n"); \
    assert(0);                         \
  }

#define ASSERT_FALSE(X)                     \
  if (!!(X))                                 \
  {                                         \
    fprintf(stderr, "assert 0 failed\n"); \
    assert(0);                          \
  }

#define ASSERT_EQ(X, Y)                  \
  if ((X) != (Y))                        \
  {                                      \
    fprintf(stderr, "assert eq failed\n"); \
    assert(0);                       \
  }

#endif /* TEST_COMMON_H */
