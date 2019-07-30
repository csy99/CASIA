package casia.isiteam.mergeResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import casia.isiteam.preprocess.Cluster;
import casia.isiteam.preprocess.KMeans;
import casia.isiteam.preprocess.TFIDFMeasure;

public class MergeCluster {

    // 外层的ArrayList是一个聚类，内层的ArrayList存储一个聚类中的关键词
    public static ArrayList<ArrayList<String>> keywordList = new ArrayList<ArrayList<String>>();
    // record info and error
    private static final Logger logger = Logger.getLogger(MergeCluster.class);
    private static final int K = 10;

    public static void mergeCluster() {
        // 使用tf-idf提取向量
        TFIDFMeasure tf = new TFIDFMeasure(keywordList);
        logger.info("get tf..");
        // 生成k-means的输入数据，是一个联合数组，第一维表示文档个数，第二维表示所有文档分出来的所有词
        int docCount = keywordList.size(); // 文档个数
        double[][] data = new double[docCount][];
        // int dimension = tf.get_numTerms();// 所有词的数目
        for (int i = 0; i < docCount; i++) {
            data[i] = tf.GetTermVector(i); // 权重向量
        }
        logger.info("Tf-Idf completed");

        // 初始化k-means算法，第一个参数表示输入数据，第二个参数表示要聚成几个类
        KMeans kmeans = new KMeans(data, K);
        // 迭代
        kmeans.Start();
        logger.info("Iteration were compelted");

        // 获取聚类结果并输出
        Cluster[] clusters = kmeans.getClusters();
        try {
            File file = new File("ClusterResult.txt");
            FileWriter writer = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(writer);
            for (Cluster cluster : clusters) {
                // 其中一个聚群的所有文档
                List<Integer> members = cluster.CurrentMembership;
                System.out.println("-----------------");
                out.write("-----------------\n");
                for (int idx : members) {
                    ArrayList<String> NewsContent = keywordList.get(idx);
                    for (int i = 0; i < NewsContent.size(); i++) {
                        System.out.print(NewsContent.get(i) + " ");
                        out.write(NewsContent.get(i) + " ");
                    }
                    System.out.println();
                    out.write("\n");
                }
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            logger.error("IO error happen in Server cluster" + e.getMessage());
        }
        logger.info("Server finished cluster!");
    }
}
