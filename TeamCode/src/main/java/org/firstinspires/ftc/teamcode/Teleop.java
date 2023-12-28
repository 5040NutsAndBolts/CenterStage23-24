package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "A Teleop", group = "Teleop")
public class Teleop extends LinearOpMode
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        Hardware robot = new Hardware(hardwareMap);
        waitForStart();
        while(opModeIsActive())
        {
            //Drivetrain code
            robot.robotODrive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

            //Transfer Code --
            //slides go up proportionally to stick value
            if (gamepad2.left_stick_y < -.05) {
                robot.transferM1.setPower(-gamepad2.left_stick_y);
                robot.transferM2.setPower(gamepad2.left_stick_y);
            }
            //Slides go down at full power
            else if(gamepad2.left_stick_y > .05) {
                robot.transferM1.setPower(-.85);
                robot.transferM2.setPower(.85);
            }
            //zeropowerbehaviour redundancy
            else {
                robot.transferM1.setPower(0);
                robot.transferM2.setPower(0);
            }
            // -- End Transfer code




            //Drone Launcher Code --
            //Spins the servo for 1 second
            if (gamepad2.b) {
                //Creates an elapsed timer
                ElapsedTime timer = new ElapsedTime();
                timer.startTime();
                //While 1 second mark has not passed
                while(timer.seconds() < 1)
                    robot.droneLaunch.setPower(1);
                robot.droneLaunch.setPower(0); //Redundancy for zeroPowerBehaviour
            }
            //-- End Drone Launcher Code

            //Intake code --
            //Spin Inwards
            if(gamepad1.right_trigger > .05) {
                robot.intakeMotor.setPower(-gamepad1.right_trigger);
                robot.intakeServo.setPower(gamepad1.right_trigger);
                robot.transferCR1.setPower(1);
                robot.transferCR2.setPower(1);
            }
            //Spin Outwards
            else if(gamepad1.left_trigger > .05) {
                robot.intakeMotor.setPower(gamepad1.left_trigger);
                robot.intakeServo.setPower(-gamepad1.left_trigger);
                robot.transferCR1.setPower(-1);
                robot.transferCR2.setPower(-1);
            }
            //If neither are pressed or both are pressed everything is set to it's zeroPowerBehavior()
            else if((gamepad1.left_trigger > .05  && gamepad1.right_trigger > .05) || (gamepad1.left_trigger < .05 && gamepad1.right_trigger < .05)) {
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);
                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
            }
            // -- End Intake Code


            //Deposit code --
            if (gamepad2.right_trigger > 0.05)
                robot.depositServoOne.setPosition(.5);
             else
                robot.depositServoOne.setPosition(0);
            if (gamepad2.left_trigger > 0.05)
                robot.depositServoTwo.setPosition(.5);
             else
                robot.depositServoTwo.setPosition(0);
            // -- End Deposit Code

            //Telemetry
            telemetry.addData("slide height", robot.transferM1.getCurrentPosition());
            telemetry.update();
        }
    }
}