package com.nottesla.roovision;

import edu.wpi.first.wpijavacv.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.bytedeco.javacv.CanvasFrame;

/**
 * Created by tesla on 2/26/16.
 */
public class VisionProcessor {
    private WPIColorImage image;
    private NetworkTable visionTable;
    private boolean debug = false;
    private CanvasFrame debugFrame;
    private boolean networkTables;

    public VisionProcessor(WPIColorImage image) {
        this.image = image;
        this.networkTables = false;
    }

    public VisionProcessor() {
        this(null);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        if (debug && debug != this.debug) {
            debugFrame = new CanvasFrame("Vision Debug");
        }
        this.debug = debug;
    }

    public WPIColorImage getImage() {
        return image;
    }

    public void setImage(WPIColorImage image) {
        this.image = image;
    }

    public WPIContour findGoalContour() {
        /* Check if image is currently null, if it is, return null */
        if (image == null) {
            return null;
        }
        /* Get just the green channel from the image */
        WPIGrayscaleImage green = image.getGreenChannel();
        /* Convert green channel to binary image, with a threshold of 85 */
        WPIBinaryImage greenBin = green.getThreshold(85, 255);
        /* Find all contours in the image */
        WPIContour contours[] = greenBin.findContours();
        /* If there are no contours, return now and do not try to process them */
        if (contours == null || contours.length == 0) {
            return null;
        }
        /* The contour highest up on the image (usually the goal) */
        WPIContour highestContour = null;
        /* Iterate through contours */
        for (int i=0; i < contours.length; ++i) {
            /* Get the contour at the current index */
            WPIContour contour = contours[i];
            /* Ensure it's less long than the image itself */
            if (contour.getWidth() >= image.getWidth() - 2) {
                continue;
            }
            /* Ensure it's less tall than the image itself */
            if (contour.getHeight() >= image.getHeight() - 2) {
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
        return highestContour;
    }

    public Target createTarget(WPIContour contour) {
        return new Target(contour);
    }

    public void updateNetworkTables(WPIContour contour) {
        if (!networkTables) {
            NetworkTable.setTeam(4373);
            NetworkTable.setClientMode();
            this.visionTable = NetworkTable.getTable("rooVision");
            System.out.println("*** " + this.visionTable + " ***");
            networkTables = true;
        }
        Target target = new Target(contour);
        WPIPoint offset = target.getOffsetOn(image);
        this.visionTable.putNumber("horizontal", offset.getX());
        this.visionTable.putNumber("vertical", offset.getY());
        if (isDebug()) {
            target.drawGuidelines(image);
            debugFrame.showImage(image.getBufferedImage());
        }
    }
}
