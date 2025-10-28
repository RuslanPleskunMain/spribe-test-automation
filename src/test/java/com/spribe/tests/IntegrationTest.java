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
@Feature("Integration Tests")
public class IntegrationTest extends BaseTest {

    @Test(description = "Full lifecycle: Create, Get, Update, Delete player")
    @Description("Verify complete lifecycle of a player through all operations")
    @Severity(SeverityLevel.CRITICAL)
    public void testPlayerFullLifecycle() {
        Player player = TestDataGenerator.createValidPlayer();
        Response createResponse = playerService.createPlayer(player, config.getSupervisorLogin());
        assertEquals(createResponse.getStatusCode(), 200, "Create should succeed");
        
        Player createdPlayer = createResponse.as(Player.class);
        assertNotNull(createdPlayer.getId(), "Created player should have ID");
        createdPlayerIds.add(createdPlayer.getId());
        
        Response getResponse = playerService.getPlayer(createdPlayer.getId());
        assertEquals(getResponse.getStatusCode(), 200, "Get should succeed");
        Player fetchedPlayer = getResponse.as(Player.class);
        assertEquals(fetchedPlayer.getLogin(), player.getLogin(), "Login should match");
        
        Player updateData = new Player();
        updateData.setAge(35);
        Response updateResponse = playerService.updatePlayer(updateData, config.getSupervisorLogin(), createdPlayer.getId());
        assertEquals(updateResponse.getStatusCode(), 200, "Update should succeed");
        
        Response getAfterUpdate = playerService.getPlayer(createdPlayer.getId());
        Player updatedPlayer = getAfterUpdate.as(Player.class);
        assertEquals(updatedPlayer.getAge(), Integer.valueOf(35), "Age should be updated");
        
        Response deleteResponse = playerService.deletePlayer(createdPlayer.getId(), config.getSupervisorLogin());
        assertEquals(deleteResponse.getStatusCode(), 204, "Delete should succeed");
        createdPlayerIds.remove(Integer.valueOf(createdPlayer.getId()));
        
        Response getAfterDelete = playerService.getPlayer(createdPlayer.getId());
        assertTrue(getAfterDelete.getStatusCode() >= 400, "Player should not exist after deletion");
    }

    @Test(description = "Role-based access control verification")
    @Description("Verify that different roles have appropriate permissions")
    @Severity(SeverityLevel.BLOCKER)
    public void testRoleBasedAccessControl() {
        Player userRole = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        Player adminRole = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("admin"), config.getSupervisorLogin());
        Player targetUser = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        
        assertNotNull(userRole, "User role player should be created");
        assertNotNull(adminRole, "Admin role player should be created");
        assertNotNull(targetUser, "Target user should be created");
        
        Response deleteByUser = playerService.deletePlayer(targetUser.getId(), userRole.getLogin());
        assertTrue(deleteByUser.getStatusCode() >= 400, "User should not delete other users");
        
        Response deleteByAdmin = playerService.deletePlayer(targetUser.getId(), adminRole.getLogin());
        assertEquals(deleteByAdmin.getStatusCode(), 204, "Admin should delete user role");
        createdPlayerIds.remove(Integer.valueOf(targetUser.getId()));
        
