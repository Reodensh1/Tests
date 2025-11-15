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

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Petstore API")
@Feature("Store operations")
public class StoreApiTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        RestAssured.filters(new AllureRestAssured());
    }

    @Step("Сформировать тело питомца для заказа")
    private Map<String, Object> buildPet(long petId) {
        Map<String, Object> pet = new HashMap<>();
        pet.put("id", petId);
        pet.put("name", "order_pet_" + petId);
        pet.put("status", "available");
        pet.put("photoUrls", Collections.singletonList("https://example.com/photo1"));
        return pet;
    }

    @Step("Создать питомца для заказа")
    private Response createPetForOrder(Map<String, Object> pet) {
        return given()
                .contentType(ContentType.JSON)
                .body(pet)
        .when()
                .post("/pet")
        .andReturn();
    }

    @Step("Сформировать тело заказа для питомца {petId}")
    private Map<String, Object> buildOrder(long petId) {
        Map<String, Object> order = new HashMap<>();
        order.put("id", System.currentTimeMillis());
        order.put("petId", petId);
        order.put("quantity", 1);
        order.put("status", "placed");
        order.put("complete", false);
        order.put("shipDate", OffsetDateTime.now().toString());
        return order;
    }

    @Step("Создать заказ")
    private Response placeOrder(Map<String, Object> order) {
        return given()
                .contentType(ContentType.JSON)
                .body(order)
        .when()
                .post("/store/order")
        .andReturn();
    }

    @Step("Получить заказ по id={orderId}")
    private Response getOrder(long orderId) {
        return given()
                .pathParam("orderId", orderId)
        .when()
                .get("/store/order/{orderId}")
        .andReturn();
    }

    @Step("Удалить заказ по id={orderId}")
    private Response deleteOrder(long orderId) {
        return given()
                .pathParam("orderId", orderId)
        .when()
                .delete("/store/order/{orderId}")
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
    @Story("Создание заказа и получение по id")
    @Description("Цепочка: создаём питомца, создаём заказ и запрашиваем его по id")
    void placeOrderAndGetOrderById() {
        long petId = System.currentTimeMillis();

        Map<String, Object> pet = buildPet(petId);
        Response createPetResponse = createPetForOrder(pet);
        assertResponseReceived(createPetResponse);

        Map<String, Object> order = buildOrder(petId);
        Response orderResponse = placeOrder(order);
        assertResponseReceived(orderResponse);

        long orderId = (long) order.get("id");
        Response getOrderResponse = getOrder(orderId);
        assertResponseReceived(getOrderResponse);
    }

    @Test
    @Story("Удаление заказа")
    void deleteOrderTest() {
        long petId = System.currentTimeMillis();

        Map<String, Object> pet = buildPet(petId);
        Response createPetResponse = createPetForOrder(pet);
        assertResponseReceived(createPetResponse);

        Map<String, Object> order = buildOrder(petId);
        Response orderResponse = placeOrder(order);
        assertResponseReceived(orderResponse);

        long orderId = (long) order.get("id");
        Response deleteResponse = deleteOrder(orderId);
        assertResponseReceived(deleteResponse);
    }
}
