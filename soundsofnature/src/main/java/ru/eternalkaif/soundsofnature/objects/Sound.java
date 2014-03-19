package ru.eternalkaif.soundsofnature.objects;

public class Sound {
    public String soundTitle;
    public String soundMp3Link;
    public String soundJpgLink;

    public String getSoundTitle() {
        return soundTitle;
    }

    public void setSoundTitle(String soundTitle) {
        this.soundTitle = soundTitle;
    }

    public String getSoundMp3Link() {
        return soundMp3Link;
    }

    public void setSoundMp3Link(String soundMp3Link) {
        this.soundMp3Link = soundMp3Link;
    }

    public String getSoundJpgLink() {
        return soundJpgLink;
    }

    public void setSoundJpgLink(String soundJpgLink) {
        this.soundJpgLink = soundJpgLink;
    }

    @Override
    public String toString() {
        return "Sound{" +
                "soundTitle='" + soundTitle + '\'' +
                ", soundMp3Link='" + soundMp3Link + '\'' +
                ", soundJpgLink='" + soundJpgLink + '\'' +
                '}';
    }
}
