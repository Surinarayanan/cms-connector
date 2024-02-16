package com.p3.content.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : Suri Aravind @Creation Date : 16/02/24
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum CMODTableType {
  MASTER_INFO(
          ConfigBean.builder().startPoint(0).endPoint(3).build(),
          List.of(
          ColumnInfo.builder().name("BANK").primary(true).lineNo(1).position(12).length(3).build(),
          ColumnInfo.builder().name("CURRENCY").primary(true).lineNo(2).position(12).length(3).build(),
          ColumnInfo.builder().name("BRANCH").primary(true).lineNo(3).position(12).length(3).build(),
          ColumnInfo.builder().name("ORG_NAME").lineNo(2).position(51).length(26).build(),
          ColumnInfo.builder().name("PROCESS_DATE").primary(true).lineNo(2).position(123).length(10).build(),
          ColumnInfo.builder().name("PROCESS_THRU").primary(true).lineNo(3).position(123).length(10).build())
  ),
  LOAN_RECONCILIATION(
          ConfigBean.builder().startPoint(170).endPoint(188).build(),
      List.of(
          ColumnInfo.builder().name("TITLE").lineNo(1).position(1).length(43).build(),
          ColumnInfo.builder().name("LOAN_DEBITS_NUMBER").lineNo(1).position(44).length(6).build(),
          ColumnInfo.builder().name("LOAN_DEBITS_AMOUNT").lineNo(1).position(64).length(11).build(),
          ColumnInfo.builder().name("LOAN_CREDITS_NUMBER").lineNo(1).position(84).length(5).build(),
          ColumnInfo.builder()
              .name("LOAN_CREDITS_AMOUNT")
              .lineNo(1)
              .position(103)
              .length(11)
              .build())),

  SAVINGS_RECONCILIATION(
          ConfigBean.builder().startPoint(191).endPoint(205).build(),
      List.of(
          ColumnInfo.builder()
                  .name("TITLE")
                  .lineNo(1)
                  .position(1)
                  .length(43)
                  .build(),
          ColumnInfo.builder()
              .name("SAVINGS_DEBITS_NUMBER")
              .lineNo(1)
              .position(44)
              .length(6)
              .build(),
          ColumnInfo.builder()
              .name("SAVINGS_DEBITS_AMOUNT")
              .lineNo(1)
              .position(64)
              .length(11)
              .build(),
          ColumnInfo.builder()
              .name("SAVINGS_CREDITS_NUMBER")
              .lineNo(1)
              .position(84)
              .length(5)
              .build(),
          ColumnInfo.builder()
              .name("SAVINGS_CREDITS_AMOUNT")
              .lineNo(1)
              .position(103)
              .length(11)
              .build()));
  public ConfigBean configBean;
  private List<ColumnInfo> columnList;
}
