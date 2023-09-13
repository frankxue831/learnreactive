package com.frank.basic.generator;

// Basic Serial Number concrete class
public class BasicSerialNumber implements SerialNumber {
    private String constantString;
    private int creditNum;
    private String appCode;


    public String getConstantString() {
        return constantString;
    }

    public void setConstantString(String constantString) {
        this.constantString = constantString;
    }

    public int getCreditNum() {
        return creditNum;
    }

    public void setCreditNum(int creditNum) {
        this.creditNum = creditNum;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    @Override
    public String toString() {
        return "BasicSerialNumber{" +
                "constantString='" + constantString + '\'' +
                ", creditNum=" + creditNum +
                ", appCode='" + appCode + '\'' +
                '}';
    }

    @Override
    public String getSerialNumber() {
        // string builder
        StringBuilder sb = new StringBuilder();
        sb.append(constantString);
        sb.append("-");
        sb.append(creditNum);
        sb.append("-");
        sb.append(appCode);
        return sb.toString();
    }
}
