package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

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

        //camera setup
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
            if(BlueFinder.width < 60 || BlueFinder.height < 30)
                auto = autoPos.right;
            else
            {
                if(BlueFinder.screenPosition.x > 100)
                    auto = autoPos.center;
                else
                    auto = autoPos.left;
            }

            telemetry.addData("Auto", auto);
            telemetry.addData("X Pos", TSEFinder.screenPosition.x);
            telemetry.addData("Y Pos", TSEFinder.screenPosition.y);
            telemetry.update();
            dashboardTelemetry.addData("auto", auto);
            dashboardTelemetry.update();
        }

        robot.resetOdometry(0,0,0);
        waitForStart();

        //this loop runs after play pressed
        while(opModeIsActive())
        {
            //strafe right
            while(robot.y > -1 && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,.5,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            //left spike mark
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
                while ((robot.x < 38) && opModeIsActive()){
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //slight forward
                while ((robot.y < -12) && opModeIsActive())
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
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back up
                while ((robot.y > -14) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.25 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                robot.transferCR1.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //strafe to wall mark
                while ((robot.x > 20) && opModeIsActive()){
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

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
                while ((robot.x < 26.5) && opModeIsActive())
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
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                robot.transferCR1.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //back away from spike mark
                while ((robot.x > 10) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.5,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //rotate so deposit faces backdrop
                while((robot.theta > 5.0 || robot.theta < 1) && opModeIsActive()) {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,0,.5);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //wall wham
                ElapsedTime bangBangTimer = new ElapsedTime();
                bangBangTimer.startTime();
                while (bangBangTimer.seconds() < 0.75 && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

            } //end of auto 2 branch

            //right spike mark
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
                while ((robot.y > 5.5) && opModeIsActive())
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
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away
                while ((robot.y < 15) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(.25 ,0,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                robot.transferCR1.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //towards wall
                while ((robot.x > 12) && opModeIsActive())
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

            } // end of auto 3 branch

            //resets where the robot thinks it is based on being pressed against the wall
            robot.odom.setPoseEstimate(new Pose2d(0, robot.y, 3 * Math.PI / 2));
            robot.updatePositionRoadRunner();

            //strafe away from wall
            while ((robot.x < 5) && opModeIsActive())
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
            while (timeOut.seconds() < 3 && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(.25 ,0,0);

                //stop moving and reset position when robot sees line
                if(robot.lineSensor.blue() > 175)
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
                telemetry.update();
            }

            //stops the robot in the park zone if it couldn't reset on the line
            if(!lineSeen)
            {
                robot.robotODrive(0,0,0);
                while(true && opModeIsActive())
                {
                    telemetry.addLine("Couldn't fine line, parking early");
                    telemetry.update();
                }
            }

            //line up with backdrop according to randomization
            if(auto == autoPos.left)
            {
                while ((robot.x < 20) && opModeIsActive())
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
                while ((robot.x < 30) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            }

            robot.transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            //raise slides
            while(robot.transferM1.getCurrentPosition() < 1200 && opModeIsActive())
            {
                robot.robotODrive(0,0,0);
                robot.transferM1.setPower(1);
            }

            robot.transferM1.setPower(.25);

            //get to backdrop (this is what happens whe no side rollers)
            while ((robot.y < 45) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(.25 ,0,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            ElapsedTime pause = new ElapsedTime();
            pause.startTime();
            while(pause.seconds() < 2)
            {
                robot.robotODrive(0,0,0);
            }

            //deposit pixel
            robot.depositServoOne.setPosition(.5);
            robot.depositServoTwo.setPosition(.5);

            //wait at backdrop
            pause.reset();
            pause.startTime();
            while(pause.seconds() < 2)
            {
                robot.robotODrive(0,0,0);
            }

            //forward away from backdrop
            while ((robot.y > 35) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(-.5 ,0,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            //closes deposit
            robot.depositServoOne.setPosition(0);
            robot.depositServoTwo.setPosition(0);

            robot.transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            //lowers slides
            while(robot.transferM1.getCurrentPosition() > 200 && opModeIsActive())
            {
                robot.robotODrive(0,0,0);
                robot.transferM1.setPower(-.05);
            }

            //strafe to wall
            while ((robot.x > 5) && opModeIsActive())
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