package com.nottesla.learncv;

import edu.wpi.first.wpijavacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
@author(Telsa)
 */
public class Main {

    public int getSeven() {
        return seven;
    }

    public void setSeven(int seven) {
        this.seven = seven;
    }

    private int seven = 1;

    public static final String FOLDER = "/Users/tesla/Downloads/RealFullField";
    public static final int k = 15;



    public static void main(String[] args) {
        File testImages[] = new File(FOLDER).listFiles();
        assert testImages != null;
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
        if (contours.length == 0) {
            return;
        }
        WPIContour highestContour = contours[0];
        for (int i=1; i<contours.length; ++i) {
            WPIContour contour = contours[i];
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

            if (contour.getY() < highestContour.getY()) {
                highestContour = contour;
            }
        }
        drawTarget(colorImage, highestContour);
        System.out.println(highestContour.getWidth());
        BufferedImage bufferedImage = colorImage.getBufferedImage();
        File f = new File("/tmp/processed/" + name.replace(".jpg", "_process.jpg"));

        try {
            ImageIO.write(bufferedImage, "JPEG", f);;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void drawTarget(WPIColorImage image, WPIContour contour) {
        image.drawRect(contour.getX(), contour.getY(), contour.getWidth(), contour.getHeight(), WPIColor.RED, 3);
        WPIPolygon polygon = contour.approxPolygon(4);
        image.drawPolygon(polygon, WPIColor.YELLOW, 2);
        WPIPoint start, end;
        int y = contour.getY() + (contour.getHeight() / 2);
        int x = contour.getX() + (contour.getWidth() / 2);
        start = new WPIPoint(contour.getX(), y);
        end = new WPIPoint(contour.getX() + contour.getWidth(), y);

        boolean direction = x < (image.getWidth() / 2);
        boolean top = y < (image.getHeight() / 2);
        image.drawLine(start, end, WPIColor.RED, 1);
        image.drawLine(new WPIPoint(x, contour.getY()), new WPIPoint(x, contour.getY() + contour.getHeight()), WPIColor.RED, 1);
        int topLength = contour.getHeight() / 5;
        int sideLength = contour.getWidth() / 5;
        if (top) {
            WPIPoint top1 = new WPIPoint(x - sideLength, contour.getY() + contour.getHeight());
            WPIPoint top2 = new WPIPoint(x + sideLength, contour.getY() + contour.getHeight());
            WPIPoint top3 = new WPIPoint(x, contour.getY() + contour.getHeight() - topLength);
            image.drawLine(top1, top2, WPIColor.RED, 2);
            image.drawLine(top1, top3, WPIColor.RED, 2);
            image.drawLine(top2, top3, WPIColor.RED, 2);
        } else {
            WPIPoint bottom1 = new WPIPoint(x - sideLength, contour.getY());
            WPIPoint bottom2 = new WPIPoint(x + sideLength, contour.getY());
            WPIPoint bottom3 = new WPIPoint(x, contour.getY() + topLength);
            image.drawLine(bottom1, bottom2, WPIColor.RED, 2);
            image.drawLine(bottom1, bottom3, WPIColor.RED, 2);
            image.drawLine(bottom2, bottom3, WPIColor.RED, 2);
        }
        WPIPoint width1, width2, width3;
        if (direction) {
            width1 = new WPIPoint(contour.getX() + contour.getWidth(), y - (topLength / 2));
            width2 = new WPIPoint(contour.getX() + contour.getWidth(), y + (topLength / 2));
            width3 = new WPIPoint(contour.getX() + contour.getWidth() - sideLength, y);
        } else {
            width1 = new WPIPoint(contour.getX(), y - (topLength / 2));
            width2 = new WPIPoint(contour.getX(), y + (topLength / 2));
            width3 = new WPIPoint(contour.getX() + (sideLength / 2), y);
        }
        image.drawLine(width1, width2, WPIColor.RED, 2);
        image.drawLine(width1, width3, WPIColor.RED, 2);
        image.drawLine(width2, width3, WPIColor.RED, 2);
    }
}
