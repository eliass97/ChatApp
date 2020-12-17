package server;

import server.command.ChangeNameCommand;
import server.command.ChangePassCommand;
import server.command.Command;
import server.command.DisconnectCommand;
import server.command.SendAllCommand;
import server.command.WhisperCommand;
import server.service.ContactManagementService;
import server.service.RequestManagementService;

import java.util.List;
import java.util.Optional;

public class ServerRequestProcessThread extends Thread {

    private final ContactManagementService contactManagementService = ContactManagementService.getInstance();
    private final RequestManagementService requestManagementService = RequestManagementService.getInstance();
    private final List<Command> commands = List.of(
            ChangeNameCommand.getInstance(),
            DisconnectCommand.getInstance(),
            WhisperCommand.getInstance(),
            ChangePassCommand.getInstance(),
            SendAllCommand.getInstance()
    );
    private static final int REQUEST_POLLING_INTERVAL = 500;

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while (true) {
            loopPause();
            if (requestManagementService.getRequests().isEmpty()) {
                continue;
            }
            Request request = requestManagementService.getRequests().remove();
            Optional<Command> command = commands.stream()
                    .filter(c -> c.canHandle(request))
                    .findFirst();
            if (command.isEmpty()) {
                contactManagementService.sendMessageToContact("[Server] Unknown command", request.getContactID());
            } else {
                command.get().handle(request);
            }
        }
    }

    private void loopPause() {
        try {
            Thread.sleep(REQUEST_POLLING_INTERVAL);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}