package Protocol;

import Protocol.ProtocolHandler;

import java.net.Socket;

/**
 * Created by jonahschueller on 20.04.17.
 */
public interface ProtocolConnection {

    void addDataPacket(DataPacket task);

    void send(DataPacket packet);

    void listen(Socket socket);
}
