package ru.netology.web.json;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.netology.web.data.CardData;

import static io.restassured.RestAssured.given;
import static ru.netology.web.data.CardData.getCardInfo;

public class BuyTour {

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    static void buy(CardData.CardInfo cardInfo, int statusCode) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(cardInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/v1/pay") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(statusCode); // код 200 OK
    }

    static void credit(CardData.CardInfo cardInfo, int statusCode) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(cardInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/v1/credit") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(statusCode); // код 200 OK
    }

    public static CardData.CardInfo newBuy(CardData.CardInfo number, int statusCode) {
        CardData.CardInfo card = getCardInfo(number.getNumber());
        buy(card, statusCode);
        return card;
    }

    public static CardData.CardInfo newCredit(CardData.CardInfo number, int statusCode) {
        CardData.CardInfo card = getCardInfo(number.getNumber());
        credit(card, statusCode);
        return card;
    }
}
