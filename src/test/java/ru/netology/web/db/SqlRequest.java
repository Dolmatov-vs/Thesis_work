package ru.netology.web.db;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import ru.netology.web.model.ListOrderOnCard;
import ru.netology.web.model.ListOrderOnCredit;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public final class SqlRequest {

    private static final String requestBuyCard = "SELECT credit_id, payment_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id ORDER BY order_entity.created;";
    private static final String requestBuyCredit = "SELECT credit_id, payment_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.credit_id = credit_request_entity.bank_id ORDER BY order_entity.created;";
    private static final String user = "app";
    private static final String password = "pass";

    public static List<ListOrderOnCard> getListOrderOnCard(String dbUrl) throws SQLException {
        val conn = DriverManager.getConnection(dbUrl, user, password);
        val runner = new QueryRunner();
        return runner.query(conn, requestBuyCard, new BeanListHandler<>(ListOrderOnCard.class));
    }

    public static List<ListOrderOnCredit> getListOrderOnCredit(String dbUrl) throws SQLException {
        val conn = DriverManager.getConnection(dbUrl, user, password);
        val runner = new QueryRunner();
        return runner.query(conn, requestBuyCredit, new BeanListHandler<>(ListOrderOnCredit.class));
    }
}
