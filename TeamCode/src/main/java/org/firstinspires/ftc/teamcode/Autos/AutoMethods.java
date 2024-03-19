package org.firstinspires.ftc.teamcode.Autos;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
import org.firstinspires.ftc.teamcode.HelperClasses.TSEFinder;
import org.firstinspires.ftc.teamcode.Mechanisms.ArduCam;
import org.firstinspires.ftc.teamcode.Mechanisms.Deposit;
import org.firstinspires.ftc.teamcode.Mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.Mechanisms.Lift;
import org.firstinspires.ftc.teamcode.Mechanisms.LineSensor;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Disabled
public class AutoMethods extends LinearOpMode {
    protected Odometry odo = new Odometry(hardwareMap);

    protected AllianceColor alC;
    protected ArduCam cam = new ArduCam(alC);
    protected OpenCvWebcam openCV = initializeOpenCv();
    protected SpikeMarkPosition smp;

    protected LineSensor ls = new LineSensor(hardwareMap);
    protected Deposit deposit = new Deposit(hardwareMap);
    protected Lift lift = new Lift(hardwareMap);
    protected Drivetrain dt = new Drivetrain(hardwareMap);

    protected Telemetry dash = FtcDashboard.getInstance().getTelemetry();


    //CAMERA METHODS ------------------------------------------------------------------------------------------------
    public OpenCvWebcam initializeOpenCv() {
        //camera setup
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);

        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                //set this to dimensions of camera
                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);}
            @Override public void onError(int errorCode) {}});

        webcam.setPipeline(cam);
        return webcam;
    }

    public void displayCameraTelemetry() {
        telemetry.addData("Auto", smp);
        telemetry.addData("X Pos", TSEFinder.screenPosition.x);
        telemetry.addData("Y Pos", TSEFinder.screenPosition.y);
        telemetry.update();
        dash.addData("auto", smp);
        dash.update();
    }


    //COLOUR SENSOR METHODS -----------------------------------------------------------------------------------------
    public boolean lineSeen() {
        return ls.getBlueValue() > 175;
    }


    //MOVEMENT METHODS ----------------------------------------------------------------------------------------------
    private double calculateSpeedArc(double dist) {
        if (dist < 10 && dist > -10)
            return -1 * (.5 * (Math.cos(dist / Math.PI))) + .5;
        else
            return 1;
    }
    public void moveTo(double x, double y, double theta) {
        while(odo.x != x && odo.y != y)
            dt.robotODrive(calculateSpeedArc(y - odo.y),calculateSpeedArc(x - odo.x),0);
    }


    //LIFT METHODS --------------------------------------------------------------------------------------------------
    public void raiseLift(){
        while(lift.getSlidePosition() < 1200)
            lift.goUp(1);
    }


    //DEPOSIT METHODS -----------------------------------------------------------------------------------------------
    public void dropPixels() {
        deposit.rightDrop();
        deposit.leftDrop();
    }


    //BACKUP METHODS ------------------------------------------------------------------------------------------------
    public void nullALCCheck(){
        if(alC == null)
            telemetry.addLine("Programming issue! Alliance color was not initialized in the auto program.");
    }

    @Override
    public void runOpMode() throws InterruptedException {}
}