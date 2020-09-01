package ru.netology.web.page.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentField {

    private ElementsCollection fieldSet = $$("[class='input__inner']");
    private SelenideElement setCardNumber = fieldSet.findBy(text("Номер карты")).$("input");
    private SelenideElement setMonth = fieldSet.findBy(text("Месяц")).$("input");
    private SelenideElement setYear = fieldSet.findBy(text("Год")).$("input");
    private SelenideElement setOwner = fieldSet.findBy(text("Владелец")).$("input");
    private SelenideElement setCvc = fieldSet.findBy(text("CVC/CVV")).$("input");
    private SelenideElement proceed = $$("button").findBy(text("Продолжить"));

    public void payment(String cardNumber, String month, String year, String owner, String cvc){
        setCardNumber.setValue(cardNumber);
        setMonth.setValue(month);
        setYear.setValue(year);
        setOwner.setValue(owner);
        setCvc.setValue(cvc);
        proceed.click();
    }
}