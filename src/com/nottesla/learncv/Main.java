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

    public static final String FOLDER = "/Users/tesla/Downloads/RealFullField";


    /**
     * Main function (entrypoint)
     *
     * @param args arguments for the program
     *
     */
    public static void main(String[] args) {
        /* Get the contents of the FOLDER directory */
        File testImages[] = new File(FOLDER).listFiles();
        /* Make sure that it didn't return null (no files in the folder) */
        assert testImages != null;
        /* Iterate over files in folder */
        for (int i = 0; i < testImages.length; ++i) {
            /* If the file is a jpg */
            if (testImages[i].getName().endsWith(".jpg")) {
                try {
                    /* Read the image from the file */
                    BufferedImage bufferedImage = ImageIO.read(testImages[i]);
                    /* Process it */
                    process(new WPIImage(bufferedImage), testImages[i].getName());
                } catch (IOException e) {
                    /* IOException occurs when for some reason we can't read the file */
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Processes a {@link WPIImage}
     *
     * @param image The image to be processed
     * @param name The name of the image (required so it can be included in the processed copy)
     *
     */
    public static void process(WPIImage image, String name) {
        /* Convert image from WPIImage to WPIColorImage */
        WPIColorImage colorImage = new WPIColorImage(image.getBufferedImage());
        /* Get just the green channel from the image */
        WPIGrayscaleImage green = colorImage.getGreenChannel();
        /* Convert green channel to binary image, with a threshold of 85 */
        WPIBinaryImage greenBin = green.getThreshold(85, 255);
        /* Find all contours in the image */
        WPIContour contours[] = greenBin.findContours();
        /* If there are no contours, return now and do not try to process them */
        if (contours == null || contours.length == 0) {
            return;
        }
        /* The contour highest up on the image (usually the goal) */
        WPIContour highestContour = null;
        /* Iterate through contours */
        for (int i=0; i < contours.length; ++i) {
            /* Get the contour at the current index */
            WPIContour contour = contours[i];
            /* Ensure it's less long than the image itself */
            if (contour.getWidth() >= colorImage.getWidth() - 2) {
                continue;
            }
            /* Ensure it's less tall than the image itself */
            if (contour.getHeight() >= colorImage.getHeight() - 2) {
                continue;
            }
            /* Ensure the width is more than 40 pixels */
            if (contour.getWidth() <= 40) {
                continue;
            }
            /* Ensure the height is more than 17 pixels */
            if (contour.getHeight() <= 17) {
                continue;
            }
            /* If there is no highest contour yet, just use this one */
            if (highestContour == null) {
                highestContour = contour;
                continue;
            }
            /* If it is higher up than the current highest contour, set the highest contour to this */
            if (contour.getY() < highestContour.getY()) {
                highestContour = contour;
            }
        }
        /* Make sure we found a suitable contour, if not return now */
        if (highestContour == null) {
            return;
        }
        /* Draw a target on the image at the location of the contour */
        drawTarget(colorImage, highestContour);
        /* Convert the image to a BufferedImage so it can be written to a file with ImageIO */
        BufferedImage bufferedImage = colorImage.getBufferedImage();
        /* Create a new file, name it (image name)_processed.jpg */
        File f = new File("/tmp/processed/" + name.replace(".jpg", "_processed.jpg"));
        try {
            /* Write the image to the file */
            ImageIO.write(bufferedImage, "JPEG", f);;
        } catch (IOException e) {
            /* Fails if the file cannot be written to */
            e.printStackTrace();
        }
    }

    public static void drawTarget(WPIColorImage image, WPIContour contour) {
        /* Draw a red rectangle with a thickness of 3 around the bounds of the contour */
        image.drawRect(contour.getX(), contour.getY(), contour.getWidth(), contour.getHeight(), WPIColor.RED, 3);
        /* Approximate a polygon that fits the contour */
        WPIPolygon polygon = contour.approxPolygon(4);
        /* Draw the polygon in yellow with a thickness of 2 */
        image.drawPolygon(polygon, WPIColor.YELLOW, 2);
        /* Find the x and y coordinates (relative to the bounds of the contour) of the center of the contour */
        int y = contour.getY() + (contour.getHeight() / 2);
        int x = contour.getX() + (contour.getWidth() / 2);
        /* Draw a horizontal line through center of the contour */
        image.drawLine(new WPIPoint(contour.getX(), y), new WPIPoint(contour.getX() + contour.getWidth(), y), WPIColor.RED, 1);
        /* Draw a vertical line through the center of the contour */
        image.drawLine(new WPIPoint(x, contour.getY()), new WPIPoint(x, contour.getY() + contour.getHeight()), WPIColor.RED, 1);
        /* Do we need to turn left or right (is the image too far right or too far left */
        boolean direction = x < (image.getWidth() / 2);
        /* Do we need to turn up or down (is the image too far down or too far up */
        boolean top = y < (image.getHeight() / 2);
        /**
         * We don't want to draw shapes that are too big,
         * so make them a fifth of the size of the rectangle
         * we drew earlier
         */
        /* How tall vertical distances should be (a fifth of the height of the curve) */
        int topLength = contour.getHeight() / 5;
        /* How long horizontal distances should be (a fifth of the width of the curve) */
        int horizLength = contour.getWidth() / 5;
        /* The three points in the first triangle */
        WPIPoint height1, height2, height3;
        /* If we need to make a triangle on top pointing down */
        if (top) {
            /* The base of the triangle */
            height1 = new WPIPoint(x - horizLength, contour.getY() + contour.getHeight());
            height2 = new WPIPoint(x + horizLength, contour.getY() + contour.getHeight());
            /* The vertex of the triangle */
            height3 = new WPIPoint(x, contour.getY() + contour.getHeight() - topLength);
        } else /* If we need to make a triangle on bottom pointing up */ {
            /* The base of the triangle */
            height1 = new WPIPoint(x - horizLength, contour.getY());
            height2 = new WPIPoint(x + horizLength, contour.getY());
            /* The vertex of the triangle */
            height3 = new WPIPoint(x, contour.getY() + topLength);
        }
        /* Connect the dots */
        image.drawLine(height1, height2, WPIColor.RED, 2);
        image.drawLine(height1, height3, WPIColor.RED, 2);
        image.drawLine(height2, height3, WPIColor.RED, 2);
        /* The three points in the second triangle */
        WPIPoint width1, width2, width3;
        /* If we need to make a triangle on the left pointing right on vice versa */
        if (direction) {
            /* The base of the triangle */
            width1 = new WPIPoint(contour.getX() + contour.getWidth(), y - (topLength / 2));
            width2 = new WPIPoint(contour.getX() + contour.getWidth(), y + (topLength / 2));
            /* The vertex of the triangle */
            width3 = new WPIPoint(contour.getX() + contour.getWidth() - horizLength, y);
        } else {
            /* The base of the triangle */
            width1 = new WPIPoint(contour.getX(), y - (topLength / 2));
            width2 = new WPIPoint(contour.getX(), y + (topLength / 2));
            /* The vertex of the triangle */
            width3 = new WPIPoint(contour.getX() + (horizLength / 2), y);
        }
        /* Connect the dots for our second triangle */
        image.drawLine(width1, width2, WPIColor.RED, 2);
        image.drawLine(width1, width3, WPIColor.RED, 2);
        image.drawLine(width2, width3, WPIColor.RED, 2);
    }
}
