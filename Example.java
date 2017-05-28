package Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jonahschueller on 28.05.17.
 */
public class Example {

    private static StringProtocol protocol = new StringProtocol("protocol") {
        @Override
        protected void protocolSetup() {
            //The first part of the header is the key
            setKeyPos(0);
            //First part of the header is 8 byte long
            addHeaderItem(8);

            //Add a node to the Protocol
            createNode("node1",new Content<String>() {
                @Override
                public void execute(DataPacket<String> packet) {
                    System.out.println("Node 1, Data : " + new String(packet.getData()));
                }
            });

            //Add another Node
            createNode("node2",new Content<String>() {
                @Override
                public void execute(DataPacket<String> packet) {
                    System.out.println("Node 2, Data : " + new String(packet.getData()));
                }
            });
        }

        @Override
        protected String evaluate(DataPacket<String> packet) {
            return packet.get(0);
        }
    };


    private Socket socket;
    private ProtocolHandler<String> handler;


    public Example(){
        handler = new ProtocolHandler<String>(protocol);
        try {
            socket = new Socket("localhost", 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Listen on the Socket
        handler.listen(socket,new Runnable() {
            @Override
            public void run() {
                handler.stopListening();
            }
        });

        //Start the handler so the DataPackets which are received will be executed.
        handler.start();
    }


    public ProtocolHandler<String> getHandler() {
        return handler;
    }

    public static void main(String[] args) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    ServerSocket server = new ServerSocket(5000);
                    Socket socket = server.accept();

                    //Create a new ProtocolHandler with the same Protocol.
                    ProtocolHandler<String> serverHandler = new ProtocolHandler<String>(protocol);
                    //Create DataPackets to send
                    DataPacket<String> packet1 = DataPacket.stringPacketBuilder(socket, "Hello!", "node1");
                    //Send the DataPacket
                    serverHandler.send(packet1);
                    //Create DataPackets to send
                    DataPacket<String> packet2 = DataPacket.stringPacketBuilder(socket, "Hello!", "node2");
                    //Send the DataPacket
                    serverHandler.send(packet2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        Example example = new Example();
    }





}
