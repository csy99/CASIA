package casia.isiteam.config;

public class Config {

    private int batch_size;
    private int total_size;
    private int start_idx;


    public void setBatch_size(int per_size) {
        this.batch_size = per_size;
    }

    public int getBatch_size() {
        return batch_size;
    }

    public int getTotal_size() {
        return total_size;
    }

    public void setTotal_size(int total_size) {
        this.total_size = total_size;
    }

    public int getStart_idx() {
        return start_idx;
    }

    public void setStart_idx(int start_idx) {
        this.start_idx = start_idx;
    }
}
