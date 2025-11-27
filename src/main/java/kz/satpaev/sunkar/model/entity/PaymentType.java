package kz.satpaev.sunkar.model.entity;

public enum PaymentType {
  KASPI("KASPI"),
  HALYK("HALYK"),
  COMBINED("Составной"),
  CASH("Наличными"),
  DUTY("Долг");
  private final String value;

  PaymentType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
