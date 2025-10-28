package com.spribe.models;

import java.util.Random;
import java.util.UUID;

/**
 * Builder for Player objects to facilitate test data creation
 */
public class PlayerBuilder {
    private String login;
    private String password;
    private String screenName;
    private String gender;
    private Integer age;
    private String role;

    private static final Random random = new Random();

    public PlayerBuilder() {
        this.login = generateRandomLogin();
        this.password = generateValidPassword();
        this.screenName = generateRandomScreenName();
        this.gender = "male";
        this.age = 25;
        this.role = "user";
    }

    public static PlayerBuilder aPlayer() {
        return new PlayerBuilder();
    }

    public PlayerBuilder withLogin(String login) {
        this.login = login;
        return this;
    }

    public PlayerBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public PlayerBuilder withScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public PlayerBuilder withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public PlayerBuilder withAge(Integer age) {
        this.age = age;
        return this;
    }

    public PlayerBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public Player build() {
        return new Player(login, password, screenName, gender, age, role);
    }

    public static String generateRandomLogin() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateRandomScreenName() {
        return "screen_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateValidPassword() {
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        StringBuilder password = new StringBuilder();
        
        password.append(letters.charAt(random.nextInt(letters.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        
        int length = 7 + random.nextInt(9);
        String combined = letters + numbers;
        
        for (int i = password.length(); i < length; i++) {
            password.append(combined.charAt(random.nextInt(combined.length())));
        }
        
        return password.toString();
    }

    public static int generateValidAge() {
        return 16 + random.nextInt(45);
    }
}
