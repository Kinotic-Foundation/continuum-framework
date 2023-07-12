package org.kinotic.continuum.api.security;

/**
 * Created by Navíd Mitchell 🤪on 7/11/23.
 */
public class ConnectedInfo {

    private String sessionId;

    private Participant participant;

    public ConnectedInfo() {
    }

    public ConnectedInfo(String sessionId, Participant participant) {
        this.sessionId = sessionId;
        this.participant = participant;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ConnectedInfo setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Participant getParticipant() {
        return participant;
    }

    public ConnectedInfo setParticipant(Participant participant) {
        this.participant = participant;
        return this;
    }

}
