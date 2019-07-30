package casia.isiteam.preprocess;

/**
 * Vector operations.
 * 
 * @author CSY
 *
 */
public class TermVector {
    public static double ComputeCosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length)
            try {
                throw new Exception("DIFFER LENGTH");
            } catch (Exception e) {
                e.printStackTrace();
            }
        double vec1 = VectorLength(vector1);
        double vec2 = VectorLength(vector2);
        double denom = (vec1 * vec2);
        if (denom == 0D)
            return 0D;
        else
            return (InnerProduct(vector1, vector2) / denom);

    }

    public static double InnerProduct(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length)
            try {
                throw new Exception("DIFFER LENGTH ARE NOT ALLOWED");
            } catch (Exception e) {
                e.printStackTrace();
            }

        double result = 0D;
        for (int i = 0; i < vector1.length; i++) {
            // 实际运行中发现vector中一些项是NaN，进行处理
            if (!Double.isNaN(vector1[i]) && !Double.isNaN(vector2[i])) {
                result += vector1[i] * vector2[i];
            }
        }
        return result;
    }

    public static double VectorLength(double[] vector) {
        double sum = 0.0D;
        for (int i = 0; i < vector.length; i++) {
            // 实际运行中发现vector中一些项是NaN，进行处理
            if (!Double.isNaN(vector[i])) {
                sum = sum + (vector[i] * vector[i]);
            }
        }
        if (Double.isNaN(sum)) { // 正常情况不应该进入这个block
            System.err.println("The sum is still NaN after the trick.");
            sum = 1e-20;
        }
        return (double) Math.sqrt(sum);
    }
}

