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
        // 1️⃣ Подготавливаем JSON-тело запроса с логином и паролем
        String jsonBody = """
                        {
                          "username": "test1739192044000@gmail.com",
                           "password": "Password@1A"                                
                        }
                """;
        // 2️⃣ Отправляем HTTP POST-запрос на сервер с JSON-данными
        Response response = Request.Post(LOGIN_URL)
                .bodyString(jsonBody, ContentType.APPLICATION_JSON).execute();
        // 3️⃣ Выводим объект `Response`, чтобы посмотреть его в отладке (возвращает ссылку на объект)
        System.out.println(response); // Пример: org.apache.http.client.fluent.Response@3549bca9

        // 4️⃣ Читаем JSON-ответ от сервера как строку
        String responseJson = response.returnContent().asString();
        System.out.println(responseJson); // Пример: {"token":"eyJhbGciOiJIUzI1NiJ9..."}

        // 5️⃣ Парсим, разбираем структуру JSON-ответа, чтобы извлечь токен
        JsonElement element = JsonParser.parseString(responseJson);
        JsonElement token = element.getAsJsonObject().get("token"); // Пример: "eyJhbGciOiJIUzI1NiJ9..."

        // 6️⃣ Выводим извлечённый токен в консоль для отладки
        System.out.println("Extracted token: " + token);

        // 7️⃣ Проверяем, что токен не null (валидный ответ)
        Assert.assertNotNull(token);
    }

    @Test
    public void LoginSuccessWithDtoTest() throws IOException {
        // 1️⃣ Формируем объект запроса с логином и паролем
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("portishead@gmail.com")
                .password("Password@1")
                .build();

        // 2️⃣ Выполняем HTTP POST-запрос с телом запроса в формате JSON
        Response response = Request.Post(LOGIN_URL)
                .bodyString(GSON.toJson(requestDto), ContentType.APPLICATION_JSON)
                .execute();

        // 3️⃣ Читаем JSON-ответ от сервера в виде строки
        String responseJson = response.returnContent().asString();
        System.out.println(responseJson); // Пример: {"token":"eyJhbGciOiJIUzI1NiJ9..."}

        // 4️⃣ Преобразуем JSON-ответ в объект AuthResponseDto
        AuthResponseDto authResponseDto = GSON.fromJson(responseJson, AuthResponseDto.class);
        System.out.println(authResponseDto); // Пример: AuthResponseDto(token=eyJhbGciOiJIUzI1NiJ9.eyJyb...)

        // 5️⃣ Извлекаем токен авторизации из ответа
        String token = authResponseDto.getToken();
        System.out.println(token); // Пример: "eyJhbGciOiJIUzI1NiJ9..."

        // 6️⃣ Проверяем, что токен не пустой (валидный ответ)
        Assert.assertNotNull(token, "Token should not be null");
    }

    @Test
    public void LoginErrorWithDtoTest() throws IOException {
        // 1️⃣ Формируем объект запроса с некорректными данными
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .username("portishead333333333333333@gmail.com")
                .password("Password@1")
                .build();

        // 2️⃣ Отправляем POST-запрос с JSON-телом
        Response response = Request.Post(LOGIN_URL)
                .bodyString(GSON.toJson(requestDto), ContentType.APPLICATION_JSON)
                .execute();

        // 3️⃣ Получаем сырой raw HTTP-ответ
        HttpResponse httpResponse = response.returnResponse();
        System.out.println("Сырой raw HTTP-ответ: " + httpResponse);

        // 4️⃣ Извлекаем тело ответа как поток
        //* HTTP-ответ может быть большим, и InputStream позволяет читать его порциями, не загружая весь ответ в память сразу
        InputStream content = httpResponse.getEntity().getContent();
        //* BufferedReader(...) – оборачивает InputStreamReader, обеспечивая буферизированное чтение (чтение данных блоками).
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));

        // 5️⃣ Читаем данные из ответа построчно
        //* StringBuilder используется для эффективного объединения строк, так как строки (String) в Java неизменяемы.
        //* В отличие от String, StringBuilder изменяет свой буфер памяти, что быстрее.
        StringBuilder sb = new StringBuilder();
        String line; // line – временная переменная для хранения одной строки, считанной из HTTP-ответа.
        while ((line = reader.readLine()) != null) {
            sb.append(line); // Записываем каждую строку в StringBuilder
            System.out.println(line);  // Печатает каждую строку в цикле пока строки не закончатся
        }

        // 6️⃣ Парсим JSON-ответ в объект ErrorDto
        ErrorDto errorDto = GSON.fromJson(sb.toString(), ErrorDto.class);
        System.out.println(errorDto);

        // 7️⃣ Проверяем, что сервер вернул ошибку 401 (Unauthorized)
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(errorDto.getStatus(), 401, "Status should be 401");
        softAssert.assertEquals(errorDto.getError(), "Unauthorized");
        softAssert.assertEquals(errorDto.getMessage(), "Login or Password incorrect");
        softAssert.assertEquals(errorDto.getPath(), "/v1/user/login/usernamepassword");
        softAssert.assertAll();
    }
}
