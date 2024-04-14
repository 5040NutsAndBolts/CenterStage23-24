package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HelperClasses.Odometry;
import org.firstinspires.ftc.teamcode.Mechanisms.Deposit;
import org.firstinspires.ftc.teamcode.Mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.Mechanisms.Dronelauncher;
import org.firstinspires.ftc.teamcode.Mechanisms.Intake;
import org.firstinspires.ftc.teamcode.Mechanisms.Lift;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MacroAutoMaker extends LinearOpMode {

    private static int parser = 0; //0 - pre split, 1-6 - lrc with park, 7 - post split
    private static int addSlide;
    private final Odometry odo = new Odometry(hardwareMap);
    private boolean park = false;

    private final ArrayList<ArrayList<Double>> preSplit = new ArrayList<>()
                                              ,postSplit = new ArrayList<>()
                                              ,left = new ArrayList<>()
                                              ,center = new ArrayList<>()
                                              ,right = new ArrayList<>()
                                              ,lPark = new ArrayList<>()
                                              ,cPark = new ArrayList<>()
                                              ,rPark = new ArrayList<>();
    private static int tabCount = 0;

    private void logCurrentPosition() {
        ArrayList<Double> temp = new ArrayList<>();
        temp.add(odo.x);
        temp.add(odo.y);
        temp.add(odo.theta);
        switch(parser) {
            case 0:
                preSplit.add(temp);
            case 1:
                if(!park)
                    left.add(temp);
                else
                    lPark.add(temp);
            case 2:
                if(!park)
                    center.add(temp);
                else
                    cPark.add(temp);
            case 3:
                if(!park)
                    right.add(temp);
                else
                    rPark.add(temp);
            case 4:
                postSplit.add(temp);
        }
    }
    private String addTab() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < tabCount; i++)
            s.append("\t");
        return s.toString();
    }

    private void writeToFile(){
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
            fileWriter.write("public class " + fileName + " extends AutoMethods { //This code was written by our macro maker, please test!\n");tabCount++;
            fileWriter.write(addTab() + "private boolean park = false;{\n");

            fileWriter.write(addTab() + "@Override\n");
            fileWriter.write(addTab() + "public void runOpMode() throws InterruptedException {\n");tabCount++;

            fileWriter.write(addTab() + "waitForStart();\n");
            fileWriter.write(addTab() + "while(opModeIsActive()) {\n");tabCount++;

            fileWriter.write(addTab() + "alC = AllianceColor.blue;\n");
            fileWriter.write(addTab() + "findSMPos(750, 300);\n");
            fileWriter.write(addTab() + "//start on macro\n");
            tabCount++;


            ArrayList<ArrayList<ArrayList<Double>>> aLP = new ArrayList<>();
            aLP.add(preSplit);//0
            aLP.add(left);//1
            aLP.add(lPark);//2
            aLP.add(center);//3
            aLP.add(cPark);//4
            aLP.add(right);//5
            aLP.add(rPark);//6
            aLP.add(postSplit);//7

            try {
            for(int i = 0; i < aLP.size();i++){  //The Great Logger
	if (i == 0 || i == 4) {
	    fileWriter.write(addTab() + "//start of " + getArrayNames(i));
	    for (ArrayList<Double> log : preSplit) {
	        if (log.size() == 3)
	            fileWriter.write(addTab() + "moveTo(" + log.get(0) + "," + log.get(1) + "," + log.get(2) + ");\n");
	        else if (log.size() == 1) {
                            fileWriter.write(addTab() + "lift.movePID(" + log.get(0) + ");\n");
                            fileWriter.write(addTab() + "lift.goDown(.8);\n");
                        }
	    }
	}
                else if(i < 4){
                    fileWriter.write(addTab()+"if(smp == SpikeMarkPosition."+getArrayNames(i)+") { //start of "+getArrayNames(i)+"\n");
                    tabCount++;
                    for(ArrayList<Double> log : aLP.get(i)) {
                        if(log.size()==3)
                            fileWriter.write(addTab() + "moveTo(" + log.get(0) + "," + log.get(1) + "," + log.get(2) + ");\n");
                        else if (log.size() == 1) {
                            fileWriter.write(addTab() + "lift.movePID(" + log.get(0) + ");\n");
                            fileWriter.write(addTab() + "lift.goDown(.8);\n");
                        }
                    }
                    //Parking!!
                    fileWriter.write(addTab()+"if(park) { //start of "+getArrayNames(i)+" parking\n");
                    tabCount++;
                    for(ArrayList<Double> log : aLP.get(i)) {
                        if(log.size() == 3)
                            fileWriter.write(addTab() + "moveTo(" + log.get(0) + "," + log.get(1) + "," + log.get(2) + ");\n");
                        else if (log.size() == 1) {
                            fileWriter.write(addTab() + "lift.movePID(" + log.get(0) + ");\n");
                            fileWriter.write(addTab() + "lift.goDown(.8);\n");
                        }
                    }                                                                    tabCount--;
                    fileWriter.write(addTab() + "} \n");        tabCount--;
                    fileWriter.write(addTab() + "}\n");
                }
            }}catch (ArrayIndexOutOfBoundsException a) {System.out.println("programming issue: " +a);}

            tabCount--;
            fileWriter.write(addTab() + "}\n");
            tabCount--;
            fileWriter.write(addTab() + "}\n");            tabCount--;
            fileWriter.write(addTab() + "}\n");            tabCount--;
            fileWriter.write(addTab() + "}\n");            tabCount--;
            fileWriter.write("}");
            fileWriter.close();
        }catch (IOException p) {System.out.println("error writing to file: " + p);}
    }
    private void resetXYTheta() {
        ArrayList<Double> temp = new ArrayList<>();
        temp.add(0.0);
        temp.add(0.0);
        switch(parser) {
            case 0:
                preSplit.add(temp);
            case 1:
                left.add(temp);
            case 2:
                center.add(temp);
            case 3:
                right.add(temp);
            case 4:
                postSplit.add(temp);
        }
    }
    private double lessDots (int c) {
        switch(parser) {
            case 0:
                return preSplit.get(preSplit.size() - 1).get(c);
            case 1:
                return left.get(left.size() - 1).get(c);
            case 2:
                return center.get(center.size() - 1).get(c);
            case 3:
                return right.get(right.size() - 1).get(c);
            case 4:
                return postSplit.get(postSplit.size()-1).get(c);
            default:
                return 0;
        }
    }
    private String getArrayNames (int p) { //this is for the filewriting cus i dont wanna figure out a better way
        switch(p) {
            case 0:
                return "preSplit";
            case 1:
                return "left";
            case 2:
                return "center";
            case 3:
                return "right";
            case 4:
                return "postSplit";
            default:
                return "error in getArrayNames ()";
        }
    }

    private void logSlidePosition(double c) {
        ArrayList<Double> temp = new ArrayList<>();
        temp.add(c);
        switch(parser) {
            case 0:
                preSplit.add(temp);
            case 1:
                left.add(temp);
            case 2:
                center.add(temp);
            case 3:
                right.add(temp);
            case 4:
                postSplit.add(temp);
        }
    }
    private double lastSlide;

    @Override
    public void runOpMode() throws InterruptedException {
        Drivetrain drivetrain = new Drivetrain(hardwareMap);
        Deposit deposit = new Deposit(hardwareMap);
        Dronelauncher dronelauncher = new Dronelauncher(hardwareMap);
        Lift lift = new Lift(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        int baseLiftPosition = lift.getSlidePosition() + 250; //the +250 is js an extra buffer cs it's not gonna be in the same position every time

        waitForStart();
        while(opModeIsActive()) {

            if(lift.getSlidePosition() > lastSlide)
                lastSlide=lift.getSlidePosition();
            else
               logSlidePosition(lift.getSlidePosition());

            //Telemetry
            telemetry.addLine("park: "+ park);
            switch(parser) {
                case 0:
                    telemetry.addLine("Branch: preSplit");
                case 1:
                    telemetry.addLine("Branch: left");
                case 2:
                    telemetry.addLine("Branch: centre");
                case 3:
                    telemetry.addLine("Branch: right");
                case 4:
                    telemetry.addLine("Branch: postSplit");
                default:
                    telemetry.addLine("Parser wasn't set! Major programming issue.");
            }

            drivetrain.robotODrive(gamepad1.left_stick_y, gamepad1.left_stick_x,
                    gamepad1.right_stick_x);

            if (gamepad1.right_stick_y < -.05)
                lift.goUp(.75);
            else if (gamepad1.right_stick_y > .05)
                lift.goDown(.75);
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


            if (gamepad1.dpad_up && parser != 4) { //Parser goes up
                parser++;
                park = false;
            }
            if (gamepad1.dpad_down && parser != 0) {  //Parser goes down
                parser--;
                park = false; //if we swap between left and center we don't want to be put in cPark js start in center
            }
            if(gamepad1.dpad_left)
                park=true;

            double spacer = .2;
            if((odo.x + spacer <= lessDots(0) || odo.x - spacer >= lessDots(0)) //Checks if robot has moved significant amount in any direction
                || (odo.y + spacer <= lessDots(1) || odo.y - spacer >= lessDots(1))
                || (odo.theta + spacer <= lessDots(2) || odo.theta - spacer >= lessDots(2)))
                    logCurrentPosition();
        }
    }
}