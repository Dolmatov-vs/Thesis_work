package ru.netology.web.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.time.MonthDay;
import java.time.Year;
import java.util.Locale;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class CardDataJSON {
    private CardDataJSON() {
    }

    @Value
    public static class CardInfoJson {
        private String cvc;
        private String holder;
        private String month;
        private String number;
        private String year;
    }

    public static CardInfoJson getCardInfoJson(String cardNumber) {
        return new CardInfoJson(getCvc(), getOwner(), getMonth(), cardNumber, getYear());
    }

    public static String getApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    public static String getInvalidCardNumber() {
        Faker faker = new Faker();
        String invalidCardNumber = faker.business().creditCardNumber();
        return invalidCardNumber;
    }

    public static String getMonth() {
        int month = MonthDay.now().getMonthValue();
        month = month + new Random().nextInt(12 - month);
        return String.format("%02d", month);
    }

    public static String getYear() {
        int year = Year.now().getValue() % 100;
        year = year + new Random().nextInt(5); // Из расчёта срока службы банковской карты 5 лет.
        return Integer.toString(year);
    }

    public static String getOwner() {
        Faker faker = new Faker(new Locale("en"));
        String holder = faker.name().lastName() + " " + faker.name().firstName();
        return holder;
    }

    public static String getCvc() {
        Faker faker = new Faker();
        int cvc = faker.number().numberBetween(1, 999);
        return String.format("%03d", cvc);
    }

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    static void buy(CardInfoJson cardInfo, int statusCode) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(cardInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/v1/pay") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(statusCode); // код 200 OK
    }

    static void credit(CardInfoJson cardInfo, int statusCode) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(cardInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/v1/credit") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(statusCode); // код 200 OK
    }

    public static CardInfoJson newBuy(String cvc, String holder, String month, String number, String year, int statusCode) {
        CardInfoJson card = getCardInfoJson(number);
        buy(card, statusCode);
        return card;
    }

    public static CardInfoJson newCredit(String cvc, String holder, String month, String number, String year, int statusCode) {
        CardInfoJson card = getCardInfoJson(number);
        credit(card, statusCode);
        return card;
    }
}
