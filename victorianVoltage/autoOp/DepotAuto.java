
        package org.firstinspires.ftc.teamcode.opModes.victorianVoltage.autoOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.ArrayList;


/**
 * @author johnson_891609
 */
        @Autonomous(name = "DepotAuto")
public class DepotAuto extends LinearOpMode{
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftFrontDrive, leftRearDrive, rightFrontDrive, rightRearDrive,lift,flip,spool;
    private Servo phoneMount,marker;//,bucketServo,trapDoor;
    private CRServo intakeServo;
    private BNO055IMU gyro;
    private final double GEAR_RATIO = 1, WHEEL_DIAMETER = 4, TICKS_PER_REV_DRIVE = 560,LIFT_RATIO=.3333,LIFT_DIAMETER=0.819,ROTATION_INCHES = 11.81/2;
    //Wheel Ticks/in = 44.57
    private final double DC = TICKS_PER_REV_DRIVE / (Math.PI * WHEEL_DIAMETER * GEAR_RATIO), ROTATION_RADIUS = DC * ROTATION_INCHES;
    private final double TICKS_PER_REV_LIFT=1680,maxheight = 22.75,minheight = 15.875,STRAFE_RATIO = 1;
    private final double LC = (TICKS_PER_REV_LIFT/(Math.PI*LIFT_DIAMETER*LIFT_RATIO));
    private MineralSensor camera;


    private static Orientation angles;
    public void runOpMode() {


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        runtime.reset();
        leftFrontDrive = (DcMotorEx)hardwareMap.dcMotor.get("drvFrontLeft");
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);

        leftRearDrive = (DcMotorEx)hardwareMap.dcMotor.get("drvRearLeft");
        leftRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRearDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRearDrive.setDirection(DcMotor.Direction.FORWARD);

