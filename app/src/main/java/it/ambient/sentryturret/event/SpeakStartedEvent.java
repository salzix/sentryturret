package it.ambient.sentryturret.event;

public class SpeakStartedEvent {
    public final int audioSessionId;

    public SpeakStartedEvent(int audioSessionId) {
        this.audioSessionId = audioSessionId;
    }

    /**
     * Basic getter
     * @return int
     */
    public int getAudioSessionId() {
        return this.audioSessionId;
    }
}