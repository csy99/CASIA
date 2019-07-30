package casia.isiteam.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Compute Tf-Idf for each word.
 * 
 * @author CSY
 *
 */
public class TFIDFMeasure {
    private ArrayList<String[]> fileContent; // 文档内容
    private int numTerm; // 词向量总数
    private int[] docFreq; // 文章出现的频率
    private int[][] termFreq;// 词向量出现的频率
    private float[][] termWeight; // 每个词向量所占权重
    private int[] maxTermFreq; // 最大词频

    private Dictionary wordsIndex = new Hashtable();

    public TFIDFMeasure(ArrayList<String[]> fileContent) {
        this.fileContent = fileContent;
        TermInit();
    }

    /**
     * 产生词向量的频率并计算所对应权重
     */
    private void TermInit() {
        ArrayList<String> terms = GenerateTerms(fileContent);
        numTerm = terms.size();
        System.out.println("after generate,terms.size: " + numTerm);

        maxTermFreq = new int[fileContent.size()];
        docFreq = new int[numTerm];
        termFreq = new int[numTerm][];
        termWeight = new float[numTerm][];

        for (int i = 0; i < numTerm; i++) {
            termWeight[i] = new float[fileContent.size()];
            termFreq[i] = new int[fileContent.size()];
            AddElement(wordsIndex, terms.get(i), i);
        }
        GenerateTermFrequency();
        GenerateTermWeight();
    }

    /**
     * 产生权重
     */
    private void GenerateTermWeight() {
        for (int i = 0; i < numTerm; i++) {
            for (int j = 0; j < fileContent.size(); j++)
                termWeight[i][j] = ComputeTermWeight(i, j);
        }
    }

    private float ComputeTermWeight(int term, int doc) {
        float tf = GetTermFrequency(term, doc);
        float idf = GetInverseDocumentFrequency(term);
        return tf * idf;
    }

    /**
     * compute Inverse Document Frequency. The log function is based on e.
     * 
     * @param term
     * @return
     */
    private float GetInverseDocumentFrequency(int term) {
        return (float) Math.log((float) (fileContent.size()) / docFreq[term]);
    }

    private float GetTermFrequency(int term, int doc) {
        int freq = termFreq[term][doc];
        int maxfreq = maxTermFreq[doc];

        return ((float) freq / maxfreq);
    }

    private Dictionary<String, Integer> GetWordFrequency(String[] input) {
        String[] cleanText = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            cleanText[i] = input[i].toLowerCase();
        }
        Arrays.sort(cleanText);
        String[] distinctWords = GetDistinctWords(cleanText);

        Dictionary<String, Integer> result = new Hashtable<String, Integer>();
        for (int i = 0; i < distinctWords.length; i++) {
            Integer tmp = CountWords(distinctWords[i], cleanText);
            result.put(distinctWords[i], tmp);
        }

        return result;
    }

    /**
     * 统计该词在一篇文档中出现的次数
     * 
     * @param word
     * @param words
     * @return
     */
    private int CountWords(String word, String[] words) {
        int itemIdx = Arrays.binarySearch(words, word);

        if (itemIdx > 0)
            while (itemIdx > 0 && words[itemIdx].equals(word))
                itemIdx--;

        int count = 0;
        while (itemIdx < words.length && itemIdx >= 0) {
            if (words[itemIdx].equals(word))
                count++;

            itemIdx++;
            if (itemIdx < words.length && !words[itemIdx].equals(word))
                break;
        }

        return count;
    }

    /**
     * 将一个文档中的所有不重复词提取出来
     * 
     * @param input
     * @return
     */
    private String[] GetDistinctWords(String[] input) {
        if (input == null)
            return new String[0];
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < input.length; i++)
            if (!list.contains(input[i]))
                list.add(input[i]);
        String[] v = new String[list.size()];
        return (String[]) list.toArray(v);
    }

    private int GetTermIndex(String term) {
        Object index = wordsIndex.get(term);
        if (index == null)
            return -1;
        return (Integer) index;
    }

    /**
     * 统计每个词出现的频率
     */
    private void GenerateTermFrequency() {
        for (int i = 0; i < fileContent.size(); i++) {
            String[] curDoc = fileContent.get(i);
            Dictionary<String, Integer> freq = GetWordFrequency(curDoc);
            Enumeration<String> enums = freq.keys();

            while (enums.hasMoreElements()) {
                String word = enums.nextElement();
                int wordFreq = freq.get(word);
                int termIndex = GetTermIndex(word);
                if (termIndex == -1)
                    continue;
                termFreq[termIndex][i] = wordFreq;
                docFreq[termIndex]++;

                if (wordFreq > maxTermFreq[i])
                    maxTermFreq[i] = wordFreq;
            }
            maxTermFreq[i] = Integer.MIN_VALUE;
        }

    }

    /**
     * 对每个文档进行编号
     * 
     * @param collection
     * @param key
     * @param newValue
     * @return
     */
    private static Object AddElement(Dictionary collection, Object key, Object newValue) {
        Object element = collection.get(key);
        collection.put(key, newValue);
        return element;
    }

    /**
     * 将所有文档中的词统计出来
     * 
     * @param docs
     * @return
     */
    private ArrayList<String> GenerateTerms(ArrayList<String[]> fileContent) {
        ArrayList<String> uniques = new ArrayList<String>();
        for (int i = 0; i < fileContent.size(); i++) {
            String[] tmp = fileContent.get(i);
            for (int j = 0; j < tmp.length; j++)
                if (!uniques.contains(tmp[j]))
                    uniques.add(tmp[j]);
        }
        return uniques;
    }

    /**
     * 对该篇文章产生词向量
     * 
     * @param doc
     * @return
     */
    public double[] GetTermVector(int doc) {
        double[] w = new double[numTerm];
        for (int i = 0; i < numTerm; i++)
            w[i] = termWeight[i][doc];
        return w;
    }

    public int get_numTerms() {
        return numTerm;
    }

}
