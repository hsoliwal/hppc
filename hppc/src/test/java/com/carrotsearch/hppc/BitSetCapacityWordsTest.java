/*
 * HPPC
 *
 * Copyright (C) 2010-2026 Carrot Search s.c. and contributors
 * All rights reserved.
 *
 * Refer to the full license file "LICENSE.txt":
 * https://github.com/carrotsearch/hppc/blob/master/LICENSE.txt
 */
package com.carrotsearch.hppc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Bounds the 64-bit requested bit capacity before conversion to int-indexed physical words. */
public class BitSetCapacityWordsTest {
  @Test
  public void normalAndMaximumIndexedWordCounts() {
    assertEquals(0, BitSet.bits2words(0L));
    assertEquals(1, BitSet.bits2words(1L));
    assertEquals(1, BitSet.bits2words(64L));
    assertEquals(2, BitSet.bits2words(65L));
    assertEquals(2, BitSet.bits2words(128L));
    assertEquals(Integer.MAX_VALUE, BitSet.bits2words(((long) Integer.MAX_VALUE) << 6));
  }

  @Test
  public void highBitCountsCannotAliasLowPhysicalCapacity() {
    long[] invalid = {1L << 38, (1L << 38) + 1, Long.MAX_VALUE};
    for (long requestedBits : invalid) {
      assertThrows(IllegalArgumentException.class, () -> BitSet.bits2words(requestedBits));
      assertThrows(IllegalArgumentException.class, () -> new BitSet(requestedBits));
      BitSet bitSet = new BitSet(64L);
      bitSet.set(0L);
      long[] before = bitSet.bits.clone();
      assertThrows(IllegalArgumentException.class, () -> bitSet.ensureCapacity(requestedBits));
      assertArrayEquals(before, bitSet.bits);
      assertTrue(bitSet.get(0L));
    }
  }
}
