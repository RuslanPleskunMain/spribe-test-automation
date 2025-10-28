package com.spribe.tests;

import com.spribe.base.BaseTest;
import com.spribe.models.Player;
import com.spribe.models.PlayerBuilder;
import com.spribe.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.*;

@Epic("Player Management")
@Feature("Update Player")
public class UpdatePlayerTest extends BaseTest {

    @Override
    @BeforeMethod
    public void setupMethod(Method method) {
        super.setupMethod(method);
        createTestPlayer();
    }

    @Test(description = "Update player age by supervisor - positive test")
    @Description("Verify that supervisor can update player age")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerAgeBySupervisor() {
        Player updateData = new Player();
        updateData.setAge(30);
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        
        Response getResponse = playerService.getPlayer(testPlayer.getId());
        Player updatedPlayer = getResponse.as(Player.class);
        assertEquals(updatedPlayer.getAge(), Integer.valueOf(30), "Age should be updated to 30");
    }

    @Test(description = "Update player screenName by supervisor - positive test")
    @Description("Verify that supervisor can update player screenName")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerScreenNameBySupervisor() {
        Player updateData = new Player();
        String newScreenName = "updated_" + PlayerBuilder.generateRandomScreenName();
        updateData.setScreenName(newScreenName);
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        
        Response getResponse = playerService.getPlayer(testPlayer.getId());
        Player updatedPlayer = getResponse.as(Player.class);
        assertEquals(updatedPlayer.getScreenName(), newScreenName, "ScreenName should be updated");
    }

    @Test(description = "Update player gender by supervisor - positive test")
    @Description("Verify that supervisor can update player gender")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerGenderBySupervisor() {
        Player updateData = new Player();
        updateData.setGender("female");
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        
        Response getResponse = playerService.getPlayer(testPlayer.getId());
        Player updatedPlayer = getResponse.as(Player.class);
        assertEquals(updatedPlayer.getGender(), "female", "Gender should be updated to female");
    }

    @Test(description = "Update player password by supervisor - positive test")
    @Description("Verify that supervisor can update player password")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePlayerPasswordBySupervisor() {
        Player updateData = new Player();
        updateData.setPassword(PlayerBuilder.generateValidPassword());
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
    }

    @Test(description = "Update player by admin - positive test")
    @Description("Verify that admin can update user role players")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerByAdmin() {
        Player updateData = new Player();
        updateData.setAge(35);
        
        Response response = playerService.updatePlayer(updateData, config.getAdminLogin(), testPlayer.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        
        Response getResponse = playerService.getPlayer(testPlayer.getId());
        Player updatedPlayer = getResponse.as(Player.class);
        assertEquals(updatedPlayer.getAge(), Integer.valueOf(35), "Age should be updated to 35");
    }

    @Test(description = "Update own profile by user - positive test")
    @Description("Verify that user can update their own profile")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateOwnProfileByUser() {
        Player user = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user, "User should be created");
        
        Player updateData = new Player();
        updateData.setAge(28);
        
        Response response = playerService.updatePlayer(updateData, user.getLogin(), user.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        
        Response getResponse = playerService.getPlayer(user.getId());
        Player updatedUser = getResponse.as(Player.class);
        assertEquals(updatedUser.getAge(), Integer.valueOf(28), "Age should be updated to 28");
    }

    @Test(description = "Update multiple fields at once - positive test")
    @Description("Verify that multiple fields can be updated in a single request")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateMultipleFields() {
        Player updateData = new Player();
        updateData.setAge(40);
        updateData.setGender("female");
        String newScreenName = "multi_" + PlayerBuilder.generateRandomScreenName();
        updateData.setScreenName(newScreenName);
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        
        Response getResponse = playerService.getPlayer(testPlayer.getId());
        Player updatedPlayer = getResponse.as(Player.class);
        assertEquals(updatedPlayer.getAge(), Integer.valueOf(40), "Age should be updated");
        assertEquals(updatedPlayer.getGender(), "female", "Gender should be updated");
        assertEquals(updatedPlayer.getScreenName(), newScreenName, "ScreenName should be updated");
    }

    // Negative tests

    @Test(description = "Update player by user without permissions - negative test")
    @Description("Verify that user cannot update other players")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerByUserForbidden() {
        Player user = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user, "User should be created");
        
        Player updateData = new Player();
        updateData.setAge(50);
        
        Response response = playerService.updatePlayer(updateData, user.getLogin(), testPlayer.getId());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for forbidden action");
    }

    @Test(description = "Update player with invalid age below minimum - negative test")
    @Description("Verify that player cannot be updated with age below 16")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerWithAgeBelowMin() {
        Player updateData = new Player();
        updateData.setAge(15);
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for invalid age");
    }

    @Test(description = "Update player with invalid age above maximum - negative test")
    @Description("Verify that player cannot be updated with age above 60")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerWithAgeAboveMax() {
        Player updateData = new Player();
        updateData.setAge(61);
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for invalid age");
    }

    @Test(description = "Update player with invalid gender - negative test")
    @Description("Verify that player cannot be updated with invalid gender")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerWithInvalidGender() {
        Player updateData = new Player();
        updateData.setGender("other");
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for invalid gender");
    }

    @Test(description = "Update player with duplicate screenName - negative test")
    @Description("Verify that player cannot be updated with duplicate screenName")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerWithDuplicateScreenName() {
        Player anotherPlayer = createAndTrackPlayer(TestDataGenerator.createValidPlayer(), config.getSupervisorLogin());
        assertNotNull(anotherPlayer, "Another player should be created");
        
        Player updateData = new Player();
        updateData.setScreenName(anotherPlayer.getScreenName());
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for duplicate screenName");
    }

    @Test(description = "Update player with non-existent ID - negative test")
    @Description("Verify that update fails for non-existent player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePlayerWithNonExistentId() {
        Player updateData = new Player();
        updateData.setAge(25);
        
        int nonExistentId = 999999;
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), nonExistentId);
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for non-existent player");
    }

    @Test(description = "Update player with short password - negative test")
    @Description("Verify that player cannot be updated with password shorter than 7 characters")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePlayerWithShortPassword() {
        Player updateData = new Player();
        updateData.setPassword("abc123");
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for short password");
    }

    @Test(description = "Update player with long password - negative test")
    @Description("Verify that player cannot be updated with password longer than 15 characters")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePlayerWithLongPassword() {
        Player updateData = new Player();
        updateData.setPassword("abcdefghij1234567890");
        
        Response response = playerService.updatePlayer(updateData, config.getSupervisorLogin(), testPlayer.getId());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for long password");
    }
}
