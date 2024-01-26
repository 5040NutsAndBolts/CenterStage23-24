package org.firstinspires.ftc.teamcode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.ftccommon.configuration.RobotConfigMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Deposit {

    public Servo dsOne;
    public Servo dsTwo;
    public Deposit() {
        dsOne = hardwareMap.get(Servo.class, "Right Deposit");
        dsTwo = hardwareMap.get(Servo.class, "Left Deposit");
    }
    public void rDrop(){dsOne.setPosition(.5);}
    public void rStop(){dsOne.setPosition(0);}
    public void lDrop(){dsTwo.setPosition(.5);}
    public void lStop(){dsTwo.setPosition(0);}
}
