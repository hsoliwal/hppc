/*
 * HPPC
 * Copyright (C) 2026 Hitesh Soliwal and contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.carrotsearch.hppc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.Test;

/** Exhaustive logical word-offset and packed-mask differential tests. */
public class BitSetMaskScanTest {
  @Test
  public void singleSetBitAgainstJdkAcrossEveryStartOffset() {
    for (int bit = 0; bit < 512; bit++) {
      BitSet actual = new BitSet();
      java.util.BitSet oracle = new java.util.BitSet();
      actual.set(bit);
      oracle.set(bit);
      for (int start = 0; start <= 576; start++) {
        int wanted = oracle.nextSetBit(start);
        assertEquals(wanted, actual.nextSetBit(start));
        assertEquals((long) wanted, actual.nextSetBit((long) start));
      }
    }
  }

  @Test
  public void mixedSparseWordsAgainstJdk() {
    Random random = new Random(0x53314e45584941L);
    for (int trial = 0; trial < 80; trial++) {
      BitSet actual = new BitSet();
      java.util.BitSet oracle = new java.util.BitSet();
      for (int entry = 0; entry < 3 * trial; entry++) {
        int bit = random.nextInt(640);
        actual.set(bit);
        oracle.set(bit);
      }
      for (int start = 0; start <= 704; start++) {
        int wanted = oracle.nextSetBit(start);
        assertEquals(wanted, actual.nextSetBit(start));
        assertEquals((long) wanted, actual.nextSetBit((long) start));
      }
    }
  }

  @Test
  public void mergedLongIndexOverflowGuardIsPreserved() {
    BitSet actual = new BitSet();
    actual.set(0);
    actual.set(63);
    actual.set(127);
    for (long first : new long[] {1L << 37, 1L << 38, 1L << 40, 1L << 48, Long.MAX_VALUE}) {
      assertEquals(-1L, actual.nextSetBit(first));
    }
  }
}
