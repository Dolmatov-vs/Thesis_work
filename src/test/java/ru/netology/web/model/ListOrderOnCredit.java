package ru.netology.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOrderOnCredit {
    private String credit_id;
    private String payment_id;
    private String status;
}
