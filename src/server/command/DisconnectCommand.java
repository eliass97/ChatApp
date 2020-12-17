package server.command;

import server.Request;

public class DisconnectCommand extends Command {

    private DisconnectCommand() {

    }

    private static class Singleton {
        private static final DisconnectCommand INSTANCE = new DisconnectCommand();
    }

    public static DisconnectCommand getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().equals("/disconnect");
    }

    @Override
    public void handle(Request request) {
        contactManagementService.sendMessageToContact("/disconnect", request.getContactID());
        contactManagementService.disconnectContact(request.getContactID());
        String message = "[Server] " + contactManagementService.findUsernameById(request.getContactID()) + " has been disconnected";
        contactManagementService.sendMessageToAllContacts(message, request.getContactID());
    }
}