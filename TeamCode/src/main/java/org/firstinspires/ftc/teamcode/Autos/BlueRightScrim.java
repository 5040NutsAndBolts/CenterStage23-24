package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "Blue Right Auto", group = "Autonomous")
public class BlueRightScrim extends LinearOpMode
{
    public enum autoPos
    {
        left,
        right,
        center
    }
    autoPos auto = autoPos.left;

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
        webcam.setPipeline(new BlueFinder());

        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.startCameraStream(webcam, 0);
        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();

        while(!isStopRequested() && !isStarted())
        {
            if(BlueFinder.screenPosition.x < 200)
                auto = autoPos.center;
            else if(BlueFinder.screenPosition.y > 570)
                auto = autoPos.left;
            else
                auto = autoPos.right;


            park toggle
            if(gamepad1.a && !aPressed)
            {
                park = !park;
                aPressed = true;
            }
            else if(!gamepad1.a)
                aPressed = false;

            telemetry.addData("Auto", auto);
            telemetry.addData("Park?", park);
            telemetry.addData("X Pos", BlueFinder.screenPosition.x);
            telemetry.addData("Y Pos", BlueFinder.screenPosition.y);
            telemetry.update();
            dashboardTelemetry.addData("Auto", auto);
            dashboardTelemetry.addData("Park?", park);
            dashboardTelemetry.addData("X Pos", BlueFinder.screenPosition.x);
            dashboardTelemetry.addData("Y Pos", BlueFinder.screenPosition.y);
            dashboardTelemetry.update();
        }

        robot.resetOdometry(0,0,0);
        waitForStart();

        //this loop runs after play pressed
        while(opModeIsActive())
        {
            //strafe left
            while(robot.y < 1 && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,-.5,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            if(auto == autoPos.left)
            {
                //turn left
                while((robot.theta < 1.5 || robot.theta > 5) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,0,-.5);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //strafe to spike mark
                while ((robot.x < 41) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //slight forward
                while ((robot.y < -3) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.25 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime depositTimer = new ElapsedTime();
                depositTimer.startTime();

                //deposit pixel
                while (depositTimer.seconds()<3 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back up
                while ((robot.y > -18) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.25 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //park only if selected during init
                if(park)
                {
                    //strafe
                    while ((robot.x < 62) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0,.5,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //forawrd to park
                    while ((robot.y < 80) && opModeIsActive()) {

                        robot.updatePositionRoadRunner();
                        robot.robotODrive(-.5 ,0,0);
                        if(robot.x<64) {
                            robot.updatePositionRoadRunner();
                            robot.robotODrive(0,.5,0);
                        }

                        if(robot.x > 66) {
                            robot.updatePositionRoadRunner();
                            robot.robotODrive(0,-.5,0);
                        }

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }
                }
            } //end of left spike mark

            if(auto == autoPos.center)
            {
                //drive to spike mark
                while ((robot.x < 30) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.5,0,0);

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
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away from spike mark
                while ((robot.x > 18) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.2,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                if(park)
                {
                    //strafing
                    while ((robot.y > -15) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0,.5,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //forward
                    while ((robot.x < 50.45) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(-.5,0,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //strafe
                    while ((robot.y < 97) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();

                        if(robot.x < 48.3)
                            robot.robotODrive(-.25,-.5,0);
                        else
                            robot.robotODrive(0, -.5, 0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }
                }
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
                while ((robot.y > 10.5) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.25 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime depositTimer = new ElapsedTime();
                depositTimer.startTime();

                //deposit pixel
                while (depositTimer.seconds()<4 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away
                while ((robot.y < 14) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.1 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                if(park)
                {
                    //strafe out
                    while ((robot.x < 62) && opModeIsActive())
                    {
                        robot.updatePositionRoadRunner();
                        robot.robotODrive(0,-.5,0);

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                    //back to park zone
                    while ((robot.y < 100) && opModeIsActive())
                    {
                        if(robot.x < 62.1){
                            robot.updatePositionRoadRunner();
                            robot.robotODrive(.5 ,-.5,0);
                        }
                        else{
                            robot.updatePositionRoadRunner();
                            robot.robotODrive(.5, 0, 0);
                        }

                        telemetry.addData("x", robot.x);
                        telemetry.addData("y", robot.y);
                        telemetry.addData("theta", robot.theta);
                        telemetry.update();
                    }

                }
            } //end of right spike mark

            while(true && opModeIsActive())
            {
                robot.robotODrive(0,0,0);
                telemetry.addLine(String.valueOf(TSEFinder.screenPosition));
            }
        }
    }
}