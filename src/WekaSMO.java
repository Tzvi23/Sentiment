import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.SMO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Random;

public class WekaSMO {

    public static int getNegCounter() {
        return negCounter;
    }

    public static int getPosCounter() {
        return posCounter;
    }

    private static DecimalFormat df2 = new DecimalFormat(".##");

    private static int negCounter, posCounter;

    public static void activateWeka(int numOfFolds) {
        DataSource source;
        {
            try {
                System.out.println("Weka SMO Activated!");
                //Initialize data
                source = new DataSource("Combined_Vector_ARFF_2000.arff");
                Instances data = source.getDataSet();
                if (data.classIndex() == -1)
                    data.setClassIndex(data.numAttributes() - 1);
                System.out.println("Finished Get Data set!");
                //Input data for evaluation
                Evaluation eval = new Evaluation(data);
                //Create Classifier
                SMO scheme = new SMO();
                scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007 \""));
                System.out.println("Finish Initialize data, Eval, Smo");
                System.out.println("Start EVAL !");
                //Start Cross Validation Model
                System.out.println("Started EVAL for " + numOfFolds + " Folds...");
                long start = System.currentTimeMillis();
                eval.crossValidateModel(scheme, data, numOfFolds, new Random(1));
                long finish = System.currentTimeMillis();
                System.out.println("### Time Eval: " + ((finish - start)/1000/60) + " min");
                System.out.print(eval.toSummaryString()); //Output Results

                //Clear variables for GC to free memory
                source = null;
                data = null;
                scheme = null;
                eval = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean trainAndTest()
    {
        DataSource testSource;
        DataSource trainSource;

        System.out.println("\n==== Starting Train and Test Sim ====");
        try {
            trainSource = new DataSource("Combined_Vector_ARFF_1400.arff");
            Instances train = trainSource.getDataSet();
            if (train.classIndex() == -1)
                train.setClassIndex(train.numAttributes() - 1);
            testSource = new DataSource("Combined_Vector_ARFF_600.arff");
            Instances test = testSource.getDataSet();
            if (test.classIndex() == -1)
                test.setClassIndex(test.numAttributes() - 1);

            System.out.println("Building new Classifier SMO");
            long start = System.currentTimeMillis();
            SMO cls = new SMO();
            cls.buildClassifier(train);

            System.out.println("Starting Evaluation");
            StringBuffer buffer = new StringBuffer();
            CSV output = new CSV();
            output.setHeader(new Instances(test,0));
            output.setBuffer(buffer);
            output.setOutputFile(new File("Predictions.csv"));
            output.printHeader();
            Evaluation evaluation = new Evaluation(test);
            evaluation.evaluateModel(cls, test, output);
            long finish = System.currentTimeMillis();
            System.out.println("### Time Eval: " + df2.format((finish - (double)start)/1000/60) + " min");
            output.printFooter();

            System.out.println("!!! Check Predictions.csv file !!!");

            String csvFile = "Predictions.csv";
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] inst = line.split(cvsSplitBy);
                if (!line.equalsIgnoreCase("")) {
                    if (inst[2].equalsIgnoreCase("1:0")) negCounter++;
                    else if (inst[2].equalsIgnoreCase("2:1")) posCounter++;
                }
            }

            System.out.println("Negative Files Prediction: " + negCounter + " | Positive Files Prediction: " + posCounter);

            } catch (Exception e) {
            e.printStackTrace();
            return false;
            }

            return true;
    }

    public static void activateWekaNaiveBayes(int numOfFolds) {
        DataSource source;
        {
            try {
                System.out.println("Weka Naive Bayes Activated!");
                //Initialize data
                source = new DataSource("Combined_Vector_ARFF_2000.arff");
                Instances data = source.getDataSet();
                if (data.classIndex() == -1)
                    data.setClassIndex(data.numAttributes() - 1);
                System.out.println("Finished Get Data set!");
                //Input data for evaluation
                Evaluation eval = new Evaluation(data);
                //Create Classifier
                NaiveBayes scheme = new NaiveBayes();
                System.out.println("Finish Initialize data, Eval, Naive Bayes");
                System.out.println("Start EVAL !");
                //Start Cross Validation Model
                System.out.println("Started EVAL for " + numOfFolds + " Folds...");
                long start = System.currentTimeMillis();
                eval.crossValidateModel(scheme, data, numOfFolds, new Random(1));
                long finish = System.currentTimeMillis();
                System.out.println("### Time Eval: " + ((finish - start)/1000/60) + " min");
                System.out.print(eval.toSummaryString()); //Output Results

                //Clear variables for GC to free memory
                source = null;
                data = null;
                scheme = null;
                eval = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void activateWekaRandomForest(int numOfFolds) {
        DataSource source;
        {
            try {
                System.out.println("Weka Random Forest Activated!");
                //Initialize data
                source = new DataSource("Combined_Vector_ARFF_2000.arff");
                Instances data = source.getDataSet();
                if (data.classIndex() == -1)
                    data.setClassIndex(data.numAttributes() - 1);
                System.out.println("Finished Get Data set!");
                //Input data for evaluation
                Evaluation eval = new Evaluation(data);
                //Create Classifier
                RandomForest scheme = new RandomForest();
                scheme.setOptions(weka.core.Utils.splitOptions("-P 100 -I 500 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1")); //TODO: change -I 100 to: -I 500 Number of trees(Iterations_
                System.out.println("Finish Initialize data, Eval, Random Forest");
                System.out.println("Start EVAL !");
                //Start Cross Validation Model
                System.out.println("Started EVAL for " + numOfFolds + " Folds...");
                long start = System.currentTimeMillis();
                eval.crossValidateModel(scheme, data, numOfFolds, new Random(1));
                long finish = System.currentTimeMillis();
                System.out.println("### Time Eval: " + ((finish - start)/1000/60) + " min");
                System.out.print(eval.toSummaryString()); //Output Results

                //Clear variables for GC to free memory
                source = null;
                data = null;
                scheme = null;
                eval = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}