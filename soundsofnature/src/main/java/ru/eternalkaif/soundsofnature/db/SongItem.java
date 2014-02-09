package ru.eternalkaif.soundsofnature.db;

public class SongItem {

    private int _id;
    private String name;
    private String songUrl;
    private String jpgUrl;

    public SongItem(int _id, String name, String songUrl, String jpgUrl, int isDownloaded) {
        this._id = _id;
        this.name = name;
        this.songUrl = songUrl;
        this.jpgUrl = jpgUrl;
        this.isDownloaded = isDownloaded;
    }

    public int isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getJpgUrl() {
        return jpgUrl;
    }

    public void setJpgUrl(String jpgUrl) {
        this.jpgUrl = jpgUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    private int isDownloaded;


}
