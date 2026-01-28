package com.intern.hub.library.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DateTimeHelper Utility Tests")
class DateTimeHelperTest {

    private static final long KNOWN_EPOCH_MILLIS = 1704067200000L; // 2024-01-01T00:00:00Z
    private static final String KNOWN_ISO_STRING = "2024-01-01T00:00:00Z";

    @Nested
    @DisplayName("toInstant Tests")
    class ToInstantTests {

        @Test
        @DisplayName("Should convert epoch millis to Instant")
        void shouldConvertEpochMillisToInstant() {
            Instant result = DateTimeHelper.toInstant(KNOWN_EPOCH_MILLIS);

            assertThat(result).isEqualTo(Instant.ofEpochMilli(KNOWN_EPOCH_MILLIS));
            assertThat(result.toEpochMilli()).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should handle zero epoch millis")
        void shouldHandleZeroEpochMillis() {
            Instant result = DateTimeHelper.toInstant(0L);

            assertThat(result).isEqualTo(Instant.EPOCH);
        }

        @Test
        @DisplayName("Should handle negative epoch millis (before 1970)")
        void shouldHandleNegativeEpochMillis() {
            long negativeMills = -86400000L; // 1 day before epoch
            Instant result = DateTimeHelper.toInstant(negativeMills);

            assertThat(result.toEpochMilli()).isEqualTo(negativeMills);
        }
    }

    @Nested
    @DisplayName("toLocalDateTime Tests")
    class ToLocalDateTimeTests {

        @Test
        @DisplayName("Should convert epoch millis to LocalDateTime in system timezone")
        void shouldConvertEpochMillisToLocalDateTime() {
            LocalDateTime result = DateTimeHelper.toLocalDateTime(KNOWN_EPOCH_MILLIS);

            // Convert back and verify
            long roundTrip = DateTimeHelper.from(result);

            // Due to timezone differences, roundtrip should give same or close value
            assertThat(roundTrip).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should return consistent results for same input")
        void shouldReturnConsistentResults() {
            LocalDateTime result1 = DateTimeHelper.toLocalDateTime(KNOWN_EPOCH_MILLIS);
            LocalDateTime result2 = DateTimeHelper.toLocalDateTime(KNOWN_EPOCH_MILLIS);

            assertThat(result1).isEqualTo(result2);
        }
    }

    @Nested
    @DisplayName("toOffsetDateTime Tests")
    class ToOffsetDateTimeTests {

        @Test
        @DisplayName("Should convert epoch millis to OffsetDateTime in UTC")
        void shouldConvertEpochMillisToOffsetDateTime() {
            OffsetDateTime result = DateTimeHelper.toOffsetDateTime(KNOWN_EPOCH_MILLIS);

            assertThat(result.getOffset()).isEqualTo(ZoneOffset.UTC);
            assertThat(result.toInstant().toEpochMilli()).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should have correct date components for known timestamp")
        void shouldHaveCorrectDateComponents() {
            OffsetDateTime result = DateTimeHelper.toOffsetDateTime(KNOWN_EPOCH_MILLIS);

            assertThat(result.getYear()).isEqualTo(2024);
            assertThat(result.getMonth()).isEqualTo(Month.JANUARY);
            assertThat(result.getDayOfMonth()).isEqualTo(1);
            assertThat(result.getHour()).isZero();
            assertThat(result.getMinute()).isZero();
            assertThat(result.getSecond()).isZero();
        }
    }

    @Nested
    @DisplayName("toZonedDateTime Tests")
    class ToZonedDateTimeTests {

        @Test
        @DisplayName("Should convert epoch millis to ZonedDateTime in system timezone")
        void shouldConvertEpochMillisToZonedDateTime() {
            ZonedDateTime result = DateTimeHelper.toZonedDateTime(KNOWN_EPOCH_MILLIS);

            assertThat(result.toInstant().toEpochMilli()).isEqualTo(KNOWN_EPOCH_MILLIS);
            assertThat(result.getZone()).isEqualTo(DateTimeHelper.getDefaultZone());
        }
    }

    @Nested
    @DisplayName("toDate Tests")
    class ToDateTests {

