package ru.netology.web.mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOrderOnCard {
    private String credit_id;
    private String status;
    private String amount;
}
