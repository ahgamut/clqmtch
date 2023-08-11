#include "src/gbits.h"
//
#include <stdio.h>

#if defined(_MSC_VER)
#define CLZ(x)      __lzcnt((x))
#define BITCOUNT(x) __popcnt((x))
#elif defined(__GNUC__) || defined(__clang__)
#define CLZ(x)      __builtin_clz((x))
#define BITCOUNT(x) __builtin_popcount((x))
#else
static inline u32 clz(u32 x)
{
  // https://graphics.stanford.edu/~seander/bithacks.html#IntegerLog
  u32 r, shift;
  r = (x > 0xFFFFu) << 4;
  x >>= r;
  shift = (x > 0xFFu) << 3;
  x >>= shift;
  r |= shift;
  shift = (x > 0xFu) << 2;
  x >>= shift;
  r |= shift;
  shift = (x > 0x3u) << 1;
  x >>= shift;
  r |= shift;
  r |= (x >> 1);
  return r;
}

static inline u32 bitcount(u32 x)
{
  // https://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
  x = x - ((x >> 1) & 0x55555555);
  x = (x & 0x33333333) + ((x >> 2) & 0x33333333);
  x = ((x + (x >> 4) & 0xF0F0F0F) * 0x1010101) >> 24;
  return x;
}
#define CLZ(x)      clz((x))
#define BITCOUNT(x) bitcount((x))
#endif

void gbits_init(gbits *g, u32 *data, u32 num_bits, u32 clear)
{
  g->data = data;
  g->valid_len = num_bits;
  g->pad_cover = ALL_ONES << (32 - (num_bits & 0x1fu));
  if (clear)
  {
    gbits_clear(g);
  }
}

void gbits_set(gbits *g, u32 i)
{
  ASSERT(i < g->valid_len);
  u32 mask = MSB_32 >> (i & 0x1fu);
  g->data[i >> 5] |= mask;
}

void gbits_reset(gbits *g, u32 i)
{
  ASSERT(i < g->valid_len);
  u32 mask = ~(MSB_32 >> (i & 0x1fu));
  g->data[i >> 5] &= mask;
}

void gbits_toggle(gbits *g, u32 i)
{
  ASSERT(i < g->valid_len);
  u32 mask = MSB_32 >> (i & 0x1fu);
  g->data[i >> 5] ^= mask;
}

u32 gbits_check(gbits *g, u32 i)
{
  ASSERT(i < g->valid_len);
  u32 mask = MSB_32 >> (i & 0x1fu);
  return 0 != (g->data[i >> 5] & mask);
}

u32 gbits_block0(gbits *g, u32 i)
{
  ASSERT(i < g->valid_len);
  return g->data[i >> 5] != 0;
}

void gbits_clear(gbits *g)
{
  u32 dlen = ((g->valid_len & 0x1fu) != 0) + (g->valid_len >> 5);
  g->data[dlen - 1] &= g->pad_cover;
  for (u32 i = 0; i < dlen; ++i) g->data[i] = 0;
}

u32 gbits_next(gbits *g, u32 i)
{
  if (i < g->valid_len)
  {
    const u32 base = (i >> 5);
    const u32 mask = g->data[base] & (ALL_ONES >> (i & 0x1fu));
    if (mask)
      return (base << 5) + CLZ(mask);
    else
      return gbits_next(g, (base + 1) << 5);
  }
  else
    return g->valid_len;
}

u32 gbits_count(const gbits *g)
{
  u32 count = 0;
  u32 dlen = ((g->valid_len & 0x1fu) != 0) + (g->valid_len >> 5);
  g->data[dlen - 1] &= g->pad_cover;
  for (u32 i = 0; i < dlen; ++i) count += BITCOUNT(g->data[i]);
  return count;
}

void gbits_copydata(gbits *g, const gbits *other)
{
  ASSERT(other->valid_len == g->valid_len);
  u32 dlen = ((g->valid_len & 0x1fu) != 0) + (g->valid_len >> 5);
  for (u32 i = 0; i < dlen; ++i) g->data[i] = other->data[i];
}

void gbits_show(const gbits *g)
{
  u32 b, count = 0;
  DEBUGF("%p ", g->data);
  for (u32 i = 0; i < g->valid_len; ++i)
  {
    b = GBITS_CHECK(g, i);
    if (b)
    {
      count += 1;
    }
    printf("%u", b);
  }
  printf(" (%u/%u)\n", count, g->valid_len);
}

void gbits_showsubset(const gbits *g, const u32 *arr, const u32 len)
{
  u32 count = 0;
  ASSERT(g->valid_len <= len);
  DEBUGF("%p ", g->data);
  for (u32 i = 0; i < g->valid_len; ++i)
  {
    if (GBITS_CHECK(g, i))
    {
      printf("%u ", arr[i]);
      count += 1;
    }
  }
  printf(" (%u/%u)\n", count, g->valid_len);
}
