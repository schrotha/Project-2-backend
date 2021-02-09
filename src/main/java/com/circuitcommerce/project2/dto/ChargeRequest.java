package com.circuitcommerce.project2.dto;

import lombok.Data;

@Data
public class ChargeRequest {

  public enum Currency {
    EUR, USD;
  }
  private String description;
  private Integer amount;
  private Currency currency;
  private String email;
  private String stripeToken;
}
