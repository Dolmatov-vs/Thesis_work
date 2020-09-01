package ru.netology.web.db;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import ru.netology.web.model.ListOrderOnCard;
import ru.netology.web.model.ListOrderOnCredit;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SqlRequest {
    public SqlRequest(){
    }

    private String requestBuyCard = "SELECT credit_id, status, amount FROM order_entity INNER JOIN payment_entity ON order_entity.payment_id = payment_entity.transaction_id ORDER BY order_entity.created;";
    private String requestBuyCredit = "SELECT credit_id, status FROM order_entity INNER JOIN credit_request_entity ON order_entity.payment_id = credit_request_entity.bank_id ORDER BY order_entity.created;";
    private String user = "app";
    private String password = "pass";

    public List<ListOrderOnCard> getListOrderOnCard(String dbUrl) throws SQLException {
        val countSQL = requestBuyCard;
        val conn = DriverManager.getConnection(dbUrl, user, password);
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCard.class));
        return list;
    }

    public List<ListOrderOnCredit> getListOrderOnCredit(String dbUrl) throws SQLException {
        val countSQL = requestBuyCredit;
        val conn = DriverManager.getConnection(dbUrl, user, password);
        val runner = new QueryRunner();
        val list = runner.query(conn, countSQL, new BeanListHandler<>(ListOrderOnCredit.class));
        return list;
    }
}
