package com.spribe.base;

import com.spribe.api.ApiClient;
import com.spribe.api.PlayerService;
import com.spribe.config.ConfigManager;
import com.spribe.models.Player;
import com.spribe.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;

public abstract class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected PlayerService playerService;
    protected ConfigManager config;
    protected List<Integer> createdPlayerIds;
    protected Player testPlayer;

    @BeforeClass
    public void setupClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
        ApiClient.setupRestAssured();
        playerService = new PlayerService();
        config = ConfigManager.getInstance();
        createdPlayerIds = new ArrayList<>();
    }

    @BeforeMethod
    public void setupMethod(Method method) {
        logger.info("Starting test: {}", method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() {
        cleanupPlayers();
    }

    protected Player createAndTrackPlayer(Player player, String editor) {
        Response response = playerService.createPlayer(player, editor);
        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            Player createdPlayer = response.as(Player.class);
            if (createdPlayer.getId() != null) {
                createdPlayerIds.add(createdPlayer.getId());
                logger.info("Created and tracking player with id: {}", createdPlayer.getId());
            }
            return createdPlayer;
        }
        return null;
    }

    protected void cleanupPlayers() {
        if (createdPlayerIds.isEmpty()) {
            return;
        }
        
        logger.info("Cleaning up {} player(s)", createdPlayerIds.size());
        // Create a copy to avoid ConcurrentModificationException
        List<Integer> idsToClean = new ArrayList<>(createdPlayerIds);
        for (Integer playerId : idsToClean) {
            try {
                playerService.deletePlayer(playerId, config.getSupervisorLogin());
                logger.info("Cleaned up player with id: {}", playerId);
            } catch (Exception e) {
                logger.warn("Failed to cleanup player with id: {}", playerId, e);
            }
        }
        createdPlayerIds.clear();
    }

    protected void createTestPlayer() {
        testPlayer = createAndTrackPlayer(TestDataGenerator.createValidPlayer(), config.getSupervisorLogin());
        assertNotNull(testPlayer, "Test player should be created");
        assertNotNull(testPlayer.getId(), "Test player ID should not be null");
        logger.info("Created test player with id: {}", testPlayer.getId());
    }
}
