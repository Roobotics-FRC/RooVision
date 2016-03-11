package com.nottesla.roovision;

import edu.wpi.first.wpijavacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
@author(Telsa)
 */
public class Main {

    /**
     * Main function (entrypoint)
     *
     * @param args arguments for the pro m
     *
     */
    public static void main(String[] args) throws IOException {
        production();
    }
    public static void debug() throws IOException {
        File folder = new File("images");
        File[] images = folder.listFiles();
        if (images == null) {
            return;
        }
        ProgressBar progressBar = new ProgressBar(images.length);
        while (!progressBar.isRun()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        VisionProcessor visionProcessor = new VisionProcessor();
        BufferedImage image;
        WPIImage wpiImage;
        WPIColorImage wpiColorImage;
        File output;
        for (int i = 0; i < images.length; ++i) {
            progressBar.setProgress(i+1);
            image = ImageIO.read(images[i]);
            wpiImage = new WPIImage(image);
            wpiColorImage = new WPIColorImage(wpiImage.getBufferedImage());
            visionProcessor.setDebug(false);
            visionProcessor.setImage(wpiColorImage);
            WPIContour contour = visionProcessor.findGoalContour();
            Target target = new Target(contour);
            target.drawGuidelines(wpiColorImage);
//            visionProcessor.updateNetworkTables(contour);
            output = new File(images[i].getPath() + ".jpg");
            ImageIO.write(wpiColorImage.getBufferedImage(), "jpg", output);
        }
    }
    public static void production() {
        ProcessingTask processingTask = new ProcessingTask(true);
        Thread processingThread = new Thread(processingTask);
        processingThread.start();
        try {
            processingThread.join();
        } catch (InterruptedException e) {
            System.err.println("Interrupted!");
        }
    }
}
