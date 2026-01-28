package com.intern.hub.library.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Snowflake ID Generator Tests")
class SnowflakeTest {

    private static final long DEFAULT_EPOCH = LocalDateTime.of(2025, 1, 1, 0, 0)
            .toInstant(ZoneOffset.UTC).toEpochMilli();

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create Snowflake with valid machine ID")
        void shouldCreateWithValidMachineId() {
            assertThatCode(() -> new Snowflake(0)).doesNotThrowAnyException();
            assertThatCode(() -> new Snowflake(512)).doesNotThrowAnyException();
            assertThatCode(() -> new Snowflake(1023)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for negative machine ID")
        void shouldThrowExceptionForNegativeMachineId() {
            assertThatThrownBy(() -> new Snowflake(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Machine ID must be between 0 and 1023");
        }

        @Test
        @DisplayName("Should throw exception for machine ID above max (1023)")
        void shouldThrowExceptionForMachineIdAboveMax() {
            assertThatThrownBy(() -> new Snowflake(1024))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Machine ID must be between 0 and 1023");
        }

        @ParameterizedTest
        @ValueSource(longs = { 0, 1, 100, 500, 1000, 1023 })
        @DisplayName("Should accept valid machine IDs in range 0-1023")
        void shouldAcceptValidMachineIds(long machineId) {
            Snowflake snowflake = new Snowflake(machineId);
            long id = snowflake.next();
            assertThat(snowflake.extractMachineId(id)).isEqualTo(machineId);
        }

        @Test
        @DisplayName("Should create Snowflake with custom epoch")
        void shouldCreateWithCustomEpoch() {
            long customEpoch = System.currentTimeMillis() - 1000;
            Snowflake snowflake = new Snowflake(1, customEpoch);

            long id = snowflake.next();
            long extractedTimestamp = snowflake.extractTimestamp(id);

            assertThat(extractedTimestamp).isGreaterThanOrEqualTo(customEpoch);
        }
    }

    @Nested
    @DisplayName("ID Generation Tests")
    class IdGenerationTests {

        @Test
        @DisplayName("Should generate positive IDs")
        void shouldGeneratePositiveIds() {
            Snowflake snowflake = new Snowflake(1);

            for (int i = 0; i < 100; i++) {
                assertThat(snowflake.next()).isPositive();
            }
        }

        @Test
        @DisplayName("Should generate unique IDs sequentially")
        void shouldGenerateUniqueIdsSequentially() {
            Snowflake snowflake = new Snowflake(1);
            Set<Long> ids = new HashSet<>();

            int count = 10000;
            for (int i = 0; i < count; i++) {
                ids.add(snowflake.next());
            }

            assertThat(ids).hasSize(count);
        }

        @RepeatedTest(5)
        @DisplayName("Should generate unique IDs concurrently")
        void shouldGenerateUniqueIdsConcurrently() throws InterruptedException {
            Snowflake snowflake = new Snowflake(1);
            Set<Long> ids = ConcurrentHashMap.newKeySet();
            int threadCount = 10;
            int idsPerThread = 1000;
            CountDownLatch latch = new CountDownLatch(threadCount);

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int t = 0; t < threadCount; t++) {
                    executor.submit(() -> {
                        try {
                            for (int i = 0; i < idsPerThread; i++) {
                                ids.add(snowflake.next());
                            }
                        } finally {
                            latch.countDown();
                        }
                    });
                }
                latch.await();
            }

            assertThat(ids).hasSize(threadCount * idsPerThread);
        }

        @Test
        @DisplayName("Should generate monotonically increasing IDs")
        void shouldGenerateMonotonicallyIncreasingIds() {
            Snowflake snowflake = new Snowflake(1);
            long previousId = 0;

            for (int i = 0; i < 1000; i++) {
                long currentId = snowflake.next();
                assertThat(currentId).isGreaterThan(previousId);
                previousId = currentId;
            }
        }
    }

    @Nested
    @DisplayName("Timestamp Extraction Tests")
    class TimestampExtractionTests {

        @Test
        @DisplayName("Should extract timestamp correctly")
        void shouldExtractTimestampCorrectly() {
            Snowflake snowflake = new Snowflake(1);
            long beforeGeneration = System.currentTimeMillis();
            long id = snowflake.next();
            long afterGeneration = System.currentTimeMillis();

            long extractedTimestamp = snowflake.extractTimestamp(id);

            assertThat(extractedTimestamp)
                    .isGreaterThanOrEqualTo(beforeGeneration)
                    .isLessThanOrEqualTo(afterGeneration);
        }

        @Test
        @DisplayName("Should extract timestamp with custom epoch")
        void shouldExtractTimestampWithCustomEpoch() {
            long customEpoch = LocalDateTime.of(2020, 1, 1, 0, 0)
                    .toInstant(ZoneOffset.UTC).toEpochMilli();
            Snowflake snowflake = new Snowflake(1, customEpoch);

            long id = snowflake.next();
            long extractedTimestamp = snowflake.extractTimestamp(id);

            assertThat(extractedTimestamp).isGreaterThan(customEpoch);
        }
    }

    @Nested
    @DisplayName("Machine ID Extraction Tests")
    class MachineIdExtractionTests {

        @ParameterizedTest
        @ValueSource(longs = { 0, 1, 100, 500, 1023 })
        @DisplayName("Should extract machine ID correctly")
        void shouldExtractMachineIdCorrectly(long machineId) {
            Snowflake snowflake = new Snowflake(machineId);
            long id = snowflake.next();

            assertThat(snowflake.extractMachineId(id)).isEqualTo(machineId);
        }

        @Test
        @DisplayName("Different machines should produce different machine IDs in output")
        void differentMachinesShouldProduceDifferentMachineIdsInOutput() {
            Snowflake snowflake1 = new Snowflake(1);
            Snowflake snowflake2 = new Snowflake(2);

            long id1 = snowflake1.next();
            long id2 = snowflake2.next();

            assertThat(snowflake1.extractMachineId(id1)).isEqualTo(1);
            assertThat(snowflake2.extractMachineId(id2)).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Sequence Overflow Tests")
    class SequenceOverflowTests {

        @Test
        @DisplayName("Should handle sequence overflow within same millisecond")
        void shouldHandleSequenceOverflow() {
            Snowflake snowflake = new Snowflake(1);
            Set<Long> ids = new HashSet<>();

            // Generate more than 4096 IDs (sequence max) quickly
            int count = 5000;
            for (int i = 0; i < count; i++) {
                ids.add(snowflake.next());
            }

            // All IDs should still be unique
            assertThat(ids).hasSize(count);
        }
    }

    @Nested
    @DisplayName("ID Structure Tests")
    class IdStructureTests {

        @Test
        @DisplayName("Should have correct bit structure")
        void shouldHaveCorrectBitStructure() {
            Snowflake snowflake = new Snowflake(1);
            long id = snowflake.next();

            // ID should be a 64-bit positive number (sign bit is 0)
            assertThat(id).isPositive();

            // Extracted values should be valid
            long timestamp = snowflake.extractTimestamp(id);
            long machineId = snowflake.extractMachineId(id);

            assertThat(timestamp).isGreaterThan(DEFAULT_EPOCH);
            assertThat(machineId).isBetween(0L, 1023L);
        }
    }
}
