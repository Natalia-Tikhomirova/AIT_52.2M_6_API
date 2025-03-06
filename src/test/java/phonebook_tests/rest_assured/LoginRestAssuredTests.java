package phonebook_tests.rest_assured;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import phonebook.dto.AuthRequestDto;
import phonebook.dto.AuthResponseDto;
import phonebook.dto.ErrorDto;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginRestAssuredTests extends TestBase {

    SoftAssert softAssert = new SoftAssert();
    AuthRequestDto body = AuthRequestDto.builder()
            .username("test1739192044000@gmail.com")
            .password("Password@1A")
            .build();

    AuthRequestDto errorBody = AuthRequestDto.builder()
            .username("test1739192044000@gmail.com")
            .password("Password@1")
            .build();

    /**
     * Простой тест логина без проверок (assert).
     * Просто выполняет запрос на авторизацию.
     * .log().all() - логирование результатов
     */
    @Test
    public void loginSimpleTestWitOutAssert() {
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .post(loginDto)
                .then()
                .log().all()

        ;
    }

    /**
     * Тест успешного логина с проверкой статус-кода 200
     * и десериализацией ответа в объект AuthResponseDto.
     */
    @Test
    public void loginSuccessTest() {
        AuthResponseDto dto = given()
                .contentType(ContentType.JSON)
                .body(body).when()
                .post(loginDto)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response().as(AuthResponseDto.class);
        System.out.println(dto);
    }

    /**
     * Тест успешного логина с проверкой статус-кода 200,
     * наличия в ответе поля "token" и извлечением токена из ответа.
     */
    @Test
    public void loginSuccessTest2() {
        String responseToken = given()
                .contentType(ContentType.JSON)
                .body(body).when()
                .post(loginDto)
                .then()
                .assertThat()
                .statusCode(200)
                .body(Matchers.containsString("token"))
                .extract().path("token");
        System.out.println(responseToken);
    }

    /**
     * Тест логина с неверным паролем. Ожидается статус-код 401.
     * Проверяются отдельные поля ответа с использованием SoftAssert.
     */
    @Test
    public void loginWrongPasswordTest() {
        ErrorDto errorDto = given()
                .contentType(ContentType.JSON)
                .body(errorBody).when()
                .post(loginDto)
                .then().assertThat()
                .statusCode(401)
                .extract().response().as(ErrorDto.class);
        System.out.println(errorDto);

        String error = errorDto.getError();
        System.out.println(error);
        softAssert.assertEquals(error, "Unauthorized");

        int status = errorDto.getStatus();
        System.out.println(status);
        softAssert.assertEquals(status, 401);

        String message = (String) errorDto.getMessage();
        System.out.println(message);
        softAssert.assertEquals(message, "Login or Password incorrect", "своё сообщение");

        String path = errorDto.getPath();
        System.out.println(path);
        softAssert.assertEquals(path, "/v1/user/login/usernamepassword");

        softAssert.assertAll();
    }

    /**
     * Тест логина с неверным паролем (альтернативный вариант).
     * Использует прямые проверки в цепочке then().assertThat()
     */
    @Test
    public void loginWrongPasswordTest2() {
        given()
                .contentType(ContentType.JSON)
                .body(errorBody).when()
                .post(loginDto)
                .then()
                .assertThat().statusCode(401)
                .assertThat().body("error", equalTo("Unauthorized"))
                .assertThat().body("message", equalTo("Login or Password incorrect"))
                .assertThat().body("status", equalTo(401))
                .assertThat().body("path", equalTo("/v1/user/login/usernamepassword"));
    }

}