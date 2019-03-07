/**
 * @Author: Tzvi Puchinsky
 */
package PreProcessing;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


//@SuppressWarnings("UnusedReturnValue")
public class DataSaver {
    private  HashMap<String, Integer> counter;

    public HashMap<String, HashMap<String, Integer>> getVectorList() {
        return vectorList;
    }

    public List<String> getBagOfWords() {
        return bagOfWords;
    }

    private HashMap<String, HashMap<String, Integer>> vectorList = new HashMap<>();

    private HashMap<String, HashMap<String, Integer>> tf;

    private HashMap<String, Integer> df;

    private HashMap<String, Integer> wordCount;

    private HashMap<String, Integer> df_Pos = new HashMap<>();

    private HashMap<String, Integer> df_Neg = new HashMap<>();

    public void setBagOfWordsAllSet(List<String> bagOfWordsAllSet) {
        this.bagOfWordsAllSet = bagOfWordsAllSet;
    }

    private List<String> bagOfWords = new ArrayList<>();

    public List getBagOfWords_Test() {
        return bagOfWords_Test;
    }

    private List<String> bagOfWordsAllSet = new ArrayList<>();

    public HashMap<String, Double> getNegTfDf() {
        return NegTfDf;
    }

    public HashMap<String, Double> getPosTfDf() {
        return PosTfDf;
    }

    private HashMap<String, Double> NegTfDf = new HashMap<>();

    private HashMap<String, Double> PosTfDf = new HashMap<>();

    private List bagOfWords_Test;

    public Tokenizer getT() {
        return t;
    }

    private Tokenizer t;

    private int wordsCounter;

    //Constructor
    public DataSaver()
    {
        wordsCounter = 0 ;
        counter = new HashMap<>();
        df = new HashMap<>();
        tf = new HashMap<>();
        wordCount = new HashMap<>();
    }


    public boolean activateDataSaver (String arg, boolean test){

        if (arg.equalsIgnoreCase("2000")) txt_sentoken("2000", test);
        else if (arg.equalsIgnoreCase("700")) txt_sentoken("1400", test);
        else if (arg.equalsIgnoreCase("300")) txt_sentoken("600", test);
        else
        {
            System.out.println("Bad Argument for activate Data Saver function");
            return false;
        }
        return true;
    }

