package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.Mechanisms.Odom;
import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
//import org.firstinspires.ftc.teamcode.roadrunnerquickstart.drive.util.Encoder;

@TeleOp(name = "Odom Test",group="teleop")
public class OdomTest extends LinearOpMode
{
    @Override
    public void runOpMode() throws InterruptedException
    {
        Odom.currentOpMode=this;
        Odometry robot = new Odometry(hardwareMap);
        Drivetrain drivetrain = new Drivetrain(hardwareMap);

        waitForStart();
        robot.resetOdometry(0,0,0);

        while(opModeIsActive())
        {
            robot.updatePositionRoadRunner();
            drivetrain.robotODrive(0, 0, gamepad1.right_stick_x);
            telemetry.addData("left", robot.leftEncoderPos);
            telemetry.addData("right", robot.rightEncoderPos);
            telemetry.addData("center", robot.centerEncoderPos);
            telemetry.addLine();
            telemetry.addData("x", robot.x);
            telemetry.addData("y", robot.y);
            telemetry.addData("theta", robot.theta);
            telemetry.addLine();
            telemetry.update();
        }
    }
}
