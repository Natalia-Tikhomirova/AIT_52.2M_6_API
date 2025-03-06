package phonebook_tests.rest_assured;

import com.google.gson.Gson;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import phonebook.dto.ContactDto;

import static io.restassured.RestAssured.given;

public class ContactTests extends TestBase{
    private static final Gson GSON = new Gson();
    @Test
    public void printContactTest(){
        ContactDto contacts = given()
                .header(AUTH, TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .get(contactDto)
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response().as(ContactDto.class);

        System.out.println(contacts);
        String contactsJson = GSON.toJson(contacts);
        System.out.println(contactsJson);
    }
}
