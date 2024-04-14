package org.firstinspires.ftc.teamcode.Autos;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
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
    protected OpenCvWebcam initializeOpenCv() {
        if(alC == null)
            throw new NullPointerException("Uninitialized alliance color"); //makes sure code doesn't break if someone forgets to initialize the alliance color

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

    protected SpikeMarkPosition findSMPos (int boundLeft, int boundRight) {
        if(cam.getScreenPosition().x > boundLeft)
            return SpikeMarkPosition.right;
        else if (cam.getScreenPosition().x < boundRight)
            return SpikeMarkPosition.left;
        else
            return SpikeMarkPosition.center;
    }

    protected void displayCameraTelemetry() {
        telemetry.addData("Auto: ", smp);
        telemetry.addData("X Pos: ", cam.getScreenPosition().x);
        telemetry.addData("Y Pos: ", cam.getScreenPosition().x);
        telemetry.addData("Score: ", cam.getScore());
        telemetry.addData("Width: ", cam.getWidth());
        telemetry.addData("Height: ", cam.getHeight());
        telemetry.update();
        dash.addData("auto num", smp);
        dash.addData("X Position: ", cam.getScreenPosition().x);
        dash.addData("Y Position: ", cam.getScreenPosition().y);
        dash.addData("Score: ", cam.getScore());
        dash.addData("Width: ", cam.getWidth());
        dash.addData("Height: ", cam.getHeight());
        dash.update();
    }

    //COLOUR SENSOR METHODS -----------------------------------------------------------------------------------------
    protected boolean lineSeen() {
        return ls.getBlueValue() > 175;
    }


    //MOVEMENT METHODS ----------------------------------------------------------------------------------------------
    protected double calculateSpeedArc(double dist) {
        if (dist < 10 && dist > -10)
            return -1 * (.5 * (Math.cos(dist / Math.PI))) + .5;
        else
            return 1;
    }
    protected void updateAutoTelemetry() {
        telemetry.addData("x", odo.x);
        telemetry.addData("y", odo.y);
        telemetry.addData("theta", odo.theta);

        dash.addData("x", odo.x);
        dash.addData("y", odo.y);
        dash.addData("theta", odo.theta);
        telemetry.update();
        dash.update();
    }
    protected void moveTo(double x, double y, double theta) {
        while(odo.x != x && odo.y != y && opModeIsActive())
            dt.robotODrive(calculateSpeedArc(y - odo.y),calculateSpeedArc(x - odo.x),0);
        odo.updatePosition();
        updateAutoTelemetry();

    }


    //LIFT METHODS --------------------------------------------------------------------------------------------------
    protected void raiseLift(){
        while(lift.getSlidePosition() < 1200)
            lift.goUp(1);
    }


    //DEPOSIT METHODS -----------------------------------------------------------------------------------------------
    protected void dropPixels() {
        deposit.rightDrop();
        deposit.leftDrop();
    }

    public void runOpMode() throws InterruptedException {}
}