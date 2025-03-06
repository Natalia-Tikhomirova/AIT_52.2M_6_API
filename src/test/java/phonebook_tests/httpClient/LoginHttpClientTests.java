package phonebook_tests.httpClient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import phonebook.dto.AuthRequestDto;
import phonebook.dto.AuthResponseDto;
import phonebook.dto.ErrorDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginHttpClientTests {
    private static final String LOGIN_URL = "https://contactapp-telran-backend.herokuapp.com/v1/user/login/usernamepassword";
    private static final Gson GSON = new Gson();
    SoftAssert softAssert = new SoftAssert();

    @Test
    public void LoginSuccessTest() throws IOException {

        // Подготавливаем тело запроса
        String jsonBody =
                """
                                {
                                  "username": "test1739192044000@gmail.com",
                                  "password": "Password@1A"
                                }
                        """;
        // Отправляем запрос
        Response response = Request.Post(LOGIN_URL)
                .bodyString(jsonBody, ContentType.APPLICATION_JSON)
                .execute();
        System.out.println(response);

        String responseJson = response.returnContent().asString();
        System.out.println(responseJson);

        JsonParser.parseString(responseJson);

        // Распарсим JSON, чтобы извлекать нужные нам ключи и значения из него
        JsonElement element = JsonParser.parseString(responseJson);
        JsonElement token = element.getAsJsonObject().get("token");

        System.out.println("Extracted token: " + token);

        Assert.assertNotNull(token);
    }

    @Test
    public void LoginSuccessWithDtoTest() throws IOException {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("portishead@gmail.com")
                .password("Password@1")
                .build();

        Response response = Request.Post(LOGIN_URL)
                .bodyString(GSON.toJson(requestDto), ContentType.APPLICATION_JSON)
                .execute();

        System.out.println(response);

        String responseJson = response.returnContent().asString();
        System.out.println(responseJson);

        AuthResponseDto authResponseDto = GSON.fromJson(responseJson, AuthResponseDto.class);
        System.out.println(authResponseDto);

        String token = authResponseDto.getToken();
        System.out.println(token);
    }

    @Test
    public void LoginErrorWithDtoTest() throws IOException {
        // Сформировали невалидный body
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("portishead333333333333333@gmail.com")
                .password("Password@1")
                .build();

        // Выполнили запрос на логин
        Response response = Request.Post(LOGIN_URL)
                .bodyString(GSON.toJson(requestDto), ContentType.APPLICATION_JSON)
                .execute();

        // Получить сырой RAW ответ
        HttpResponse httpResponse = response.returnResponse();
        System.out.println("Получить сырой RAW ответ: " + httpResponse);

        // Извлечь тело ответ:
        InputStream content = httpResponse.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));

        StringBuilder sb = new StringBuilder();
        String line; // Временная переменная для хранения 1 строки HTTP-ответа
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            System.out.println(line);
        }
        // парсим JSON в объект ErrorDto
        ErrorDto errorDto = GSON.fromJson(sb.toString(), ErrorDto.class);
        System.out.println(errorDto);
        System.out.println(errorDto.getError());
        softAssert.assertEquals(errorDto.getError(), "Unauthorized");
        System.out.println(errorDto.getStatus());
        softAssert.assertEquals(errorDto.getStatus(), 401);
        System.out.println(errorDto.getMessage());
        softAssert.assertEquals(errorDto.getMessage(), "Login or Password incorrect");
        System.out.println(errorDto.getPath());
        softAssert.assertEquals(errorDto.getPath(), "/v1/user/login/usernamepassword");

        softAssert.assertAll(); // Итог Ассёртов
    }
}
