package ru.eternalkaif.soundsofnature.objects;

public class Sound {
    public String soundtitle;
    public String soundmp3link;
    public String soundjpglink;

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

    @Override
    public String toString() {
        return "Sound{" +
                "soundtitle='" + soundtitle + '\'' +
                ", soundmp3link='" + soundmp3link + '\'' +
                ", soundjpglink='" + soundjpglink + '\'' +
                '}';
    }
}
