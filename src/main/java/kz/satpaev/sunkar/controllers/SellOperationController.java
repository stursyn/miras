package kz.satpaev.sunkar.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kz.satpaev.sunkar.callbacks.ItemRemoveButtonCallback;
import kz.satpaev.sunkar.model.dto.ItemDto;
import kz.satpaev.sunkar.model.entity.Item;
import kz.satpaev.sunkar.model.entity.Sale;
import kz.satpaev.sunkar.model.entity.SaleItem;
import kz.satpaev.sunkar.repository.ItemRepository;
import kz.satpaev.sunkar.repository.SaleItemRepository;
import kz.satpaev.sunkar.repository.SaleRepository;
import kz.satpaev.sunkar.service.NiimbotB1PrinterService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static kz.satpaev.sunkar.Main.applicationContext;
import static kz.satpaev.sunkar.util.ByteUtil.calculateChecksum;

@Component
public class SellOperationController implements Initializable {

    StringBuilder sb = new StringBuilder();

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private SaleItemRepository sellItemRepository;

    @FXML
    private MenuBar menu;
    @FXML
    private TableView<ItemDto> itemTable;
    @FXML
    public TableColumn<ItemDto, String> barcode;
    @FXML
    public TableColumn<ItemDto, String> itemName;
    @FXML
    public TableColumn<ItemDto, Integer> price;
    @FXML
    public TableColumn<ItemDto, Integer> count;
    @FXML
    public TableColumn<ItemDto, Integer> totalPrice;
    @FXML
    public TableColumn<ItemDto, String> operation;
    @FXML
    private Label totalSum;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        barcode.setCellValueFactory(new PropertyValueFactory<>("Barcode"));
        itemName.setCellValueFactory(new PropertyValueFactory<>("ItemName"));
        price.setCellValueFactory(new PropertyValueFactory<>("Price"));
        count.setCellValueFactory(new PropertyValueFactory<>("Count"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("TotalPrice"));

        operation.setCellFactory(new ItemRemoveButtonCallback(() -> {
            countTotalSum();
            return null;
        }, itemRepository));

        barcode.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.1));
        itemName.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.65));
        price.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.05));
        count.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.05));
        totalPrice.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.05));
        operation.prefWidthProperty().bind(itemTable.widthProperty().multiply(0.1));
    }

    public void keyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
            String barCode = sb.toString();

            ItemDto foundItem = null;
            for (ItemDto item : itemTable.getItems()) {
                if (barCode.equals(item.getBarcode())) {
                    item.setCount(item.getCount() + 1);
                    item.setTotalPrice(item.getCount() * item.getPrice());
                    foundItem = item;

                    itemTable.refresh();
                }
            }

            if (foundItem == null) {
                itemTableAddNewItem(barCode);
            }

            countTotalSum();
            return;
        }

        sb.append(event.getText());
    }

    private void itemTableAddNewItem(String barCode) {
        Item dbItem = itemRepository.findItemByBarcode(barCode);
        if (dbItem != null) {
            ItemDto displayItem = new ItemDto();
            displayItem.setBarcode(dbItem.getBarcode());
            displayItem.setItemName(dbItem.getName());
            if (dbItem.getSellPrice() != null) {
                displayItem.setPrice(dbItem.getSellPrice().doubleValue());
            }
            displayItem.setCount(1);
            displayItem.setTotalPrice(displayItem.getCount() * displayItem.getPrice());
            itemTable.getItems().add(displayItem);
        } else {
            dbAddNewItem("Неизвестный товар");
            itemTableAddNewItem(barCode);
        }
    }

    public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
        EAN13Writer barcodeWriter = new EAN13Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.EAN_13, 300, 150);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    @SneakyThrows
    public BufferedImage getImage() {
        int pxWidth = 400;   // 50 мм → пиксели
        int pxHeight = (int)(30 / 25.4 * 203); // 30 мм → пиксели

        BufferedImage img = new BufferedImage(pxWidth, pxHeight, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, pxWidth, pxHeight);
        g.setColor(Color.BLACK);

        // 2. Рисуем текст
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Привет, B1!", 10, 30);

        // 3. Генерируем QR код (ZXing)
        String barcodeText = new Random().nextInt(1_000_000, 9_999_999)
                + "" + new Random().nextInt(10_000, 99_999);
        BufferedImage qr = generateEAN13BarcodeImage(barcodeText + calculateChecksum(barcodeText));

        g.drawImage(qr, 10, 40, null);
        g.dispose();
        return img;
    }

    public void printFile() {
        try (var printer = new NiimbotB1PrinterService("COM3", 9600)) {
            printer.testPrinter(getImage());
            System.out.println("finish printing");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("finish method");
    }

    public void paidAll() {
        if (itemTable.getItems().size() <= 0) return;

        var sale = new Sale();
        sale.setSaleTime(LocalDateTime.now());
        sale.setAmount(new BigDecimal(totalSum.getText()));
        sale = saleRepository.save(sale);

        var saveList = new ArrayList<SaleItem>();
        for (ItemDto item : itemTable.getItems()) {
            var saleItem = new SaleItem();
            saleItem.setItemBarcode(item.getBarcode());
            saleItem.setSaleId(sale.getId());
            saleItem.setQuantity(item.getCount());
            saleItem.setUnitPrice(BigDecimal.valueOf(item.getPrice()));
            saveList.add(saleItem);
        }
        sellItemRepository.saveAll(saveList);

        itemTable.getItems().clear();
        countTotalSum();
    }

    public void dbAddNewItem(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Item.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            ItemController controller = loader.getController();
            controller.barcode.setText(sb.toString());
            if (!StringUtils.isEmpty(name)) {
                controller.name.setText(name);
            }
            controller.quantity.setText("1");
            controller.sellPrice.requestFocus();

            Stage stage = new Stage();
            stage.setTitle("Добавление продукта");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            scene.setOnKeyReleased(controller::keyPressed);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void countTotalSum() {
        sb.setLength(0);

        double total = 0;
        for (ItemDto item : itemTable.getItems()) {
            total += item.getTotalPrice();
        }
        totalSum.setText(total + "");
    }
}
