package phonebook_tests.rest_assured;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeMethod;

public class TestBase {
    public final String loginDto = "/user/login/usernamepassword";
    public final String contactDto = "/contacts";
    public final String AUTH = "Authorization";
    public final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoicG9ydGlzaGVhZEBnbWFpbC5jb20iLCJpc3MiOiJSZWd1bGFpdCIsImV4cCI6MTc0MTc3NjEyNywiaWF0IjoxNzQxMTc2MTI3fQ.4aVkD6Ljxxq46EbUOloJtekpD0a0E6raVSbA6PXiryU";

    @BeforeMethod
    public void init() {
        RestAssured.baseURI = "https://contactapp-telran-backend.herokuapp.com";
        RestAssured.basePath = "v1";
    }
}
