package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


public class BlinkinTest extends LinearOpMode {

    // declares the lights
    RevBlinkinLedDriver lights;
    int temporary = 1;
    public void runOpMode() throws InterruptedException
    {
        // initializes the object lights
    lights = hardwareMap.get(RevBlinkinLedDriver.class, "lights");
        // sets the pattern/color of the lights
        waitForStart();
        ElapsedTime timer = new ElapsedTime();
        timer.startTime();
        while(opModeIsActive())
        {
            // important to start time in loop so init doesn't have timer running before game start
            if(time >= 145)
            {
                lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE);
            }
        }
    }



}
