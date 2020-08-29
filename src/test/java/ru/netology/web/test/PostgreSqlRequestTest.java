package ru.netology.web.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.mode.ListOrderOnCard;
import ru.netology.web.mode.ListOrderOnCredit;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.CardDataJSON.*;

public class PostgreSqlRequestTest {

    String requestBuyCard = "SELECT credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id ORDER BY order_entity.created;";
    String requestBuyCredit = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id ORDER BY order_entity.created;";

    int statusCodeOK = 200;
    int statusServerError = 500;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @Test
    void buyCardIfCardApproved() throws SQLException {
        val countSQL = requestBuyCard;
        val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        newBuy(getCvc(), getOwner(), getMonth(), getApprovedCardNumber(), getYear(), statusCodeOK);

        val newList = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        assertEquals(list.size() + 1, newList.size());
        assertEquals("APPROVED", newList.get(newList.size() - 1).getStatus());
        assertEquals(null, newList.get(newList.size() - 1).getCredit_id());
        assertEquals("4500000", newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardDeclined() throws SQLException {
        val countSQL = requestBuyCard;
        val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        newBuy(getCvc(), getOwner(), getMonth(), getDeclinedCardNumber(), getYear(), statusCodeOK);

        val newList = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        assertEquals(list.size() + 1, newList.size());
        assertEquals("DECLINED", newList.get(newList.size() - 1).getStatus());
        assertEquals(null, newList.get(newList.size() - 1).getCredit_id());
        assertEquals(null, newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardInvalid() throws SQLException {
        val countSQL = requestBuyCard;
        val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        newBuy(getCvc(), getOwner(), getMonth(), getInvalidCardNumber(), getYear(), statusServerError);

        val newList = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size(), newList.size());
    }

    @Test
    void buyCreditIfCardApproved() throws SQLException {
        val countSQL = requestBuyCredit;
        val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        newCredit(getCvc(), getOwner(), getMonth(), getApprovedCardNumber(), getYear(), statusCodeOK);

        val newList = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size() + 1, newList.size());
        assertEquals("APPROVED", newList.get(newList.size() - 1).getStatus());
        assertEquals(null, newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void buyCreditIfCardDeclined() throws SQLException {
        val countSQL = requestBuyCredit;
        val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        newCredit(getCvc(), getOwner(), getMonth(), getDeclinedCardNumber(), getYear(), statusCodeOK);

        val newList = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size() + 1, newList.size());
        assertEquals("DECLINED", newList.get(newList.size() - 1).getStatus());
        assertEquals(null, newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void buyCreditIfCardInvalid() throws SQLException {
        val countSQL = requestBuyCredit;
        val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        newCredit(getCvc(), getOwner(), getMonth(), getInvalidCardNumber(), getYear(), statusServerError);

        val newList = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size(), newList.size());
    }
}
