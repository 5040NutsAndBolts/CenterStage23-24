package org.firstinspires.ftc.teamcode.HelperClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.Mechanisms.LineSensor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Odometry  {
    //Odometry Helper Class Variables
    public double x = 0;
    public double y = 0;
    public double theta = 0;
    private static LinearOpMode currentOpMode;

    private DcMotorEx leftOdom, rightOdom, centerOdom;

    // Real world distance traveled by the wheels
    private double leftOdomTraveled, rightOdomTraveled, centerOdomTraveled;

    // Odometry encoder positions
    public int leftEncoderPos;
    public int centerEncoderPos;
    public int rightEncoderPos;

    //used in our odo but not RoadRunner classes
    private static final double ODOM_TICKS_PER_IN = 335.4658854;
    private static double trackwidth = 15.57716028;

    public Odometry (HardwareMap hardwareMap) {
        super();
        //Odom
        leftOdom = hardwareMap.get(DcMotorEx.class, "Front Right");
        rightOdom = hardwareMap.get(DcMotorEx.class, "Back Right");
        centerOdom = hardwareMap.get(DcMotorEx.class, "Front Left");
    }


    //constructs localizer object using one parameter of a list of three wheel positions
    public ThreeTrackingWheelLocalizer odom = new ThreeTrackingWheelLocalizer
            (
                    new ArrayList<>(Arrays.asList(
                            new Pose2d(6.294091345, 0, Math.PI / 2), //center wheel
                            new Pose2d(0, trackwidth/2, 0), //right wheel
                            new Pose2d(0, -trackwidth/2, 0))) //left wheel
            )
    {
        //overrides getWheelPositions method
        @Override
        public List<Double> getWheelPositions()
        {
            ArrayList<Double> wheelPositions = new ArrayList<>(3);
            wheelPositions.add(centerOdomTraveled);
            wheelPositions.add(leftOdomTraveled);
            wheelPositions.add(rightOdomTraveled);
            return wheelPositions;
        }
    };

    private int getDeltaLeftTicks()
    {
        try
        {
            int total=-leftOdom.getCurrentPosition();
            int oldPos = leftEncoderPos;
            leftEncoderPos=total;
            return oldPos - total;
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    private int getDeltaRightTicks()
    {
        try
        {
            int total=rightOdom.getCurrentPosition();
            int oldPos = rightEncoderPos;
            rightEncoderPos=total;
            return oldPos - total;
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    private int getDeltaCenterTicks()
    {
        try
        {
            int total=centerOdom.getCurrentPosition();
            int oldPos = centerEncoderPos;
            centerEncoderPos=total;
            return oldPos - total;
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    public void updatePosition()
    {
        /*try
        {
            bulkData = expansionHub.getBulkInputData();
        }
        catch(Exception e)
        {
            return;
        }*/

        // Change in the distance (centimeters) since the last update for each odometer
        double deltaLeftDist = -(getDeltaLeftTicks()/ ODOM_TICKS_PER_IN );
        double deltaRightDist = -(getDeltaRightTicks()/ ODOM_TICKS_PER_IN );
        double deltaCenterDist = getDeltaCenterTicks()/ ODOM_TICKS_PER_IN;

        //adjusts for physical diffrences in pods
        if(deltaLeftDist < 0)
            deltaRightDist *= (1.00052425 * 0.99770514);
        else if (deltaLeftDist > 0)
            deltaRightDist *= (1.00076168 * 0.99859913);

        leftOdomTraveled += deltaLeftDist;
        rightOdomTraveled += deltaRightDist;
        centerOdomTraveled += deltaCenterDist;

        //add negatives here to reverse x or y directions
        odom.update();
        theta = odom.getPoseEstimate().component3();
        x = odom.getPoseEstimate().component1();
        y = odom.getPoseEstimate().component2();
    }

    public void resetOdometry(double x, double y, double theta)
    {
        odom.setPoseEstimate(new Pose2d(-x, -y, theta));

        leftOdomTraveled = 0;
        rightOdomTraveled = 0;
        leftOdomTraveled = 0;

        leftEncoderPos = 0;
        rightEncoderPos = 0;
        centerEncoderPos = 0;

        // Resets encoder values then sets them back to run without encoders because wheels and odometry are same pointer
        leftOdom.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftOdom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightOdom.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightOdom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        centerOdom.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        centerOdom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
