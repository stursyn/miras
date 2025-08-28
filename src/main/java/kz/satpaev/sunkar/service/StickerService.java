package kz.satpaev.sunkar.service;

import kz.satpaev.sunkar.model.entity.Item;
import lombok.SneakyThrows;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StickerService {

    private static final int PX_W = 400;
    private static final int PX_H = 240;

//    public static void main(String[] args) {
//        // Создаем окно
//        JFrame frame = new JFrame("Display BufferedImage");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 240);
//
//        // JPanel для отображения изображения
//        JPanel panel = new JPanel() {
//            @SneakyThrows
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                g.drawImage(new StickerService().renderStickerDemo(null), 0, 0, null);
//            }
//        };
//
//        frame.add(panel);
//        frame.setVisible(true);
//    }

    public BufferedImage renderStickerDemo(Item item
    ) throws Exception {

        // Prepare canvas
        BufferedImage img = new BufferedImage(PX_W, PX_H, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, PX_W, PX_H);

        // Safe margins (thermal heads often clip 2–4 px)
        int m = 1;

        // Border (optional)
        g.setColor(Color.BLACK);
        g.draw(new Line2D.Double(m, PX_H/4, PX_W-m, PX_H/4));

        g.draw(new Line2D.Double(m, PX_H/2, PX_W-m, PX_H/2));
        g.draw(new Line2D.Double(m, PX_H/2 + 1, PX_W-m, PX_H/2+ 1));
        g.draw(new Line2D.Double(m, PX_H/2 + 2, PX_W-m, PX_H/2+ 2));

        g.draw(new Line2D.Double(m, 3*PX_H/4, PX_W-m, 3*PX_H/4));
        g.dispose();

        return img;
    }

    public BufferedImage renderSticker(Item item
    ) throws Exception {

        // Prepare canvas
        BufferedImage img = new BufferedImage(PX_W, PX_H, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, PX_W, PX_H);

        // crisp B/W for thermal printers
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Safe margins (thermal heads often clip 2–4 px)
        int m = 6;

        // Border (optional)
        g.setColor(Color.BLACK);
        g.draw(new RoundRectangle2D.Double(1, 1, PX_W - 2, PX_H - 2, 8, 8));

        // Fonts (choose printer-friendly)
        Font fName   = new Font("Arial", Font.BOLD, 20);
        Font fPrice  = new Font("Arial", Font.BOLD, 24);
        Font fSmall  = new Font("Arial", Font.PLAIN, 14);

        int x = m;
        int y = m + 24;

        // Product name (single line, elide if too long)
        g.setFont(fName);
        drawOneLineElided(g, item.getName(), x, y, PX_W - 2*m);

        // Price
        y += 26;
        var text = "₸ " + item.getSellPrice();
        g.setFont(fPrice);
        g.drawString(text, x, y);

        if(item.getWeight() != null) {
            // Weight (kg)
            y += 20;
            g.setFont(fSmall);
            String weightText = String.format("Вес: %d г", item.getWeight().intValue());
            g.drawString(weightText, x, y);
        }
        // Date info (Packed:)
        y += 20;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        g.setFont(fSmall);
        String dateText = "Дата: " + LocalDate.now().format(df);
        g.drawString(dateText, x, y);
    // full width inside margins
        int bcX = m;
        int bcY = PX_H - m - 80;  // leave room for human-readable text

        BufferedImage bc = makeEAN13(item.getBarcode());

        // Center barcode horizontally
        bcX = m + ( (PX_W - 2*m) - bc.getWidth()) / 2;
        g.drawImage(bc, bcX, bcY, null);

        g.dispose();
        return img;
    }

    // Generate EAN-13 barcode image (1-bit) using ZXing
    private BufferedImage makeEAN13(String ean13) throws Exception {
        Barcode barcode = BarcodeFactory.createEAN13(ean13.substring(0,12));
        barcode.setBarHeight(80);
        barcode.setBarWidth(3);

        return BarcodeImageHandler.getImage(barcode);
    }

    // Draw a single line and add "…" if it exceeds maxWidth
    private void drawOneLineElided(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontRenderContext frc = g.getFontRenderContext();
        if (g.getFont().getStringBounds(text, frc).getWidth() <= maxWidth) {
            g.drawString(text, x, y);
            return;
        }
        String ell = "…";
        int lo = 0, hi = text.length();
        while (lo < hi) {
            int mid = (lo + hi + 1) >>> 1;
            String s = text.substring(0, mid) + ell;
            if (g.getFont().getStringBounds(s, frc).getWidth() <= maxWidth) lo = mid; else hi = mid - 1;
        }
        g.drawString(text.substring(0, lo) + ell, x, y);
    }
}