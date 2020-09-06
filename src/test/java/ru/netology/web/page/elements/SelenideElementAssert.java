package ru.netology.web.page.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;

public class SelenideElementAssert {

    private String redColorError = "rgba(255, 92, 92, 1)";
    private String colorError = "color";
    private int timeOut = 10000;

    public void shouldHaveAlertColor(SelenideElement field, String massage){
        field.shouldBe(visible, text(massage), cssValue(colorError, redColorError));
    }

    public void shouldHaveStatus(SelenideElement field, Condition condition){
        field.waitUntil(condition, timeOut);
    }
}
