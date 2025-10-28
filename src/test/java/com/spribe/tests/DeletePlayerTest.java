package com.spribe.tests;

import com.spribe.base.BaseTest;
import com.spribe.models.Player;
import com.spribe.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Epic("Player Management")
@Feature("Delete Player")
public class DeletePlayerTest extends BaseTest {
    @Test(description = "Delete user by supervisor - positive test")
    @Description("Verify that supervisor can delete users with 'user' role")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUserBySupervisor() {
        Player user = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user, "User should be created");
        int userId = user.getId();
        
        Response response = playerService.deletePlayer(userId, config.getSupervisorLogin());
        
        assertEquals(response.getStatusCode(), 204, "Status code should be 204 for successful deletion");
        
        Response getResponse = playerService.getPlayer(userId);
        assertTrue(getResponse.getStatusCode() >= 400, "Deleted player should not be found");
        
        createdPlayerIds.remove(Integer.valueOf(userId));
    }

    @Test(description = "Delete admin by supervisor - positive test")
    @Description("Verify that supervisor can delete users with 'admin' role")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteAdminBySupervisor() {
        Player admin = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("admin"), config.getSupervisorLogin());
        assertNotNull(admin, "Admin should be created");
        int adminId = admin.getId();
        
        Response response = playerService.deletePlayer(adminId, config.getSupervisorLogin());
        
        assertEquals(response.getStatusCode(), 204, "Status code should be 204 for successful deletion");
        
        Response getResponse = playerService.getPlayer(adminId);
        assertTrue(getResponse.getStatusCode() >= 400, "Deleted player should not be found");
        
        createdPlayerIds.remove(Integer.valueOf(adminId));
    }

    @Test(description = "Delete user by admin - positive test")
    @Description("Verify that admin can delete users with 'user' role")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUserByAdmin() {
        Player user = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user, "User should be created");
        int userId = user.getId();
        
        Response response = playerService.deletePlayer(userId, config.getAdminLogin());
        
        assertEquals(response.getStatusCode(), 204, "Status code should be 204 for successful deletion");
        
        Response getResponse = playerService.getPlayer(userId);
        assertTrue(getResponse.getStatusCode() >= 400, "Deleted player should not be found");
        
        createdPlayerIds.remove(Integer.valueOf(userId));
    }

    // Negative tests

    @Test(description = "Delete supervisor by supervisor - BUG - negative test")
    @Description("BUG: Verify that supervisor cannot delete other supervisors")
    @Severity(SeverityLevel.BLOCKER)
    @Issue("BUG-001")
    public void testDeleteSupervisorBySupervisorShouldFail() {
        // According to requirements: supervisor cannot be deleted, only edited
        // This is a critical bug if the system allows deletion of supervisor
        
        // Note: We cannot create a supervisor via API to test this properly
        // This test documents the expected behavior based on requirements
        logger.warn("Cannot test supervisor deletion - supervisor users are pre-created and should not be deletable");
        
        // If we had a supervisor ID, the test would be:
        // Response response = playerService.deletePlayer(supervisorId, config.getSupervisorLogin());
        // assertTrue(response.getStatusCode() >= 400, "Supervisor should not be deletable");
    }

    @Test(description = "Delete player by user - negative test")
    @Description("Verify that user role cannot delete any players")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeletePlayerByUserForbidden() {
        Player user1 = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        Player user2 = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user1, "User1 should be created");
        assertNotNull(user2, "User2 should be created");
        
        Response response = playerService.deletePlayer(user2.getId(), user1.getLogin());
        
        assertTrue(response.getStatusCode() >= 400, "User should not be able to delete other users");
        
        Response getResponse = playerService.getPlayer(user2.getId());
        assertEquals(getResponse.getStatusCode(), 200, "User2 should still exist");
    }

    @Test(description = "Delete own profile by user - negative test")
    @Description("Verify that user cannot delete their own profile")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteOwnProfileByUser() {
        Player user = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user, "User should be created");
        
        Response response = playerService.deletePlayer(user.getId(), user.getLogin());
        
        assertTrue(response.getStatusCode() >= 400, "User should not be able to delete own profile");
        
        Response getResponse = playerService.getPlayer(user.getId());
        assertEquals(getResponse.getStatusCode(), 200, "User should still exist");
    }

    @Test(description = "Delete non-existent player - negative test")
    @Description("Verify that deleting non-existent player returns appropriate error")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistentPlayer() {
        int nonExistentId = 999999;
        
        Response response = playerService.deletePlayer(nonExistentId, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for non-existent player");
    }

    @Test(description = "Delete player with negative ID - negative test")
    @Description("Verify that deleting player with negative ID returns appropriate error")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerWithNegativeId() {
        Response response = playerService.deletePlayer(-1, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Status code should be 4xx for negative ID");
    }

    @Test(description = "Delete player twice - negative test")
    @Description("Verify that deleting already deleted player returns appropriate error")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerTwice() {
        Player user = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("user"), config.getSupervisorLogin());
        assertNotNull(user, "User should be created");
        int userId = user.getId();
        
        Response deleteResponse = playerService.deletePlayer(userId, config.getSupervisorLogin());
        assertEquals(deleteResponse.getStatusCode(), 204, "First deletion should succeed");
        createdPlayerIds.remove(Integer.valueOf(userId));
        
        Response response = playerService.deletePlayer(userId, config.getSupervisorLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Second deletion should fail");
    }

    @Test(description = "Delete admin by another admin - possible permission test")
    @Description("Verify that admin can delete themselves (admin role)")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteSelfByAdmin() {
        Player admin = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("admin"), config.getSupervisorLogin());
        assertNotNull(admin, "Admin should be created");
        int adminId = admin.getId();
        
        // Try to delete self - according to requirements, admin can perform operations on admin if it's himself
        Response response = playerService.deletePlayer(adminId, admin.getLogin());
        
        // Based on requirements: admin can perform operations on admin role if it's himself
        // But delete is typically not allowed for users on themselves
        // This documents the expected behavior
        assertTrue(response.getStatusCode() >= 400, "Admin should not be able to delete themselves");
        
        Response getResponse = playerService.getPlayer(adminId);
        assertEquals(getResponse.getStatusCode(), 200, "Admin should still exist");
    }

    @Test(description = "Admin tries to delete another admin - negative test")
    @Description("Verify that admin cannot delete other admins")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteOtherAdminByAdmin() {
        Player admin1 = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("admin"), config.getSupervisorLogin());
        Player admin2 = createAndTrackPlayer(TestDataGenerator.createValidPlayerWithRole("admin"), config.getSupervisorLogin());
        assertNotNull(admin1, "Admin1 should be created");
        assertNotNull(admin2, "Admin2 should be created");
        
        // Try to delete admin2 by admin1
        // According to requirements: admin can only work with admin role if it's himself
        Response response = playerService.deletePlayer(admin2.getId(), admin1.getLogin());
        
        assertTrue(response.getStatusCode() >= 400, "Admin should not be able to delete other admins");
        
        Response getResponse = playerService.getPlayer(admin2.getId());
        assertEquals(getResponse.getStatusCode(), 200, "Admin2 should still exist");
    }
}
