package com.nottesla.learncv;

import edu.wpi.first.wpijavacv.*;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
@author(Telsa)
 */
public class Main {
    static OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();
    public static final String FOLDER = "/Users/tesla/Downloads/RealFullField";

    public static void main(String[] args) {
        File testImages[] = new File(FOLDER).listFiles();
        for (int i = 0; i < testImages.length; ++i) {
            if (testImages[i].getName().endsWith(".jpg")) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(testImages[i]);
                    process(new WPIImage(bufferedImage), testImages[i].getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void process(WPIImage image, String name) {
        WPIColorImage colorImage = new WPIColorImage(image.getBufferedImage());
        WPIGrayscaleImage green = colorImage.getGreenChannel();
        WPIBinaryImage greenBin = green.getThreshold(85, 255);
        /* DEBUG
        CanvasFrame c = ne
        CanvasFrame(name + " Combined");
        CanvasFrame r = new CanvasFrame(name + " Red");
        CanvasFrame g = new CanvasFrame(name + " Green");
        CanvasFrame b = new CanvasFrame(name + " Blue");
        c.showImage(combined.getBufferedImage());
        r.showImage(redBin.getBufferedImage());
        g.showImage(greenBin.getBufferedImage());
        b.showImage(blueBin.getBufferedImage());
        new Scanner(System.in).nextLine();
        */
        WPIContour contours[] = greenBin.findContours();
        for (WPIContour contour : contours) {
            if (contour.getWidth() >= colorImage.getWidth() - 2) {
                continue;
            }
            if (contour.getHeight() >= colorImage.getHeight() - 2) {
                continue;
            }
            if (contour.getWidth() <= 40) {
                continue;
            }
            if (contour.getHeight() <= 17) {
                continue;
            }
            colorImage.drawRect(contour.getX(), contour.getY(), contour.getWidth(), contour.getHeight(), WPIColor.RED, 2);
        }
        BufferedImage bufferedImage = colorImage.getBufferedImage();
        File f = new File("/tmp/processed/" + name.replace(".jpg", "_process.jpg"));

        try {
            ImageIO.write(bufferedImage, "JPEG", f);;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
