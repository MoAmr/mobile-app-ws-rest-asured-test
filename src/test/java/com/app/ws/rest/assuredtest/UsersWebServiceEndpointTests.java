package com.app.ws.rest.assuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UsersWebServiceEndpointTests {

    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String EMAIL_ADDRESS = "admin@test.com";
    private final String PASSWORD = "123";
    private final String JSON = "application/json";
    private static String authorizationHeader;
    private static String userId;
    private static List<Map<String, String>> addresses;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    /**
     * Method's Name: test User Login
     * Method's Description: Tests the validity of user's logging in
     */
    @Test
    final void a() {

        Map<String, String> loginDetails = new HashMap<>();
        loginDetails.put("email", EMAIL_ADDRESS);
        loginDetails.put("password", PASSWORD);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginDetails)
                .when()
                .post(CONTEXT_PATH + "/users/login")
                .then()
                .statusCode(200)
                .extract().response();

        authorizationHeader = response.header("Authorization");
        userId = response.header("UserID");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);
    }

    /**
     * Method's Name: test Get User Details
     * Method's Description: Tests the validity of retrieving user's details
     */
    @Test
    final void b() {

        Response response = given()
                // Passing a Path Param to the response
                .pathParam("id", userId)
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .when()
                .get(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        // Returns a list from the jsonPath()
        addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");

        assertNotNull(userPublicId);
        assertNotNull(userEmail);
        assertNotNull(firstName);
        assertNotNull(lastName);
        assertEquals(EMAIL_ADDRESS, userEmail);

        assertTrue(addresses.size() == 2);
        assertTrue(addressId.length() == 30);
    }

    /**
     * Method's Name: test Update User Details
     * Method's Description: Tests the validity of updating user's details
     */
    @Test
    final void c() {

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "Moh2");
        userDetails.put("lastName", "Kassim2");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization", authorizationHeader)
                .pathParam("id", userId)
                .body(userDetails)
                .when()
                .put(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                /** Extracting the response here to work with it and take JSON document and read its key value pairs
                 * and assert if the values are correct and asserts are passing! */
                .extract()
                .response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");


        assertEquals("Moh", firstName);
        assertEquals("Kassim", lastName);
        assertNotNull(storedAddresses);
        assertTrue(addresses.size() == storedAddresses.size());
        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
    }

    /**
     * Method's Name: test Delete User Details
     * Method's Description: Tests the validity of updating user's details
     */
    @Test
    final void d() {

        Response response = given()
                .header("Authorization", authorizationHeader)
                .accept(JSON)
                .pathParam("id", userId)
                .when()
                .delete(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");

        assertEquals("SUCCESS", operationResult);

    }
}
