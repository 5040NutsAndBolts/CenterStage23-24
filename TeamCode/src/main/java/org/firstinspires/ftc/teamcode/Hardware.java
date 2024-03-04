package org.firstinspires.ftc.teamcode;


import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.hardware.bosch.BNO055IMU;
//import com.qualcomm.hardware.bosch.BHI260IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public class Hardware
{
    //Drive Motors Declaration
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;

    //gyro settup
    //public BNO055IMU imu;
    //public double adjust;

    //Transfer Motors and Servos Declaration
    public DcMotorEx transferM1;
    public DcMotorEx transferM2;
    public CRServo transferCR1;
    public CRServo transferCR2;

    //Drone Launcher Servo Declaration
    public Servo droneLaunch;

    //Intake Servos Declaration
    public CRServo intakeServo;
    public DcMotor intakeMotor;

    //Deposit Servos Declaration
    public Servo depositServoOne;
    public Servo depositServoTwo;

    //pull up motor
    public DcMotorEx hangMotor;

    //Line sensor
    public ColorSensor lineSensor;

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

    public Hardware(HardwareMap hardwareMap)
    {
        //Drive Motor Initialization
        frontLeft = hardwareMap.get(DcMotorEx.class, "Front Left");
        frontRight = hardwareMap.get(DcMotorEx.class, "Front Right");
        backLeft = hardwareMap.get(DcMotorEx.class, "Back Left");
        backRight = hardwareMap.get(DcMotorEx.class, "Back Right");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //gyro setup
        /*BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);*/

        //Transfer Motor Config -- Raise motor
        transferM1 = hardwareMap.get(DcMotorEx.class, "Transfer Motor 1");
        transferM2 = hardwareMap.get(DcMotorEx.class, "Transfer Motor 2");

        transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        transferM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        transferM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        transferM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        transferM2.setDirection(DcMotorSimple.Direction.REVERSE);

        //Intake Config - Transfer servos spin with intake servos/motors
        intakeServo = hardwareMap.get(CRServo.class, "Intake Servo");
        intakeMotor = hardwareMap.get(DcMotor.class, "Intake Motor");
        transferCR1 = hardwareMap.get(CRServo.class, "Transfer Servo 1");
        transferCR2 = hardwareMap.get(CRServo.class, "Transfer Servo 2");
        //Drone Launcher Config
        droneLaunch = hardwareMap.get(Servo.class, "Drone Launcher");

        //Deposit Servo Config
        depositServoOne = hardwareMap.get(Servo.class, "Right Deposit");
        depositServoTwo = hardwareMap.get(Servo.class, "Left Deposit");

        //color sensor
        lineSensor = hardwareMap.get(ColorSensor.class, "Line Sensor");

        //hang motor
        hangMotor = hardwareMap.get(DcMotorEx.class, "Hang Motor");

        //Odom
        leftOdom = hardwareMap.get(DcMotorEx.class, "Front Right");
        rightOdom = hardwareMap.get(DcMotorEx.class, "Back Right");
        centerOdom = hardwareMap.get(DcMotorEx.class, "Front Left");
    }

    //robot-oriented drive method
    public void robotODrive(double forward, double sideways, double rotation)
    {
        //adds all the inputs together to get the number to scale it by
        double scale = Math.abs(rotation) + Math.abs(forward) + Math.abs(sideways);

        //Scales the inputs between 0-1 for the setPower() method
        if (scale > 1) {
            forward /= scale;
            rotation /= scale;
            sideways /= scale;
        }

        //Zeroes out and opposing or angular force from the Mechanum wheels
        frontLeft.setPower(forward - rotation - sideways);
        backLeft.setPower(forward - rotation + sideways);
        frontRight.setPower(forward + rotation + sideways);
        backRight.setPower(forward + rotation - sideways);
    }

    //field oriented drive
    /*public void fieldODrive(double forward, double sideways, double rotation, boolean reset)
    {
        double P = Math.hypot(sideways, forward);
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);

        double robotAngle = Math.atan2(forward, sideways);

        if (reset)
            adjust = angles.firstAngle;

        double v5 = P * Math.sin(robotAngle - angles.firstAngle + adjust) + P * Math.cos(robotAngle - angles.firstAngle + adjust) - rotation;
        double v6 = P * Math.sin(robotAngle - angles.firstAngle + adjust) - P * Math.cos(robotAngle - angles.firstAngle + adjust) + rotation;
        double v7 = P * Math.sin(robotAngle - angles.firstAngle + adjust) - P * Math.cos(robotAngle - angles.firstAngle + adjust) - rotation;
        double v8 = P * Math.sin(robotAngle - angles.firstAngle + adjust) + P * Math.cos(robotAngle - angles.firstAngle + adjust) + rotation;

        frontLeft.setPower(v5);
        frontRight.setPower(v6);
        backLeft.setPower(v7);
        backRight.setPower(v8);
    }*/
}