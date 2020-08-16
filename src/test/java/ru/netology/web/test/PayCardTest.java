package ru.netology.web.test;

import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.CardData;
import ru.netology.web.page.HomePage;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Condition.*;


public class PayCardTest {

    SelenideElement statusOk = $(byText("Операция одобрена Банком."));
    SelenideElement statusError = $(byText("Ошибка! Банк отказал в проведении операции."));
    int timeOut = 10000;

    @BeforeEach
    void setup(){
        open("http://localhost:8080/");
    }

    @Test
    void shouldSuccessfulPurchaseOfTheTour(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);
        statusError.waitUntil(hidden, timeOut);
    }
    @Test
    void shouldErrorPurchaseTour(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(visible, timeOut);
        statusOk.waitUntil(hidden,timeOut);
    }
    @Test
    void shouldErrorBuyingTourIfCardDoesNotExist(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getInvalidCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
       statusError.waitUntil(visible,timeOut);
       statusOk.waitUntil(hidden, timeOut);

    }
}
