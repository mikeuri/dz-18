package automation.base;

import automation.Config;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeMethod;

public class BaseAPITest extends BaseTestNG
{
    public String authHeaderValue = Config.API_AUTH_HEADER.value;
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        RestAssured.baseURI = Config.HTTP_BASE_URL.value;
    }
}
