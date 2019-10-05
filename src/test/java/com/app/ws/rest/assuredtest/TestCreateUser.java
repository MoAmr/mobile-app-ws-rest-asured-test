package com.app.ws.rest.assuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class TestCreateUser {

    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String CONTENT_TYPE = "application/json";

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    final void testCreateUser() {

        List<Map<String, Object>> userAddresses = new ArrayList<>();

        Map<String, Object> shippingAddresses = new HashMap<>();

        shippingAddresses.put("city", "Cairo");
        shippingAddresses.put("country", "Egypt");
        shippingAddresses.put("streetName", "123 Street");
        shippingAddresses.put("postalCode", "ABCCBA");
        shippingAddresses.put("type", "shipping");

        Map<String, Object> billingAddresses = new HashMap<>();

        billingAddresses.put("city", "Cairo");
        billingAddresses.put("country", "Egypt");
        billingAddresses.put("streetName", "123 Street");
        billingAddresses.put("postalCode", "ABCCBA");
        billingAddresses.put("type", "billing");

        userAddresses.add(shippingAddresses);
        userAddresses.add(billingAddresses);

        Map<String, Object> userDetails = new HashMap<>();

        userDetails.put("firstName", "Admin");
        userDetails.put("lastName", "Test");
        userDetails.put("email", "admin@test.com");
        userDetails.put("password", "123");
        userDetails.put("addresses", userAddresses);

        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .body(userDetails)
                .when()
                .post(CONTEXT_PATH + "/users")
                .then()
                .statusCode(200)
                .contentType(CONTENT_TYPE)
                .extract()
                .response();

        String userId = response.jsonPath().getString("userId");
        assertNotNull(userId);
        assertTrue(userId.length() == 30);

        String bodyString = response.body().asString();
        try {
            JSONObject responseBodyJson = new JSONObject(bodyString);
            JSONArray addresses = responseBodyJson.getJSONArray("addresses");

            assertNotNull(addresses);
            assertTrue(addresses.length() == 2);

            String addressId = addresses.getJSONObject(0).getString("addressId");
            assertNotNull(addressId);
            assertTrue(addressId.length() == 30);

        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }
}

