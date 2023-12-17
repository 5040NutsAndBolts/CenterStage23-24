package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.FtcDashboard;
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
    int auto = 1;
    boolean deposited = false;
    boolean banged = false;
    boolean stopReseting = false;
    boolean uwu = true;

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
        webcam.setPipeline(new BlueFinder());

        Telemetry dashboardTelemetry = FtcDashboard.getInstance().getTelemetry();

        while(!isStopRequested() && !isStarted())
        {
            if(BlueFinder.width < 30)
                auto = 3;
            else
            {
                if(BlueFinder.screenPosition.x > 70)
                    auto = 2;
                else
                    auto = 1;
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
        while(opModeIsActive() && !stopReseting)
        {
            //strafe right
            while(robot.y > -1.0980085 && opModeIsActive())
            {
                robot.updatePositionRoadRunner();
                robot.robotODrive(0,.5,0);

                telemetry.addData("x", robot.x);
                telemetry.addData("y", robot.y);
                telemetry.addData("theta", robot.theta);
                telemetry.update();
            }

            //left spike mark
            if(auto == 1) {
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
                while ((robot.y < -13) && opModeIsActive())
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
                while (depositTimer.seconds()<3 && opModeIsActive() && deposited==false)
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }

                deposited = true;
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
                while (bangBangTimer.seconds() < 0.5 && opModeIsActive() && banged==false){
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.addData("bang Time",bangBangTimer);
                    telemetry.update();
                }
                uwu= false;
                banged = true;
                robot.robotODrive(0,0,0);

                while (uwu==false && opModeIsActive()) {
                    telemetry.addData("in loop?", uwu);
                    telemetry.update();
                }

            } //end of auto 1 branch

            //center spike mark
            if(auto == 2)
            {
                //drive to spike mark
                while ((robot.x < 26.5) && opModeIsActive()){
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
                while (depositTimer.seconds()<4 && opModeIsActive() && !deposited)
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }
                deposited = true;
                robot.transferCR1.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

                //back away from spike mark
                while ((robot.x > 10) && opModeIsActive()){
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
                while (bangBangTimer.seconds() < 0.5 && opModeIsActive() && !banged)
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }
                uwu= false;
                banged = true;
                robot.robotODrive(0,0,0);

                while (uwu==false && opModeIsActive()) {
                    telemetry.addData("in loop?", uwu);
                    telemetry.update();
                }
            } //end of auto 2 branch

            //right spike mark
            if(auto == 3)
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
                while ((robot.y > 6) && opModeIsActive())
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
                while (depositTimer.seconds()<4 && opModeIsActive() && !deposited)
                {
                    robot.robotODrive(0, 0, 0);
                    robot.transferCR1.setPower(-1);
                    robot.intakeMotor.setPower(1);
                    robot.intakeServo.setPower(-1);

                    telemetry.addData("time", depositTimer.seconds());
                    telemetry.update();
                }
                deposited = true;
                robot.transferCR1.setPower(0);
                robot.intakeMotor.setPower(0);
                robot.intakeServo.setPower(0);

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
                while (bangBangTimer.seconds() < 0.5 && opModeIsActive() && banged==false){
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,.5,0);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.addData("bang Time",bangBangTimer);
                    telemetry.update();
                }
                uwu= false;
                banged = true;
                robot.robotODrive(0,0,0);

                while (uwu==false && opModeIsActive()) {
                    telemetry.addData("in loop?", uwu);
                    telemetry.update();
                }
            } // end of auto 3 branch

            robot.robotODrive(0,0,0);

        } //end of while(opModeIsActive)

        stopReseting = true;
    }
}