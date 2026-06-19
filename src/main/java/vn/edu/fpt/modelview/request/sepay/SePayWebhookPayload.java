package vn.edu.fpt.modelview.request.sepay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SePayWebhookPayload {
    private Long id;

    private String gateway;

    @JsonProperty("transactionDate")
    private String transactionDate;

    @JsonProperty("accountNumber")
    private String accountNumber;

    private String content;
    @JsonProperty("transferType")
    private String transferType;

    @JsonProperty("transferAmount")
    private BigDecimal transferAmount;

    @JsonProperty("referenceCode")
    private String referenceCode;
}
