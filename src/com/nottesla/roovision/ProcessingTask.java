package com.nottesla.roovision;


import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIContour;
import edu.wpi.first.wpijavacv.WPIFFmpegVideo;
import edu.wpi.first.wpijavacv.WPIImage;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;

/**
 * Created by tesla on 2/26/16.
 */
public class ProcessingTask implements Runnable {
    private static final String IMAGE_URL = "http://roboRIO-4373.local:8080/nextImage";

    private VisionProcessor visionProcessor;
    private WPIFFmpegVideo camera;
    private ToIplImage toIplImage;

    public ProcessingTask(boolean debug) {
        camera = new WPIFFmpegVideo(IMAGE_URL);
        toIplImage = new ToIplImage();
        visionProcessor = new VisionProcessor();
        visionProcessor.setDebug(debug);
    }

    public ProcessingTask() {
        this(false);
    }

    @Override
    public void run() {
        WPIImage image = null;
        WPIColorImage colorImage = null;
        try {
            image = camera.getNewImage();
        } catch (WPIFFmpegVideo.BadConnectionException e) {
            e.printStackTrace();
        }

        if (image == null)
            return;

        visionProcessor.setImage(new WPIColorImage(image.getBufferedImage()));
        WPIContour goal = visionProcessor.findGoalContour();
        visionProcessor.updateNetworkTables(goal);
    }
}
