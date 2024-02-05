package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "Red Left Auto", group = "Autonomous")
public class RedLeftScrim extends LinearOpMode
{
    public enum autoPos
    {
        left,
        right,
        center
    }
    autoPos auto = autoPos.right;

    //park toggle variables
    public boolean park = false;
    public boolean aPressed;

    @Override
    public void runOpMode() throws InterruptedException
    {
        //initializes robot
        Odometry robot = new Odometry(hardwareMap);

        //camera setup
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);

        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });
        webcam.setPipeline(new RedFinder());

        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.startCameraStream(webcam, 0);
        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();

        while(!isStopRequested() && !isStarted())
        {
            if(RedFinder.screenPosition.x > 800)
                auto = autoPos.right;
            else if (RedFinder.screenPosition.x < 350)
                auto = autoPos.left;
            else
                auto = autoPos.center;

            /*park toggle
            if(gamepad1.a && !aPressed)
            {
                park = !park;
                aPressed = true;
            }
            else if(!gamepad1.a)
                aPressed = false;*/

            telemetry.addData("Auto", auto);
            telemetry.addData("Park?", park);
            telemetry.addData("X Pos", RedFinder.screenPosition.x);
            telemetry.addData("Y Pos", RedFinder.screenPosition.y);
            telemetry.update();
            dashboardTelemetry.addData("Auto", auto);
            dashboardTelemetry.addData("Park?", park);
            dashboardTelemetry.update();
        }

        waitForStart();

        //this loop runs after play pressed
        while(opModeIsActive())
        {
            //strafe right
            while (robot.y > -1 && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0, .5, 0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            if(auto == autoPos.left)
            {
                //turn left
                while ((robot.theta < 1.5 || robot.theta > 5) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0, 0, -.5);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
//
                //strafe to spike mark
                while ((robot.x < 38) && opModeIsActive()){
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //slight forward
                while ((robot.y < -11) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.5 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime depositTimer = new ElapsedTime();
                depositTimer.startTime();

                //deposit spike mark
                while(depositTimer.seconds() < 5 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(0.5);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away
                while ((robot.y > -14) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.25 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                if(park)
                {
                    //strafe out
                    while ((robot.x < 65) && opModeIsActive()){
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0,.5,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //back away
                    while ((robot.y > -105) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        if(robot.x < 63)
                            robot.robotODrive(.5, .25, 0);
                        else
                            robot.robotODrive(.5 ,0,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }
                }

                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);
            }

            if(auto == autoPos.center)
            {
                //drive to spike mark
                while ((robot.x < 32.5) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.4,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime depositTimer = new ElapsedTime();
                depositTimer.startTime();

                //deposit purple pixel
                while (depositTimer.seconds()<4 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(.5);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away from spike mark
                while ((robot.x > 18) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.25,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                if(park)
                {
                    //strafe left
                    while (robot.y < 12 && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0, -.5, 0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //forward
                    while ((robot.x < 50) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(-.5,0,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //strafe right to park
                    while (robot.y > -90 && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0, .5, 0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }
                }

                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

            } //end of center spike mark

            if(auto == autoPos.right)
            {
                //turn right
                while((robot.theta > 4.85 || robot.theta < 1) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,0,.5);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //strafe to spike mark
                while ((robot.x < 36) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //slight forward
                while ((robot.y > 5.7) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.2 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime depositTimer = new ElapsedTime();
                depositTimer.startTime();

                //deposit purple pixel
                while (depositTimer.seconds()<4 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(0.5);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back up
                while ((robot.y < 12.5) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.1 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                if(park)
                {
                    //strafe left
                    while ((robot.x < 55) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0,-.5,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //into park
                    while ((robot.y > -60) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        if(robot.x > 58)
                            robot.robotODrive(-.5 ,.25,0);
                        else
                            robot.robotODrive(-.5 ,0,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //strafe left
                    while ((robot.x < 80) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0,.5,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //into park
                    while ((robot.y > -85) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        if(robot.x > 58)
                            robot.robotODrive(-.5 ,.25,0);
                        else
                            robot.robotODrive(-.5 ,0,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }
                }

                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);
            } //end of right spike mark

            robot.robotODrive(0,0,0);
            while(true && opModeIsActive())
            {
                telemetry.addLine("program done");
                telemetry.update();
            }
        }
    }
}