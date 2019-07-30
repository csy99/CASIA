package casia.isiteam.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.ibatis.annotations.Param;
import casia.isiteam.model.News;

public interface RNewsDao {
    // allocate an id for each sub process so that each sub process will know the range of news it
    // should process
    Queue<Integer> subpro_id = new LinkedList<Integer>();

    public News getNews(long id);

    public LinkedList<News> getNewsBatch(@Param("start") int start, @Param("length") int length);

    // allocate id for each sub process
    public int getProId();

    // get all the news each process should proceed
    public LinkedList<News> allocateNews(int id);

    // server collects data after processed by each client
    public void collectResult(ArrayList<ArrayList<String>> list);
}
