package casia.isiteam.preprocess;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    // 索引
    public List<Integer> CurrentMembership = new ArrayList<Integer>();
    // 聚类中心
    double[] Mean;

    public Cluster(int dataindex, double[] data) {
        CurrentMembership.add(dataindex);
        Mean = data;
    }

    // 计算聚类对象的均值
    public void UpdateMean(double[][] coordinates) {
        // 根据 mCurrentMembership 取得原始资料点对象 coord，该对象是 coordinates 的一个子集；
        // 然后取出该子集的均值；取均值的算法很简单，可以把 coordinates 想象成一个 m*n 的距阵 ,
        // 每个均值就是每个纵向列的取和平均值 ，该值保存在 mCenter 中

        for (int i = 0; i < CurrentMembership.size(); i++) {
            double[] coord = coordinates[CurrentMembership.get(i)];
            for (int j = 0; j < coord.length; j++) {
                Mean[j] += coord[j]; // 纵向列的和
            }
            for (int j = 0; j < coord.length; j++) {
                Mean[j] /= coord.length; // 纵向平均值
            }
        }
    }
}

