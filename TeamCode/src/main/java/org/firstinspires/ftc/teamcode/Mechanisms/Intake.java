package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    //Intake Servos Declaration
    private CRServo intakeServo;
    private DcMotor intakeMotor;

    private CRServo transferCR1;
    private CRServo transferCR2;
    public Intake(HardwareMap hardwareMap) {
        //Intake Config - Transfer servos spin with intake servos/motors
        intakeServo = hardwareMap.get(CRServo.class, "Intake Servo");
        intakeMotor = hardwareMap.get(DcMotor.class, "Intake Motor");
        transferCR1 = hardwareMap.get(CRServo.class, "Transfer Servo 1");
        transferCR2 = hardwareMap.get(CRServo.class, "Transfer Servo 2");
    }
    public void spinInwards(double g) {
        intakeMotor.setPower(-g);
        intakeServo.setPower(g);
        transferCR1.setPower(1);
        transferCR2.setPower(1);
    }
    public void spinOutwards(double g) {
        intakeMotor.setPower(g);
        intakeServo.setPower(-g);
        transferCR1.setPower(-1);
        transferCR2.setPower(-1);
    }
    public void setPowerZero(){
        intakeMotor.setPower(0);
        intakeServo.setPower(0);
        transferCR1.setPower(0);
        transferCR2.setPower(0);
    }
}
