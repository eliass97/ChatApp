package server.command;

import server.Request;
import server.service.ContactManagementService;

public abstract class Command {

    protected final ContactManagementService contactManagementService = ContactManagementService.getInstance();

    public abstract boolean canHandle(Request request);

    public abstract void handle(Request request);
}
