package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Deposit {
    //Deposit Servos Declaration
    private Servo depositServoOne;
    private Servo depositServoTwo;
    public Deposit(HardwareMap hardwareMap){
        //Deposit Servo Config
        depositServoOne = hardwareMap.get(Servo.class, "Right Deposit");
        depositServoTwo = hardwareMap.get(Servo.class, "Left Deposit");
    }
    public void rightDrop(){
        depositServoOne.setPosition(.5);
    }
    public void rightZeroPosition(){
        depositServoOne.setPosition(0);
    }



    public void leftDrop(){
        depositServoTwo.setPosition(.5);
    }
    public void leftZeroPosition(){
        depositServoTwo.setPosition(0);
    }
}
