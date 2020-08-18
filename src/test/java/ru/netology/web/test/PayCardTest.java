package ru.netology.web.test;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.CardData;
import ru.netology.web.page.HomePage;

import java.time.MonthDay;
import java.time.Year;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Condition.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PayCardTest {

    SelenideElement statusOk = $(byText("Операция одобрена Банком."));
    SelenideElement statusError = $(byText("Ошибка! Банк отказал в проведении операции."));
    SelenideElement fieldCardNumber = $$("[class='input__inner']").findBy(text("Номер карты"));
    SelenideElement fieldMonth  = $$("[class='input__inner']").findBy(text("Месяц"));
    SelenideElement fieldYear  = $$("[class='input__inner']").findBy(text("Год"));
    SelenideElement fieldOwner  = $$("[class='input__inner']").findBy(text("Владелец"));
    SelenideElement fieldCvc  = $$("[class='input__inner']").findBy(text("CVC/CVV"));

    String redColorError = "rgba(255, 92, 92, 1)";
    String incorrectFormat = "Неверный формат";
    String requiredField = "Поле обязательно для заполнения";
    String specificSymbols = "~!@#$%^&*()_+<>?:\"{}[];',./| ё№-=";

    int timeOut = 10000;
    int currentMonth = MonthDay.now().getMonthValue();
    int currentYear = Year.now().getValue() % 100;

    public String subtractMonth (int currentMonth){
        if (currentMonth == 1)
            return "12";
        return String.format("%02d", currentMonth--);
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }
    @AfterAll
    static void tearDownAll(){
        SelenideLogger.removeListener("AllureSelenide");
    }

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

    @Test
    void shouldErrorIfAllFieldsAreEmpty(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(null);
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                null,
                null,
                null,
                null);
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfAllFieldsAreLatinCharacter(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo("Card");
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                "Mo",
                "Ye",
                "Ivan Ivanov",
                "Cvc");
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(hidden, cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfAllFieldsAreCyrillicCharacters(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo("Карта");
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                "Месяц",
                "Год",
                "Владелец",
                "свс");
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfAllFieldsAreSpecialCharacter(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(specificSymbols);
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                specificSymbols,
                specificSymbols,
                specificSymbols,
                specificSymbols);
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldCardNumberEnterLess16Digit(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo("4444 4444 4444 444");
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldCardNumberEnterMore16Digit(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber()+"1");
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(visible, timeOut);
        assertEquals(CardData.getApprovedCardNumber(), fieldCardNumber.$("input").getValue());
    }

    @Test
    void shouldErrorIfEnterMonth00(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                "00",
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldMonth.shouldBe(visible, text("Неверно указан срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfEnterMonth13(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                "13",
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldMonth.shouldBe(visible, text("Неверно указан срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldMonthEnterOneDigit(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                "0",
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden,timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldMonth.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldSuccessfulIfCardExpiredInCurrentMonthCurrentYear(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                String.format("%02d", currentMonth),
                String.format("%02d", currentYear),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);
        statusError.waitUntil(hidden, timeOut);
    }

    @Test
    void shouldErrorIfCardExpiredInLastMonthCurrentYear(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                String.format("%02d", subtractMonth(currentMonth)),
                String.format("%02d", currentYear),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(hidden, timeOut);
        statusError.waitUntil(hidden, timeOut);
        fieldMonth.shouldBe(visible, text("Истёк срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfCardExpiresInCurrentMonthLastYear(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                String.format("%02d", currentMonth),
                String.format("%02d", currentYear-1),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(hidden, timeOut);
        statusError.waitUntil(hidden, timeOut);
        fieldYear.shouldBe(visible, text("Истёк срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldSuccessfulIfCardExpiresInDecemberAfterFiveYear(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                "12",
                String.format("%02d", currentYear + 5),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);
        statusError.waitUntil(hidden, timeOut);
    }

    @Test
    void shouldSuccessfulIfCardExpirationDateDoesNotExist(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                "01",
                String.format("%02d", currentYear + 6),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(hidden, timeOut);
        statusError.waitUntil(hidden, timeOut);
        fieldYear.shouldBe(visible, text("Неверно указан срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfEnterCvc000(){
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                "000");
        statusOk.waitUntil(hidden, timeOut);
        statusError.waitUntil(hidden, timeOut);
        fieldCvc.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

}
