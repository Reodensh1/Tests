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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Petstore API")
@Feature("Pet operations")
public class PetApiTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        RestAssured.filters(new AllureRestAssured());
    }

    @Step("Сформировать тело питомца со статусом {status} и тегом {tagName}")
    private Map<String, Object> buildPet(long petId, String status, String tagName) {
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "dogs");

        Map<String, Object> tag = new HashMap<>();
        tag.put("id", 1);
        tag.put("name", tagName);

        Map<String, Object> pet = new HashMap<>();
        pet.put("id", petId);
        pet.put("name", "doggie_" + petId);
        pet.put("status", status);
        pet.put("photoUrls", Collections.singletonList("https://example.com/photo1"));
        pet.put("category", category);
        pet.put("tags", Collections.singletonList(tag));

        return pet;
    }

    @Step("Отправить запрос на создание питомца")
    private Response createPet(Map<String, Object> pet) {
        return given()
                .contentType(ContentType.JSON)
                .body(pet)
        .when()
                .post("/pet")
        .andReturn();
    }

    @Step("Отправить запрос на обновление питомца")
    private Response updatePet(Map<String, Object> pet) {
        return given()
                .contentType(ContentType.JSON)
                .body(pet)
        .when()
                .put("/pet")
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
    @Story("Создание и обновление питомца")
    @Description("Пример цепочки: создаём питомца и затем обновляем его данные")
    void addAndUpdatePet() {
        long petId = System.currentTimeMillis();

        Map<String, Object> newPet = buildPet(petId, "available", "update-tag");
        Response createResponse = createPet(newPet);
        assertResponseReceived(createResponse);

        Map<String, Object> updatedPet = buildPet(petId, "sold", "update-tag");
        updatedPet.put("name", "updated_name_" + petId);

        Response updateResponse = updatePet(updatedPet);
        assertResponseReceived(updateResponse);
    }

    @Step("Поиск питомцев по статусу {status}")
    private Response findPetsByStatusRequest(String status) {
        return given()
                .queryParam("status", status)
        .when()
                .get("/pet/findByStatus")
        .andReturn();
    }

    @Test
    @Story("Поиск питомцев по статусу")
    void findPetsByStatus() {
        String status = "available";
        Response response = findPetsByStatusRequest(status);
        assertResponseReceived(response);
    }

    @Step("Поиск питомцев по тегу {tagName}")
    private Response findPetsByTagsRequest(String tagName) {
        return given()
                .queryParam("tags", tagName)
        .when()
                .get("/pet/findByTags")
        .andReturn();
    }

    @Test
    @Story("Поиск питомцев по тегам")
    void findPetsByTags() {
        String tagName = "tag_" + System.currentTimeMillis();
        Response response = findPetsByTagsRequest(tagName);
        assertResponseReceived(response);
    }

    @Step("Обновление питомца через форму (id={petId}, name={newName}, status={newStatus})")
    private Response updatePetWithFormRequest(long petId, String newName, String newStatus) {
        return given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", newName)
                .formParam("status", newStatus)
        .when()
                .post("/pet/{petId}", petId)
        .andReturn();
    }

    @Test
    @Story("Обновление питомца через форму")
    void updatePetWithForm() {
        long petId = System.currentTimeMillis();
        Response response = updatePetWithFormRequest(petId, "form_name_" + petId, "pending");
        assertResponseReceived(response);
    }

    @Step("Удалить питомца с id={petId}")
    private Response deletePetRequest(long petId) {
        return given()
        .when()
                .delete("/pet/{petId}", petId)
        .andReturn();
    }

    @Test
    @Story("Удаление питомца")
    void deletePet() {
        long petId = System.currentTimeMillis();
        Response response = deletePetRequest(petId);
        assertResponseReceived(response);
    }
}
