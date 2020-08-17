package ru.netology.web.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class CardData {
    private CardData(){
    }

    @Value
    public static class CardInfo{
        private String cardNumber;
        private String month;
        private String year;
        private String owner;
        private String cvc;
    }

    public static CardInfo getCardInfo(String cardNumber){
        return new CardInfo(
                cardNumber,
                getMonth(),
                getYear(),
                getOwner(),
                getCvc());
    }

    public static String getApprovedCardNumber(){
        return "4444 4444 4444 4441";
    }
    public static String getDeclinedCardNumber(){
        return "4444 4444 4444 4442";
    }
    public static String getInvalidCardNumber(){
        Faker faker = new Faker();
        String invalidCardNumber = faker.business().creditCardNumber();
        return invalidCardNumber;
    }

    public static String getMonth(){
        int month = MonthDay.now().getMonthValue();
        month = month + new Random().nextInt(12 - month);
        if (month < 10)
            return "0"+Integer.toString(month);
        return Integer.toString(month);
    }

    public static String getYear(){
        int year = Year.now().getValue() % 100;
        year = year + new Random().nextInt(5); // Из расчёта срока службы банковской карты
        return Integer.toString(year);
    }

    public static String getOwner(){
        Faker faker = new Faker(new Locale("en"));
        String owner = faker.name().lastName()+" "+faker.name().firstName();
        return owner;
    }

    public static String getCvc(){
        Faker faker = new Faker();
        String cvc = Integer.toString(faker.number().numberBetween(100, 999));
        return cvc;
    }


}
