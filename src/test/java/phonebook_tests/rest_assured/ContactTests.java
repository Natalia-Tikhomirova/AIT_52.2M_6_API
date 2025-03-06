package phonebook_tests.rest_assured;

import com.google.gson.Gson;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import phonebook.dto.ContactDto;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ContactTests extends TestBase{
    private static final Gson GSON = new Gson();

    @Test
    public void printContacts() {
        List<ContactDto> contacts = given()
                .header(AUTH, TOKEN)
                .when().get(contactDto)
                .then()
                .assertThat().statusCode(200)
                .extract().jsonPath().getList("contacts", ContactDto.class);

        // Контакты в виде представителя класса ContactDto до преобразования в JSON
        System.out.println(contacts);
        // Контакты в виде JSON после преобразования из представителя класса ContactDto
        System.out.println(GSON.toJson(contacts));

        // Печать контактов в цикле
        contacts.forEach(contact ->
                System.out.printf("Name: %s%nEmail: %s%nPhone: %s%n------------%n",
                        contact.getName(), contact.getEmail(), contact.getPhone()));

    }
}
