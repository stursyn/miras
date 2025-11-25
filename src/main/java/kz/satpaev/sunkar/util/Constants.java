package kz.satpaev.sunkar.util;

import kz.satpaev.sunkar.controllers.keyboardfx.KeyboardView;

import java.time.format.DateTimeFormatter;

public class Constants {
  public static final String OPACITY_RECTANGLE_ID = "#CUSTOM_OPACITY_RECTANGLE_ID";
  public static final String TENGE_SUFFIX = " тг.";
  public static final String UNIVERSAL_PRODUCT_BARCODE = "UniversalProduct";
  public static final String UNIVERSAL_PRODUCT_TITLE = "Универсальный продукт";
  public static final KeyboardView KEYBOARD_VIEW = new KeyboardView("keyboard-full-kz.xml", "keyboard-full-us.xml");
  public static final KeyboardView KEYBOARD_VIEW_NUMERIC = new KeyboardView("keyboard-numeric.xml");
  public static DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");;
  public static DateTimeFormatter ruDateTimeFormatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public static final String KZ_GS1_CODE = "487";
  public static final String MERCHANT_CODE = "26";
}
