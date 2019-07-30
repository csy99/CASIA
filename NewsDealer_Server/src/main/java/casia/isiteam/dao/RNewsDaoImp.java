package casia.isiteam.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import org.springframework.beans.factory.annotation.Autowired;
import casia.isiteam.model.News;
import casia.isiteam.mergeResult.*;

public class RNewsDaoImp implements RNewsDao {
    @Autowired
    private RNewsDao nd;
    // count how many times that collectResult methods are called
    private int collectCount = 0;

    public News getNews(long id) {
        return nd.getNews(id);
    }

    public LinkedList<News> getNewsBatch(int start, int length) {
        return nd.getNewsBatch(start, length);
    }

    public LinkedList<News> getNewsBatchWithId(long id, int length) {
        return nd.getNewsBatchWithId(id, length);
    }

    public int getProId() {
        return Service.allocateId();
    }

    public LinkedList<News> allocateNews(int id) {
        return Service.allocateNews(id);
    }

    public void collectResult(ArrayList<ArrayList<String>> list) {
        MergeCluster.keywordList.addAll(list);
        collectCount++;
        // this condition will be satisfied when all sub-processes have completed their jobs
        if (collectCount >= Service.TOTAL_SIZE / Service.BATCH_SIZE) {
            MergeCluster.mergeCluster();
        }
    }


}
