package ru.netology.web.mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOrderOnCredit {
    private String credit_id;
    private String status;
    private String amount;
}
