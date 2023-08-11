#ifndef GBITS_H
#define GBITS_H

#include "src/utils.h"

#define MSB_32   0x80000000u
#define ALL_ONES 0xffffffffu

struct gbits
{
  u32 *data;
  u32 pad_cover;
  u32 valid_len;
};

typedef struct gbits gbits;
void gbits_init(gbits *, u32 *, u32, u32);

void gbits_set(gbits *, u32);
void gbits_reset(gbits *, u32);
void gbits_toggle(gbits *, u32);
u32 gbits_check(gbits *, u32);
u32 gbits_block0(gbits *, u32);

#define GBITS_SET(g, x)    ((g)->data[(x) >> 5] |= (MSB_32 >> ((x)&0x1fu)))
#define GBITS_RESET(g, x)  ((g)->data[(x) >> 5] &= ~(MSB_32 >> ((x)&0x1fu)))
#define GBITS_TOGGLE(g, x) ((g)->data[(x) >> 5] ^= (MSB_32 >> ((x)&0x1fu)))
#define GBITS_CHECK(g, x)  (0 != ((g)->data[(x) >> 5] & (MSB_32 >> ((x)&0x1fu))))
#define GBITS_BLOCK0(g, x) (0 != ((g)->data[(x) >> 5]))

u32 gbits_count(const gbits *);
u32 gbits_next(gbits *, u32);
void gbits_clear(gbits *);
void gbits_copydata(gbits *, const gbits *);
void gbits_show(const gbits *);
void gbits_showsubset(const gbits *, const u32 *, const u32);

#endif /* GBITS_H */
