package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PayCard {
    private SelenideElement heading = $("[class='heading heading_size_m heading_theme_alfa-on-white']");
    private SelenideElement paymentField = $("[class='form form_size_m form_theme_alfa-on-white']");
    private ElementsCollection fieldset = $$("[class='input__inner']");
    private SelenideElement setCardNumber = fieldset.findBy(text("Номер карты")).$("input");
    private SelenideElement setMonth = fieldset.findBy(text("Месяц")).$("input");
    private SelenideElement setYear = fieldset.findBy(text("Год")).$("input");
    private SelenideElement setOwner = fieldset.findBy(text("Владелец")).$("input");
    private SelenideElement setCvc = fieldset.findBy(text("CVC/CVV")).$("input");
    private SelenideElement proceed = $$("button").findBy(text("Продолжить"));


    public PayCard() {
        heading.shouldHave(text("Оплата по карте"));
        paymentField.shouldBe(visible);
    }

    public void buyTour(String cardNumber, String month, String year, String owner, String cvc) {
        setCardNumber.setValue(cardNumber);
        setMonth.setValue(month);
        setYear.setValue(year);
        setOwner.setValue(owner);
        setCvc.setValue(cvc);
        proceed.click();
    }
}
