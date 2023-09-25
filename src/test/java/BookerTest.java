import automation.app.BookerAPI;
import automation.base.BaseAPITest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

public class BookerTest extends BaseAPITest
{
    @Test
    public void createBooking() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("firstname", "Michael");
        requestParams.put("lastname", "Urisman");
        requestParams.put("totalprice", 10);
        requestParams.put("depositpaid", true);

        Map<String, String> bookingdates = new HashMap<>();
        bookingdates.put("checkin", "2023-01-01");
        bookingdates.put("checkout", "2023-02-02");

        requestParams.put("bookingdates", bookingdates);
        requestParams.put("additionalneeds", "Breakfast");

        Integer bookingID = RestAssured
            .given()
                .body(requestParams.toString())
                .header(new Header("x-custom-header", "value"))
                .contentType("application/json")
                .accept("application/json")
            .when()
                .post("/booking")
            .then()
                .statusCode(200)
                .body("booking.firstname", Matchers.equalTo("Michael"))
                .body("booking.lastname", Matchers.equalTo("Urisman"))
                .extract().body().path("bookingid");

        RestAssured
            .given()
                .accept("application/json")
            .when()
                .get("/booking/" + bookingID)
            .then()
                .statusCode(200)
                .body("firstname", Matchers.equalTo("Michael"))
                .body("lastname", Matchers.equalTo("Urisman"));
    }

    @Test
    public void getAll() {
        RestAssured
            .when()
                .get("/booking")
            .then()
                .statusCode(200)
                .assertThat().body("size()", notNullValue())
                .assertThat().body("size()", greaterThan(0));
    }

    @Test
    public void changeTotalPrice() {
        Integer randomBookingID = BookerAPI.getRandomBookingId();
        Integer newPriceValue = 20;
        String requestBody = String.format("{\"totalprice\" : %s}", newPriceValue);

        RestAssured
            .given()
                .body(requestBody)
                .header(new Header("Authorization", authHeaderValue))
                .contentType("application/json")
                .accept("application/json")
            .when()
                .patch("/booking/" + randomBookingID)
            .then()
                .statusCode(200)
                .body("totalprice", Matchers.equalTo(newPriceValue));
    }

    @Test
    public void deleteBooking() {
        Integer randomBookingID = BookerAPI.getRandomBookingId();

        RestAssured
            .given()
                .header(new Header("Authorization", authHeaderValue))
            .when()
                .delete("/booking/" + randomBookingID)
            .then()
                .statusCode(201);

        List<Integer> updatedBookingIDs = RestAssured.given().get("/booking").jsonPath().getList("bookingid");
        Assert.assertListNotContainsObject(updatedBookingIDs, randomBookingID, "bookingid: "
                + randomBookingID.toString());
    }

    @Test
    public void updateBooking() {
        Integer randomBookingID = BookerAPI.getRandomBookingId();

        String newFirstName = "Michael";
        String newAdditionalNeeds = "TV";

        LinkedHashMap<String,String> bodyToUpdate = RestAssured
            .given()
                .accept("application/json")
            .when()
                .get("/booking/" + randomBookingID)
            .then()
                .extract().body().path("$");

        System.out.println("BookingID: " + randomBookingID);
        System.out.println("Body to update: \n" + bodyToUpdate);

        JSONObject newRequestBody = new JSONObject(bodyToUpdate);
        newRequestBody.put("firstname", newFirstName);
        newRequestBody.put("additionalneeds", newAdditionalNeeds);

        RestAssured
            .given()
                .body(newRequestBody.toString())
                .header(new Header("Authorization", authHeaderValue))
                .contentType("application/json")
                .accept("application/json")
            .when()
                .put("/booking/" + randomBookingID)
            .then()
                .statusCode(200);

        RestAssured
            .given()
                .accept("application/json")
            .when()
                .get("/booking/" + randomBookingID)
            .then()
                .statusCode(200)
                .log().body()
                .body("firstname", Matchers.equalTo(newFirstName))
                .body("additionalneeds", Matchers.equalTo(newAdditionalNeeds));
    }
}
