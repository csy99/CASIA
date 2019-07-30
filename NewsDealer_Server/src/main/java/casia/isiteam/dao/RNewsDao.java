package casia.isiteam.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.ibatis.annotations.Param;
import casia.isiteam.model.News;

public interface RNewsDao {
    // allocate an id for each sub process
    Queue<Integer> subpro_id = new LinkedList<Integer>();

    // get data from database with their id
    public News getNews(long id);

    // get data from database based on the size of data
    public LinkedList<News> getNewsBatch(@Param("start") int start, @Param("length") int length);

    // get data whose id is larger than the id of last processed news from database 
    public LinkedList<News> getNewsBatchWithId(@Param("id") long id, @Param("length") int length);
    
    // allocate id for each sub process
    public int getProId();

    // get all the news each process should proceed
    public LinkedList<News> allocateNews(int id);

    // server collects data after processed by each client
    public void collectResult(ArrayList<ArrayList<String>> list);
}
