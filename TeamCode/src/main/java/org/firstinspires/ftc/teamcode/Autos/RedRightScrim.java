package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Red Right Auto", group = "Autonomous")
public class RedRightScrim extends LinearOpMode
{
    public enum autoPos
    {
        left,
        right,
        center
    }
    autoPos auto = autoPos.left;

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
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });
        webcam.setPipeline(new RedFinder());

        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();

        while(!isStopRequested() && !isStarted())
        {
            if((RedFinder.height < 30 || RedFinder.width < 30) || RedFinder.screenPosition.x < 20)
                auto = autoPos.left;
            else
            {
                if(RedFinder.screenPosition.x < 100)
                    auto = autoPos.center;
                else
                    auto = autoPos.right;
            }

            telemetry.addData("Auto", auto);
            telemetry.addData("X Pos", TSEFinder.screenPosition.x);
            telemetry.addData("Y Pos", TSEFinder.screenPosition.y);
            telemetry.update();
            dashboardTelemetry.addData("Auto", auto);
            dashboardTelemetry.update();
        }

        robot.resetOdometry(0,0,0);
        waitForStart();

        //this loop runs after play pressed
        while(opModeIsActive())
        {
            //strafe left
            while (robot.y < 2 && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0, -.25, 0);

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
                while ((robot.y < -6.5) && opModeIsActive())
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

                //deposit spike mark
                while(depositTimer.seconds() < 4 && opModeIsActive())
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                //back away
                while ((robot.y > -10) && opModeIsActive())
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

                //strafe to spike mark
                while ((robot.x > 12) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,-.5,0);

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
                    robot.robotODrive(0,-.25,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.addData("bang Time",bangBangTimer);
                    telemetry.update();
                }
            }

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

                robot.transferCR1.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //rotate so deposit faces backdrop
                while((robot.theta < 1.5 || robot.theta > 5) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,0,-.5);

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
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            }

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
                while ((robot.y > 11) && opModeIsActive())
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

                //back up
                while ((robot.y < 13) && opModeIsActive())
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

                //strafe towards wall
                while ((robot.x > 20) && opModeIsActive()){
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }

                //turn left
                while((robot.theta < 1.5 || robot.theta > 4) && opModeIsActive())
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
                    robot.robotODrive(0,-.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.addData("bang Time",bangBangTimer);
                    telemetry.update();
                }
            }

            //update robots position based on being pressed against wall
            robot.odom.setPoseEstimate(new Pose2d(0, robot.y, Math.PI / 2));
            robot.updatePositionRoadRunner();

            //strafe away from wall
            while ((robot.x < 5) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,.5,0);

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
                if(robot.lineSensor.red() > 80)
                {
                    robot.robotODrive(0,0,0);
                    robot.updatePositionRoadRunner();
                    robot.odom.setPoseEstimate(new Pose2d(robot.x, -35, robot.theta));
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
                while(true && opModeIsActive())
                {
                    telemetry.addLine("Couldn't fine line, parking early");
                    telemetry.update();
                }
            }

            //line up with backdrop
            //uniform code
            /*while ((robot.x < 25) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,.5,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }*/

            //line up with backdrop according to randomization
            if(auto == autoPos.left)
            {
                while ((robot.x < 19) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            }

            if(auto == autoPos.center)
            {
                while ((robot.x < 22) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

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
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
            }

            //raise slides here

            //get to backdrop
            while ((robot.y > -44) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(.25 ,0,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            //drop pixel here

            //away from backdrop
            while ((robot.y < -30) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(-.5 ,0,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            //towards wall
            while ((robot.x > 10) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,-.5,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            //into park
            while ((robot.y > -53) && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(.5 ,0,0);

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
        }
    }
}