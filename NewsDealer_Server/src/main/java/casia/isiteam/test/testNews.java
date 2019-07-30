package casia.isiteam.test;

import java.util.LinkedList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import casia.isiteam.dao.RNewsDao;
import casia.isiteam.model.News;

/**
 * This is a test class used to verify if we can connect to the mysql database successfully.
 * 
 * @author CSY
 *
 */
public class testNews {

    public static void main(String[] args) {

        ApplicationContext ctx = null;
        ctx = new FileSystemXmlApplicationContext("conf/ApplicationContext.xml");
        RNewsDao newsDao = (RNewsDao) ctx.getBean("NewsDao");
        News n1 = new News();

        n1 = newsDao.getNews(697294323);
        System.out.println(n1.toString());
        System.out.println("this is its id " + n1.getId());

        LinkedList<News> newsList = new LinkedList<News>();
        newsList = newsDao.getNewsBatch(0, 50);
        for (News news : newsList) {
            System.out.println(news);
            System.out.println("--------------------------------------------");
        }

        ((AbstractApplicationContext) ctx).close();
    }

}
