#include "test/common.h"
//
#include "src/gbits.h"

u32 values[] = {0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};

int main()
{
  gbits b1, b2;
  u32 nbits = ARRAYLEN(values);
  u32 dlen = ((nbits & 0x1fu) != 0) + (nbits >> 5);

  u32 *data1 = (u32 *)(malloc(sizeof(u32) * nbits));
  u32 *data2 = (u32 *)(malloc(sizeof(u32) * nbits));

  gbits_init(&b1, data1, nbits, 1);
  gbits_init(&b2, data2, nbits, 1);

  GBITS_SET(&b1, 0);
  gbits_set(&b1, 5);
  GBITS_TOGGLE(&b1, 7);
  gbits_toggle(&b1, 9);

  ASSERT_EQ(gbits_count(&b1), 4);
  ASSERT_EQ(gbits_next(&b1, 9 + 1), nbits);
  gbits_show(&b1);
  gbits_showsubset(&b1, values, nbits);

  gbits_copydata(&b2, &b1);
  GBITS_RESET(&b2, 9);

  ASSERT_TRUE(GBITS_CHECK(&b2, 0));
  ASSERT_TRUE(gbits_check(&b2, 5));
  ASSERT_FALSE(GBITS_CHECK(&b2, 9));
  ASSERT_TRUE(gbits_check(&b2, 7));

  gbits_show(&b2);
  gbits_showsubset(&b2, values, nbits);

  gbits_clear(&b2);
  ASSERT_FALSE(gbits_block0(&b2, 6));
  ASSERT_FALSE(GBITS_BLOCK0(&b2, 6));
  ASSERT_EQ(gbits_count(&b2), 0);

  free(data1);
  free(data2);
  return 0;
}
