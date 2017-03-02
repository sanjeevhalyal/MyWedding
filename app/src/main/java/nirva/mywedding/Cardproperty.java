package nirva.mywedding;

/**
 * Created by sanje on 28-02-2017.
 */

public class Cardproperty {
     String image;
    String log;
    String lat;
    String like;
    String user;
    String text;

    public Cardproperty(String image, String log, String lat, String like, String user, String text) {
        this.image = image;
        this.log = log;
        this.lat = lat;
        this.like = like;
        this.user = user;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public Cardproperty() {
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
