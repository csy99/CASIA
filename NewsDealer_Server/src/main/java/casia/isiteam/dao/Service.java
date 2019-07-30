package casia.isiteam.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import casia.isiteam.config.Config;
import casia.isiteam.model.News;

public class Service {
    // the sum of news we get from the database
    public static int TOTAL_SIZE;
    // the size of news that a sub process should deal with
    public static int BATCH_SIZE;
    // the index of the first News that are processed today
    public static int first_idx = 0;
    // the index of the last News that are processed today
    public static int last_idx = 0;
    // the ID of the last News that are processed today
    public static long last_id = -1;
    // the sum of news we get from the database
    private static final Logger logger = Logger.getLogger(Service.class);

    // allocate an id for each sub process so that each sub process will know the range of news it
    // should process
    public static Queue<Integer> subpro_id = null;
    public static LinkedList<News> allNews = new LinkedList<News>();

    public static void readNews(ApplicationContext ctx) {
        RNewsDao newsDao = (RNewsDao) ctx.getBean("RNewsDao");
        Config config = (Config) ctx.getBean("configBean");
        TOTAL_SIZE = config.getTotal_size();
        BATCH_SIZE = config.getBatch_size();
        first_idx = config.getStart_idx();
        last_idx += TOTAL_SIZE; // 获取处理新闻的最后一个idx
        // allNews = newsDao.getNewsBatch(first_idx, TOTAL_SIZE);
        // 从文件中获取last_id
        try {
            File file = new File("id.txt");
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                fw.write(-1 + "");
                fw.close();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            // 文件中只有一个last_id
            line = br.readLine();
            if (line == null)
                logger.error("id.txt is not kept normally");
            last_id = Long.parseLong(line);
            br.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        allNews = newsDao.getNewsBatchWithId(last_id, TOTAL_SIZE);
        last_id = allNews.getLast().getId();

        // write info into a file in case that the process is terminated
        try {
            File file = new File("id.txt");
            if (!file.exists())
                file.createNewFile();
            // append mode
            // FileWriter fw = new FileWriter(file, true);
            // 为了方便程序断掉的时候从文件中读取，直接采用重写文件的模式。即文件中只保留最新的last_id
            FileWriter fw = new FileWriter(file);
            fw.write(last_id + "");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        logger.info("all news size: " + allNews.size());
        logger.info("The last News id is: " + last_id);
        logger.info("The last News index is: " + last_idx);
        return;
    }

    /**
     * Generate Id for each subprocess (client)
     */
    public static void generateId() {
        subpro_id = new LinkedList<Integer>();
        // only generate 100 id, things might go wrong if more than 100 clients make requests
        for (int i = 1; i < 101; i++)
            subpro_id.add(i);
    }

    /**
     * initialize and allocate id to each sub-process
     * 
     * @return id
     */
    public static int allocateId() {
        // if this queue is null, that means ids have not been generated yet
        // this ensure that this queue will only be initialized once
        if (subpro_id == null) {
            logger.error("NO id is generated.");
        }
        if (allNews == null || allNews.isEmpty()) {// make sure the data is read successfully
            logger.error("NO data is acquired.");
        }
        int id = subpro_id.poll();
        return id;
    }

    /**
     * allocate different News range according to each sub-process's id
     *
     * @param id
     * @return
     */
    public static LinkedList<News> allocateNews(int id) {
        // make sure the data is read successfully
        if (subpro_id == null || allNews.size() < BATCH_SIZE) {
            logger.error("Not initialized normally.");
        }
        int startIdx = (id - 1) * BATCH_SIZE;
        LinkedList<News> tmp = new LinkedList<News>();
        // 取出所有新闻中的一部分
        for (int i = startIdx; i < startIdx + BATCH_SIZE; i++) {
            tmp.add(allNews.get(i));
        }
        return tmp;
        // 直接return会报错，因为sublist将LInkedList还原成了List类型，该类型未实现序列化
        // return allNews.subList(startIdx, startIdx + NORMAL_SIZE);
    }
}
