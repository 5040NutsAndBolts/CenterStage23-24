package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.HelperClasses.PID;

public class Lift {
    private PID pidController;
    public static double
            p = .01,
            i = 0,
            d = 0;
    //Transfer Motors and Servos Declaration
    private DcMotorEx transferM1;
    private DcMotorEx transferM2;

    public Lift(HardwareMap hardwareMap) {
        transferM1 = hardwareMap.get(DcMotorEx.class, "Transfer Motor 1");
        transferM2 = hardwareMap.get(DcMotorEx.class, "Transfer Motor 2");

        setZeroPowerBehaviour(DcMotor.ZeroPowerBehavior.BRAKE);

        transferM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        transferM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        transferM2.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void goUp(double g) {    //slides go up proportionally to stick value
        setZeroPowerBehaviour(DcMotor.ZeroPowerBehavior.BRAKE);
            transferM1.setPower(-g);
            transferM2.setPower(-g);
    }
    public void goDown(double g) {
        if(transferM1.getCurrentPosition() < 50) { //lets the slides gently rest if too low to prevent crashing
            setZeroPowerBehaviour(DcMotor.ZeroPowerBehavior.FLOAT);
            setPower(0);
        }
        else {//Slides go down at a reduced speed
            setPower(-g*.15);
        }
    }
    public void setPowerZero() { //hard stops the slides
        setPower(0);
    }
    public int getSlidePosition() { // returns integer average of the two slides
        return (transferM1.getCurrentPosition() + transferM2.getCurrentPosition()) / 2;
    }
    public void movePID(double desiredHeight) { //Likely only going to be used for auto
        pidController = new PID(desiredHeight - getSlidePosition(), p, i, d); //resets data values
        while(desiredHeight != getSlidePosition() || desiredHeight != getSlidePosition()){
            pidController.update(desiredHeight-getSlidePosition());
            goUp(pidController.getPID());
        }
    }

    private void setPower(double amt) {
        transferM1.setPower(amt);
        transferM2.setPower(amt);
    }
    public void setZeroPowerBehaviour(DcMotor.ZeroPowerBehavior zpb) {
        transferM1.setZeroPowerBehavior(zpb);
        transferM2.setZeroPowerBehavior(zpb);
    }
}
