/**
 * @Author: Tzvi Puchinsky
 */
package PreProcessing;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Tokenizer {

    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    private void setWholeText(String content) {
        wholeText = content;
    }

    private String wholeText;

    private SentiWord sentiwordnet;

    @SuppressWarnings("FieldCanBeLocal")
    private static char specialCharacter = '^';

    public Tokenizer(String path)
    {
        setPath(path);
        System.out.println("Building SentiWord...");
        String pathToSWN = "SentiWordNet_3.0.0_20130122.txt";
        try {
            sentiwordnet = new SentiWord(pathToSWN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> sentencesList;

    public List<String> getSentencesList() {
        return sentencesList;
    }

    public ArrayList<List<String>> getWordsList() {
        return wordsList;
    }

    public void setSentencesList(List<String> sentencesList) {
        this.sentencesList = sentencesList;
    }

    public void setWordsList(ArrayList<List<String>> wordsList) {
        this.wordsList = wordsList;
    }

    private ArrayList<List<String>> wordsList;



    private List<String> stopWords=
            Arrays.asList("a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "it", "it's", "its", "itself", "let's", "me", "more", "most", "my", "myself", "nor", "of", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", "she'll", "she's", "should", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd", "we'll", "we're", "we've", "were", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves",
                    ".", ",", ";", ":", "!", "?", "'", "\"", "-",")","(", "--", "}", "{", "`", "^","&","*","#","$","/", "%", "=", "[0-9]");

    private List<String> specialCharacters = Arrays.asList(".", ",", ";", ":", "!", "?", "'", "\"", "-",")","(", "--", "}", "{", "`", "^","&","*","#","$","/", "%", "=", "[0-9]",">","<", "\u0012" , "\u0013", "\u0014", "\u0005", "\u0000" ,"\u0001", "\u0010", "\u0008", "\u0002" ,"\u0003" , "\u000b", "\u0004" , "\u0018", "\u0010");

    private List<String> spaceWords = Arrays.asList("--", "_" , "'", "-");
    //###
    private LinkedList<String> sentences;

    public LinkedList<LinkedList<String>> getWords() {
        return words;
    }

    private LinkedList<LinkedList<String>> words = new LinkedList();

    private static String removeCode = "#ToRemove!#";
    //#########################


    public void ActivateTokenizer()
    {
        try {

            if (!this.words.isEmpty())this.words.clear();

            setWholeText(new String(Files.readAllBytes(Paths.get(path))));

            for (String r : specialCharacters)wholeText = StringUtils.remove(wholeText, r);

            for (String exp : spaceWords) {
                wholeText = wholeText.replaceAll(exp, " ");
            }

            //noinspection unchecked
            sentences = new LinkedList<String>(Arrays.asList(wholeText.split("\n")));
            for (String sentence : sentences) {
                words.add(new LinkedList<String>(Arrays.asList(sentence.split(" "))));
            }

            for (LinkedList<String> word : words) {
                for (int j = 0; j < word.size(); j++) {
                    if ((stopWords.contains(word.get(j))) || (removeNeutralWordsSentiWord(word.get(j), false) == 0) || StringUtils.isNumeric(word.get(j)) || StringUtils.isEmpty(word.get(j)) || word.get(j).length() == 1) {
                        word.set(j, removeCode);
                    }
                }
                word.removeAll(Collections.singleton(removeCode));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public String convertListToString (List<String> target)
    {
        String newString = "";
        for (String var : target) {
            newString += var + " ";
        }
        newString = newString.replace("\r",""); //Special Character that the mac added just to be sure for further work
        return newString;
    }

    private void testPrintReplace(String content)
    {
        System.out.println("## This is Test Print for eliminating \\n ## \n");
        System.out.print(content);
        System.out.println("\n################### \n");
    }

    public void testPrintSplit (List content)
    {
        System.out.println("## This is Test Print for splitting by \\. ## \n");
        for (int i = 0; i < content.size() ; i++)
        {
            System.out.print("["+ i + "] => " + content.get(i) + "\n");
        }
        System.out.println("\n################### \n");


    }

    /**
     * Converts small words with dots like Dr. takes the . converts to specialCharacter like ^ and replaces its back
     * This function not in use just saved for later if needed
     * @param data
     * ArrayList<List<String>> => words list after splitting
     * @param upperData
     * List<String> => sentences List after splitting by \n
     */
    @SuppressWarnings("unused")
    public void convertDotsToSpecialCharacter (ArrayList<List<String>> data, List<String> upperData)
    {
        System.out.println(data);
        for (List<String> aData : data) {
            for (int x = 0; x < aData.size(); x++) {
                if (((aData.get(x).length() <= 4) && (aData.get(x).length() >= 2)) && x <= (aData.size() - 2) && aData.get(x).contains(".")) {
                    aData.set(x, aData.get(x).replace('.', specialCharacter));
                }
            }
        }
    }

    private double removeNeutralWordsSentiWord(String word, boolean print)
    {

        double totalScore = 0;
        boolean flag = true;
        try {
            totalScore += this.sentiwordnet.extract(word, "a");
            flag = false;
        }
        catch (NullPointerException e)
        {
            if (print)System.out.println("Remove SentiWord Function: Word not in SentiWord data Base 'a' => " + word);
        }

        try {
            totalScore += this.sentiwordnet.extract(word, "n");
            flag = false;
        }
        catch (NullPointerException e)
        {
            if (print)System.out.println("Remove SentiWord Function: Word not in SentiWord data Base 'n' => " + word);


        }

        try {
            totalScore += this.sentiwordnet.extract(word, "r");
            flag = false;
        }
        catch (NullPointerException e)
        {
            if (print)System.out.println("Remove SentiWord Function: Word not in SentiWord data Base 'r' => " + word);
        }

        try {
            totalScore += this.sentiwordnet.extract(word, "v");
            flag = false;
        }
        catch (NullPointerException e)
        {
            if (print)System.out.println("Remove SentiWord Function: Word not in SentiWord data Base 'v' => " + word);
        }
        if (flag)
        {
            if (print)System.out.println("Not in SentiWord Dict => " + word);
            return -99; // -99 symbolizes not in dict
        }
        if (print)System.out.println("SentiWord Score for " + word + " : " + totalScore);
        return totalScore;
    }
}