
/**
 * Represents information about the server that the client is connected to
 */
export class ServerInfo {
    /**
     * The UUID for this node
     */
    public nodeId!: string
    /**
     * Returns a human-readable name for this node
     */
    public nodeName!: string
}
