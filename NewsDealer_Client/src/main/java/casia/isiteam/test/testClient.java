package casia.isiteam.test;

import java.util.LinkedList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import casia.isiteam.dao.RNewsDao;
import casia.isiteam.model.News;

/**
 * Check if every client can get data successfully from the server
 * @author CSY
 *
 */
public class testClient {
    public static void main(String[] args) {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        RNewsDao newsDao = (RNewsDao) ctx.getBean("dataExchangeProxyService");
        int proId = newsDao.getProId();
        System.out.println("This is a pro id:" + proId);
        List<News> newsList = newsDao.allocateNews(proId);
        for (News news : newsList) {
            System.out.println(news);
            System.out.println("--------------------------------------------");
        }

    }
}
