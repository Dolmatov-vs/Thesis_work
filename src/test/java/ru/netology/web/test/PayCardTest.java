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

import java.time.MonthDay;
import java.time.Year;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Condition.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PayCardTest {

    private String redColorError = "rgba(255, 92, 92, 1)";
    private String colorError = "color";
    private String incorrectFormat = "Неверный формат";
    private String requiredField = "Поле обязательно для заполнения";
    private String cardExpired = "Истёк срок действия карты";
    private String cardExpirationNotCorrect = "Неверно указан срок действия карты";
    private String specificSymbols = "~!@#$%^&*()_+<>?:\"{}[];',./| ё№-=";

    private int timeOut = 10000;
    private int currentMonth = MonthDay.now().getMonthValue();
    private int currentYear = Year.now().getValue() % 100;

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

        terminalPos.getStatusOk().waitUntil(visible, timeOut);
        terminalPos.getStatusError().waitUntil(hidden, timeOut);
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

        terminalPos.getStatusError().waitUntil(visible, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
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

        terminalPos.getStatusError().waitUntil(visible, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
    }

    @Test
    void shouldErrorIfAllFieldsAreEmpty() {
        val cardInfo = CardData.getCardInfo(null);
        val terminalPos = new HomePage().buyCard().buyTour(cardInfo.getNumber(),
                null,
                null,
                null,
                null);

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldCardNumber().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldMonth().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldYear().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldOwner().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldCvc().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldCardNumber().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldMonth().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldYear().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldOwner().shouldBe(hidden, cssValue(colorError, redColorError));
        terminalPos.getFieldCvc().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldCardNumber().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldMonth().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldYear().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldOwner().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldCvc().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldCardNumber().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldMonth().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldYear().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
        terminalPos.getFieldCvc().shouldBe(visible, text(requiredField), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldCardNumber().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(visible, timeOut);
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldMonth().shouldBe(visible, text(cardExpirationNotCorrect), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldMonth().shouldBe(visible, text(cardExpirationNotCorrect), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldMonth().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusOk().waitUntil(visible, timeOut);
        terminalPos.getStatusError().waitUntil(hidden, timeOut);
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

        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getFieldMonth().shouldBe(visible, text(cardExpired), cssValue(colorError, redColorError));
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

        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getFieldYear().shouldBe(visible, text(cardExpired), cssValue(colorError, redColorError));
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

        terminalPos.getStatusOk().waitUntil(visible, timeOut);
        terminalPos.getStatusError().waitUntil(hidden, timeOut);
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

        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getFieldYear().shouldBe(visible, text(cardExpirationNotCorrect), cssValue(colorError, redColorError));
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

        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getFieldCvc().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
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

        terminalPos.getStatusError().waitUntil(hidden, timeOut);
        terminalPos.getStatusOk().waitUntil(hidden, timeOut);
        terminalPos.getFieldOwner().shouldBe(visible, text(incorrectFormat), cssValue(colorError, redColorError));
    }
}
