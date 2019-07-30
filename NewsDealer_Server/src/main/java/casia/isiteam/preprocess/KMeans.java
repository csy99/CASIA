package casia.isiteam.preprocess;

import java.util.Random;
import org.apache.log4j.Logger;

public class KMeans {
    // 数据数量
    final int dataCount;
    // 前一维度为数据数量，后一维度为每条数据的特征数量
    final double[][] dataCoord;
    // 聚类总数量
    final int K;
    // 聚类
    private Cluster[] clusters;

    // 记录和跟踪每个资料点属于哪个群聚类。clusterAssignments[i]=j表示第 i个资料点对象属于第 j个群聚类
    final int[] clusterAssignments;
    /// 定义一个变量用于记录和跟踪每个资料点离聚类最近
    private final int[] nearestCluster;
    // 资料点到中心点的距离,其中_distanceCache[i][j]表示第i个资料点到第j个群聚对象中心点的距离；
    private final double[][] distanceCache;
    // 用来初始化的随机种子
    private static final Random rd = new Random(5);
    private static final Logger logger = Logger.getLogger(KMeans.class);

    public KMeans(double[][] data, int K) {
        dataCoord = data;
        dataCount = data.length;
        this.K = K;
        clusters = new Cluster[K];
        clusterAssignments = new int[dataCount];
        nearestCluster = new int[dataCount];
        distanceCache = new double[dataCount][K];
        InitRandom();
    }

    public void Start() {
        int iter = 0;
        while (true) {
            logger.debug("Iteration " + (iter++) + "...");
            logger.debug("cluster length: " + clusters.length);
            // 1.计算聚类均值ֵ
            for (int i = 0; i < K; i++) {
                clusters[i].UpdateMean(dataCoord);
            }

            // 2.计算数据到每一个中心的距离
            for (int i = 0; i < dataCount; i++) {
                for (int j = 0; j < K; j++) {
                    double dist = getDistance(dataCoord[i], clusters[j].Mean);
                    distanceCache[i][j] = dist;
                }
            }

            for (int i = 0; i < dataCount; i++) {
                nearestCluster[i] = nearestCluster(i);
            }

            // 4、比较每个数据最近的聚类是否就是它所属的聚类
            // 如果全相等表示所有的点已经是最佳距离了，直接返回；
            int k = 0;
            for (int i = 0; i < dataCount; i++) {
                if (nearestCluster[i] == clusterAssignments[i])
                    k++;
            }
            if (k == dataCount)
                break;

            // 5、否则需要重新调整资料点和群聚类的关系，调整完毕后再重新开始循环；
            // 需要修改每个聚类的成员和表示某个数据属于哪个聚类的变量
            for (int j = 0; j < K; j++) {
                clusters[j].CurrentMembership.clear();
            }
            for (int i = 0; i < dataCount; i++) {
                clusters[nearestCluster[i]].CurrentMembership.add(i);
                clusterAssignments[i] = nearestCluster[i];
            }
        }
    }

    // 计算某个数据离哪个聚类最近
    int nearestCluster(int ndx) {
        int nearest = -1;
        double min = Double.MAX_VALUE;
        for (int c = 0; c < K; c++) {
            double d = distanceCache[ndx][c];
            if (d < min) {
                min = d;
                nearest = c;
            }
        }
        if (nearest == -1) {
            logger.error("Something wrong with nearestCluster");
        }
        return nearest;
    }

    static double getDistance(double[] coord, double[] center) {
        return 1 - TermVector.ComputeCosineSimilarity(coord, center);
    }

    private void InitRandom() {
        for (int i = 0; i < K; i++) {
            int temp = rd.nextInt(dataCount);
            clusterAssignments[temp] = i; // 记录第temp个资料属于第i个聚类
            clusters[i] = new Cluster(temp, dataCoord[temp]);
        }
    }

    public Cluster[] getClusters() {
        return clusters;
    }
}
