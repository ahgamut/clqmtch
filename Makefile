
LIB_NAME = clqmtch
CC = gcc
CFLAGS = -I./ -O2 -fPIC

ifeq ($(MODE),)
	CFLAGS += -DNDEBUG
endif
ifeq ($(MODE), dbg)
	CFLAGS += -g3
endif

LIB_SONAME = lib$(LIB_NAME).so

LIB_SOURCES = $(wildcard src/*.c)
LIB_HEADERS = $(wildcard src/*.h)
LIB_OBJS = $(LIB_SOURCES:%.c=%.o)

TEST_SOURCES = $(wildcard test/*.c)
TEST_OBJS = $(LIB_SOURCES:%.c=%.o)
TEST_BINS = $(TEST_SOURCES:%.c=%)
TEST_RUNS = $(TEST_SOURCES:%.c=%.runs)

all: lib

lib: $(LIB_SONAME)

$(LIB_SONAME): $(LIB_OBJS)
	$(CC) -shared -o $@ $^

test: lib $(TEST_RUNS) $(TEST_BINS)

%.runs: %
	./$^ && echo $^ was successful

test_%: test_%.o lib
	$(CC) -o $@ $< $(LIB_OBJS)

%.o: %.c
	$(CC) -c -o $@ $(CFLAGS) $^

clean:
	rm -f $(TEST_OBJS)
	rm -f $(TEST_BINS)
	rm -f $(LIB_SONAME)
	rm -f $(LIB_OBJS)

.PHONY: clean test