    private boolean txt_sentoken (String arg, boolean test)
    {
        t = new Tokenizer("");

        int tempCounter = 1;
        File negDir = new File("txt_sentoken_" + arg +"\\neg");
        if (!Files.exists(Paths.get(negDir.getPath())))
        {
            System.out.println("### ERROR txt_sentoken_" + arg +"\\neg does not exists");
            return false;
        }
        //Creates Neg Folder for output. If exists skips
        File NegName = new File("Neg_" + arg);
        if (!NegName.exists()) NegName.mkdir();

        System.out.println("####### Negative Dir ########");

        File[] directoryListing = negDir.listFiles();
        if (directoryListing != null){
            for (File f : directoryListing){
                System.out.println(tempCounter);
                tempCounter++;
                System.out.println(f.getPath());
                t.setPath(f.getPath());
                t.ActivateTokenizer();
                try {
                    writeToCsv(t,0, f, this, arg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        tempCounter = 1 ;

        File posDir = new File("txt_sentoken_" + arg +"\\pos");
        if (!Files.exists(Paths.get(posDir.getPath())))
        {
            System.out.println("### ERROR txt_sentoken_" + arg +"\\pos does not exists");
            return false;
        }
        //Creates Neg Folder for output. If exists skips
        File PosName = new File("Pos_"+ arg);
        if (!PosName.exists()) PosName.mkdir();

        System.out.println("####### Positive Dir ########");

        File[] directoryListing2 = posDir.listFiles();
        if (directoryListing != null){
            for (File f : directoryListing2){
                System.out.println(tempCounter);
                tempCounter++;
                System.out.println(f.getPath());
                t.setPath(f.getPath());
                t.ActivateTokenizer();
                try {
                    writeToCsv(t,1, f, this, arg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        try {
            if (this.bagOfWordsAllSet.size() != 0)this.bagOfWords = this.bagOfWordsAllSet;
            buildDF(this, arg);
            writeCombinedVectorsArff(this, arg, test);
            clearData(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void clearData(DataSaver saver)
    {
        saver.tf.clear();
        saver.df_Neg.clear();
        saver.df_Pos.clear();
    }

    private void buildDF(DataSaver saver, String arg)
    {
        int counter = 1;

        System.out.println("Building DF Document Frequency for " + arg);

        File negDir = new File("txt_sentoken_" + arg +"\\neg");
        File posDir = new File("txt_sentoken_" + arg +"\\pos");

        int numOfFiles = negDir.listFiles().length + posDir.listFiles().length;

        File[] directoryList = negDir.listFiles();
        if (directoryList != null)
        {
            for (File f : directoryList)
            {
                System.out.println("DF => " + counter + "/" + numOfFiles);
                for (String key : bagOfWords)
                {
                    if (saver.tf.get(f.getName()).containsKey(key) && !df.containsKey(key))df.put(key, 1);
                    else if (saver.tf.get(f.getName()).containsKey(key) && df.containsKey(key))df.put(key, df.get(key) + 1);
                }
                counter++;
            }
        }
        directoryList = posDir.listFiles();
        if (directoryList != null)
        {
            for (File f : directoryList)
            {
                System.out.println("DF => " + counter + "/" + numOfFiles);
                for (String key : bagOfWords)
                {
                    if (saver.tf.get(f.getName()).containsKey(key) && !df.containsKey(key))df.put(key, 1);
                    else if (saver.tf.get(f.getName()).containsKey(key) && df.containsKey(key))df.put(key, df.get(key) + 1);
                }
                counter++;
            }
        }
    }

    private int writeCombinedVectorsArff(DataSaver saver, String arg, boolean test) throws IOException
    {
        double tfIdf = 0;

        int counter = 1;
        System.out.println("Writing Combined ARFF for " + arg);

        FileWriter pw = new FileWriter(new File("Combined_Vector_ARFF_" + arg +".arff"));
        File negDir = new File("txt_sentoken_" + arg +"\\neg");
        File posDir = new File("txt_sentoken_" + arg +"\\pos");

        int numOfFiles = negDir.listFiles().length + posDir.listFiles().length;

        pw.append("@RELATION tf-idf_NEG+POS_" + arg +"\n");
        for (String key : saver.bagOfWords)pw.append("@ATTRIBUTE " + key + " NUMERIC\n");
        pw.append("@ATTRIBUTE POS_NEG_0_1 {0,1}");
        pw.append("\n\n@DATA\n");

        File[] directoryListing = negDir.listFiles();
        for (File f : directoryListing)
        {
            System.out.println("ARFF => " + counter + "/" + numOfFiles);
            for (String key : saver.bagOfWords)
            {

                try
                {
                    tfIdf =  (1 + Math.log10(Double.valueOf(saver.tf.get(f.getName()).get(key)))) * Math.log10((Double.parseDouble(arg) / saver.df.get(key)));

                }
                catch (NullPointerException e)
                {
                    tfIdf = 0;
                }
                pw.append(tfIdf +",");
            }
            counter++;
            if (!test)pw.append("0\n");
            else pw.append("?\n");
        }

        directoryListing = posDir.listFiles();
        for (File f : directoryListing)
        {
            System.out.println("ARFF => " + counter + "/" + numOfFiles);
            for (String key : saver.bagOfWords)
            {
                try
                {
                    tfIdf =  (1 + Math.log10(Double.valueOf(saver.tf.get(f.getName()).get(key)))) * Math.log10((Double.parseDouble(arg) / saver.df.get(key)));
                }
                catch (NullPointerException e)
                {
                    tfIdf = 0;
                }
                pw.append(tfIdf +",");

            }
            counter++;
            if (!test)pw.append("1\n");
            else pw.append("?\n");
        }

        pw.close();
        return 1;
    }

    private int writeToCsv (Tokenizer t, int pos, File name, DataSaver saver, String arg) throws FileNotFoundException
    {
        this.wordsCounter = 0;
        String path ;
        if(pos == 0) path = "Neg_" + arg + "\\" + name.getName() +".csv";
        else path = "Pos_" + arg + "\\"  + name.getName() +".csv";
        PrintWriter pw = new PrintWriter(path);
        StringBuilder sb = new StringBuilder();
        returnHashMapCounter(t,name , saver, pos);
        sb.append("File Name ," + name.getName() + ", \n");
        sb.append("WordCount," + wordsCounter + "\n");
        this.wordCount.put(name.getName(), wordsCounter);
        sb.append("Words List \n");
        for (int i = 0 ; i < t.getWords().size() ; i ++) {
                sb.append("[" + i + "]," + t.convertListToString(t.getWords().get(i)) + "\n");
        }

        sb.append("Hashmap \n");

        for (String key: this.counter.keySet()){
            sb.append(key + "," + counter.get(key) + "\n");
        }

        pw.write(sb.toString());
        pw.close();
        return 1;
    }

    private  HashMap<String, Integer> returnHashMapCounter(Tokenizer t, File name, DataSaver saver, int posNeg)
    {

        HashMap<String,Integer> local = new HashMap<>();
        HashMap<String,Integer> localTF = new HashMap<>();
        if (!this.counter.isEmpty())this.counter.clear();
        for (int i = 0 ; i < t.getWords().size() ; i ++){
            for (int j = 0 ; j < t.getWords().get(i).size() ; j++){
                if (!t.getWords().get(i).get(j).equals(""))wordsCounter++;
                counter.put(t.getWords().get(i).get(j), 1);

                if (!bagOfWords.contains(t.getWords().get(i).get(j)))bagOfWords.add(t.getWords().get(i).get(j));
                if (posNeg == 0)
                {
                    if (!saver.df_Neg.containsKey(t.getWords().get(i).get(j)))
                    {
                        saver.df_Neg.put(t.getWords().get(i).get(j), 1);
                    }
                    else if (saver.df_Neg.containsKey(t.getWords().get(i).get(j)) && !(local.containsKey(t.getWords().get(i).get(j))))saver.df_Neg.put(t.getWords().get(i).get(j), saver.df_Neg.get(t.getWords().get(i).get(j)) + 1);
                }
                else
                {
                    if (!saver.df_Pos.containsKey(t.getWords().get(i).get(j)))
                    {
                        saver.df_Pos.put(t.getWords().get(i).get(j), 1);
                    }
                    else if (saver.df_Pos.containsKey(t.getWords().get(i).get(j)) && !(local.containsKey(t.getWords().get(i).get(j))))saver.df_Pos.put(t.getWords().get(i).get(j), saver.df_Pos.get(t.getWords().get(i).get(j)) + 1);
                }

                local.put(t.getWords().get(i).get(j),1);
                vectorList.put(name.getName(),local);

                localTF.put(t.getWords().get(i).get(j),1);
                saver.tf.put(name.getName(),localTF);




                for (int g =0 ; g < t.getWords().size() ; g++){
                    int occurrences = Collections.frequency(t.getWords().get(g),t.getWords().get(i).get(j));
                    if ((g != i ) && (occurrences != 0))
                    {
                        counter.put(t.getWords().get(i).get(j), counter.get(t.getWords().get(i).get(j)) + occurrences);
                        localTF.put(t.getWords().get(i).get(j),localTF.get(t.getWords().get(i).get(j)) + occurrences);
                        tf.put(name.getName(),localTF);
                    }
                }


            }
        }

        return counter;
    }

}
