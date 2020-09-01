package ru.netology.web.test;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.github.javafaker.Faker;
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
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PayCreditTest {

    private SelenideElement statusOk = $(byText("Операция одобрена Банком."));
    private SelenideElement statusError = $(byText("Ошибка! Банк отказал в проведении операции."));
    private SelenideElement fieldCardNumber = $$("[class='input__inner']").findBy(text("Номер карты"));
    private SelenideElement fieldMonth = $$("[class='input__inner']").findBy(text("Месяц"));
    private SelenideElement fieldYear = $$("[class='input__inner']").findBy(text("Год"));
    private SelenideElement fieldOwner = $$("[class='input__inner']").findBy(text("Владелец"));
    private SelenideElement fieldCvc = $$("[class='input__inner']").findBy(text("CVC/CVV"));

    private String redColorError = "rgba(255, 92, 92, 1)";
    private String incorrectFormat = "Неверный формат";
    private String requiredField = "Поле обязательно для заполнения";
    private String specificSymbols = "~!@#$%^&*()_+<>?:\"{}[];',./| ё№-=";

    private int timeOut = 10000;
    private int currentMonth = MonthDay.now().getMonthValue();
    private int currentYear = Year.now().getValue() % 100;

    Faker faker = new Faker(new Locale("en"));

    public String subtractMonth(int currentMonth) {
        if (currentMonth == 1)
            return "12";
        return String.format("%02d", currentMonth--);
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:8080/");
    }

    @Test
    void shouldSuccessfulPurchaseOfTheTour() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);
        statusError.waitUntil(hidden, timeOut);
    }

    @Test
    void shouldErrorPurchaseTour() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(visible, timeOut);
        statusOk.waitUntil(hidden, timeOut);
    }

    @Test
    void shouldErrorBuyingTourIfCardDoesNotExist() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getInvalidCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(visible, timeOut);
        statusOk.waitUntil(hidden, timeOut);
    }

    @Test
    void shouldErrorIfAllFieldsAreEmpty() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(null);
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                null,
                null,
                null,
                null);
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfAllFieldsAreLatinCharacter() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo("Card");
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                "Mo",
                "Ye",
                "Ivan Ivanov",
                "Cvc");
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(hidden, cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfAllFieldsAreCyrillicCharacters() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo("Карта");
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                "Месяц",
                "Год",
                "Владелец",
                "свс");
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfAllFieldsAreSpecialCharacter() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(specificSymbols);
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                specificSymbols,
                specificSymbols,
                specificSymbols,
                specificSymbols);
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldMonth.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldYear.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
        fieldCvc.shouldBe(visible, text(requiredField), cssValue("color", redColorError));
        assertEquals("' -", fieldOwner.$("input").getValue());
    }

    @Test
    void shouldErrorIfFieldCardNumberEnterLess16Digit() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo("4444 4444 4444 444");
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldCardNumber.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldCardNumberEnterMore16Digit() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber() + "1");
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(visible, timeOut);
        assertEquals(CardData.getApprovedCardNumber(), fieldCardNumber.$("input").getValue());
    }

    @Test
    void shouldErrorIfEnterMonth00() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                "00",
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldMonth.shouldBe(visible, text("Неверно указан срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfEnterMonth13() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                "13",
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldMonth.shouldBe(visible, text("Неверно указан срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldMonthEnterOneDigit() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                "0",
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldMonth.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldSuccessfulIfCardExpiredInCurrentMonthCurrentYear() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                String.format("%02d", currentMonth),
                String.format("%02d", currentYear),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);
        statusError.waitUntil(hidden, timeOut);
    }

    @Test
    void shouldErrorIfCardExpiredInLastMonthCurrentYear() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
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
    void shouldErrorIfCardExpiresInCurrentMonthLastYear() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                String.format("%02d", currentMonth),
                String.format("%02d", currentYear - 1),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(hidden, timeOut);
        statusError.waitUntil(hidden, timeOut);
        fieldYear.shouldBe(visible, text("Истёк срок действия карты"), cssValue("color", redColorError));
    }

    @Test
    void shouldSuccessfulIfCardExpiresInDecemberAfterFiveYear() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                "12",
                String.format("%02d", currentYear + 5),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);
        statusError.waitUntil(hidden, timeOut);
    }

    @Test
    void shouldSuccessfulIfCardExpirationDateDoesNotExist() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
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
    void shouldErrorIfEnterCvc000() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                "000");
        statusOk.waitUntil(hidden, timeOut);
        statusError.waitUntil(hidden, timeOut);
        fieldCvc.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldOwnerStartsSpace() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                " " + cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldOwnerEndsSpace() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner() + " ",
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldOwnerStartsApostrophe() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                "'" + cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldOwnerEndsApostrophe() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner() + "'",
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldOwnerStartsDash() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                "-" + cardInfo.getOwner(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfFieldOwnerEndsDash() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner() + "-",
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }

    @Test
    void shouldErrorIfInFieldOwnerEnterOneWord() {
        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                faker.name().lastName(),
                cardInfo.getCvc());
        statusError.waitUntil(hidden, timeOut);
        statusOk.waitUntil(hidden, timeOut);
        fieldOwner.shouldBe(visible, text(incorrectFormat), cssValue("color", redColorError));
    }
}