        rightFrontDrive = (DcMotorEx)hardwareMap.dcMotor.get("drvFrontRight");
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);

        rightRearDrive = (DcMotorEx)hardwareMap.dcMotor.get("drvRearRight");
        rightRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRearDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRearDrive.setDirection(DcMotor.Direction.REVERSE);

        lift = (DcMotorEx)hardwareMap.dcMotor.get("lift");
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setDirection(DcMotor.Direction.FORWARD);

        flip = (DcMotorEx)hardwareMap.dcMotor.get("flip");

        flip.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flip.setDirection(DcMotor.Direction.REVERSE);



        spool = (DcMotorEx)hardwareMap.dcMotor.get("spool");
        spool.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spool.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        spool.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spool.setDirection(DcMotor.Direction.FORWARD);

        phoneMount = hardwareMap.servo.get("phoneMount");
        phoneMount.setDirection(Servo.Direction.FORWARD);
        phoneMount.setPosition(0);

        marker = hardwareMap.servo.get("marker");
        marker.setDirection(Servo.Direction.FORWARD);
        marker.setPosition(0);

        intakeServo = hardwareMap.crservo.get("intakeServo");
        intakeServo.setDirection(CRServo.Direction.FORWARD);
        intakeServo.setPower(0);

        camera = new MineralSensor(this);

        waitForStart();
        phoneMount.setPosition(.5);
        try {
         //  disconnect();
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
            parameters.loggingEnabled      = true;
            parameters.loggingTag          = "IMU";
            parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
            gyro = hardwareMap.get(BNO055IMU.class, "gyro");
            gyro.initialize(parameters);
            angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

            move(6,1);
            strafeLeft(3);
            flip.setPower(0);
            while(opModeIsActive() && camera.mineral == MineralSensor.Mineral.NONE) {
                phoneMount.setPosition(0);
                turn(90,"left");
                turn(90,"right");
                turn(150,"left");
                turn(45,"left");


                runtime.reset();
                double tim = runtime.time();
                while(tim + 4 > runtime.time() && opModeIsActive()) {
                    telemetry.addData("Status", "Pausing");
                    telemetry.update();
                    heartbeat();
                }
               // mineralSense();
                heartbeat();

            }

            switch (camera.mineral.getName()) {

                case "Center":
                    phoneMount.setPosition(0);
                    strafeRight(7);
                    move(40, 1);
                    //intakeMotor.setPower(0);

                    deposit();
                //  move(-45,0.8);

                    move(-25,1);
                    turn(75,"left");
                    move(35,1);
                    turn(15,"left");
                    strafeLeft(50);
                    move(35,1);
                    crater();

                    break;
                case "Left":
                    phoneMount.setPosition(0);
                    strafeRight(5);
                    move(8, 1);
                    //intakeMotor.setPower(0);
                    strafeRight(18);
                    move(30, 1);
                    strafeLeft(22);
                    move(8,1);
                    deposit();
                    strafeRight(55);
                    move(-20,1);
                    strafeLeft(5);
                    turn(150,"left");
                    strafeLeft(40);

                    move(29,1);

                    crater();

                    break;
                case "Right":
                    phoneMount.setPosition(0);
                    strafeRight(6);
                    move(10, 1);
                    //intakeMotor.setPower(0);
                    strafeLeft(17);
                    move(29, 1);
                    strafeRight(20);
                    move(6,1);
                    deposit();
                    strafeRight(55);
                    move(-35,1);
                    strafeLeft(10);
                    turn(150,"left");
                    strafeLeft(30);
                    move(30,1);
                    crater();

                    break;

            }

        }catch (InterruptedException e){}

    }
    public void disconnect()throws InterruptedException{
        int ticks = (int)(6.2*LC);
        telemetry.addData("Total Ticks: ", ticks);
        telemetry.update();
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        lift.setTargetPosition(-ticks);
        lift.setPower(1);
        while (lift.isBusy() && opModeIsActive()) {
            heartbeat();
            telemetry.addData("Total Ticks: ", ticks);
            telemetry.addData("Current Ticks: ", lift.getCurrentPosition());
            telemetry.update();
        }
        lift.setPower(0);

    }
    public void turn(int degree,String dir)throws InterruptedException{
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        gyro = hardwareMap.get(BNO055IMU.class, "gyro");
        gyro.initialize(parameters);
        angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        int deg = 0;
        //Time control for velocity calculations
        double currentT = runtime.time();
        double deltaT = 0;


        double oldDeg = Math.abs(angles.firstAngle);
        double deltaDeg = 0;

        if (dir.equals("right"))
            deg = degree;
        else if (dir.equals("left"))
            deg = degree;
        telemetry.addData("desired angle", deg);
        telemetry.update();

        double LFDmotorPower = 0;
        double LRDmotorPower = 0;
        double RFDmotorPower = 0;
        double RRDmotorPower = 0;


        double LFDold = leftFrontDrive.getCurrentPosition();
        double LRDold = leftRearDrive.getCurrentPosition();
        double RFDold = rightFrontDrive.getCurrentPosition();
        double RRDold = rightRearDrive.getCurrentPosition();


        double angleVelocity = 0;
        double linearVelocity = 0;

        double LFDCorrection = 1;
        double LRDCorrection = 1;
        double RFDCorrection = 1;
        double RRDCorrection = 1;

        double LFDVelocity = 0;
        double LRDVelocity = 0;
        double RFDVelocity = 0;
        double RRDVelocity = 0;

        double tAV = 0;
     //   double targetVelocity = targetAngleVelocity * ROTATION_RADIUS; //Ticks/sec
        boolean over = false;
        int direction = dir.equals("left") ? 1 : -1;

        boolean firstTrial = true;
        double firstCorrection = 0;

        //44.57 ticks/in
//0.662
            Pid turnControlLFD = new Pid(0.38,0,0.1);
            Pid turnControlLRD = new Pid(0.38,0,0.1);
            Pid turnControlRFD = new Pid(0.38,0,0.1);
            Pid turnControlRRD = new Pid(0.38,0,0.1);
        //1500 is max speed
            runtime.reset();

            while (deg-Math.abs(angles.firstAngle) > 1.25 || (LFDCorrection > 10 || Math.abs(LRDCorrection) > 10
                    || Math.abs(RFDCorrection) > 10 || Math.abs(RRDCorrection) > 10)&& opModeIsActive()) {

                deltaT = runtime.time() - currentT;
                currentT = runtime.time();
       /*
                deltaDeg = 0.0174533*(Math.abs(angles.firstAngle) - oldDeg);
                oldDeg = 0.0174533*Math.abs(angles.firstAngle);



                //NOTES FOR TOMORROW:
                /*
                    Compute PID for wheel velocity and convert to desired angular velocity. Set as a ratio of angular velocity.

                angleVelocity = deltaDeg / deltaT;
                linearVelocity = angleVelocity * ROTATION_RADIUS;


                targetAngleVelocity =  (deg*0.0174533) * (1-((Math.abs(angles.firstAngle)*0.0174533)/(deg*0.0174533)));
                targetVelocity = targetAngleVelocity * ROTATION_RADIUS;


                LFDVelocity = (leftFrontDrive.getCurrentPosition() - LFDold)/deltaT;
                LRDVelocity = (leftRearDrive.getCurrentPosition() - LRDold)/deltaT;
                RFDVelocity = (rightFrontDrive.getCurrentPosition() - RFDold)/deltaT;
                RRDVelocity = (rightRearDrive.getCurrentPosition() - RRDold)/deltaT;

                LFDold = leftFrontDrive.getCurrentPosition();
                LRDold = leftRearDrive.getCurrentPosition();
                RFDold = rightFrontDrive.getCurrentPosition();
                RRDold = rightRearDrive.getCurrentPosition();

                LFDCorrection = turnControlLFD.controlOutput(-targetVelocity,LFDVelocity,deltaT);
                LRDCorrection = turnControlLRD.controlOutput(-targetVelocity,LRDVelocity,deltaT);
                RFDCorrection = turnControlRFD.controlOutput(targetVelocity,RFDVelocity,deltaT);
                RRDCorrection = turnControlRRD.controlOutput(targetVelocity,RRDVelocity,deltaT);
*/

                    if(deg - Math.abs(angles.firstAngle) > 60)
                        tAV = 500;
                    else if (deg - Math.abs(angles.firstAngle)>30)
                        tAV = 300;
                    else
                        tAV = 50;

                LFDCorrection = turnControlLFD.controlOutput((over ? tAV : -tAV)*direction,leftFrontDrive.getVelocity(AngleUnit.DEGREES),deltaT);
                LRDCorrection = turnControlLRD.controlOutput((over ? tAV : -tAV)*direction,leftRearDrive.getVelocity(AngleUnit.DEGREES),deltaT);
                RFDCorrection = turnControlRFD.controlOutput((over ? -tAV : tAV)*direction,rightFrontDrive.getVelocity(AngleUnit.DEGREES),deltaT);
                RRDCorrection = turnControlRRD.controlOutput((over ? -tAV : tAV)*direction,rightRearDrive.getVelocity(AngleUnit.DEGREES),deltaT);

                LFDmotorPower += LFDCorrection;
                LRDmotorPower +=  LRDCorrection;
                RFDmotorPower +=  RFDCorrection;
                RRDmotorPower +=  RRDCorrection;



                    if(Math.abs(angles.firstAngle) < deg)
                        over = false;
                     else
                        over = true;





                leftFrontDrive.setVelocity((LFDmotorPower),AngleUnit.DEGREES);
                leftRearDrive.setVelocity(LRDmotorPower,AngleUnit.DEGREES);

                rightFrontDrive.setVelocity(RFDmotorPower,AngleUnit.DEGREES);
                rightRearDrive.setVelocity(RRDmotorPower,AngleUnit.DEGREES);


       /*
                leftFrontDrive.setVelocity(-100,AngleUnit.DEGREES);
                leftRearDrive.setVelocity(-100,AngleUnit.DEGREES);
                rightFrontDrive.setVelocity(100,AngleUnit.DEGREES);
                rightRearDrive.setVelocity(100,AngleUnit.DEGREES);*/


                angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

                telemetry.addData("angle", angles.firstAngle);
                telemetry.addData("Target Angle", deg);
                /*
                telemetry.addData("LFD Velocity", LFDVelocity);
                telemetry.addData("LRD Velocity", LRDVelocity);
                telemetry.addData("RFDMotor Velocity", RFDVelocity);
                telemetry.addData("RRDMotor Velocity", RRDVelocity);
                telemetry.addData("Target Angle Velocity", targetAngleVelocity);
                telemetry.addData("Target Velocity", targetVelocity);
                telemetry.addData("LFDPower", LFDmotorPower);

*/

                telemetry.addData("LFDCorrection: ", LFDCorrection);
                telemetry.addData("LRDCorrection: ", LRDCorrection);
                telemetry.addData("RFDCorrection: ", RFDCorrection);
                telemetry.addData("RRDCorrection: ", RRDCorrection);

                telemetry.addData("First Correction: ", firstCorrection);

                double percentError = (Math.abs(deg-Math.abs(angles.firstAngle))/deg)*100;
                telemetry.addData("LFD VELOCITY: ", leftFrontDrive.getVelocity(AngleUnit.DEGREES));
                telemetry.addData("LRD VELOCITY: ", leftRearDrive.getVelocity(AngleUnit.DEGREES));
                telemetry.addData("RFD VELOCITY: ", rightFrontDrive.getVelocity(AngleUnit.DEGREES));
                telemetry.addData("RRD VELOCITY: ", rightRearDrive.getVelocity(AngleUnit.DEGREES));


                telemetry.addData("Percent Error: ",  percentError + "%");
                telemetry.update();



                firstTrial = false;
                heartbeat();
            }

        leftFrontDrive.setPower(0);
        leftRearDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightRearDrive.setPower(0);
    }
    public void strafeRight(double distance) throws InterruptedException {
        int ticks = (int) (distance * DC*1.061947);
        int ticksadj = (int) (distance* DC * 1.061947*1.188119);
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rightRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setTargetPosition(-ticksadj);
        leftFrontDrive.setPower(-.4);

        leftRearDrive.setTargetPosition(ticks);
        leftRearDrive.setPower(.4);

        rightFrontDrive.setTargetPosition(ticksadj);
        rightFrontDrive.setPower(.4);

        rightRearDrive.setTargetPosition(-ticks);
        rightRearDrive.setPower(-.4);
        while (opModeIsActive() && leftFrontDrive.isBusy() && leftRearDrive.isBusy() && rightFrontDrive.isBusy() && rightRearDrive.isBusy()) {
            heartbeat();
        }
        rightFrontDrive.setPower(0);
        rightRearDrive.setPower(0);
        leftFrontDrive.setPower(0);
        leftRearDrive.setPower(0);
    }

    public void strafeLeft(double distance) throws InterruptedException {
        int ticks = (int) (distance * DC*1.061947);
        int ticksadj = (int) (distance* DC * 1.061947*1.188119);
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rightRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setTargetPosition(ticksadj);
        leftFrontDrive.setPower(.4);

        leftRearDrive.setTargetPosition(-ticks);
        leftRearDrive.setPower(-.4);

        rightFrontDrive.setTargetPosition(-ticksadj);
        rightFrontDrive.setPower(-.4);

        rightRearDrive.setTargetPosition(ticks);
        rightRearDrive.setPower(.4);
        while (opModeIsActive() && leftFrontDrive.isBusy() && leftRearDrive.isBusy() && rightFrontDrive.isBusy() && rightRearDrive.isBusy()) {
            heartbeat();
        }
        rightFrontDrive.setPower(0);
        rightRearDrive.setPower(0);
        leftFrontDrive.setPower(0);
        leftRearDrive.setPower(0);
    }
    private void deposit()throws InterruptedException{
        runtime.reset();
        double tim = runtime.time();
        marker.setPosition(1);
        while(tim + 1.5 > runtime.time() && opModeIsActive()) {
            marker.setPosition(1);
            heartbeat();
        }
        move(-6,0.75);
        marker.setPosition(0.3);
    }
    private void crater()throws InterruptedException{
        runtime.reset();
        double tim = runtime.time();
        marker.setPosition(0.7);
        while(tim + 1 > runtime.time() && opModeIsActive()) {
            flip.setPower(0.3);
            heartbeat();
        }
        flip.setPower(0);
    }
    public void move(double distance, double power) throws InterruptedException {
        int ticks = (int) (distance * DC);
        if(distance<0) {
            //  ticks *= -1;
            power *= -1;
        }
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRearDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rightRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRearDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setTargetPosition(ticks);
        leftFrontDrive.setPower(power);

        leftRearDrive.setTargetPosition(ticks);
        leftRearDrive.setPower(power);

        rightFrontDrive.setTargetPosition(ticks);
        rightFrontDrive.setPower(power);

        rightRearDrive.setTargetPosition(ticks);
        rightRearDrive.setPower(power);
        while (opModeIsActive() && leftFrontDrive.isBusy() && leftRearDrive.isBusy() && rightFrontDrive.isBusy() && rightRearDrive.isBusy()) {
            heartbeat();
        }
        rightFrontDrive.setPower(0);
        rightRearDrive.setPower(0);
        leftFrontDrive.setPower(0);
        leftRearDrive.setPower(0);
    }
    private void mineralSense() throws InterruptedException{
        ArrayList<Integer> setting = new ArrayList<Integer>();
        camera.startTFOD();
        runtime.reset();
        double tim = runtime.time();
        while(camera.mineral == MineralSensor.Mineral.NONE && opModeIsActive()){
            // telemetry.addData("ORIENTATION","FINDING" + camera.mineral.getName());
            camera.update();
            // telemetry.update();
            if(camera.mineral != MineralSensor.Mineral.NONE){
                while(setting.size()<=250 && opModeIsActive()){
                    heartbeat();
                    if(camera.mineral == MineralSensor.Mineral.CENTER)
                        setting.add(0);
                    else if(camera.mineral == MineralSensor.Mineral.RIGHT)
                        setting.add(1);
                    else if(camera.mineral == MineralSensor.Mineral.LEFT)
                        setting.add(2);
                    heartbeat();

                }
            }
            heartbeat();

            if(tim + 6 < runtime.time() && camera.mineral == MineralSensor.Mineral.NONE) {
                setting.add(0);
                heartbeat();
                break;
            }
        } camera.stopTFOD();

        int left = 0;
        int right = 0;
        int center = 0;
        for(Integer x : setting)
            switch(x) {
                case 0:
                    center++;
                    break;
                case 1:
                    right++;
                    break;
                case 2:
                    left++;
                    break;
            }
        if(center > left && center > right)
            camera.mineral = MineralSensor.Mineral.CENTER;
        else if(left > center && left > right)
            camera.mineral = MineralSensor.Mineral.LEFT;
        else
            camera.mineral = MineralSensor.Mineral.RIGHT;

        telemetry.addData("ORIENTATION",""+camera.mineral.getName());
        telemetry.update();
    }
    private void heartbeat() throws InterruptedException {
        if (!opModeIsActive()) {
            throw new InterruptedException();
        }
    }
}

