package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.page.elements.PaymentField;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PayCard {
    private SelenideElement heading = $("[class='heading heading_size_m heading_theme_alfa-on-white']");
    private SelenideElement paymentField = $("[class='form form_size_m form_theme_alfa-on-white']");

    public PayCard() {
        heading.shouldHave(text("Оплата по карте"));
        paymentField.shouldBe(visible);
    }

    public void buyTour(String cardNumber, String month, String year, String owner, String cvc) {
        new PaymentField().payment(cardNumber, month, year, owner, cvc);
    }
}
