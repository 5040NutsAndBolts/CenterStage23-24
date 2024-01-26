package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Intake {
    private CRServo mouthServo;
    private DcMotor mouthMotor;
    private CRServo transferCR1;
    private CRServo transferCR2;
    public Intake(){
        mouthServo = hardwareMap.get(CRServo.class, "Intake Servo");
        mouthMotor = hardwareMap.get(DcMotor.class, "Intake Motor");
        transferCR1 = hardwareMap.get(CRServo.class, "Transfer Servo 1");
        transferCR2 = hardwareMap.get(CRServo.class, "Transfer Servo 2");
    }
    public void spinIn(double cI){
        mouthMotor.setPower(-cI);
        mouthServo.setPower(cI);
        transferCR1.setPower(1);
        transferCR2.setPower(1);
    }
    public void spinOut(double cI){
        mouthMotor.setPower(cI);
        mouthServo.setPower(-cI);
        transferCR1.setPower(-1);
        transferCR2.setPower(-1);
    }
    //If neither are pressed or both are pressed everything is set to it's zeroPowerBehavior()
    public void stop(){
        mouthMotor.setPower(0);
        mouthServo.setPower(0);
        transferCR1.setPower(0);
        transferCR2.setPower(0);
    }
}