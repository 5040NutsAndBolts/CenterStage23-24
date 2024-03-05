package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "A Teleop Two-Driver", group = "Teleop")
public class TwoDriverTeleop extends LinearOpMode
{
    boolean slowMode;
    boolean robotDrive = true;
    double driveSpeed = 1;

    boolean dUP1Pressed;

    @Override
    public void runOpMode() throws InterruptedException
    {
        Hardware robot = new Hardware(hardwareMap);
        waitForStart();
        while(opModeIsActive())
        {
            //sets slowmode
            if (gamepad1.dpad_up && !dUP1Pressed)
            {
                slowMode = !slowMode;
                dUP1Pressed = true;
            }
            else if (!gamepad1.dpad_up)
                dUP1Pressed = false;

            //sets drivespeed
            if (slowMode)
                driveSpeed = .5;
            else
                driveSpeed = 1;

            //drive mode toggles
            if (gamepad1.dpad_right)
                robotDrive = true;
            if (gamepad1.dpad_left)
                robotDrive = false;

            //Drivetrain code
            //if(robotDrive)
                robot.robotODrive(gamepad1.left_stick_y * driveSpeed, gamepad1.left_stick_x * driveSpeed,
                        gamepad1.right_stick_x * driveSpeed);
            //if(!robotDrive)
            //    robot.fieldODrive(gamepad1.left_stick_y * driveSpeed, gamepad1.left_stick_x * driveSpeed,
            //            gamepad1.right_stick_x * driveSpeed, gamepad1.dpad_down);

            //SLides Code --
            //slides go up proportionally to stick value
            if (gamepad2.left_stick_y < -.05)
            {
                robot.transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                robot.transferM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                robot.transferM1.setPower(-gamepad2.left_stick_y);
                robot.transferM2.setPower(-gamepad2.left_stick_y);
            }
            //Slides go down at reduced speed
            else if(gamepad2.left_stick_y > .05)
            {
                if(robot.transferM1.getCurrentPosition() < 50)
                {
                    robot.transferM1.setPower(0);
                    robot.transferM2.setPower(0);
                    robot.transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    robot.transferM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }
                else
                {
                    robot.transferM1.setPower(-gamepad2.left_stick_y * .15);
                    robot.transferM2.setPower(-gamepad2.left_stick_y * .15);
                }
            }
            else
            {
                robot.transferM1.setPower(0);
                robot.transferM2.setPower(0);
            }
            // -- End Transfer code

            //Drone Launcher Code --
            //Spins the servo for 1 second
            if (gamepad1.b)
             robot.droneLaunch.setPosition(0);
            else
                robot.droneLaunch.setPosition(.5);
            //-- End Drone Launcher Code

            //Intake code --
            //Spin Inwards
            if(gamepad1.right_trigger > .05)
            {
                robot.intakeMotor.setPower(-gamepad1.right_trigger);
                robot.intakeServo.setPower(gamepad1.right_trigger);
                robot.transferCR1.setPower(1);
                robot.transferCR2.setPower(1);
            }
            //Spin Outwards
            else if(gamepad1.left_trigger > .05)
            {
                robot.intakeMotor.setPower(gamepad1.left_trigger);
                robot.intakeServo.setPower(-gamepad1.left_trigger);
                robot.transferCR1.setPower(-1);
                robot.transferCR2.setPower(-1);
            }
            //If neither are pressed or both are pressed everything is set to it's zeroPowerBehavior()
            else if((gamepad1.left_trigger > .05  && gamepad1.right_trigger > .05) || (gamepad1.left_trigger < .05 && gamepad1.right_trigger < .05))
            {
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);
                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
            }
            // -- End Intake Code


            //Deposit code --
            if (gamepad2.right_bumper)
                robot.depositServoOne.setPosition(.5);
            else
                robot.depositServoOne.setPosition(0);
            if (gamepad2.left_bumper)
                robot.depositServoTwo.setPosition(.5);
            else
                robot.depositServoTwo.setPosition(0);
            // -- End Deposit Code


            //pull up code
            if(gamepad1.left_bumper)
                robot.hangMotor.setPower(1);
            else
                robot.hangMotor.setPower(0);

            //Telemetry
            telemetry.addData("slide height", robot.transferM1.getCurrentPosition());
            telemetry.addData("slowmode?", slowMode);
            telemetry.addData("robot drive?", robotDrive);
            telemetry.addLine();
            if(slowMode)
                telemetry.addLine("YOU ARE IN SLOWMODE");
            if(!robotDrive)
                telemetry.addLine("YOU ARE USING FIELD ORIENTED DRIVE");
            telemetry.update();
        }
    }
}