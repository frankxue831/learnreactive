package com.frank.basic.generator;

public class BasicSerialNumberBuilder implements SerialNumberBuilder {

  private String constantString;
  private int creditNum;
  private String appCode;

  public BasicSerialNumberBuilder() {
  }

  public SerialNumberBuilder withConstantString(String constant) {
    constantString = constant;
    return this;
  }

  public SerialNumberBuilder withCreditNum(int creditnum) {
    creditNum = creditnum;
    return this;
  }

  public SerialNumberBuilder withAppCode(String appcode) {
    appCode = appcode;
    return this;
  }

  @Override
  public BasicSerialNumber build() {
    BasicSerialNumber serialNumber = new BasicSerialNumber();
    serialNumber.setConstantString(constantString);
    serialNumber.setCreditNum(creditNum);
    serialNumber.setAppCode(appCode);
    return serialNumber;
  }

  public static void main(String[] args) {
    BasicSerialNumberBuilder builder = new BasicSerialNumberBuilder();
    builder.withConstantString("ABC");
    builder.withCreditNum(123);
    builder.withAppCode("XYZ");
    BasicSerialNumber serialNumber = builder.build();
    System.out.println(serialNumber);
  }

}

