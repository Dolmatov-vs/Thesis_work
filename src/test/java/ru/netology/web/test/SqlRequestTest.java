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

public class SqlRequestTest {

    @Test
    void printListOrderOnCard() throws SQLException {
        val countSQL = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id;";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        ) {
            val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));
            for (int i=0; i < all.size(); i++ ){
                assertEquals(null, all.get(i).getCredit_id());
            }
        }
    }

    @Test
    void printListOrderOnCredit() throws SQLException {
        val countSQL = "SELECT payment_id, credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id;";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
        ) {
            val all = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));
            for (int i=0; i < all.size(); i++ ){
                assertEquals(null, all.get(i).getCredit_id());
                assertEquals("4500000", all.get(i).getAmount());
            }
        }
    }
}
