package server.command;

import server.Request;

public class SendAllCommand extends Command {

    private SendAllCommand() {

    }

    private static class Singleton {
        private static final SendAllCommand INSTANCE = new SendAllCommand();
    }

    public static SendAllCommand getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return !request.getMessage().startsWith("/");
    }

    @Override
    public void handle(Request request) {
        String name = contactManagementService.findUsernameById(request.getContactID());
        if (name != null) {
            String message = "[All] " + name + ": " + request.getMessage();
            contactManagementService.sendMessageToAllContacts(message, request.getContactID());
        }
    }
}