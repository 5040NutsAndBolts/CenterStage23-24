package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    //Transfer Motors and Servos Declaration
    private DcMotorEx transferM1;
    private DcMotorEx transferM2;
    public Lift(HardwareMap hardwareMap){
        //Transfer Motor Config -- Raise motor
        transferM1 = hardwareMap.get(DcMotorEx.class, "Transfer Motor 1");
        transferM2 = hardwareMap.get(DcMotorEx.class, "Transfer Motor 2");

        transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        transferM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        transferM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        transferM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        transferM2.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    //slides go up proportionally to stick value
    public void goUp(double g) {
            transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            transferM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            transferM1.setPower(-g);
            transferM2.setPower(-g);
    }
    //Slides go down at a reduced speed
    public void goDown(double g){
        if(transferM1.getCurrentPosition() < 50)
        {
            transferM1.setPower(0);
            transferM2.setPower(0);
            transferM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            transferM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        else
        {
            transferM1.setPower(-g * .15);
            transferM2.setPower(-g * .15);
        }
    }
    public void setPowerZero(){
        transferM1.setPower(0);
        transferM2.setPower(0);
    }
    public int getSlidePosition(){
        return (transferM1.getCurrentPosition() + transferM2.getCurrentPosition()) / 2;
    }
}
