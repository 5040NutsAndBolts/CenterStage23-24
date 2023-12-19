package org.firstinspires.ftc.teamcode.Autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

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

    @Override
    public void runOpMode() throws InterruptedException
    {
        //initializes robot
        Hardware robot = new Hardware(hardwareMap);

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

        waitForStart();

        //this loop runs after play pressed
        while(opModeIsActive())
        {
            if(auto == autoPos.left)
            {

            }

            if(auto == autoPos.center)
            {

            }

            if(auto == autoPos.right)
            {

            }
        }
    }
}