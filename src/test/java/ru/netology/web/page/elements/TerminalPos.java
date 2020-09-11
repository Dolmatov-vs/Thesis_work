package ru.netology.web.page.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Data;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@Data
public class TerminalPos {

    private final SelenideElement statusOk = $(byText("Операция одобрена Банком."));
    private final SelenideElement statusError = $(byText("Ошибка! Банк отказал в проведении операции."));

    private final ElementsCollection cardFields = $$("[class='input__inner']");
    private final SelenideElement fieldCardNumber = cardFields.findBy(text("Номер карты"));
    private final SelenideElement fieldMonth = cardFields.findBy(text("Месяц"));
    private final SelenideElement fieldYear = cardFields.findBy(text("Год"));
    private final SelenideElement fieldOwner = cardFields.findBy(text("Владелец"));
    private final SelenideElement fieldCvc = cardFields.findBy(text("CVC/CVV"));
    private final SelenideElement proceed = $$("button").findBy(text("Продолжить"));

    public void payment(String cardNumber, String month, String year, String owner, String cvc) {
        fieldCardNumber.$("input").setValue(cardNumber);
        fieldMonth.$("input").setValue(month);
        fieldYear.$("input").setValue(year);
        fieldOwner.$("input").setValue(owner);
        fieldCvc.$("input").setValue(cvc);
        proceed.click();
    }
}
