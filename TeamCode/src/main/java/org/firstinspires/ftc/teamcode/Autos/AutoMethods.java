package org.firstinspires.ftc.teamcode.Autos;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
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

    //CAMERA METHODS ------------------------------------------------------------------------------------------------
    public static ArduCam initializeCamera(AllianceColor allianceColor) {
        return new ArduCam(allianceColor);
    }

    public static OpenCvWebcam initializeOpenCv(HardwareMap hardwareMap, ArduCam camera) {
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

        webcam.setPipeline(camera);
        return webcam;
    }
    public static SpikeMarkPosition spikeMarkFinder(ArduCam cam) {
        //This is what to tune when the positions of what we are looking for change, like when we move the camera
        if (cam.getWidth() < 60 || cam.getHeight() < 30)
            return SpikeMarkPosition.right;
        else {
            if (cam.getScreenPosition().x > 100)
                return SpikeMarkPosition.center;
            else
                return SpikeMarkPosition.left;
        }
    }

    public static void displayCameraTelemetry(Telemetry tele, Telemetry dashTele, SpikeMarkPosition spikePos) {
        tele.addData("Auto", spikePos);
        tele.addData("X Pos", TSEFinder.screenPosition.x);
        tele.addData("Y Pos", TSEFinder.screenPosition.y);
        tele.update();
        dashTele.addData("auto", spikePos);
        dashTele.update();
    }


    //COLOUR SENSOR METHODS -----------------------------------------------------------------------------------------
    public static boolean lineSeen(LineSensor lineSensor) {
        return lineSensor.getBlueValue() > 175;
    }


    //MOVEMENT METHODS ----------------------------------------------------------------------------------------------
    public static void displayMovementTelemetry(Telemetry tele, Odometry odo) {
        tele.addData("x", odo.x);
        tele.addData("y", odo.y);
        tele.addData("theta", odo.theta);
        tele.update();
    }

    public static void moveX(double distance, Odometry odo, Drivetrain dt, Telemetry tele){
        while (odo.x < distance){
            odo.updatePositionRoadRunner();
            dt.robotODrive(0, 1, 0);
            displayMovementTelemetry(tele, odo);
        }
    }
    public static void moveX(double distance, double speed, Odometry odo, Drivetrain dt, Telemetry tele){
        while (odo.x < distance){
            odo.updatePositionRoadRunner();
            dt.robotODrive(0, speed, 0);
            displayMovementTelemetry(tele, odo);
        }
    }

    public static void moveY(double distance, Odometry odo, Drivetrain dt, Telemetry tele){
        while (odo.y < distance){
            odo.updatePositionRoadRunner();
            dt.robotODrive(0, 1, 0);
            displayMovementTelemetry(tele, odo);
        }
    }
    public static void moveY(double distance, double speed, Odometry odo, Drivetrain dt, Telemetry tele){
        while (odo.y < distance){
            odo.updatePositionRoadRunner();
            dt.robotODrive(0, speed, 0);
            displayMovementTelemetry(tele, odo);
        }
    }

    public static void turnClockwise(double angle, Odometry odo, Drivetrain dt, Telemetry tele) {
        while (odo.theta < angle){
        odo.updatePositionRoadRunner();
        dt.robotODrive(0, 0, 1);
        displayMovementTelemetry(tele, odo);
        }
    }
    public static void turnClockwise(double angle, double speed, Odometry odo, Drivetrain dt, Telemetry tele) {
        while (odo.theta < angle){
        odo.updatePositionRoadRunner();
        dt.robotODrive(0, 0, speed);
        displayMovementTelemetry(tele, odo);
        }
    }

    public static void turnCounterClockwise(double angle, Odometry odo, Drivetrain dt, Telemetry tele) {
        while (odo.theta < angle){
        odo.updatePositionRoadRunner();
        dt.robotODrive(0, 0, -1);
        displayMovementTelemetry(tele, odo);
        }
    }
    public static void turnCounterClockwise(double angle, double speed, Odometry odo, Drivetrain dt, Telemetry tele) {
        while (odo.theta < angle){
        odo.updatePositionRoadRunner();
        dt.robotODrive(0, 0, -speed);
        displayMovementTelemetry(tele, odo);
        }
    }


    //LIFT METHODS --------------------------------------------------------------------------------------------------
    public static void raiseLift(Lift lift){
        while(lift.getSlidePosition() < 1200)
            lift.goUp(1);
    }


    //DEPOSIT METHODS -----------------------------------------------------------------------------------------------
    public static void dropPixels(Deposit deposit) {
        deposit.rightDrop();
        deposit.leftDrop();
    }

    @Override
    public void runOpMode() throws InterruptedException {}
}