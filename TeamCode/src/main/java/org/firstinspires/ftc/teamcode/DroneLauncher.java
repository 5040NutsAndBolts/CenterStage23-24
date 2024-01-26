package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class DroneLauncher {
    public CRServo droneLaunch;
    public DroneLauncher(){
        droneLaunch = hardwareMap.get(CRServo.class, "Drone Launcher");
    }
    //Spins the servo for 1 second
    public void fire(){
        //Creates an elapsed timer
        ElapsedTime timer = new ElapsedTime();
        timer.startTime();
        //While 1 second mark has not passed
        while(timer.seconds() < 1)
            droneLaunch.setPower(1);
        droneLaunch.setPower(0); //Redundancy for zeroPowerBehaviour
    }
}
