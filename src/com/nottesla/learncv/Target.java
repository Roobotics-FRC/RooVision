package com.nottesla.learncv;

import edu.wpi.first.wpijavacv.*;

/**
 * Created by tesla on 2/26/16.
 */
public class Target {
    private WPIPoint center, bottomLeft, bottomRight, topLeft, topRight;
    private WPIImage image;
    private WPIContour contour;

    public Target(WPIContour contour) {
        this.contour = contour;
        this.bottomLeft = new WPIPoint(contour.getX(), contour.getY());
        this.bottomRight = new WPIPoint(contour.getX() + contour.getWidth(), contour.getY());
        this.topLeft = new WPIPoint(contour.getX(), contour.getY() + contour.getHeight());
        this.topRight = new WPIPoint(contour.getX() + contour.getWidth(), contour.getY() + contour.getHeight());
        this.center = new WPIPoint(contour.getX() + (contour.getWidth() / 2), contour.getY() + (contour.getHeight() / 2));
    }

    public WPIPoint getCenter() {
        return center;
    }

    public void setCenter(WPIPoint center) {
        this.center = center;
    }

    public WPIPoint getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(WPIPoint bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public WPIPoint getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(WPIPoint bottomRight) {
        this.bottomRight = bottomRight;
    }

    public WPIPoint getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(WPIPoint topLeft) {
        this.topLeft = topLeft;
    }

    public WPIPoint getTopRight() {
        return topRight;
    }

    public void setTopRight(WPIPoint topRight) {
        this.topRight = topRight;
    }

    public WPIImage getImage() {
        return image;
    }

    public void setImage(WPIImage image) {
        this.image = image;
    }

    public WPIContour getContour() {
        return contour;
    }

    public void setContour(WPIContour contour) {
        this.contour = contour;
    }

    public void drawGuidelines(WPIColorImage image) {
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

    public WPIPoint getOffsetOn(WPIImage image) {
        int offsetX = (image.getWidth() / 2) - center.getX();
        int offsetY = (image.getHeight() / 2) - center.getY();
        return new WPIPoint(offsetX, offsetY);
    }
}
