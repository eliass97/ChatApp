package server.command;

import server.Request;

public class ChangePassCommand extends Command {

    private ChangePassCommand() {

    }

    private static class Singleton {
        private static final ChangePassCommand INSTANCE = new ChangePassCommand();
    }

    public static ChangePassCommand getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().length() > 12 && request.getMessage().startsWith("/changepass");
    }

    @Override
    public void handle(Request request) {
        String newPassword = getRequestedPassword(request);
        if (!contactManagementService.isPasswordAcceptable(newPassword)) {
            contactManagementService.sendMessageToContact("[Server] Password was not accepted", request.getContactID());
            return;
        }
        boolean ok = contactManagementService.changeContactPass(request.getContactID(), newPassword);
        if (ok) {
            contactManagementService.sendMessageToContact("[Server] Password changed", request.getContactID());
        } else {
            contactManagementService.sendMessageToContact("[Server] Failed to change password", request.getContactID());
        }
    }

    private String getRequestedPassword(Request request) {
        return request.getMessage().substring(12);
    }
}