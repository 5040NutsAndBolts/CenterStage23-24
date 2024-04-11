package org.firstinspires.ftc.teamcode.HelperClasses;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Autos.AutoMethods;
import org.firstinspires.ftc.teamcode.Mechanisms.ArduCam;
import org.firstinspires.ftc.teamcode.Mechanisms.Drivetrain;

//this entire class is literally just to log data
@TeleOp(name = "ILoveData", group = "Teleop")
public class ILoveData extends AutoMethods {
    private boolean neutralToggle = false, slowmode=false;
    private int lP, lC;
    public void addTelemetry(String s) {
        telemetry.addLine(s);
        dash.addLine(s);
    }
    public void logRobotPosition() {//for getting Auto positions so we aren't guessing
        addTelemetry("Position log #" + lP + " at " + odo.x + ", " + odo.y + " " + odo.theta);
    }
    public void logCameraData() {//makes tweaking camera stuff easier
        lC++;
        addTelemetry("Camera log #" + lC + " looking for" + alC +" at " + cam.getScreenPosition().x
                + ", " + cam.getScreenPosition().y + " with " + cam.getScore() + "(score), "
                + cam.getHeight() + "(width), and" + cam.getHeight() + "(height)");
        //By the way, notice the Oxford Comma. If you don't use it, get out of my code.
    }
    @Override
    public void runOpMode() throws InterruptedException {

        lift.setZeroPowerBehaviour(DcMotor.ZeroPowerBehavior.FLOAT);
        while(opModeIsActive()){
            if (gamepad1.dpad_up && !neutralToggle) //this is just so we can push around the dt real easy
                dt.neutral();
            else if (gamepad1.dpad_up && neutralToggle) {//when you're not neutral js retap dpadup
                if(!slowmode)
                    dt.robotODrive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                else
                    dt.robotODrive(gamepad1.left_stick_y * .5, gamepad1.left_stick_x * .5, gamepad1.right_stick_x * .5);

                if (gamepad1.right_bumper)
                    deposit.rightDrop();
                else
                    deposit.rightZeroPosition();
                if (gamepad1.left_bumper)
                    deposit.leftDrop();
                else
                    deposit.leftZeroPosition();

                if (gamepad1.dpad_right)
                    logCameraData();
                if (gamepad1.dpad_left)
                    logRobotPosition();

                //telemetry!! (the point of this class)s
                displayCameraTelemetry();
                updateAutoTelemetry();
                addTelemetry("Slide positions (avg int): " + lift.getSlidePosition());
                addTelemetry("Linesensor sees line: " + lineSeen());
                addTelemetry("Neutral: " + neutralToggle);
                addTelemetry("Slowmode: " + dt.getSlowMode());
                telemetry.update();
                dash.update();
            }
        }
    }
}
