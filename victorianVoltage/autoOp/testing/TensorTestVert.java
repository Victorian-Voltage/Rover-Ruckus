
package org.firstinspires.ftc.teamcode.opModes.victorianVoltage.autoOp.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


/**
 * @modified by johnson_891609
 */


public class TensorTestVert {

    public LinearOpMode op;
    public TensorTestVert(LinearOpMode o){
        op = o;
        init();
    }

    public enum Mineral {
        NONE("None"), CENTER("Center"), LEFT("Left"), RIGHT("Right");

        private String name;
        Mineral(String s){
            name = s;
        }

        public String getName(){
            return name;
        }
    }

    public Mineral mineral = Mineral.NONE;
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "AbkgPNn/////AAABmXrsNdhzKkhLoA4mrbzKZosHAiTxnwNm9YAu52+NoYp+VnyN850PEmPG1kqae64hTGMYyz6eUu/Y75bi1LqfsjsVslJBEzljTJFM/SwCRUQPIWktoRaRdY19xPKhJBikCCWCfvImOYJPaBBVc5V8eK4/U5FH5Df6jCE1lSttiLoCHvG0BePN5+tstNX601FIKOw7/VRDvPQzsW9Qdx2NPP5Z8gaozv0TfSAGOwb+3J+I4B2LL390W2koNThElzAwTqV4asgIaLtBeAOEDzeqwlxLcKDuSDkSD8lDRAV9oBkREfdWN88jRSs4LFktT/QI+RU9XKmZhNNJ9hzvQc6rNoDx3KFChUl2DbTGy7cmkYgI";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    public void init(){
        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        }
    }

    public void stopTFOD() {

        tfod.shutdown();

    }

    public void startTFOD() {

        tfod.activate();
    }

    public void update() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.



        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {

                if (updatedRecognitions.size() > 1) {
                    int goldMineralX = -1;
                    int silverMineral1X = -1;

                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL) && recognition.getTop() > 450) {

                            goldMineralX = (int) recognition.getLeft();


                        } else if (silverMineral1X == -1 && recognition.getTop() > 450) {
                            silverMineral1X = (int) recognition.getLeft();
                        }

                        if(recognition.getLabel().equals(LABEL_GOLD_MINERAL))
                            op.telemetry.addData("Y-Val-GOLD",""+recognition.getTop());
                        else
                            op.telemetry.addData("Y-Val-SILVER",""+recognition.getTop());
                        op.telemetry.update();
                    }
                    op.telemetry.update();/*
                    if (silverMineral1X != -1) {
                        if (goldMineralX == -1) {
                            mineral = Mineral.LEFT;

                        } else if (goldMineralX > silverMineral1X) {
                            mineral = Mineral.RIGHT;

                        } else {
                            mineral = Mineral.CENTER;

                        }
                    }*/
                }

            }
        }


    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = op.hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", op.hardwareMap.appContext.getPackageName());

        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.7;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
}