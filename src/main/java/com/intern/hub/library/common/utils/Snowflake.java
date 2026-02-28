package com.intern.hub.library.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;

public final class Snowflake {

  private static final long SEQUENCE_BITS   = 12L;
  private static final long MACHINE_ID_BITS = 10L;
  private static final long TIMESTAMP_BITS  = 41L;

  private static final long SEQUENCE_MASK   = (1L << SEQUENCE_BITS) - 1;
  private static final long MACHINE_ID_MASK = (1L << MACHINE_ID_BITS) - 1;

  private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;                          // 12
  private static final long TIMESTAMP_SHIFT  = SEQUENCE_BITS + MACHINE_ID_BITS;        // 22

  private static final long DEFAULT_EPOCH =
      LocalDateTime.of(2025, 1, 1, 0, 0)
          .toInstant(ZoneOffset.UTC)
          .toEpochMilli();

  private final AtomicLong state = new AtomicLong(0L);

  private final long machineIdShifted;
  private final long epoch;

  public Snowflake(long machineId) {
    this(machineId, DEFAULT_EPOCH);
  }


  public Snowflake(long machineId, long customEpoch) {
    if (machineId < 0 || machineId > MACHINE_ID_MASK) {
      throw new IllegalArgumentException("Machine ID must be between 0 and " + MACHINE_ID_MASK);
    }
    this.epoch = customEpoch;
    this.machineIdShifted = machineId << MACHINE_ID_SHIFT; // pre-compute
  }


  public long next() {
    while (true) {
      long now     = currentTime();
      long current = state.get();
      long lastTs = current >>> SEQUENCE_BITS;
      long seq    = current & SEQUENCE_MASK;
      long nextTs;
      long nextSeq;
      if (now > lastTs) {
        nextTs  = now;
        nextSeq = 0L;
      } else {
        nextTs  = lastTs;
        nextSeq = seq + 1;
        if (nextSeq > SEQUENCE_MASK) {
          nextTs  = spinUntilNextMs(lastTs);
          nextSeq = 0L;
        }
      }
      long newState = (nextTs << SEQUENCE_BITS) | nextSeq;
      if (state.compareAndSet(current, newState)) {
        return (nextTs << TIMESTAMP_SHIFT) | machineIdShifted | nextSeq;
      }
    }
  }

  public long extractTimestamp(long id) {
    long relativeTs = (id >>> TIMESTAMP_SHIFT) & ((1L << TIMESTAMP_BITS) - 1);
    return relativeTs + epoch;
  }

  public long extractMachineId(long id) {
    return (id >>> MACHINE_ID_SHIFT) & MACHINE_ID_MASK;
  }

  private long currentTime() {
    return System.currentTimeMillis() - epoch;
  }

  private long spinUntilNextMs(long lastTs) {
    long ts;
    do {
      ts = currentTime();
    } while (ts <= lastTs);
    return ts;
  }

}