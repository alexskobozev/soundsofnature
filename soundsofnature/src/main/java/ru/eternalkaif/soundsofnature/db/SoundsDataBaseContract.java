package ru.eternalkaif.soundsofnature.db;

import android.provider.BaseColumns;

public class SoundsDataBaseContract {

    public static class Sounds {
        public static final String DEFAULT_SORT = NamesColoumns.SOUNDTITLE + " DESC";

        public static final String TABLE_NAME = "sounds";

        private String soundtitle;
        private String soundmp3link;
        private String soundjpglink;
        private Boolean downloaded;

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

        public Boolean getDownloaded() {
            return downloaded;
        }

        public void setDownloaded(Boolean downloaded) {
            this.downloaded = downloaded;
        }

        public class NamesColoumns implements BaseColumns {
            public static final String SOUNDTITLE = "soundtitle";
            public static final String SOUNDMP3LINK = "soundmp3link";
            public static final String SOUNDJPGLINK = "soundjpglink";
            public static final String DOWNLOADED = "downloaded";
        }
    }

}
