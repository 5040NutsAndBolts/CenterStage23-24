package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class LineSensor {
    //Line sensor
    private ColorSensor lineSensor;
    public LineSensor(HardwareMap hardwareMap) {
        //color sensor
        lineSensor = hardwareMap.get(ColorSensor.class, "Line Sensor");
        lineSensor.enableLed(false);
    }

    public int getBlueValue() {
        return lineSensor.blue();
    }
    public int getRedValue() {
        return lineSensor.red();
    }
}