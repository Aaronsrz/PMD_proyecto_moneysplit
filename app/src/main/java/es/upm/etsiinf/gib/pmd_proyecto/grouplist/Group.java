package es.upm.etsiinf.gib.pmd_proyecto.grouplist;

public class Group {
    private String groupEmoji;
    private String groupName;

    public Group(String emoji, String name) {
        this.groupEmoji = emoji;
        this.groupName = name;
    }

    public String getEmoji() {
        return groupEmoji;
    }

    public String getName() {
        return groupName;
    }
}
