package com.intern.hub.library.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RandomGenerator Utility Tests")
class RandomGeneratorTest {

    @Nested
    @DisplayName("generateSecretKey Tests")
    class GenerateSecretKeyTests {

        @Test
        @DisplayName("Should generate non-null secret key")
        void shouldGenerateNonNullSecretKey() {
            String secretKey = RandomGenerator.generateSecretKey();

            assertThat(secretKey).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("Should generate URL-safe Base64 encoded key")
        void shouldGenerateUrlSafeBase64EncodedKey() {
            String secretKey = RandomGenerator.generateSecretKey();

            // URL-safe Base64 uses A-Z, a-z, 0-9, -, _ and no padding (=)
            assertThat(secretKey).matches("^[A-Za-z0-9_-]+$");
        }

        @Test
        @DisplayName("Should generate key that decodes to 64 bytes")
        void shouldGenerateKeyThatDecodesTo64Bytes() {
            String secretKey = RandomGenerator.generateSecretKey();

            byte[] decoded = Base64.getUrlDecoder().decode(secretKey);
            assertThat(decoded).hasSize(64);
        }

        @RepeatedTest(10)
        @DisplayName("Should generate unique keys on each call")
        void shouldGenerateUniqueKeys() {
            Set<String> keys = new HashSet<>();

            for (int i = 0; i < 100; i++) {
                keys.add(RandomGenerator.generateSecretKey());
            }

            assertThat(keys).hasSize(100);
        }
    }

    @Nested
    @DisplayName("randomNumberString Tests")
    class RandomNumberStringTests {

        @ParameterizedTest
        @ValueSource(ints = { 1, 5, 10, 20, 100 })
        @DisplayName("Should generate string of specified length")
        void shouldGenerateStringOfSpecifiedLength(int length) {
            String result = RandomGenerator.randomNumberString(length, true);

            assertThat(result).hasSize(length);
        }

        @Test
        @DisplayName("Should contain only digits")
        void shouldContainOnlyDigits() {
            String result = RandomGenerator.randomNumberString(20, true);

            assertThat(result).matches("^\\d+$");
        }

        @Test
        @DisplayName("Should not have leading zero when disallowed")
        void shouldNotHaveLeadingZeroWhenDisallowed() {
            // Run multiple times to ensure leading zero is never present
            for (int i = 0; i < 100; i++) {
                String result = RandomGenerator.randomNumberString(10, false);

                assertThat(result.charAt(0)).isNotEqualTo('0');
            }
        }

        @Test
        @DisplayName("Should allow leading zero when allowed")
        void shouldAllowLeadingZeroWhenAllowed() {
            // Generate many strings and verify some have leading zeros
            Set<Character> firstChars = new HashSet<>();

            for (int i = 0; i < 1000; i++) {
                String result = RandomGenerator.randomNumberString(10, true);
                firstChars.add(result.charAt(0));
            }

            // With 1000 tries, we should see '0' as first char at least once
            assertThat(firstChars).contains('0');
        }

        @Test
        @DisplayName("Should throw exception for zero length")
        void shouldThrowExceptionForZeroLength() {
            assertThatThrownBy(() -> RandomGenerator.randomNumberString(0, true))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Length must be a positive integer");
        }

        @Test
        @DisplayName("Should throw exception for negative length")
        void shouldThrowExceptionForNegativeLength() {
            assertThatThrownBy(() -> RandomGenerator.randomNumberString(-5, true))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Length must be a positive integer");
        }

        @Test
        @DisplayName("Should generate single digit without leading zero correctly")
        void shouldGenerateSingleDigitWithoutLeadingZeroCorrectly() {
            Set<String> results = new HashSet<>();

            for (int i = 0; i < 100; i++) {
                String result = RandomGenerator.randomNumberString(1, false);
                results.add(result);
                assertThat(result).isNotEqualTo("0");
            }

            // Should only contain 1-9
            assertThat(results).allMatch(s -> s.matches("[1-9]"));
        }
    }

    @Nested
    @DisplayName("randomAlphaNumericString Tests")
    class RandomAlphaNumericStringTests {

        private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

        @ParameterizedTest
        @ValueSource(ints = { 1, 5, 10, 32, 64, 128 })
        @DisplayName("Should generate string of specified length")
        void shouldGenerateStringOfSpecifiedLength(int length) {
            String result = RandomGenerator.randomAlphaNumericString(length);

            assertThat(result).hasSize(length);
        }

        @Test
        @DisplayName("Should contain only alphanumeric characters")
        void shouldContainOnlyAlphanumericCharacters() {
            String result = RandomGenerator.randomAlphaNumericString(100);

            assertThat(result).matches(ALPHANUMERIC_PATTERN);
        }

        @Test
        @DisplayName("Should include uppercase letters")
        void shouldIncludeUppercaseLetters() {
            // Generate long string to ensure we get all character types
            String result = RandomGenerator.randomAlphaNumericString(1000);

            assertThat(result).containsPattern("[A-Z]");
        }

        @Test
        @DisplayName("Should include lowercase letters")
        void shouldIncludeLowercaseLetters() {
            String result = RandomGenerator.randomAlphaNumericString(1000);

            assertThat(result).containsPattern("[a-z]");
        }

        @Test
        @DisplayName("Should include digits")
        void shouldIncludeDigits() {
            String result = RandomGenerator.randomAlphaNumericString(1000);

            assertThat(result).containsPattern("[0-9]");
        }

