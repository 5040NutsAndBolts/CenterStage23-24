package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp(name = "Mechanism Test", group = "Teleop")
public class MechanismTest extends OpMode
{
    static final double MAX_POS = 1.0;
    static final double MIN_POS = 0.0;
    double  position = MIN_POS;

    public ColorSensor lineSensor;

    @Override
    public void init() //initialization method
    {
        lineSensor = hardwareMap.get(ColorSensor.class, "Line Sensor");
        lineSensor.enableLed(false);
    }

    @Override
    public void loop() //teleop loop
    {
        lineSensor.enableLed(true);
        telemetry.addData("Red",  lineSensor.red());
        telemetry.addData("Green", lineSensor.green());
        telemetry.addData("Blue", lineSensor.blue());
        telemetry.update();
    }
}
