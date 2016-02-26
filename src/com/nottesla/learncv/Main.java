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

    /**
     * Main function (entrypoint)
     *
     * @param args arguments for the program
     *
     */
    public static void main(String[] args) {
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
