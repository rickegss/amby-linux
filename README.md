# Amby - Adaptive Brightness Daemon

A lightweight Java daemon for Linux that automatically adjusts screen brightness based on ambient light conditions detected through your webcam using computer vision techniques.

## Overview

Amby uses OpenCV to analyze real-time camera feed and dynamically adjusts screen brightness to match environmental lighting conditions. The system implements smooth transitions to prevent abrupt brightness changes, providing a comfortable viewing experience similar to modern smartphone adaptive brightness systems.

## Technical Implementation

### Computer Vision Approach

The daemon captures frames from the default webcam (device 0) and processes them through the following pipeline:

1. **Frame Capture**: 640x480 resolution at approximately 30 FPS
2. **Grayscale Conversion**: Using `Imgproc.cvtColor()` with `COLOR_BGR2GRAY`
3. **Mean Luminance Calculation**: `Core.mean()` computes average pixel intensity across the entire frame
4. **Brightness Mapping**: Linear transformation with 2.4x multiplier to map luminance (0-255) to brightness percentage (0-100)
5. **Smooth Transitions**: Incremental adjustment (±1% per iteration) to avoid jarring changes

### System Integration

Brightness control is achieved through the `brightnessctl` utility, which interfaces with the Linux backlight subsystem. The daemon spawns system processes using `ProcessBuilder` to execute brightness adjustments.

## Requirements

- **OS**: Linux (tested on Arch)
- **Java**: JDK 17 or higher
- **System Tools**: `brightnessctl` must be installed
- **Hardware**: Functional webcam (V4L2 compatible)
- **Permissions**: User must have access to `/sys/class/backlight` or be in the `video` group

## Dependencies

- **OpenCV 4.9.0-0**: Computer vision library (openpnp distribution)
- **nu.pattern.OpenCV**: Native library loader

## Building

### Maven

```bash
mvn clean package
```

The Maven Shade plugin generates an uber-JAR with all dependencies at `target/amby-1.0-SNAPSHOT.jar`.

## Installation

1. Install system dependencies:
```bash
sudo pacman -S brightnessctl  # Arch Linux
```

2. Build the project:
```bash
mvn clean package
```

3. Run the daemon:
```bash
java -jar target/amby-1.0-SNAPSHOT.jar
```

## Configuration

### Brightness Calculation Formula

```
calculated_brightness = (mean_luminance / 255.0) × 100 × 2.4
```

- **Minimum threshold**: 15% (prevents excessively dark screen)
- **Maximum cap**: 100%
- **Sampling rate**: Every 30 frames (~1 second at 30 FPS)
- **Adjustment rate**: ±1% every 2 frames (~15 adjustments/second)

### Performance Tuning

Modify `frameCounter` conditions in `Main.java`:

- **Line 33**: `frameCounter % 30` - brightness calculation frequency
- **Line 42**: `frameCounter % 2` - adjustment application frequency

## Architecture

```
Main.java
├── VideoCapture initialization (OpenCV)
├── Frame processing loop
│   ├── Grayscale conversion
│   ├── Mean luminance calculation
│   └── Target brightness determination
└── BrightnessController invocation

BrightnessController.java
└── System brightness adjustment via brightnessctl
```

## Known Limitations

- Requires `brightnessctl` with appropriate permissions
- Camera must support V4L2 (Video4Linux2) interface
- No GUI configuration interface
- Single camera support only (device index 0)
- Linear brightness mapping may not suit all environments

## Testing Environment

- **Distribution**: Arch Linux
- **Kernel**: Linux 6.x
- **Display Server**: X11/Wayland compatible
- **Backlight Interface**: Intel/AMD integrated graphics

## Future Enhancements

- Configuration file support (YAML/JSON)
- Multiple camera selection
- Custom brightness curves
- Systemd service integration
- GUI settings panel

## License

MIT License.

## References

- OpenCV Documentation: https://docs.opencv.org/4.9.0/
- brightnessctl: https://github.com/Hummer12007/brightnessctl
- Maven Shade Plugin: https://maven.apache.org/plugins/maven-shade-plugin/
