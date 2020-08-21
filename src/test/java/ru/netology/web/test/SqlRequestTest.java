package ru.netology.web.test;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.jupiter.api.Test;
import ru.netology.web.mode.ListOrderOnCard;
import ru.netology.web.mode.ListOrderOnCredit;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.CardDataJSON.*;

public class SqlRequestTest {

    int statusCodeOK = 200;
    int statusServerError = 500;

    @Test
    void buyCardIfCardApproved() throws SQLException {
        val countSQL = "SELECT payment_id, credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        newBuy(getCvc(), getOwner(), getMonth(), getApprovedCardNumber(),getYear(), statusCodeOK);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        assertEquals("APPROVED", all.get(all.size() - 1).getStatus());
        assertEquals(null, all.get(all.size() - 1).getCredit_id());
        assertEquals("4500000", all.get(all.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardDeclined() throws SQLException {
        val countSQL = "SELECT payment_id, credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        newBuy(getCvc(), getOwner(), getMonth(), getDeclinedCardNumber(),getYear(), statusCodeOK);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));

        assertEquals("DECLINED", all.get(all.size() - 1).getStatus());
        assertEquals(null, all.get(all.size() - 1).getCredit_id());
        assertEquals(null, all.get(all.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardInvalid() throws SQLException {
        val countSQL = "SELECT payment_id, credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        newBuy(getCvc(), getOwner(), getMonth(), getInvalidCardNumber(),getYear(), statusServerError);

        val listNew = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size(), listNew.size());
    }

    @Test
    void buyCreditIfCardApproved() throws SQLException {
        val countSQL = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        newCredit(getCvc(), getOwner(), getMonth(), getApprovedCardNumber(),getYear(), statusCodeOK);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals("APPROVED", all.get(all.size() - 1).getStatus());
        assertEquals(null, all.get(all.size() - 1).getCredit_id());
    }

    @Test
    void buyCreditIfCardDeclined() throws SQLException {
        val countSQL = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();

        newCredit(getCvc(), getOwner(), getMonth(), getDeclinedCardNumber(),getYear(), statusCodeOK);

        val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals("DECLINED", all.get(all.size() - 1).getStatus());
        assertEquals(null, all.get(all.size() - 1).getCredit_id());
    }

    @Test
    void buyCreditIfCardInvalid() throws SQLException {
        val countSQL = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id;";
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        newCredit(getCvc(), getOwner(), getMonth(), getInvalidCardNumber(),getYear(), statusServerError);

        val listNew = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));

        assertEquals(list.size(), listNew.size());
    }
}
