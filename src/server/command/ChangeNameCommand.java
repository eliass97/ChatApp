package server.command;

import server.Request;

public class ChangeNameCommand extends Command {

    private ChangeNameCommand() {

    }

    private static class Singleton {
        private static final ChangeNameCommand INSTANCE = new ChangeNameCommand();
    }

    public static ChangeNameCommand getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().length() > 12 && request.getMessage().startsWith("/changename");
    }

    @Override
    public void handle(Request request) {
        String newUsername = getRequestedUsername(request);
        if (!contactManagementService.isUsernameAcceptable(newUsername)) {
            contactManagementService.sendMessageToContact("[Server] Username was not accepted", request.getContactID());
            return;
        }
        if (contactManagementService.contactExists(newUsername)) {
            contactManagementService.sendMessageToContact("[Server] This username is already in use", request.getContactID());
            return;
        }
        boolean ok = contactManagementService.changeContactName(request.getContactID(), newUsername);
        if (ok) {
            contactManagementService.sendMessageToContact("[Server] Username changed", request.getContactID());
        } else {
            contactManagementService.sendMessageToContact("[Server] Failed to change username", request.getContactID());
        }
    }

    private String getRequestedUsername(Request request) {
        return request.getMessage().substring(12);
    }
}