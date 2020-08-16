package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class HomePage {
    private SelenideElement buyCard = $$("button").findBy(matchText("Купить"));
    private SelenideElement buyCredit = $$("button").findBy(matchText("Купить в кредит"));

    public PayCard buyCard(){
        buyCard.click();
        return new PayCard();
    }

    public PayCredit buyCredit(){
        buyCredit.click();
        return new PayCredit();
    }
}
