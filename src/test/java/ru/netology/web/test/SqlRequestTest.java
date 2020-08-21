package ru.netology.web.test;

import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.CardData;
import ru.netology.web.mode.ListOrderOnCard;
import ru.netology.web.mode.ListOrderOnCredit;
import ru.netology.web.page.HomePage;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlRequestTest {
    SelenideElement statusOk = $("[class='notification notification_visible notification_status_ok notification_has-closer notification_stick-to_right notification_theme_alfa-on-white']");
    SelenideElement statusError = $("[class='notification notification_visible notification_status_error notification_has-closer notification_stick-to_right notification_theme_alfa-on-white']");
    int timeOut = 10000;

    @BeforeEach
    void setup() {
        open("http://localhost:8080/");
    }

    @Test
    void buyCreditIfCardApproved() throws SQLException {
        val countSQL = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);
        statusError.waitUntil(hidden,timeOut);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(null, all.get(all.size() - 1).getCredit_id());
        assertEquals("APPROVED", all.get(all.size() - 1).getStatus());
    }

    @Test
    void buyCreditIfCardDeclined() throws SQLException {
        val countSQL = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(hidden, timeOut);
//        statusError.waitUntil(visible,timeOut);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(null, all.get(all.size() - 1).getCredit_id());
        assertEquals("DECLINED", all.get(all.size() - 1).getStatus());
    }

    @Test
    void buyCreditIfCardInvalid() throws SQLException {
        val countSQL = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getInvalidCardNumber());
        homePage.buyCredit().creditBuyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);

        val listNew = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size(), listNew.size());
    }

    @Test
    void buyCardIfCardApproved() throws SQLException {
        val countSQL = "SELECT payment_id, credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getApprovedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        assertEquals(null, all.get(all.size() - 1).getCredit_id());
        assertEquals("APPROVED", all.get(all.size() - 1).getStatus());
        assertEquals("4500000", all.get(all.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardDeclined() throws SQLException {
        val countSQL = "SELECT payment_id, credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        assertEquals(null, all.get(all.size() - 1).getCredit_id());
        assertEquals("DECLINED", all.get(all.size() - 1).getStatus());
        assertEquals(null, all.get(all.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardInvalid() throws SQLException {
        val countSQL = "SELECT payment_id, credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        val homePage = new HomePage();
        val cardInfo = CardData.getCardInfo(CardData.getInvalidCardNumber());
        homePage.buyCard().buyTour(
                cardInfo.getCardNumber(),
                cardInfo.getMonth(),
                cardInfo.getYear(),
                cardInfo.getOwner(),
                cardInfo.getCvc());
        statusOk.waitUntil(visible, timeOut);

        val listNew = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size(), listNew.size());
    }
}
