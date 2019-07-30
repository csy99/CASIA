package casia.isiteam.starter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import casia.isiteam.dao.RNewsDao;
import casia.isiteam.model.News;
import casia.isiteam.preprocess.Cluster;
import casia.isiteam.preprocess.KMeans;
import casia.isiteam.preprocess.TFIDFMeasure;
import casia.isiteam.preprocess.Tokenizer;
import casia.isiteam.util.Language;
import casia.isiteam.util.Path;

public class ClientStarter {

    // the specific id of this client (allocated by Server)
    private static int pro_Id;
    // record info and error
    private static final Logger logger = Logger.getLogger(ClientStarter.class);

    /**
     * 将list中的元素写入指定地点，每个文本的命名方式为id+title
     * 
     * @param list
     * @param id
     * @param title
     */
    public static void writeToFile(List<String> list, long id, String title) {
        BufferedWriter bw = null;

        try {
            String fileName = id + ".txt";
            File filedir = new File(Path.path + pro_Id);
            String filePath = Path.path + pro_Id + "/" + fileName;
            if (!filedir.exists())// 如果这一级文件夹不存在，新创建一个
                filedir.mkdirs();
            bw = new BufferedWriter(new FileWriter(filePath));
            for (int i = 0; i < list.size(); i++) {
                bw.write(list.get(i) + " ");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        int K = 7;// 聚类数量
        ApplicationContext ctx = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        RNewsDao newsDao = (RNewsDao) ctx.getBean("dataExchangeProxyService");
        int proId = newsDao.getProId();
        pro_Id = proId;
        logger.info("This is a pro id:" + proId);

        // 对每一条新闻进行分词，之后存到一个文本中，命名方式为id+title
        List<News> newsList = newsDao.allocateNews(proId);
        logger.info("Here is the size of news batch " + newsList.size());
        ArrayList<String[]> fileContent = new ArrayList<String[]>();

        for (News news : newsList) {
            String text = news.getTitle() + " " + news.getContent();
            if (!Language.isChineseOrEnglish(text)) {// 不处理中文和英文以外的新闻
                continue;
            }
            List<String> tokenizedStr = Tokenizer.tokenize(text, true);
            // 为了符合接口所进行的转换
            String[] tmp = tokenizedStr.toArray(new String[tokenizedStr.size()]);
            fileContent.add(tmp);
            // 如果是对所有数据进行tf-idf操作，需要每个节点将已经分词的数据写入文本再读取。
            // 如果仅仅对该节点所获取到的数据进行tf-idf操作，可以直接对已经分好词的数据进行处理
            // writeToFile(tokenizedStr, news.getId(), news.getTitle());
        }
        logger.info("client " + pro_Id + " finished tokenization and writing!");

        // 使用tf-idf提取向量
        TFIDFMeasure tf = new TFIDFMeasure(fileContent);
        logger.info("get tf..");
        // 生成k-means的输入数据，是一个联合数组，第一维表示文档个数，第二维表示所有文档分出来的所有词
        int docCount = fileContent.size(); // 文档个数
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

        // 外层的ArrayList是一个聚类，内层的ArrayList存储一个聚类中的关键词
        ArrayList<ArrayList<String>> keywordList = new ArrayList<ArrayList<String>>(K);
        // 获取聚类结果并输出
        Cluster[] clusters = kmeans.getClusters();
        for (Cluster cluster : clusters) {
            // 其中一个聚群的所有文档
            List<Integer> members = cluster.CurrentMembership;
            ArrayList<String> clusterKeyword = new ArrayList<String>();
            System.out.println("-----------------");
            for (int idx : members) {
                String[] NewsContent = fileContent.get(idx);
                // 只取出每篇文章前20词存储（因为包含标题关键词，比较重要）
                for (int i = 0; i < Math.min(20, NewsContent.length); i++) {
                    System.out.println(NewsContent[i] + " ");
                    clusterKeyword.add(NewsContent[i]);
                }
                System.out.println();
            }
            keywordList.add(clusterKeyword);
        }
        logger.info("client " + pro_Id + " finished cluster!");

        // 将该子节点（本客户端）的关键词传输给服务端，表示处理已经完成
        newsDao.collectResult(keywordList);
        logger.info("client " + pro_Id + " uploaded data to the server!");
    }

}
