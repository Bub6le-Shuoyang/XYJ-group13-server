package com.xyj.xyjserver.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class WalletTransactionVO {
    private Long id;
    private String title;
    private String type;
    private BigDecimal amount;
    private Date time;
}