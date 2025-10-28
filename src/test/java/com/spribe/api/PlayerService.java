package com.spribe.api;

import com.spribe.models.Player;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * Service class for Player API operations
 */
public class PlayerService {
    private static final Logger logger = LogManager.getLogger(PlayerService.class);
    private static final String PLAYER_CREATE_PATH = "/player/create/{editor}";
    private static final String PLAYER_DELETE_PATH = "/player/delete/{editor}";
    private static final String PLAYER_GET_PATH = "/player/get";
    private static final String PLAYER_UPDATE_PATH = "/player/update/{editor}/{id}";

    @Step("Create player with editor: {editor}")
    public Response createPlayer(Player player, String editor) {
        logger.info("Creating player with editor: {}, player: {}", editor, player);
        
        // BUG: API uses GET instead of POST (REST violation)
        // BUG: API requires query parameters instead of JSON body
        Response response = given()
                .spec(ApiClient.getRequestSpec())
                .pathParam("editor", editor)
                .queryParam("age", player.getAge())
                .queryParam("gender", player.getGender())
                .queryParam("login", player.getLogin())
                .queryParam("password", player.getPassword())
                .queryParam("role", player.getRole())
                .queryParam("screenName", player.getScreenName())
                .when()
                .get(PLAYER_CREATE_PATH);
        
        logger.info("Create player response status: {}", response.getStatusCode());
        return response;
    }

    @Step("Delete player with id: {playerId} by editor: {editor}")
    public Response deletePlayer(int playerId, String editor) {
        logger.info("Deleting player with id: {} by editor: {}", playerId, editor);
        
        Response response = given()
                .spec(ApiClient.getRequestSpec())
                .pathParam("editor", editor)
                .queryParam("playerId", playerId)
                .when()
                .delete(PLAYER_DELETE_PATH);
        
        logger.info("Delete player response status: {}", response.getStatusCode());
        return response;
    }

    @Step("Get player with id: {playerId}")
    public Response getPlayer(int playerId) {
        logger.info("Getting player with id: {}", playerId);
        
        // BUG: API uses POST instead of GET (REST violation)
        // BUG: API requires JSON body instead of query parameter
        String requestBody = "{\"playerId\": " + playerId + "}";
        Response response = given()
                .spec(ApiClient.getRequestSpec())
                .body(requestBody)
                .when()
                .post(PLAYER_GET_PATH);
        
        logger.info("Get player response status: {}", response.getStatusCode());
        return response;
    }

    @Step("Update player with id: {id} by editor: {editor}")
    public Response updatePlayer(Player player, String editor, int id) {
        logger.info("Updating player with id: {} by editor: {}, updates: {}", id, editor, player);
        
        Response response = given()
                .spec(ApiClient.getRequestSpec())
                .pathParam("editor", editor)
                .pathParam("id", id)
                .body(player)
                .when()
                .patch(PLAYER_UPDATE_PATH);
        
        logger.info("Update player response status: {}", response.getStatusCode());
        return response;
    }
}