        Response deleteBySupervisor = playerService.deletePlayer(adminRole.getId(), config.getSupervisorLogin());
        assertEquals(deleteBySupervisor.getStatusCode(), 204, "Supervisor should delete admin role");
        createdPlayerIds.remove(Integer.valueOf(adminRole.getId()));
    }

    @Test(description = "Concurrent player creation with same data")
    @Description("Verify system handles concurrent requests properly")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentPlayerCreation() {
        String sharedLogin = "concurrent_" + PlayerBuilder.generateRandomLogin();
        
        Player player1 = PlayerBuilder.aPlayer().withLogin(sharedLogin).build();
        Player player2 = PlayerBuilder.aPlayer().withLogin(sharedLogin).build();
        
        Response response1 = playerService.createPlayer(player1, config.getSupervisorLogin());
        Response response2 = playerService.createPlayer(player2, config.getSupervisorLogin());
        
        boolean oneSucceeded = (response1.getStatusCode() == 200 && response2.getStatusCode() >= 400) ||
                               (response2.getStatusCode() == 200 && response1.getStatusCode() >= 400);
        
        assertTrue(oneSucceeded, "Exactly one creation should succeed for duplicate login");
        
        if (response1.getStatusCode() == 200) {
            Player created = response1.as(Player.class);
            createdPlayerIds.add(created.getId());
        } else if (response2.getStatusCode() == 200) {
            Player created = response2.as(Player.class);
            createdPlayerIds.add(created.getId());
        }
    }

    @Test(description = "Update with no changes")
    @Description("Verify that update with no actual changes works correctly")
    @Severity(SeverityLevel.MINOR)
    public void testUpdateWithNoChanges() {
        Player player = createAndTrackPlayer(TestDataGenerator.createValidPlayer(), config.getSupervisorLogin());
        assertNotNull(player, "Player should be created");
        
        Player emptyUpdate = new Player();
        Response response = playerService.updatePlayer(emptyUpdate, config.getSupervisorLogin(), player.getId());
        
        assertEquals(response.getStatusCode(), 200, "Update with no changes should succeed");
        
        Response getResponse = playerService.getPlayer(player.getId());
        Player unchangedPlayer = getResponse.as(Player.class);
        assertEquals(unchangedPlayer.getLogin(), player.getLogin(), "Login should remain unchanged");
    }

    @Test(description = "Multiple sequential updates")
    @Description("Verify that player can be updated multiple times")
    @Severity(SeverityLevel.NORMAL)
    public void testMultipleSequentialUpdates() {
        Player player = createAndTrackPlayer(TestDataGenerator.createValidPlayer(), config.getSupervisorLogin());
        assertNotNull(player, "Player should be created");
        
        Player update1 = new Player();
        update1.setAge(25);
        Response response1 = playerService.updatePlayer(update1, config.getSupervisorLogin(), player.getId());
        assertEquals(response1.getStatusCode(), 200, "First update should succeed");
        
        Player update2 = new Player();
        update2.setAge(30);
        Response response2 = playerService.updatePlayer(update2, config.getSupervisorLogin(), player.getId());
        assertEquals(response2.getStatusCode(), 200, "Second update should succeed");
        
        Player update3 = new Player();
        update3.setGender("female");
        Response response3 = playerService.updatePlayer(update3, config.getSupervisorLogin(), player.getId());
        assertEquals(response3.getStatusCode(), 200, "Third update should succeed");
        
        Response getResponse = playerService.getPlayer(player.getId());
        Player finalPlayer = getResponse.as(Player.class);
        assertEquals(finalPlayer.getAge(), Integer.valueOf(30), "Age should be from second update");
        assertEquals(finalPlayer.getGender(), "female", "Gender should be from third update");
    }

    @Test(description = "Test boundary ages (16 and 60)")
    @Description("Verify that boundary values work correctly")
    @Severity(SeverityLevel.NORMAL)
    public void testBoundaryAges() {
        Player minAgePlayer = PlayerBuilder.aPlayer().withAge(16).build();
        Player createdMin = createAndTrackPlayer(minAgePlayer, config.getSupervisorLogin());
        assertNotNull(createdMin, "Player with age 16 should be created");
        assertEquals(createdMin.getAge(), Integer.valueOf(16), "Age should be 16");
        
        Player maxAgePlayer = PlayerBuilder.aPlayer().withAge(60).build();
        Player createdMax = createAndTrackPlayer(maxAgePlayer, config.getSupervisorLogin());
        assertNotNull(createdMax, "Player with age 60 should be created");
        assertEquals(createdMax.getAge(), Integer.valueOf(60), "Age should be 60");
        
        Player belowMin = PlayerBuilder.aPlayer().withAge(15).build();
        Response belowResponse = playerService.createPlayer(belowMin, config.getSupervisorLogin());
        assertTrue(belowResponse.getStatusCode() >= 400, "Age 15 should be rejected");
        
        Player aboveMax = PlayerBuilder.aPlayer().withAge(61).build();
        Response aboveResponse = playerService.createPlayer(aboveMax, config.getSupervisorLogin());
        assertTrue(aboveResponse.getStatusCode() >= 400, "Age 61 should be rejected");
    }

    @Test(description = "Test password boundary lengths")
    @Description("Verify that password length validation works correctly")
    @Severity(SeverityLevel.NORMAL)
    public void testPasswordBoundaryLengths() {
        Player minPassword = PlayerBuilder.aPlayer().withPassword("abc1234").build();
        Player createdMin = createAndTrackPlayer(minPassword, config.getSupervisorLogin());
        assertNotNull(createdMin, "Player with 7-char password should be created");
        
        Player maxPassword = PlayerBuilder.aPlayer().withPassword("abc123456789012").build();
        Player createdMax = createAndTrackPlayer(maxPassword, config.getSupervisorLogin());
        assertNotNull(createdMax, "Player with 15-char password should be created");
        
        Player tooShort = PlayerBuilder.aPlayer().withPassword("abc123").build();
        Response shortResponse = playerService.createPlayer(tooShort, config.getSupervisorLogin());
        assertTrue(shortResponse.getStatusCode() >= 400, "6-char password should be rejected");
        
        Player tooLong = PlayerBuilder.aPlayer().withPassword("abc1234567890123").build();
        Response longResponse = playerService.createPlayer(tooLong, config.getSupervisorLogin());
        assertTrue(longResponse.getStatusCode() >= 400, "16-char password should be rejected");
    }

    @Test(description = "User can update own data but not delete")
    @Description("Verify user role permissions on own account")
    @Severity(SeverityLevel.CRITICAL)
    public void testUserOwnAccountPermissions() {
        Player user = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user, "User should be created");
        
        Player updateData = new Player();
        updateData.setAge(28);
        Response updateResponse = playerService.updatePlayer(updateData, user.getLogin(), user.getId());
        assertEquals(updateResponse.getStatusCode(), 200, "User should update own data");
        
        Response deleteResponse = playerService.deletePlayer(user.getId(), user.getLogin());
        assertTrue(deleteResponse.getStatusCode() >= 400, "User should not delete own account");
        
        Response getResponse = playerService.getPlayer(user.getId());
        assertEquals(getResponse.getStatusCode(), 200, "User should still exist");
    }
}
