package org.kinotic.continuum.api.security;

/**
 * Created by Navíd Mitchell 🤪on 7/11/23.
 */
public class ConnectedInfo {

    private String sessionId;

    private String replyToId;

    private Participant participant;

    public ConnectedInfo() {
    }

    public ConnectedInfo(String sessionId,
                         String replyToId,
                         Participant participant) {
        this.sessionId = sessionId;
        this.replyToId = replyToId;
        this.participant = participant;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ConnectedInfo setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * This id is the only valid "reply-to" scope that can be used by the client.
     * @return the reply to id
     */
    public String getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(String replyToId) {
        this.replyToId = replyToId;
    }

    public Participant getParticipant() {
        return participant;
    }

    public ConnectedInfo setParticipant(Participant participant) {
        this.participant = participant;
        return this;
    }

}
