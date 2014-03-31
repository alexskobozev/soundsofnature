package ru.eternalkaif.soundsofnature.objects;

import org.jetbrains.annotations.NotNull;

public class Sound {
    public String soundtitle;
    public String soundmp3link;
    public String soundjpglink;
    private boolean downloaded;

    public Sound(String soundtitle, String soundmp3link, String soundjpglink, int downloaded) {
        this.soundtitle = soundtitle;
        this.soundmp3link = soundmp3link;
        this.soundjpglink = soundjpglink;
        this.downloaded = downloaded > 0;
    }

    public Sound() {

    }

    public String getSoundtitle() {
        return soundtitle;
    }

    public void setSoundtitle(String soundtitle) {
        this.soundtitle = soundtitle;
    }

    public String getSoundmp3link() {
        return soundmp3link;
    }

    public void setSoundmp3link(String soundmp3link) {
        this.soundmp3link = soundmp3link;
    }

    public String getSoundjpglink() {
        return soundjpglink;
    }

    public void setSoundjpglink(String soundjpglink) {
        this.soundjpglink = soundjpglink;
    }

    @NotNull
    @Override
    public String toString() {
        return "Sound{" +
                "soundtitle='" + soundtitle + '\'' +
                ", soundmp3link='" + soundmp3link + '\'' +
                ", soundjpglink='" + soundjpglink + '\'' +
                '}';
    }

    public boolean isDownloaded() {
        return downloaded;
    }
}
