package ru.eternalkaif.soundsofnature.objects;

import java.util.ArrayList;

public class Soundlist {

    public ArrayList<Sound> soundList;

    public Soundlist() {
        soundList = new ArrayList<Sound>();
    }

    public ArrayList<Sound> getSoundList() {
        return soundList;
    }

    public void setSoundList(ArrayList<Sound> soundList) {
        this.soundList = soundList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("***SONGLIST***\n");
        if (soundList.isEmpty()) {
            stringBuilder.append("***NO SONGS***");
        } else {
            for (int i = 0; i < soundList.size(); i++) {
                stringBuilder.append("Song ").append(i).append(": ");
                stringBuilder.append(soundList.get(i).toString());
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
