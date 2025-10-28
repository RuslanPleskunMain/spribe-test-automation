package com.spribe.utils;

import com.spribe.models.Player;
import com.spribe.models.PlayerBuilder;

/**
 * Utility class for generating test data
 */
public class TestDataGenerator {

    public static Player createValidPlayer() {
        return PlayerBuilder.aPlayer().build();
    }

    public static Player createValidPlayerWithRole(String role) {
        return PlayerBuilder.aPlayer()
                .withRole(role)
                .build();
    }

    public static Player createPlayerWithAgeBelowMin() {
        return PlayerBuilder.aPlayer()
                .withAge(15)
                .build();
    }

    public static Player createPlayerWithAgeAboveMax() {
        return PlayerBuilder.aPlayer()
                .withAge(61)
                .build();
    }

    public static Player createPlayerWithShortPassword() {
        return PlayerBuilder.aPlayer()
                .withPassword("abc123")
                .build();
    }

    public static Player createPlayerWithLongPassword() {
        return PlayerBuilder.aPlayer()
                .withPassword("abcdefghij1234567890")
                .build();
    }

    public static Player createPlayerWithPasswordNoNumbers() {
        return PlayerBuilder.aPlayer()
                .withPassword("abcdefgh")
                .build();
    }

    public static Player createPlayerWithPasswordNoLetters() {
        return PlayerBuilder.aPlayer()
                .withPassword("12345678")
                .build();
    }

    public static Player createPlayerWithPasswordSpecialChars() {
        return PlayerBuilder.aPlayer()
                .withPassword("abc@123!")
                .build();
    }

    public static Player createPlayerWithInvalidGender() {
        return PlayerBuilder.aPlayer()
                .withGender("other")
                .build();
    }

    public static Player createPlayerWithInvalidRole() {
        return PlayerBuilder.aPlayer()
                .withRole("superadmin")
                .build();
    }
}
