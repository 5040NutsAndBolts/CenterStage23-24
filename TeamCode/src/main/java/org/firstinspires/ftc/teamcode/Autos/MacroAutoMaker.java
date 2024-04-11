package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
import org.firstinspires.ftc.teamcode.Mechanisms.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MacroAutoMaker extends LinearOpMode {
    Drivetrain drivetrain;
    Deposit deposit;
    Dronelauncher dronelauncher;
    Lift lift;
    Intake intake;

    protected static int parser = 0; //0 - pre split, 123 - lrc
    protected static int baseLiftPosition;
    protected Odometry odo = new Odometry(hardwareMap);

    private static ArrayList<ArrayList<Double>> preSplit = new ArrayList<>()
                                               ,left = new ArrayList<>()
                                               ,center = new ArrayList<>()
                                               ,right = new ArrayList<>();
    private static int tabCount = 0;

    private void logCurrentPosition() {
        ArrayList<Double> temp = new ArrayList<>();
        temp.add(odo.x);
        temp.add(odo.y);
        temp.add(odo.theta);
        if(lift.getSlidePosition() > baseLiftPosition)
            temp.add((double)lift.getSlidePosition());
        switch(parser) {
            case 0:
                preSplit.add(temp);
            case 1:
                left.add(temp);
            case 2:
                center.add(temp);
            case 3:
                right.add(temp);
        }
    }
    private String addTab() {
        StringBuilder s = new StringBuilder();for (int i = 0; i < tabCount; i++) {s.append("\t");}return s.toString();}

    public void writeToFile(){
        LocalDateTime now = null;
        String fileName = "badFileName";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM_dd_HH_mm_ss");
            fileName = "MacroAuto" + now.format(dateFormatter);
        }
        try {
            File nf = new File("org\\firstinspires\\ftc\\teamcode\\Autos\\ILoveDataLogs\\" + fileName + ".java");
            System.out.println("File created: " + nf.getName());
            if (!nf.createNewFile())
                System.out.println("File already exists");

            FileWriter fileWriter = new FileWriter(nf.getPath());
            fileWriter.write("package ILoveDataLogs;\n");
            fileWriter.write("public class " + fileName + " extends AutoMethods {\n");tabCount++;

            fileWriter.write(addTab() + "@Override\n");
            fileWriter.write(addTab() + "public void runOpMode() throws InterruptedException {\n");tabCount++;

            fileWriter.write(addTab() + "waitForStart();\n");
            fileWriter.write(addTab() + "while(opModeIsActive()) {\n");tabCount++;

            fileWriter.write(addTab() + "alC = AllianceColor.blue;\n");
            fileWriter.write(addTab() + "findSMPos(750, 300);\n");
            fileWriter.write(addTab() + "//start on macro\n");

            fileWriter.write(addTab()+"if (smp == SpikeMarkPosition.left && Op) { //start of left spike mark\n");
            tabCount++;

            ArrayList<ArrayList<ArrayList<Double>>> allLoggedPositions = new ArrayList<>();
            allLoggedPositions.add(preSplit);
            allLoggedPositions.add(left);
            allLoggedPositions.add(center);
            allLoggedPositions.add(right);
            for(int i = 0; i < allLoggedPositions.size();i++){
                switch (i) {
                    case 0 :
                        fileWriter.write(addTab()+"//start of presplit");
                        for (int a = 0; a < allLoggedPositions.size(); a++)
                            fileWriter.write(addTab() + "moveTo(" + allLoggedPositions.get(0) + "," + allLoggedPositions.get(1) + "," + allLoggedPositions.get(2) + ");\n");

                        tabCount--;
                        fileWriter.write(addTab() + "}\n");

                    case 1:
                        fileWriter.write(addTab()+"if(smp == SpikeMarkPosition.left) { //start of left\n");
                        tabCount++;
                        for (ArrayList<Double> psLRC : allLoggedPositions.get(i))
                            fileWriter.write(addTab() + "moveTo(" + psLRC.get(0) + "," + psLRC.get(1) + "," + psLRC.get(2) + ");\n");

                        tabCount--;
                        fileWriter.write(addTab() + "}\n");

                    case 2:
                        fileWriter.write(addTab()+"if(smp == SpikeMarkPosition.center) { //start of center\n");
                        tabCount++;
                        for (ArrayList<Double> psLRC : allLoggedPositions.get(i))
                            fileWriter.write(addTab() + "moveTo(" + psLRC.get(0) + "," + psLRC.get(1) + "," + psLRC.get(2) + ");\n");

                        tabCount--;
                        fileWriter.write(addTab() + "}\n");

                    case 3:
                        fileWriter.write(addTab()+"if(smp == SpikeMarkPosition.right) { //start of right\n");
                        tabCount++;
                        for (ArrayList<Double> psLRC : allLoggedPositions.get(i))
                            fileWriter.write(addTab() + "moveTo(" + psLRC.get(0) + "," + psLRC.get(1) + "," + psLRC.get(2) + ");\n");

                        tabCount--;
                        fileWriter.write(addTab() + "}\n");
                }
            }
            tabCount--;
            fileWriter.write(addTab() + "}\n");            tabCount--;
            fileWriter.write(addTab() + "}\n");            tabCount--;
            fileWriter.write(addTab() + "}\n");            tabCount--;
            fileWriter.write("}");
            fileWriter.close();
        }catch (IOException p) {System.out.println("error writing to file: " + p);}
    }
    private static double lessDots (int c) {
        switch(parser) {
            case 0:
                return preSplit.get(preSplit.size() - 1).get(c);
            case 1:
                return left.get(left.size() - 1).get(c);
            case 2:
                return center.get(center.size() - 1).get(c);
            case 3:
                return right.get(right.size() - 1).get(c);
            default:
                return 0;
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        while(opModeIsActive()) {
            ElapsedTime timer = new ElapsedTime();
            timer.startTime();

            drivetrain = new Drivetrain(hardwareMap);
            deposit = new Deposit(hardwareMap);
            dronelauncher = new Dronelauncher(hardwareMap);
            lift = new Lift(hardwareMap);
            intake = new Intake(hardwareMap);
            baseLiftPosition=lift.getSlidePosition()+50;

            drivetrain.robotODrive(gamepad1.left_stick_y, gamepad1.left_stick_x,
                    gamepad1.right_stick_x);

            if (gamepad1.right_stick_y < -.05)
                lift.goUp(gamepad1.right_stick_y);
            else if (gamepad1.right_stick_y > .05)
                lift.goDown(gamepad1.right_stick_y);
            else
                lift.setPowerZero();


            if (!gamepad1.b)
                dronelauncher.launch();

            if (gamepad1.right_trigger > .05)
                intake.spinInwards(gamepad1.right_trigger);
                //Spin Outwards
            else if (gamepad1.left_trigger > .05)
                intake.spinOutwards(gamepad1.right_trigger);
                //If neither are pressed or both are pressed everything is set to it's zeroPowerBehavior()
            else if ((gamepad1.left_trigger > .05 && gamepad1.right_trigger > .05) || (gamepad1.left_trigger < .05 && gamepad1.right_trigger < .05))
                intake.setPowerZero();

            if (gamepad1.right_bumper)
                deposit.rightDrop();
            else
                deposit.rightZeroPosition();
            if (gamepad1.left_bumper)
                deposit.leftDrop();
            else
                deposit.leftZeroPosition();


            if (gamepad1.dpad_left)
                parser = 1;
            else if (gamepad1.dpad_up)
                parser = 2;
            else if (gamepad1.dpad_right)
                parser = 3;
            else if (gamepad1.dpad_down) {
                writeToFile();
                System.exit(0);
            }

            if (timer.milliseconds() % 200 == 0){
                double spacer = .2;
                    if((odo.x + spacer <= lessDots(0) || odo.x - spacer >= lessDots(0)) //Checks if robot has moved significant amount in any direction
                    || (odo.y + spacer <= lessDots(1) || odo.y - spacer >= lessDots(1))
                    || (odo.theta + spacer <= lessDots(2) || odo.theta - spacer >= lessDots(2)))
                        logCurrentPosition();
            }
            if(getRuntime() == 20)
        }
    }
}