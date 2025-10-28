package com.spribe.tests;

import com.spribe.base.BaseTest;
import com.spribe.models.Player;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.*;

@Epic("Player Management")
@Feature("Get Player")
public class GetPlayerTest extends BaseTest {

    @Override
    @BeforeMethod
    public void setupMethod(Method method) {
        super.setupMethod(method);
        createTestPlayer();
    }

    @Test(description = "Get existing player by valid ID - positive test")
    @Description("Verify that player can be retrieved by valid player ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPlayerByValidId() {
        Response response = playerService.getPlayer(testPlayer.getId());
        
        assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Player retrievedPlayer = response.as(Player.class);
        assertNotNull(retrievedPlayer, "Retrieved player should not be null");
        assertEquals(retrievedPlayer.getId(), testPlayer.getId(), "Player ID should match");
        assertEquals(retrievedPlayer.getLogin(), testPlayer.getLogin(), "Login should match");
        assertEquals(retrievedPlayer.getScreenName(), testPlayer.getScreenName(), "Screen name should match");
        assertEquals(retrievedPlayer.getAge(), testPlayer.getAge(), "Age should match");
        assertEquals(retrievedPlayer.getGender(), testPlayer.getGender(), "Gender should match");
        assertEquals(retrievedPlayer.getRole(), testPlayer.getRole(), "Role should match");
    }

    @Test(description = "Get player multiple times - positive test")
    @Description("Verify that player can be retrieved multiple times with consistent data")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerMultipleTimes() {
        Response response1 = playerService.getPlayer(testPlayer.getId());
        assertEquals(response1.getStatusCode(), 200, "First request status should be 200");
        Player player1 = response1.as(Player.class);
        
        Response response2 = playerService.getPlayer(testPlayer.getId());
        assertEquals(response2.getStatusCode(), 200, "Second request status should be 200");
        Player player2 = response2.as(Player.class);
        
        assertEquals(player1.getId(), player2.getId(), "Player IDs should match");
        assertEquals(player1.getLogin(), player2.getLogin(), "Logins should match");
    }

    // Negative tests

    @Test(description = "Get player with non-existent ID - negative test")
    @Description("Verify that getting player with non-existent ID returns appropriate error")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPlayerWithNonExistentId() {
        int nonExistentId = 999999;
        Response response = playerService.getPlayer(nonExistentId);
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for non-existent player");
    }

    @Test(description = "Get player with negative ID - negative test")
    @Description("Verify that getting player with negative ID returns appropriate error")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerWithNegativeId() {
        Response response = playerService.getPlayer(-1);
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for negative ID");
    }

    @Test(description = "Get player with zero ID - negative test")
    @Description("Verify that getting player with zero ID returns appropriate error")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerWithZeroId() {
        Response response = playerService.getPlayer(0);
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for zero ID");
    }
}
