package com.spribe.tests;

import com.spribe.base.BaseTest;
import com.spribe.models.Player;
import com.spribe.models.PlayerBuilder;
import com.spribe.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Epic("Player Management")
@Feature("Create Player")
public class CreatePlayerTest extends BaseTest {

    @Test(description = "Create player with supervisor role - positive test")
    @Description("Verify that supervisor can successfully create a user")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerBySupervisor() {
        Player player = TestDataGenerator.createValidPlayer();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Player createdPlayer = response.as(Player.class);
        assertNotNull(createdPlayer.getId(), "Player ID should not be null");
        assertEquals(createdPlayer.getLogin(), player.getLogin(), "Login should match");
        assertEquals(createdPlayer.getScreenName(), player.getScreenName(), "Screen name should match");
        assertEquals(createdPlayer.getAge(), player.getAge(), "Age should match");
        assertEquals(createdPlayer.getGender(), player.getGender(), "Gender should match");
        assertEquals(createdPlayer.getRole(), player.getRole(), "Role should match");
        
        createdPlayerIds.add(createdPlayer.getId());
    }

    @Test(description = "Create player with admin role - positive test")
    @Description("Verify that admin can successfully create a user")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerByAdmin() {
        Player player = TestDataGenerator.createValidPlayer();
        
        Response response = playerService.createPlayer(player, config.getAdminLogin());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Player createdPlayer = response.as(Player.class);
        assertNotNull(createdPlayer.getId(), "Player ID should not be null");
        assertEquals(createdPlayer.getLogin(), player.getLogin(), "Login should match");
        
        createdPlayerIds.add(createdPlayer.getId());
    }

    @Test(description = "Create player with admin role assignment")
    @Description("Verify that admin role can be assigned to a new player")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithAdminRole() {
        Player player = TestDataGenerator.createValidPlayerWithRole("admin");
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Player createdPlayer = response.as(Player.class);
        assertEquals(createdPlayer.getRole(), "admin", "Role should be admin");
        
        createdPlayerIds.add(createdPlayer.getId());
    }

    @Test(description = "Create player with minimum valid age (16)")
    @Description("Verify that player can be created with age 16 (boundary value)")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithMinAge() {
        Player player = PlayerBuilder.aPlayer().withAge(16).build();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Player createdPlayer = response.as(Player.class);
        assertEquals(createdPlayer.getAge(), Integer.valueOf(16), "Age should be 16");
        
        createdPlayerIds.add(createdPlayer.getId());
    }

    @Test(description = "Create player with maximum valid age (60)")
    @Description("Verify that player can be created with age 60 (boundary value)")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithMaxAge() {
        Player player = PlayerBuilder.aPlayer().withAge(60).build();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Player createdPlayer = response.as(Player.class);
        assertEquals(createdPlayer.getAge(), Integer.valueOf(60), "Age should be 60");
        
        createdPlayerIds.add(createdPlayer.getId());
    }

    @Test(description = "Create player with female gender")
    @Description("Verify that player can be created with female gender")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithFemaleGender() {
        Player player = PlayerBuilder.aPlayer().withGender("female").build();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Player createdPlayer = response.as(Player.class);
        assertEquals(createdPlayer.getGender(), "female", "Gender should be female");
        
        createdPlayerIds.add(createdPlayer.getId());
    }

    // Negative tests

    @Test(description = "Create player by user without permissions - negative test")
    @Description("Verify that user role cannot create new players")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerByUserForbidden() {
        // First create a user with 'user' role
        Player userPlayer = TestDataGenerator.createValidPlayerWithRole("user");
        Player createdUser = createAndTrackPlayer(userPlayer, config.getSupervisorLogin());
        assertNotNull(createdUser, "User player should be created");

        Player newPlayer = TestDataGenerator.createValidPlayer();
        Response response = playerService.createPlayer(newPlayer, createdUser.getLogin());
        
        assertTrue(response.getStatusCode() == 403 || response.getStatusCode() == 401, 
                "Status code should be 403 or 401 for forbidden action");
    }

    @Test(description = "Create player with age below minimum (15) - negative test")
    @Description("Verify that player cannot be created with age below 16")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithAgeBelowMin() {
        Player player = TestDataGenerator.createPlayerWithAgeBelowMin();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for invalid age");
    }

    @Test(description = "Create player with age above maximum (61) - negative test")
    @Description("Verify that player cannot be created with age above 60")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithAgeAboveMax() {
        Player player = TestDataGenerator.createPlayerWithAgeAboveMax();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for invalid age");
    }

    @Test(description = "Create player with duplicate login - negative test")
    @Description("Verify that player cannot be created with duplicate login")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithDuplicateLogin() {
        Player player1 = PlayerBuilder.aPlayer().build();
        Player createdPlayer = createAndTrackPlayer(player1, config.getSupervisorLogin());
        assertNotNull(createdPlayer, "First player should be created");

        Player player2 = PlayerBuilder.aPlayer()
                .withLogin(createdPlayer.getLogin())
                .build();
        
        Response response = playerService.createPlayer(player2, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for duplicate login");
    }

    @Test(description = "Create player with duplicate screenName - negative test")
    @Description("Verify that player cannot be created with duplicate screenName")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithDuplicateScreenName() {
        Player player1 = PlayerBuilder.aPlayer().build();
        Player createdPlayer = createAndTrackPlayer(player1, config.getSupervisorLogin());
        assertNotNull(createdPlayer, "First player should be created");

        Player player2 = PlayerBuilder.aPlayer()
                .withScreenName(createdPlayer.getScreenName())
                .build();
        
        Response response = playerService.createPlayer(player2, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for duplicate screenName");
    }

    @Test(description = "Create player with short password - negative test")
    @Description("Verify that player cannot be created with password shorter than 7 characters")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithShortPassword() {
        Player player = TestDataGenerator.createPlayerWithShortPassword();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for short password");
    }

    @Test(description = "Create player with long password - negative test")
    @Description("Verify that player cannot be created with password longer than 15 characters")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithLongPassword() {
        Player player = TestDataGenerator.createPlayerWithLongPassword();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for long password");
    }

    @Test(description = "Create player with password without numbers - negative test")
    @Description("Verify that player cannot be created with password without numbers")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithPasswordNoNumbers() {
        Player player = TestDataGenerator.createPlayerWithPasswordNoNumbers();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for password without numbers");
    }

    @Test(description = "Create player with password without letters - negative test")
    @Description("Verify that player cannot be created with password without letters")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithPasswordNoLetters() {
        Player player = TestDataGenerator.createPlayerWithPasswordNoLetters();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for password without letters");
    }

    @Test(description = "Create player with password with special characters - negative test")
    @Description("Verify that player cannot be created with password containing special characters")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithPasswordSpecialChars() {
        Player player = TestDataGenerator.createPlayerWithPasswordSpecialChars();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for password with special characters");
    }

    @Test(description = "Create player with invalid gender - negative test")
    @Description("Verify that player cannot be created with gender other than 'male' or 'female'")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithInvalidGender() {
        Player player = TestDataGenerator.createPlayerWithInvalidGender();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for invalid gender");
    }

    @Test(description = "Create player with invalid role - negative test")
    @Description("Verify that player cannot be created with role other than 'admin' or 'user'")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithInvalidRole() {
        Player player = TestDataGenerator.createPlayerWithInvalidRole();
        
        Response response = playerService.createPlayer(player, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for invalid role");
    }
}
