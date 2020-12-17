package server.command;

import server.Request;

import java.util.UUID;

public class WhisperCommand extends Command {

    private WhisperCommand() {

    }

    private static class Singleton {
        private static final WhisperCommand INSTANCE = new WhisperCommand();
    }

    public static WhisperCommand getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().length() > 9 && request.getMessage().startsWith("/whisper");
    }

    @Override
    public void handle(Request request) {
        String[] parts = request.getMessage().split(" ");
        String recipientUsername;
        if (parts.length < 2) {
            return;
        }
        recipientUsername = parts[1];
        UUID recipientID = contactManagementService.findIdByUsername(recipientUsername);
        if (recipientID == null) {
            contactManagementService.sendMessageToContact("[Server] Invalid recipient username", request.getContactID());
            return;
        }
        String name = contactManagementService.findUsernameById(request.getContactID());
        String message = "[Private] " + name + ": " + request.getMessage().substring(10 + parts[1].length());
        boolean ok = contactManagementService.sendMessageToContact(message, recipientID);
        if (!ok) {
            message = "[Server] " + recipientUsername + " is currently offline";
            contactManagementService.sendMessageToContact(message, request.getContactID());
        }
    }
}