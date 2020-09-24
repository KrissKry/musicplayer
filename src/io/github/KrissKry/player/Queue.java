package io.github.KrissKry.player;

import java.util.ArrayList;
import java.util.List;

public class Queue {

    private boolean visible = false;
    private List<String> queuedTracksName = new ArrayList<>();
    private List<String> queuedTracksPath = new ArrayList<>();
    public Queue() {}

    public void addToQueue(String track) {
        System.out.println(track);
        queuedTracksPath.add(track);
        queuedTracksName.add( track.substring( track.lastIndexOf("\\") + 1 ));
    }

    public String getNextFromQueue() {
        try {
            String temp = queuedTracksPath.get(0);
            queuedTracksName.remove(0);
            queuedTracksPath.remove(0);
            return temp;

        } catch (IndexOutOfBoundsException x) {

            return null;
        }
    }

    public List<String> getQueuedTracksName() {
        return queuedTracksName;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean getVisible() { return visible; }

    public boolean hasTracksInQueue() { return queuedTracksName.size() > 0; }
}
