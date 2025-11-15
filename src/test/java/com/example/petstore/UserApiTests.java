package com.example.petstore;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Petstore API")
@Feature("User operations")
public class UserApiTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        RestAssured.filters(new AllureRestAssured());
    }

    @Step("Сформировать объект пользователя username={username}")
    private Map<String, Object> buildUser(String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", System.currentTimeMillis());
        user.put("username", username);
        user.put("firstName", "First_" + username);
        user.put("lastName", "Last_" + username);
        user.put("email", username + "@example.com");
        user.put("password", "password123");
        user.put("phone", "+1234567890");
        user.put("userStatus", 1);
        return user;
    }

    @Step("Отправить запрос на создание пользователя {username}")
    private Response createUser(Map<String, Object> user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
        .when()
                .post("/user")
        .andReturn();
    }

    @Step("Отправить запрос на получение пользователя {username}")
    private Response getUser(String username) {
        return given()
                .pathParam("username", username)
        .when()
                .get("/user/{username}")
        .andReturn();
    }

    @Step("Отправить запрос на логин пользователя {username}")
    private Response loginUser(String username, String password) {
        return given()
                .queryParam("username", username)
                .queryParam("password", password)
        .when()
                .get("/user/login")
        .andReturn();
    }

    @Step("Отправить запрос на логаут пользователя")
    private Response logoutUser() {
        return given()
        .when()
                .get("/user/logout")
        .andReturn();
    }

    @Step("Отправить запрос на обновление пользователя {username}")
    private Response updateUser(String username, Map<String, Object> updatedUser) {
        return given()
                .contentType(ContentType.JSON)
                .pathParam("username", username)
                .body(updatedUser)
        .when()
                .put("/user/{username}")
        .andReturn();
    }

    @Step("Отправить запрос на удаление пользователя {username}")
    private Response deleteUser(String username) {
        return given()
                .pathParam("username", username)
        .when()
                .delete("/user/{username}")
        .andReturn();
    }

    @Step("Проверить, что HTTP-ответ получен")
    private void assertResponseReceived(Response response) {
        Allure.step("HTTP-ответ не null и код статуса задан", () -> {
            assertNotNull(response, "response не должен быть null");
            assertTrue(response.getStatusCode() > 0, "statusCode должен быть > 0");
        });
    }

    @Test
    @Story("Создание пользователя и получение по username")
    @Description("Демонстрация цепочки: POST /user и GET /user/{username}")
    void createUserAndGetByUsername() {
        String username = "user_" + System.currentTimeMillis();
        Map<String, Object> user = buildUser(username);

        Response createResponse = createUser(user);
        assertResponseReceived(createResponse);

        Response getResponse = getUser(username);
        assertResponseReceived(getResponse);
    }

    @Step("Отправить запрос /user/createWithArray")
    private Response createUsersWithArray(List<Map<String, Object>> users) {
        return given()
                .contentType(ContentType.JSON)
                .body(users)
        .when()
                .post("/user/createWithArray")
        .andReturn();
    }

    @Test
    @Story("Создание нескольких пользователей через массив")
    void createUsersWithArrayInput() {
        String username1 = "arrayUser1_" + System.currentTimeMillis();
        String username2 = "arrayUser2_" + System.currentTimeMillis();
        List<Map<String, Object>> users = Arrays.asList(
                buildUser(username1),
                buildUser(username2)
        );

        Response response = createUsersWithArray(users);
        assertResponseReceived(response);
    }

    @Step("Отправить запрос /user/createWithList")
    private Response createUsersWithList(List<Map<String, Object>> users) {
        return given()
                .contentType(ContentType.JSON)
                .body(users)
        .when()
                .post("/user/createWithList")
        .andReturn();
    }

    @Test
    @Story("Создание нескольких пользователей через список")
    void createUsersWithListInput() {
        String username1 = "listUser1_" + System.currentTimeMillis();
        String username2 = "listUser2_" + System.currentTimeMillis();
        List<Map<String, Object>> users = Arrays.asList(
                buildUser(username1),
                buildUser(username2)
        );

        Response response = createUsersWithList(users);
        assertResponseReceived(response);
    }

    @Test
    @Story("Обновление пользователя")
    void updateUserTest() {
        String username = "updateUser_" + System.currentTimeMillis();
        Map<String, Object> user = buildUser(username);

        Response createResponse = createUser(user);
        assertResponseReceived(createResponse);

        Map<String, Object> updatedUser = buildUser(username);
        updatedUser.put("firstName", "UpdatedFirst");
        updatedUser.put("lastName", "UpdatedLast");

        Response updateResponse = updateUser(username, updatedUser);
        assertResponseReceived(updateResponse);
    }

    @Test
    @Story("Удаление пользователя")
    void deleteUserTest() {
        String username = "deleteUser_" + System.currentTimeMillis();
        Map<String, Object> user = buildUser(username);

        Response createResponse = createUser(user);
        assertResponseReceived(createResponse);

        Response deleteResponse = deleteUser(username);
        assertResponseReceived(deleteResponse);
    }

    @Test
    @Story("Логин и логаут пользователя")
    void loginAndLogoutUser() {
        String username = "loginUser_" + System.currentTimeMillis();
        Map<String, Object> user = buildUser(username);

        Response createResponse = createUser(user);
        assertResponseReceived(createResponse);

        Response loginResponse = loginUser(username, "password123");
        assertResponseReceived(loginResponse);

        Response logoutResponse = logoutUser();
        assertResponseReceived(logoutResponse);
    }
}
