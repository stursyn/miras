package kz.satpaev.sunkar.service;

import kz.satpaev.sunkar.model.dto.StickerDTO;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import static kz.satpaev.sunkar.util.Constants.ruDateTimeFormatter;

@Service
public class StickerService {

    private static final int PX_W = 400;
    private static final int PX_H = 240;

    public BufferedImage renderSticker(StickerDTO dto) throws Exception {

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
        drawOneLineElided(g, dto.getName(), x, y, PX_W - 2*m);

        // Price
        y += 26;
        var text = "₸ " + dto.getPrice();
        g.setFont(fPrice);
        g.drawString(text, x, y);

        if(dto.getWeight() != null) {
            // Weight (kg)
            y += 20;
            g.setFont(fSmall);
            String weightText = String.format("Вес: %d г", dto.getWeight());
            g.drawString(weightText, x, y);
        }
        // Date info (Packed:)
        y += 20;
        g.setFont(fSmall);
        String dateText = "Дата: " + dto.getLocalDate().format(ruDateTimeFormatter);
        g.drawString(dateText, x, y);
    // full width inside margins
        int bcX = m;
        int bcY = PX_H - m - 80;  // leave room for human-readable text

        BufferedImage bc = makeEAN13(dto.getBarcode());

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