package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class HomePage {
    private static final SelenideElement buyCard = $$("button").findBy(matchText("Купить"));
    private static final SelenideElement buyCredit = $$("button").findBy(matchText("Купить в кредит"));

    public PayCardPage buyCard() {
        buyCard.click();
        return new PayCardPage();
    }

    public PayCreditPage buyCredit() {
        buyCredit.click();
        return new PayCreditPage();
    }
}
