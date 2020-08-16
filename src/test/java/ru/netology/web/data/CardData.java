package ru.netology.web.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.Year;
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
        return "4444444444444441";
    }
    public static String getDeclinedCardNumber(){
        return "4444444444444442";
    }
    public static String getInvalidCardNumber(){
        Faker faker = new Faker();
        String invalidCardNumber = faker.business().creditCardNumber();
        return invalidCardNumber;
    }

    public static String getMonth(){
        String[] months = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        return months[new Random().nextInt(months.length-1)];
    }

    public static String getYear(){
        int year = Year.now().getValue() % 100;
        year = year + new Random().nextInt(4) + 1; //Срок действия карты в диапазоне от Текущий год +1 до текущий год + 5
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
