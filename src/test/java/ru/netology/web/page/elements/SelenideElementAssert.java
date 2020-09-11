package ru.netology.web.page.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;

public final class SelenideElementAssert {

    private static final String redColorError = "rgba(255, 92, 92, 1)";
    private static final String colorError = "color";
    private static final int timeOut = 10000;

    public static void shouldHaveAlertColor(SelenideElement field, String massage, Condition condition){
        field.shouldBe(condition, text(massage), cssValue(colorError, redColorError));
    }

    public static void shouldHaveStatus(SelenideElement field, Condition condition){
        field.waitUntil(condition, timeOut);
    }
}
