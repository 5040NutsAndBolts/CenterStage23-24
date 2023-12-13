package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import com.acmerobotics.dashboard.FtcDashboard;

@TeleOp(name = "Vision Test", group = "Teleop")
public class VisionTest extends LinearOpMode
{
    int auto = 1;

    @Override
    public void runOpMode() throws InterruptedException
    {
        //camera settup
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

        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.startCameraStream(webcam, 0);
        Telemetry dashboardTelemetry = dashboard.getTelemetry();

        webcam.setPipeline(new TSEFinder());

        waitForStart();
        while(opModeIsActive())
        {
            if(TSEFinder.width < 30)
                auto = 3;
            else
            {
                if(TSEFinder.screenPosition.x > 70)
                    auto = 2;
                else
                    auto = 1;
            }

            telemetry.addData("auto num", auto);
            telemetry.addData("X Position", TSEFinder.screenPosition.x);
            telemetry.addData("Y Position", TSEFinder.screenPosition.y);
            telemetry.addLine();
            telemetry.addData("Area", TSEFinder.score);
            telemetry.addData("Width", TSEFinder.width);
            telemetry.addData("Height", TSEFinder.height);
            telemetry.update();

            dashboardTelemetry.addData("auto num", auto);
            dashboardTelemetry.addData("X Position", TSEFinder.screenPosition.x);
            dashboardTelemetry.addData("Y Position", TSEFinder.screenPosition.y);
            dashboardTelemetry.addLine();
            dashboardTelemetry.addData("Area", TSEFinder.score);
            dashboardTelemetry.addData("Width", TSEFinder.width);
            dashboardTelemetry.addData("Height", TSEFinder.height);
            dashboardTelemetry.update();
        }
    }
}
