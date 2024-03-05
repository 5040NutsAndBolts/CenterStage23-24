package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
import org.firstinspires.ftc.teamcode.Hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Blue Left Auto", group = "Autonomous")
public class BlueLeftScrim extends LinearOpMode
{
    public enum autoPos
    {
        left,
        right,
        center
    }
    autoPos auto = autoPos.right;

    public boolean lineSeen;

    @Override
    public void runOpMode() throws InterruptedException
    {
        //initializes robot
        Odometry robot = new Odometry(hardwareMap);


        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);

        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                //set this to dimensions of camera
                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });
        webcam.setPipeline(new BlueFinder());

        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();

        while(!isStopRequested() && !isStarted())
        {
            telemetry.addData("BlueFinder X", BlueFinder.screenPosition.x);
            telemetry.addData("BlueFinder Y", BlueFinder.screenPosition.y);
            dashboardTelemetry.update();

            if(BlueFinder.screenPosition.x > 750)
                auto = autoPos.right;
            else if (BlueFinder.screenPosition.x < 250)
                auto = autoPos.left;
            else
                auto = autoPos.center;

            telemetry.addData("Auto", auto);
            telemetry.addData("X Pos", TSEFinder.screenPosition.x);
            telemetry.addData("Y Pos", TSEFinder.screenPosition.y);
            telemetry.update();
            dashboardTelemetry.addData("auto", auto);
            dashboardTelemetry.update();
        }

        robot.resetOdometry(0,0,0);
        robot.transferM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.transferM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        //this loop runs after play pressed
        while(opModeIsActive()) {
            //left spike mark//
            if(auto == autoPos.left)
            {
                //strafe to spike mark
                while ((robot.x < 28) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.5,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

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
                while ((robot.x < 36.5) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //slight forward
                while ((robot.y < 24) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.25 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime depositTimer = new ElapsedTime();
                depositTimer.startTime();

                //deposit pixel
                while (depositTimer.seconds()<5 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(.75);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                ElapsedTime dontBreakTheBackBoard = new ElapsedTime();
                dontBreakTheBackBoard.startTime();
                //back up
                while ((robot.y > 0) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.18 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                    if(dontBreakTheBackBoard.seconds() > 2.75)
                        break;
                }

                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //strafe to wall mark
                while ((robot.x > 20) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //turn left
                while((robot.theta < 4.6) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,0,-.5);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime bangBangTimer = new ElapsedTime();
                bangBangTimer.startTime();

                //bang into wall
                while (bangBangTimer.seconds() < 0.75 && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.addData("bang Time",bangBangTimer);
                    telemetry.update();
                }

            } //end of auto 1 branch

            //center spike mark
            if(auto == autoPos.center)
            {
                //drive to spike mark
                while ((robot.x < 29.7) && opModeIsActive())
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
                while (depositTimer.seconds() < 4 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(.75);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away from spike mark
                while ((robot.x > 8) && opModeIsActive())
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

                //forward
                while ((robot.x < 51) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            } //end of auto 2 branch

            //right spike mark
            if(auto == autoPos.right)
            {
                //strafe to spike mark
                while ((robot.x < 21) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(-.5,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

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

                //slight forward
                while ((robot.y > 3) && opModeIsActive())
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
                while (depositTimer.seconds()< 5 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.transferCR2.setPower(-1);
                    robot.intakeMotor.setPower(.75);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away
                while ((robot.y < 18) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.18 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                robot.transferCR1.setPower(0);
                robot.transferCR2.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //towards wall
                while ((robot.x > 15) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                ElapsedTime bangBangTimer = new ElapsedTime();
                bangBangTimer.startTime();

                //bang into wall
                while (bangBangTimer.seconds() < 0.75 && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.addData("bang Time",bangBangTimer);
                    telemetry.update();
                }

                ElapsedTime dontBreakTheBackboard = new ElapsedTime();
                dontBreakTheBackboard.startTime();
                //strafe to spike mark
                while ((robot.x < 40) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.5,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                    if(dontBreakTheBackboard.seconds() > 2.5)
                        break;
                }

            } // end of auto 3 branch

            //resets where the robot thinks it is based on being pressed against the wall
            robot.odom.setPoseEstimate(new Pose2d(0, robot.y, 3 * Math.PI / 2));
            robot.updatePositionRoadRunner();

            //strafe away from wall
            while ((robot.x < 3) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,-.5,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            ElapsedTime timeOut = new ElapsedTime();
            timeOut.startTime();

            //approach backdrop until robot sees line or hits timeout
            while (timeOut.seconds() < 4 && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(.20 ,0,0);

                //stop moving and reset position when robot sees line
                if(robot.lineSensor.blue() > 300)
                {
                    robot.robotODrive(0,0,0);
                    robot.updatePositionRoadRunner();
                    robot.odom.setPoseEstimate(new Pose2d(robot.x, 35, robot.theta));
                    robot.updatePositionRoadRunner();
                    lineSeen = true;
                    break;
                }

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.addData("time", timeOut.seconds());
                telemetry.update();
            }

            //stops the robot in the park zone if it couldn't reset on the line
            if(!lineSeen)
            {
                robot.robotODrive(0,0,0);
                telemetry.addLine("Couldn't fine line, parking early");
                telemetry.update();
                requestOpModeStop();
            }

            //line up with backdrop according to randomization
            if(auto == autoPos.left)
            {
                while ((robot.x < 25) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            }

            if(auto == autoPos.center)
            {
                while ((robot.x < 25) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            }

            if(auto == autoPos.right)
            {
                while ((robot.x < 25) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            }

            //robot.transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            ////raise slides
            //while(robot.transferM1.getCurrentPosition() < 800 && opModeIsActive())
            //{
            //    robot.robotODrive(0,0,0);
            //    robot.transferM1.setPower(1);
            //    robot.transferM2.setPower(1);
            //}
//
            //robot.transferM1.setPower(.1);
            //robot.transferM2.setPower(.1);
//
            ////get to backdrop (this is what happens whe no side rollers)
            //ElapsedTime timeCease = new ElapsedTime();
            //timeCease.startTime();
//
            ////forward a little to backdrop
            //while ((robot.y < 50) && opModeIsActive())
            //{
            //    robot.updatePositionRoadRunner();
            //    robot.robotODrive(.25 ,0,0);
//
            //    telemetry.addData("x", robot.x);
            //    telemetry.addData("y", robot.y);
            //    telemetry.addData("theta", robot.theta);
            //    telemetry.update();
//
            //    if(timeCease.seconds() > 3)
            //        break;
            //}
//
            //ElapsedTime pause = new ElapsedTime();
            //pause.startTime();
            //while(pause.seconds() < 1)
            //{
            //    robot.robotODrive(0,0,0);
            //}
//
            ////deposit pixel
            //robot.depositServoOne.setPosition(.5);
            //robot.depositServoTwo.setPosition(.5);
//
            ////wait at backdrop
            //pause.reset();
            //pause.startTime();
            //while(pause.seconds() < 1)
            //{
            //    robot.robotODrive(0,0,0);
            //}
//
            ////forward away from backdrop
            //while ((robot.y > 34) && opModeIsActive())
            //{
            //    robot.updatePositionRoadRunner();
            //    robot.robotODrive(-.5 ,0,0);
//
            //    telemetry.addData("x", robot.x);
            //    telemetry.addData("y", robot.y);
            //    telemetry.addData("theta", robot.theta);
            //    telemetry.update();
            //}
//
            ////closes deposit
            //robot.depositServoOne.setPosition(0);
            //robot.depositServoTwo.setPosition(0);
//
            //robot.transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            //robot.transferM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            ////lowers slides
            //while(robot.transferM1.getCurrentPosition() > 50 && opModeIsActive())
            //{
            //    robot.robotODrive(0,0,0);
            //    robot.transferM1.setPower(-.2);
            //    robot.transferM2.setPower(-.2);
            //}

            robot.transferM1.setPower(0);
            robot.transferM2.setPower(0);

            //strafe to wall
            while ((robot.x > 8) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,.5,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            //back into parking zone
            while ((robot.y < 52) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(.25 ,0,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            robot.robotODrive(0,0,0);

            while(true && opModeIsActive())
            {
                telemetry.addLine("program done");
                telemetry.update();
            }

        } //end of while(opModeIsActive)
    }
}