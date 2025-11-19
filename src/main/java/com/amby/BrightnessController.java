package com.amby;

import java.io.IOException;

public class BrightnessController {
    public void setBrightness(int percentage){
        if (percentage < 0) percentage = 0;
        if (percentage > 100) percentage = 100;

        try{
            ProcessBuilder pb = new ProcessBuilder("brightnessctl", "set", percentage + "%");
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Brightness set error: " + e.getMessage());
        }
    }
}
