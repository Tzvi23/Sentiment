/**
 * @Author: Tzvi Puchinsky
 */
import PreProcessing.*;

import java.util.List;
import java.util.Scanner;

//Important!!! Need to Set the java heap to: -Xms512M -Xmx1500M
public class Program {

    public static void main(String[] args) {
        DataSaver saver_2000 = new DataSaver();

        System.out.println("Make sure you have txt_sentoken folder with the project");

        //Building Data Base
        Scanner reader = new Scanner(System.in);
        System.out.println("Q: Build Data Base? y/n");
        char ans = 0;
        while ((ans != 'y') && (ans != 'n')) {
            ans = reader.next().charAt(0);
            if ((ans != 'y') && (ans != 'n')) System.out.println("Bad key enter again");
        }

        if (ans == 'y') {
            if (saver_2000.activateDataSaver("2000", false)) {
                System.out.println("Saver 2000 successfully finished building data base");
            } else System.out.println("Saver 2000 Failed!!");

            List<String> allWords = saver_2000.getBagOfWords();

            System.out.println("Building Saver 700 Files");
            DataSaver saver700 = new DataSaver();
            saver700.setBagOfWordsAllSet(allWords);

            System.out.println("Building Saver 700 Files - Start");
            if (saver700.activateDataSaver("700", false)) System.out.println("Saver 700 finished!");
            else System.out.println("Saver 700 Failed!!");

            System.out.println("Building Saver 300 Files");
            DataSaver saver300 = new DataSaver();
            saver300.setBagOfWordsAllSet(allWords);

            System.out.println("Building Saver 300 Files - Start");
            if (saver300.activateDataSaver("300", true)) System.out.println("Saver 300 finished!");
            else System.out.println("Saver 300 Failed!!");

            reader.reset();
            System.out.println("Choose Option: \n" +
                    "1 - Cross Validation for model (Using 2000 files)\n" +
                    "2 - Train & Test \n" +
                    "3 - Exit \n" +
                    "Enter Number option: ");
            int option;
            option = reader.nextInt();
            switch (option) {
                case 1:
                    reader.reset();
                    System.out.println("Enter Number of Folds [1-10]");
                    int folds = reader.nextInt();
                    saver_2000 = null;
                    saver300 = null;
                    saver700 = null;
                    WekaSMO.activateWekaNaiveBayes(folds);
                    break;
                case 2:
                    saver_2000 = null;
                    WekaSMO.trainAndTest();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Bad Option");
            }
        }
        reader.reset();
        System.out.println("Choose Option: \n" +
                "1 - Cross Validation for model (Using 2000 files)\n" +
                "2 - Train & Test \n" +
                "3 - Exit \n" +
                "Enter Number option: ");
        int option;
        option = reader.nextInt();
        switch (option) {
            case 1:
                reader.reset();
                System.out.println("Enter Number of Folds [1-10]");
                int folds = reader.nextInt();
                WekaSMO.activateWekaRandomForest(folds);
                break;
            case 2:
                saver_2000 = null;
                WekaSMO.trainAndTest();
                break;
            case 3:
                return;
            default:
                System.out.println("Bad Option");
        }
    }
}
