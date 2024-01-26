package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Transfer {

    public DcMotorEx motor;

    public Transfer() {
        motor = hardwareMap.get(DcMotorEx.class, "Transfer Motor 1");

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    //slides go up proportionally to stick value
    public void up(){
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setPower(-gamepad2.left_stick_y);
    }
    //Slides go down at full power
    public void down(){
        if(motor.getCurrentPosition() < 150) {
            motor.setPower(0);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        else
            motor.setPower(-gamepad2.left_stick_y * .15);
    }
    //zeropowerbehaviour redundancy
    public void stop(){
        motor.setPower(0);
    }
}
