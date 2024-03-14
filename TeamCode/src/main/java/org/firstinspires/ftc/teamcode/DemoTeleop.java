package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Demo Mode", group = "Teleop")
public class DemoTeleop extends LinearOpMode
{
    boolean slowMode;
    double driveSpeed = 1;

    boolean bPressed;

    @Override
    public void runOpMode() throws InterruptedException
    {
        Hardware robot = new Hardware(hardwareMap);
        robot.lineSensor.enableLed(false);

        waitForStart();

        while(opModeIsActive())
        {
            //sets slowmode
            if (gamepad1.b && !bPressed)
            {
                slowMode = !slowMode;
                bPressed = true;
            }
            else if (!gamepad1.b)
                bPressed = false;

            //sets drivespeed
            if (slowMode)
                driveSpeed = .25;
            else
                driveSpeed = .75;

            //Drivetrain code
            robot.robotODrive(gamepad1.left_stick_y * driveSpeed, gamepad1.left_stick_x * driveSpeed,
                    gamepad1.right_stick_x * driveSpeed);

            //Drone Launcher Code --
            if (gamepad1.b)
                robot.droneLaunch.setPosition(0);
            else
                robot.droneLaunch.setPosition(.5);
            //-- End Drone Launcher Code

            //Telemetry
            telemetry.addData("slowmode?", slowMode);
            telemetry.update();
        }
    }
}