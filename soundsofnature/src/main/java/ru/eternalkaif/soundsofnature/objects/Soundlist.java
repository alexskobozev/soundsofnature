package ru.eternalkaif.soundsofnature.objects;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Soundlist {

    public ArrayList<Sound> soundlist;

    public Soundlist() {
        soundlist = new ArrayList<Sound>();
    }

    public ArrayList<Sound> getSoundlist() {
        return soundlist;
    }

    public void setSoundlist(ArrayList<Sound> soundlist) {
        this.soundlist = soundlist;
    }

    @NotNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("***SONGLIST***\n");
        if (soundlist.isEmpty()) {
            stringBuilder.append("***NO SONGS***");
        } else {
            for (int i = 0; i < soundlist.size(); i++) {
                stringBuilder.append("Song ").append(i).append(": ");
                stringBuilder.append(soundlist.get(i).toString());
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
