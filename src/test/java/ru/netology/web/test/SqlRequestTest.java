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

    private static final String approved = "APPROVED";
    private static final String declined = "DECLINED";
    private static final String amount = "4500000";
    private static final String getJdbcUrl = System.getProperty("jdbcUrl");

    private static final int statusCodeOK = 200;
    private static final int statusServerError = 500;

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
        val list = new SqlRequest().getListOrderOnCard(getJdbcUrl);
        val newCard = CardData.getCardInfo(CardData.getApprovedCardNumber());
        newBuy(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCard(getJdbcUrl);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(approved, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
        assertEquals(amount, newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardDeclined() throws SQLException {
        val list = new SqlRequest().getListOrderOnCard(getJdbcUrl);
        val newCard = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        newBuy(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCard(getJdbcUrl);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(declined, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
        assertEquals(amount, newList.get(newList.size() - 1).getAmount());
    }

    @Test
    void buyCardIfCardInvalid() throws SQLException {
        val list = new SqlRequest().getListOrderOnCard(getJdbcUrl);
        val newCard = CardData.getCardInfo(CardData.getInvalidCardNumber());
        newBuy(newCard, statusServerError);
        val newList = new SqlRequest().getListOrderOnCard(getJdbcUrl);

        assertEquals(list.size(), newList.size());
    }

    @Test
    void buyCreditIfCardApproved() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(getJdbcUrl);
        val newCard = CardData.getCardInfo(CardData.getApprovedCardNumber());
        newCredit(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCredit(getJdbcUrl);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(approved, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void buyCreditIfCardDeclined() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(getJdbcUrl);
        val newCard = CardData.getCardInfo(CardData.getDeclinedCardNumber());
        newCredit(newCard, statusCodeOK);
        val newList = new SqlRequest().getListOrderOnCredit(getJdbcUrl);

        assertEquals(list.size() + 1, newList.size());
        assertEquals(declined, newList.get(newList.size() - 1).getStatus());
        assertNull(newList.get(newList.size() - 1).getCredit_id());
    }

    @Test
    void buyCreditIfCardInvalid() throws SQLException {
        val list = new SqlRequest().getListOrderOnCredit(getJdbcUrl);
        val newCard = CardData.getCardInfo(CardData.getInvalidCardNumber());
        newCredit(newCard, statusServerError);
        val newList = new SqlRequest().getListOrderOnCredit(getJdbcUrl);

        assertEquals(list.size(), newList.size());
    }
}
