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


@Autonomous(name = "Red Right Auto", group = "Autonomous")
public class RedRightScrim extends LinearOpMode
{
    public enum autoNum{
        left,
        right,
        center
    }
    autoNum auto = autoNum.left;

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

        while(!isStopRequested() && !isStarted())
        {
            if(RedFinder.width < 30)
                auto = autoNum.right;
            else
            {
                if(RedFinder.screenPosition.x > 70)
                    auto = autoNum.center;
                else
                    auto = autoNum.left;
            }

            telemetry.addData("Auto", auto);
            telemetry.addData("X Pos", TSEFinder.screenPosition.x);
            telemetry.addData("Y Pos", TSEFinder.screenPosition.y);
            telemetry.update();
        }

        waitForStart();

        //this loop runs after play pressed
        while(opModeIsActive())
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

            //
            if(auto == autoNum.right)
            {
                while((robot.theta > 4.85 || robot.theta < 1) && opModeIsActive())
                {
                    robot.updatePositionRoadRunner();
                    robot.robotODrive(0,0,.5);

                    telemetry.addData("x", robot.x);
                    telemetry.addData("y", robot.y);
                    telemetry.addData("theta", robot.theta);
                    telemetry.update();
                }


            }
        }
    }
}