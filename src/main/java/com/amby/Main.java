package com.amby;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Main {
    public static void main(String[] args) {
        BrightnessController controller = new BrightnessController();

        OpenCV.loadLocally();
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error on loading camera.");
            return;
        }

        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

        Mat frame = new Mat();
        Mat grayFrame = new Mat();

        System.out.println("Amby Running... Press any key on window to stop.");

        int targetBrightness = 50;
        int currentBrightness = 50;
        int frameCounter = 0;

        while (true) {
            if (camera.read(frame)) {

                if (frameCounter % 30 == 0) {
                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                    Scalar px_mean = Core.mean(grayFrame);
                    double lightInput = px_mean.val[0];

                    int calculated = (int) ((lightInput / 255.0) * 100 * 2.4);

                    if (calculated < 10) calculated = 15;
                    if (calculated > 100) calculated = 100;

                    targetBrightness = calculated;
                }

                if (frameCounter % 2 == 0) {
                    if (currentBrightness < targetBrightness) {
                        currentBrightness++;
                    } else if (currentBrightness > targetBrightness) {
                        currentBrightness--;
                    }

                    controller.setBrightness(currentBrightness);

                    System.out.printf("\rTarget: %d%% | Current: %d%%   ", targetBrightness, currentBrightness);
                }
                frameCounter++;
            }
        }
    }
}