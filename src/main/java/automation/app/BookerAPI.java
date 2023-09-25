package automation.app;

import io.restassured.RestAssured;

import java.util.List;
import java.util.Random;

public class BookerAPI {
    public static Integer getRandomBookingId() {
        List<Integer> bookingIDs = RestAssured.given().get("/booking").jsonPath().getList("bookingid");
        Random rand = new Random();

        return bookingIDs.get(rand.nextInt(bookingIDs.size()));
    }
}
