package ru.netology.web.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.MonthDay;
import java.time.Year;
import java.util.Locale;
import java.util.Random;

public class CardData {
    private CardData() {
    }

    @Value
    public static class CardInfo {
        private String cvc;
        private String holder;
        private String month;
        private String number;
        private String year;
    }

    public static CardInfo getCardInfo(String cardNumber) {
        return new CardInfo(getCvc(), getOwner(), getMonth(), cardNumber, getYear());
    }

    public static String getApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    public static String getInvalidCardNumber() {
        Faker faker = new Faker();
        return faker.business().creditCardNumber();
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
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public static String getCvc() {
        Faker faker = new Faker();
        int cvc = faker.number().numberBetween(1, 999);
        return String.format("%03d", cvc);
    }
}
