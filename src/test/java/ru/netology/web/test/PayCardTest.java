package ru.netology.web.test;

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
import ru.netology.web.page.elements.SelenideElementAssert;

import java.time.MonthDay;
import java.time.Year;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Condition.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PayCardTest {

    private static final String incorrectFormat = "Неверный формат";
    private static final String requiredField = "Поле обязательно для заполнения";
    private static final String cardExpired = "Истёк срок действия карты";
    private static final String cardExpirationNotCorrect = "Неверно указан срок действия карты";
    private static final String specificSymbols = "~!@#$%^&*()_+<>?:\"{}[];',./| ё№-=";

    private static final int currentMonth = MonthDay.now().getMonthValue();
    private static final int currentYear = Year.now().getValue() % 100;

    Faker faker = new Faker(new Locale("en"));

    public String subtractMonth(int currentMonth) {
        if (currentMonth == 1)
            return "12";
        currentMonth--;
        return String.format("%02d", currentMonth);
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
    void shouldSuccessfulPurchaseOfTheTourIfCardApproved() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), visible);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
    }

    @Test
    void shouldErrorPurchaseTourIfCardDeclined() {
        val cardInfo = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), visible);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
    }

    @Test
    void shouldErrorBuyingTourIfCardDoesNotExist() {
        val cardInfo = CardData.getCardInfo(CardData.getInvalidCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), visible);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
    }

    @Test
    void shouldErrorIfAllFieldsAreEmpty() {
        val cardInfo = CardData.getCardInfo(null);
        val terminalPos = new HomePage().buyCard().buyTour(cardInfo.getNumber(),
                null,
                null,
                null,
                null);

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCardNumber(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldYear(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCvc(), requiredField, visible);
    }

    @Test
    void shouldErrorIfAllFieldsAreLatinCharacter() {
        val cardInfo = CardData.getCardInfo("Card");
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                "Month",
                "Year",
                "Ivan Ivanov",
                "Cvc");

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCardNumber(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldYear(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), requiredField, hidden);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCvc(), requiredField, visible);
    }

    @Test
    void shouldErrorIfAllFieldsAreCyrillicCharacters() {
        val cardInfo = CardData.getCardInfo("Карта");
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                "Месяц",
                "Год",
                "Владелец",
                "свс");

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCardNumber(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldYear(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCvc(), requiredField, visible);
    }

    @Test
    void shouldErrorIfAllFieldsAreSpecialCharacter() {
        val cardInfo = CardData.getCardInfo(specificSymbols);
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                specificSymbols,
                specificSymbols,
                specificSymbols,
                specificSymbols);

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCardNumber(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldYear(), requiredField, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCvc(), requiredField, visible);
        assertEquals("' -", terminalPos.getFieldOwner().$("input").getValue());
    }

    @Test
    void shouldErrorIfFieldCardNumberEnterLess16Digit() {
        val cardInfo = CardData.getCardInfo("4444 4444 4444 444");
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCardNumber(), incorrectFormat, visible);
    }

    @Test
    void shouldApplicationSentIfFieldCardNumberEnterMore16Digit() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber() + "1");
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), visible);

        assertEquals(CardData.getApprovedCardNumber(), terminalPos.getFieldCardNumber().$("input").getValue());
    }

    @Test
    void shouldErrorIfEnterMonth00() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                "00",
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), cardExpirationNotCorrect, visible);
    }

    @Test
    void shouldErrorIfEnterMonth13() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                "13",
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), cardExpirationNotCorrect, visible);
    }

    @Test
    void shouldErrorIfFieldMonthEnterOneDigit() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                "0",
                cardInfo.getYear(),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), incorrectFormat, visible);
    }

    @Test
    void shouldSuccessfulIfCardExpiredInCurrentMonthCurrentYear() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                String.format("%02d", currentMonth),
                String.format("%02d", currentYear),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), visible);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
    }

    @Test
    void shouldErrorIfCardExpiredInLastMonthCurrentYear() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                subtractMonth(currentMonth),
                String.format("%02d", currentYear),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldMonth(), cardExpired, visible);
    }

    @Test
    void shouldErrorIfCardExpiresInCurrentMonthLastYear() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                String.format("%02d", currentMonth),
                String.format("%02d", currentYear - 1),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldYear(), cardExpired, visible);
    }

    @Test
    void shouldSuccessfulIfCardExpiresInDecemberAfterFiveYear() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                "12",
                String.format("%02d", currentYear + 5),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), visible);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);
    }

    @Test
    void shouldErrorIfCardExpirationDateDoesNotExist() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                "01",
                String.format("%02d", currentYear + 6),
                cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldYear(), cardExpirationNotCorrect, visible);
    }

    @Test
    void shouldErrorIfEnterCvc000() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder(),
                "000");

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldCvc(), incorrectFormat, visible);
    }

    @Test
    void shouldErrorIfFieldOwnerStartsSpace() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                " " + cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
    }

    @Test
    void shouldErrorIfFieldOwnerEndsSpace() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder() + " ",
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
    }

    @Test
    void shouldErrorIfFieldOwnerStartsApostrophe() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                "'" + cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
    }

    @Test
    void shouldErrorIfFieldOwnerEndsApostrophe() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder() + "'",
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
    }

    @Test
    void shouldErrorIfFieldOwnerStartsDash() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                "-" + cardInfo.getHolder(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
    }

    @Test
    void shouldErrorIfFieldOwnerEndsDash() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getHolder() + "-",
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
    }

    @Test
    void shouldErrorIfInFieldOwnerEnterOneWord() {
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        val terminalPos = new HomePage().buyCard().buyTour(
                cardInfo.getNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                faker.name().lastName(),
                cardInfo.getCvc());

        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusOk(), hidden);
        SelenideElementAssert.shouldHaveStatus(terminalPos.getStatusError(), hidden);

        SelenideElementAssert.shouldHaveAlertColor(terminalPos.getFieldOwner(), incorrectFormat, visible);
    }
}