        @Test
        @DisplayName("Should convert epoch millis to Date")
        void shouldConvertEpochMillisToDate() {
            Date result = DateTimeHelper.toDate(KNOWN_EPOCH_MILLIS);

            assertThat(result.getTime()).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should handle zero epoch millis")
        void shouldHandleZeroEpochMillis() {
            Date result = DateTimeHelper.toDate(0L);

            assertThat(result.getTime()).isZero();
        }
    }

    @Nested
    @DisplayName("toISOString Tests")
    class ToIsoStringTests {

        @Test
        @DisplayName("Should convert epoch millis to ISO string in UTC")
        void shouldConvertEpochMillisToIsoString() {
            String result = DateTimeHelper.toISOString(KNOWN_EPOCH_MILLIS);

            assertThat(result).isEqualTo(KNOWN_ISO_STRING);
        }

        @Test
        @DisplayName("Should format with proper ISO 8601 format")
        void shouldFormatWithProperIsoFormat() {
            String result = DateTimeHelper.toISOString(KNOWN_EPOCH_MILLIS);

            // ISO format: YYYY-MM-DDTHH:MM:SSZ
            assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z");
        }
    }

    @Nested
    @DisplayName("from(Instant) Tests")
    class FromInstantTests {

        @Test
        @DisplayName("Should convert Instant to epoch millis")
        void shouldConvertInstantToEpochMillis() {
            Instant instant = Instant.ofEpochMilli(KNOWN_EPOCH_MILLIS);

            long result = DateTimeHelper.from(instant);

            assertThat(result).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should handle Instant.EPOCH")
        void shouldHandleInstantEpoch() {
            long result = DateTimeHelper.from(Instant.EPOCH);

            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("from(LocalDateTime) Tests")
    class FromLocalDateTimeTests {

        @Test
        @DisplayName("Should convert LocalDateTime to epoch millis")
        void shouldConvertLocalDateTimeToEpochMillis() {
            LocalDateTime localDateTime = DateTimeHelper.toLocalDateTime(KNOWN_EPOCH_MILLIS);

            long result = DateTimeHelper.from(localDateTime);

            assertThat(result).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should be inverse of toLocalDateTime")
        void shouldBeInverseOfToLocalDateTime() {
            long original = System.currentTimeMillis();
            LocalDateTime intermediate = DateTimeHelper.toLocalDateTime(original);
            long result = DateTimeHelper.from(intermediate);

            assertThat(result).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("from(ZonedDateTime) Tests")
    class FromZonedDateTimeTests {

        @Test
        @DisplayName("Should convert ZonedDateTime to epoch millis")
        void shouldConvertZonedDateTimeToEpochMillis() {
            ZonedDateTime zonedDateTime = DateTimeHelper.toZonedDateTime(KNOWN_EPOCH_MILLIS);

            long result = DateTimeHelper.from(zonedDateTime);

            assertThat(result).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should handle different timezone correctly")
        void shouldHandleDifferentTimezoneCorrectly() {
            ZonedDateTime newYork = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.of("America/New_York"));
            ZonedDateTime tokyo = newYork.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));

            long fromNewYork = DateTimeHelper.from(newYork);
            long fromTokyo = DateTimeHelper.from(tokyo);

            assertThat(fromNewYork).isEqualTo(fromTokyo);
        }
    }

    @Nested
    @DisplayName("from(OffsetDateTime) Tests")
    class FromOffsetDateTimeTests {

        @Test
        @DisplayName("Should convert OffsetDateTime to epoch millis")
        void shouldConvertOffsetDateTimeToEpochMillis() {
            OffsetDateTime offsetDateTime = DateTimeHelper.toOffsetDateTime(KNOWN_EPOCH_MILLIS);

            long result = DateTimeHelper.from(offsetDateTime);

            assertThat(result).isEqualTo(KNOWN_EPOCH_MILLIS);
        }
    }

    @Nested
    @DisplayName("from(Date) Tests")
    class FromDateTests {

        @Test
        @DisplayName("Should convert Date to epoch millis")
        void shouldConvertDateToEpochMillis() {
            Date date = new Date(KNOWN_EPOCH_MILLIS);

            long result = DateTimeHelper.from(date);

            assertThat(result).isEqualTo(KNOWN_EPOCH_MILLIS);
        }
    }

    @Nested
    @DisplayName("fromISOString Tests")
    class FromIsoStringTests {

        @Test
        @DisplayName("Should convert ISO string to epoch millis")
        void shouldConvertIsoStringToEpochMillis() {
            long result = DateTimeHelper.fromISOString(KNOWN_ISO_STRING);

            assertThat(result).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should handle ISO string with timezone offset")
        void shouldHandleIsoStringWithTimezoneOffset() {
            String isoWithOffset = "2024-01-01T05:00:00+05:00"; // Same as 2024-01-01T00:00:00Z
            long result = DateTimeHelper.fromISOString(isoWithOffset);

            assertThat(result).isEqualTo(KNOWN_EPOCH_MILLIS);
        }

        @Test
        @DisplayName("Should throw exception for invalid ISO string")
        void shouldThrowExceptionForInvalidIsoString() {
            assertThatThrownBy(() -> DateTimeHelper.fromISOString("invalid"))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("currentTimeMillis Tests")
    class CurrentTimeMillisTests {

        @Test
        @DisplayName("Should return current time in millis")
        void shouldReturnCurrentTimeInMillis() {
            long before = System.currentTimeMillis();
            long result = DateTimeHelper.currentTimeMillis();
            long after = System.currentTimeMillis();

            assertThat(result)
                    .isGreaterThanOrEqualTo(before)
                    .isLessThanOrEqualTo(after);
        }
    }

    @Nested
    @DisplayName("now Tests")
    class NowTests {

        @Test
        @DisplayName("Should return current LocalDateTime")
        void shouldReturnCurrentLocalDateTime() {
            LocalDateTime before = LocalDateTime.now(DateTimeHelper.getDefaultZone());
            LocalDateTime result = DateTimeHelper.now();
            LocalDateTime after = LocalDateTime.now(DateTimeHelper.getDefaultZone());

            assertThat(result)
                    .isAfterOrEqualTo(before)
                    .isBeforeOrEqualTo(after);
        }
    }

    @Nested
    @DisplayName("getDefaultZone Tests")
    class GetDefaultZoneTests {

        @Test
        @DisplayName("Should return system default zone")
        void shouldReturnSystemDefaultZone() {
            ZoneId result = DateTimeHelper.getDefaultZone();

            assertThat(result).isEqualTo(ZoneId.systemDefault());
        }

        @Test
        @DisplayName("Should return consistent zone")
        void shouldReturnConsistentZone() {
            ZoneId result1 = DateTimeHelper.getDefaultZone();
            ZoneId result2 = DateTimeHelper.getDefaultZone();

            assertThat(result1).isEqualTo(result2);
        }
    }

    @Nested
    @DisplayName("Round Trip Conversion Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Instant roundtrip should preserve value")
        void instantRoundtripShouldPreserveValue() {
            long original = System.currentTimeMillis();
            Instant instant = DateTimeHelper.toInstant(original);
            long result = DateTimeHelper.from(instant);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("ZonedDateTime roundtrip should preserve value")
        void zonedDateTimeRoundtripShouldPreserveValue() {
            long original = System.currentTimeMillis();
            ZonedDateTime zdt = DateTimeHelper.toZonedDateTime(original);
            long result = DateTimeHelper.from(zdt);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("Date roundtrip should preserve value")
        void dateRoundtripShouldPreserveValue() {
            long original = System.currentTimeMillis();
            Date date = DateTimeHelper.toDate(original);
            long result = DateTimeHelper.from(date);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("ISO string roundtrip should preserve value")
        void isoStringRoundtripShouldPreserveValue() {
            // Use a known value to avoid millisecond precision issues
            long original = KNOWN_EPOCH_MILLIS;
            String isoString = DateTimeHelper.toISOString(original);
            long result = DateTimeHelper.fromISOString(isoString);

            assertThat(result).isEqualTo(original);
        }
    }
}