        @RepeatedTest(10)
        @DisplayName("Should generate unique strings")
        void shouldGenerateUniqueStrings() {
            Set<String> strings = new HashSet<>();

            for (int i = 0; i < 100; i++) {
                strings.add(RandomGenerator.randomAlphaNumericString(32));
            }

            assertThat(strings).hasSize(100);
        }

        @Test
        @DisplayName("Should throw exception for zero length")
        void shouldThrowExceptionForZeroLength() {
            assertThatThrownBy(() -> RandomGenerator.randomAlphaNumericString(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Length must be a positive integer");
        }

        @Test
        @DisplayName("Should throw exception for negative length")
        void shouldThrowExceptionForNegativeLength() {
            assertThatThrownBy(() -> RandomGenerator.randomAlphaNumericString(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Length must be a positive integer");
        }
    }

    @Nested
    @DisplayName("randomSecureBytes Tests")
    class RandomSecureBytesTests {

        @ParameterizedTest
        @ValueSource(ints = { 1, 16, 32, 64, 128, 256 })
        @DisplayName("Should generate byte array of specified length")
        void shouldGenerateByteArrayOfSpecifiedLength(int length) {
            byte[] result = RandomGenerator.randomSecureBytes(length);

            assertThat(result).hasSize(length);
        }

        @Test
        @DisplayName("Should generate non-null byte array")
        void shouldGenerateNonNullByteArray() {
            byte[] result = RandomGenerator.randomSecureBytes(16);

            assertThat(result).isNotNull();
        }

        @RepeatedTest(10)
        @DisplayName("Should generate different byte arrays on each call")
        void shouldGenerateDifferentByteArraysOnEachCall() {
            byte[] result1 = RandomGenerator.randomSecureBytes(32);
            byte[] result2 = RandomGenerator.randomSecureBytes(32);

            assertThat(result1).isNotEqualTo(result2);
        }

        @Test
        @DisplayName("Should generate bytes with good distribution")
        void shouldGenerateBytesWithGoodDistribution() {
            byte[] bytes = RandomGenerator.randomSecureBytes(10000);

            // Count occurrences of each byte value
            int[] counts = new int[256];
            for (byte b : bytes) {
                counts[b & 0xFF]++;
            }

            // Each byte value should appear roughly 10000/256 ≈ 39 times
            // Allow wide variance but ensure no value is completely missing or dominant
            for (int count : counts) {
                assertThat(count).isGreaterThan(0);
                assertThat(count).isLessThan(200); // No single value should dominate
            }
        }

        @Test
        @DisplayName("Should throw exception for zero length")
        void shouldThrowExceptionForZeroLength() {
            assertThatThrownBy(() -> RandomGenerator.randomSecureBytes(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Length must be a positive integer");
        }

        @Test
        @DisplayName("Should throw exception for negative length")
        void shouldThrowExceptionForNegativeLength() {
            assertThatThrownBy(() -> RandomGenerator.randomSecureBytes(-10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Length must be a positive integer");
        }
    }

    @Nested
    @DisplayName("randomBoolean Tests")
    class RandomBooleanTests {

        @Test
        @DisplayName("Should return boolean value")
        void shouldReturnBooleanValue() {
            boolean result = RandomGenerator.randomBoolean();

            // Just verify it returns a valid boolean (true or false)
            assertThat(result).isIn(true, false);
        }

        @Test
        @DisplayName("Should generate both true and false over many calls")
        void shouldGenerateBothTrueAndFalse() {
            boolean foundTrue = false;
            boolean foundFalse = false;

            for (int i = 0; i < 100; i++) {
                boolean result = RandomGenerator.randomBoolean();
                if (result) {
                    foundTrue = true;
                } else {
                    foundFalse = true;
                }
                if (foundTrue && foundFalse) {
                    break;
                }
            }

            assertThat(foundTrue).isTrue();
            assertThat(foundFalse).isTrue();
        }

        @Test
        @DisplayName("Should have roughly 50/50 distribution")
        void shouldHaveRoughlyEqualDistribution() {
            int trueCount = 0;
            int totalCount = 10000;

            for (int i = 0; i < totalCount; i++) {
                if (RandomGenerator.randomBoolean()) {
                    trueCount++;
                }
            }

            // Expect roughly 50% true with some variance
            double percentage = (double) trueCount / totalCount;
            assertThat(percentage).isBetween(0.45, 0.55);
        }
    }

    @Nested
    @DisplayName("Security Tests")
    class SecurityTests {

        @Test
        @DisplayName("Generated secrets should be cryptographically random")
        void generatedSecretsShouldBeCryptographicallyRandom() {
            // Generate many secrets and verify high entropy
            Set<String> secrets = new HashSet<>();

            for (int i = 0; i < 1000; i++) {
                secrets.add(RandomGenerator.generateSecretKey());
            }

            // All should be unique (collision in 1000 tries would indicate weak randomness)
            assertThat(secrets).hasSize(1000);
        }

        @Test
        @DisplayName("Random bytes should not have predictable patterns")
        void randomBytesShouldNotHavePredictablePatterns() {
            byte[] bytes1 = RandomGenerator.randomSecureBytes(32);
            byte[] bytes2 = RandomGenerator.randomSecureBytes(32);
            byte[] bytes3 = RandomGenerator.randomSecureBytes(32);

            // Verify no two are equal
            assertThat(bytes1).isNotEqualTo(bytes2);
            assertThat(bytes2).isNotEqualTo(bytes3);
            assertThat(bytes1).isNotEqualTo(bytes3);
        }
    }
}
