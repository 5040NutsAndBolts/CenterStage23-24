package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.*;

@TeleOp(name = "B Teleop One-Driver", group = "Teleop")
public class Teleop extends LinearOpMode {
    boolean robotDrive = true;
    double driveSpeed = 1;
    @Override
    public void runOpMode() throws InterruptedException
    {
        Drivetrain drivetrain = new Drivetrain(hardwareMap);
        LineSensor lineSensor = new LineSensor(hardwareMap);
        Deposit deposit = new Deposit(hardwareMap);
        Dronelauncher dronelauncher = new Dronelauncher(hardwareMap);
        Lift lift = new Lift(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        waitForStart();

        while(opModeIsActive())
        {
            //sets slowmode
            if (gamepad1.dpad_up)
                drivetrain.toggleSlowMode();

            //drive mode toggles
            if (gamepad1.dpad_right)
                robotDrive = true;
            if (gamepad1.dpad_left)
                robotDrive = false;


            //Drivetrain
            if(robotDrive)
                drivetrain.robotODrive(gamepad1.left_stick_y * driveSpeed, gamepad1.left_stick_x * driveSpeed,
                        gamepad1.right_stick_x * driveSpeed);
            else
                drivetrain.fieldODrive(gamepad1.left_stick_y * driveSpeed, gamepad1.left_stick_x * driveSpeed,
                        -gamepad1.right_stick_x * driveSpeed, gamepad1.dpad_down);


            //Transfer
            if (gamepad1.right_stick_y < -.05)
                lift.goUp(gamepad1.right_stick_y);
            else if (gamepad1.right_stick_y > .05)
                lift.goDown(gamepad1.right_stick_y);
            else
                lift.setPowerZero();


            //Drone Launcher
            if (!gamepad1.b)
                dronelauncher.launch();


            //Intake
            //Spin Inwards
            if(gamepad1.right_trigger > .05)
                intake.spinInwards(gamepad1.right_trigger);
            //Spin Outwards
            else if(gamepad1.left_trigger > .05)
                intake.spinOutwards(gamepad1.right_trigger);
            //If neither are pressed or both are pressed everything is set to it's zeroPowerBehavior()
            else if((gamepad1.left_trigger > .05  && gamepad1.right_trigger > .05) || (gamepad1.left_trigger < .05 && gamepad1.right_trigger < .05))
                intake.setPowerZero();


            //Deposit
            if (gamepad1.right_bumper)
                deposit.rightDrop();
             else
                deposit.rightZeroPosition();
            if (gamepad1.left_bumper)
                deposit.leftDrop();
             else
                deposit.leftZeroPosition();


            //Telemetry
            telemetry.addData("slide height", lift.getSlidePosition());
            telemetry.addData("slowmode?", drivetrain.getSlowMode());
            telemetry.addData("robot drive?", robotDrive);
            telemetry.addLine();
            if(drivetrain.getSlowMode())
                telemetry.addLine("YOU ARE IN SLOWMODE");
            if(!robotDrive)
                telemetry.addLine("YOU ARE USING FIELD ORIENTED DRIVE");
            telemetry.update();
        }
    }
}