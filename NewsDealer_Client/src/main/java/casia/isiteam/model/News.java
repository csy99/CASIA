package casia.isiteam.model;

import java.io.Serializable;

/**
 * 数据库中映射过来的java的实例对象
 * @author CSY
 *
 */
public class News implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 5844055351745528057L;
    private long id;
    private String title;
    private String content;
    private String url;
    private int genus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getGenus() {
        return genus;
    }

    public void setGenus(int genus) {
        this.genus = genus;
    }

    public String toString(){
        return "News[id="+id+" , content="+content+" , genus="+genus+"]";
    }

}
