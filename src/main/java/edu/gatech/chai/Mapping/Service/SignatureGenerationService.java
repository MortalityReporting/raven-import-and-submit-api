package edu.gatech.chai.Mapping.Service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SignatureGenerationService {
    private static final Logger logger = LoggerFactory.getLogger(SignatureGenerationService.class);
    
    public byte[] generateBase64ForPngOfHandwritingName(String name){
        // Choose a font (replace with your desired font path)
        Font font = new Font("Brush Script MT", Font.PLAIN, 48); // Example font.
        // Calculate image dimensions
        BufferedImage image = new BufferedImage(calculateWidth(name, font), calculateHeight(font), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Set rendering hints for smooth text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Set font and color
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        // Draw the text
        g2d.drawString(name, 10, image.getHeight() - 20);

        g2d.dispose();
        // Encode base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ImageIO.write(image, "PNG", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    private static int calculateWidth(String text, Font font) {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        int width = g2d.getFontMetrics().stringWidth(text) + 20; // Add padding
        g2d.dispose();
        return width;
    }

    private static int calculateHeight(Font font) {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        int height = g2d.getFontMetrics().getHeight() + 20; // Add padding
        g2d.dispose();
        return height;
    }
}
