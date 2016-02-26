package com.nottesla.learncv;

import edu.wpi.first.wpijavacv.*;
import org.bytedeco.javacpp.helper.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.videoInputLib;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameConverter;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.awt.image.BufferedImage;

/**
 * Created by tesla on 2/26/16.
 */
public class ProcessingTask implements Runnable {
    private static final String CAMERA_IP = "1.3.3.7";

    private VisionProcessor visionProcessor;
    private WPICamera camera;

    public ProcessingTask(boolean debug) {
        camera = new WPICamera(CAMERA_IP);
        visionProcessor = new VisionProcessor();
        visionProcessor.setDebug(debug);
    }

    public ProcessingTask() {
        this(false);
    }

    @Override
    public void run() {
        WPIImage image = null;
        while (true) {
            try {
                image = camera.getNewImage();
            } catch (WPIFFmpegVideo.BadConnectionException ignored) {
            }
            if (image == null) {
                continue;
            }
            visionProcessor.setImage(new WPIColorImage(image.getBufferedImage()));
            WPIContour goal = visionProcessor.findGoalContour();
            visionProcessor.updateNetworkTables(goal);
        }
    }
}
