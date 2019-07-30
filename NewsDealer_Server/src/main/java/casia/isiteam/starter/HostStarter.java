package casia.isiteam.starter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.log4j.Logger;
import casia.isiteam.dao.Service;

public class HostStarter {
    // the size of news that a sub process should deal with
    static int BATCH_SIZE = Service.BATCH_SIZE;
    // the sum of news we get from the database
    static int TOTAL_SIZE = Service.TOTAL_SIZE;
    // record info and error
    private static final Logger logger = Logger.getLogger(HostStarter.class);

    public static void main(String[] args) {
        ApplicationContext app_ctx = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        logger.info("RMI start with Spring......");
        Service.readNews(app_ctx);
        logger.info("Loading data successfully.");
        Service.generateId();
        logger.info("Ids Generated.");
        
        //循环调用,做成每日定时任务
        /**
         * while(true){
         *  ...
         *  Thread.sleep(1000*60*60*24);
         * }
         */
    }
}
