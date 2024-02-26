package org.firstinspires.ftc.teamcode.Mechanisms;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ColorSensor;
//import com.qualcomm.hardware.bosch.BHI260IMU;


public class Odom
{

    //Odometry Helper Class Variables
    public double x = 0, y = 0, theta = 0;
    public static LinearOpMode currentOpMode;

    public DcMotorEx leftOdom, rightOdom, centerOdom;

    // Real world distance traveled by the wheels
    public double leftOdomTraveled, rightOdomTraveled, centerOdomTraveled;

    // Odometry encoder positions
    public int leftEncoderPos, centerEncoderPos, rightEncoderPos;

    //used in our odo but not RoadRunner classes
    public static final double ODOM_TICKS_PER_IN = 335.4658854;
    public static double trackwidth = 15.57716028;

    public Odom(HardwareMap hardwareMap)
    {
        //Odom
        leftOdom = hardwareMap.get(DcMotorEx.class, "Front Right");
        rightOdom = hardwareMap.get(DcMotorEx.class, "Back Right");
        centerOdom = hardwareMap.get(DcMotorEx.class, "Front Left");
    }
}