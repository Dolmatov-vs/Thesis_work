package ru.netology.web.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.CardData;
import ru.netology.web.db.SqlRequest;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.web.json.BuyTour.*;

public class SqlRequestTest {

    private String urlPostgreSql = "jdbc:postgresql://localhost:5432/app";
    private String urlMySql = "jdbc:mysql://localhost:3306/app";
    private String approved = "APPROVED";
    private String declined = "DECLINED";
    private String amount = "4500000";

    private int statusCodeOK = 200;
    private int statusServerError = 500;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @Test
    void postgreSqlBuyCardIfCardApproved() throws SQLException {
        val list = new SqlRequest().getListOrderOnCard(urlPostgreSql);
        val newCard = CardData.getCardInfo(CardData.getApprovedCardNumber());
        newBuy(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCard(urlPostgreSql);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(approved, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
        assertEquals(amount, newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void postgreSqlBuyCardIfCardDeclined() throws SQLException {
        val list = new SqlRequest().getListOrderOnCard(urlPostgreSql);
        val newCard = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        newBuy(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCard(urlPostgreSql);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(declined, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
        assertNull(newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void postgreSqlBuyCardIfCardInvalid() throws SQLException {

        val list = new SqlRequest().getListOrderOnCard(urlPostgreSql);
        val newCard = CardData.getCardInfo(CardData.getInvalidCardNumber());
        newBuy(newCard, statusServerError);
        val newList = new SqlRequest().getListOrderOnCard(urlPostgreSql);

        assertEquals(list.size(), newList.size());
    }

    @Test
    void postgreSqlBuyCreditIfCardApproved() throws SQLException {

        val list = new SqlRequest().getListOrderOnCredit(urlPostgreSql);
        val newCard = CardData.getCardInfo(CardData.getApprovedCardNumber());
        newCredit(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCredit(urlPostgreSql);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(approved, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void postgreSqlBuyCreditIfCardDeclined() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(urlPostgreSql);
        val newCard = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        newCredit(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCredit(urlPostgreSql);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(declined, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void postgreSqlBuyCreditIfCardInvalid() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(urlPostgreSql);
        val newCard = CardData.getCardInfo(CardData.getInvalidCardNumber());
        newCredit(newCard, statusServerError);
        val newList = new SqlRequest().getListOrderOnCredit(urlPostgreSql);

        assertEquals(list.size(), newList.size());
    }


    @Test
    void mySqlBuyCardIfCardApproved() throws SQLException {
        val list = new SqlRequest().getListOrderOnCard(urlMySql);
        val newCard = CardData.getCardInfo(CardData.getApprovedCardNumber());
        newBuy(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCard(urlMySql);

        assertEquals(list.size()+1, newList.size());
        assertEquals(approved, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
        assertEquals(amount, newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void mySqlBuyCardIfCardDeclined() throws SQLException {
        val list = new SqlRequest().getListOrderOnCard(urlMySql);
        val newCard = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        newBuy(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCard(urlMySql);

        assertEquals(list.size()+1, newList.size());
        assertEquals(declined, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
        assertNull(newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void mySqlBuyCardIfCardInvalid() throws SQLException {
        val list = new SqlRequest().getListOrderOnCard(urlMySql);
        val newCard = CardData.getCardInfo(CardData.getInvalidCardNumber());
        newBuy(newCard, statusServerError);
        val newList = new SqlRequest().getListOrderOnCard(urlMySql);

        assertEquals(list.size(), newList.size());
    }


    @Test
    void mySqlBuyCreditIfCardApproved() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(urlMySql);
        val newCard = CardData.getCardInfo(CardData.getApprovedCardNumber());
        newCredit(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCredit(urlMySql);

        assertEquals(list.size()+1, newList.size());
        assertEquals(approved, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void mySqlBuyCreditIfCardDeclined() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(urlMySql);
        val newCard = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        newCredit(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCredit(urlMySql);

        assertEquals(list.size()+1, newList.size());
        assertEquals(declined, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void mySqlBuyCreditIfCardInvalid() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(urlMySql);
        val newCard = CardData.getCardInfo(CardData.getInvalidCardNumber());
        newCredit(newCard, statusServerError);
        val newList = new SqlRequest().getListOrderOnCredit(urlMySql);

        assertEquals(list.size(), newList.size());
    }
}
