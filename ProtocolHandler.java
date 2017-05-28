package Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jonahschueller on 23.05.17.
 *
 * The Class ProtocolHandler represents a Helper-Class to read DataPackets from a Socket Connection.
 * To make a ProtocolHandler work you have to specify a Protocol with the Protocol Class.
 * With the Method listen(Socket) the DataPacket's will automatically be read from the network connection and executed.
 *
 * Example Code:
 *
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *
 * StringProtocol protocol = new StringProtocol("MyProtocol"){
 *
 *      @Override
 *      protected void protocolSetup() {
 *          setKeyPos(0);
 *          addHeaderItem(8);
 *
 *          createNode("nodeOne", packet -> {
 *              System.out.println("NodeOne got called: Data: " + new String(packet.getData()));
 *          });
 *          createNode("nodeTwo", packet -> {
 *              System.out.println("NodeTwo got called: Data: " + new String(packet.getData()));
 *          });
 *      }
 *
 *      @Override
 *      protected String evaluate(DataPacket<String> packet) {
 *          return packet.get(0);
 *      }
 * }
 *
 * ProtocolHandler<String> handler = new ProtocolHandler<String>(protocol);
 * handler.start();
 *
 * Socket socket = new Socket("localhost", 9999);
 * handler.listen(socket, ()-> handler.stopListening());
 *
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *
 * This is an example where the String based protocol has two nodes: ["nodeOne", "nodeTwo"]. The protocol has a 8 byte header and the automatically added data part.
 *
 * The handler object is the ProtocolHandler wrapper for the protocol.
 *
 *
 *
 *
 */
public class ProtocolHandler<T> implements Runnable, ProtocolConnection {

    public Protocol protocol;
    private static final char EXPAND = ':';
    public static final char SPLIT = ';';
    private Thread executer;
    private Node currentNode;
    private CopyOnWriteArrayList<DataPacket> packets;
    private boolean executing, listening, started;

    public ProtocolHandler(Protocol protocol) {
        //compatible(protocol);
        this.protocol = protocol;
        executer = new Thread(this);
        packets = new CopyOnWriteArrayList<>();
        currentNode = Protocol.getROOT();
        executing = false;
    }


    private void compatible(Protocol p){
        if (!p.getGenericName().equals(getGenericName())){
            try {
                throw new ProtocolException("Incompatible Types. Protocol<" + p.getGenericName() + "> + ProtocolHandler<" + getGenericName() + "> is incompatible");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (executing){
            return;
        }
        executing = true;
        while (executing){
            if (!packets.isEmpty()){
                taskManagment(packets.get(0));
            }else{
                taskWait();
            }
        }
    }

    private void taskManagment(DataPacket task){
        packets.remove(0);
        setup(task);
        execute(task);
    }

    private void taskWait(){
        synchronized (packets){
            try {
                packets.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setup(DataPacket packet){
        if (packet != null){
            for (int i = 0;i < protocol.getNodes().size();i++) {
                Node node = (Node) protocol.getNodes().get(i);
                if (node.getRef().equals(protocol.evaluate(packet))){
                    currentNode = node;
                    return;
                }

            }
            currentNode = Protocol.getROOT();
            try {
                 protocol.print();
                throw new ProtocolException(protocol.evaluate(packet));

            } catch (ProtocolException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Reads DataPackets out of the Socket's InputStream.
     * @param socket
     */
    @Override
    public void listen(Socket socket){
        listen(socket, ()->{});
    }


    /**
     * Sends a DataPacket to the Owner of the packet
     * @param packet
     * @see DataPacket
     */
    public void send(DataPacket packet){
        if (packet ==  null)
            return;
        try {
            protocol.sendPacket(packet, packet.getOwner());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a DataPacket from the InputStream.
     * @param stream
     * @throws IOException
     */
    public void read(InputStream stream) throws IOException {
        read(null, stream);
    }

    /**
     * Reads a DataPacket from the InputStream and sets the Owner of the Packet to the given Socket.
     * @param stream
     * @throws IOException
     */
    public void read(Socket socket, InputStream stream) throws IOException {
        DataPacket<T> packet = new DataPacket<T>(socket);
        packet = protocol.read(packet, stream);
        if (packet != null)
            addDataPacket(packet);
    }

    /**
     * Listens on the InputStream of the given Socket and creates DataPackets out of the data.
     * @param socket Network-Connection
     * @param action Method call in case of an IOException
     */
    public void listen(Socket socket, Runnable action){
        listening = true;
        Thread listen = new Thread(){
            @Override
            public void run() {
                while (listening){
                    try {
                        read(socket, socket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                        action.run();
                    }
                }
            }
        };
        listen.start();
    }

    /**
     * Stops listening for DataPackets.
     */
    public void stopListening() {
        this.listening = false;
    }


    private void execute(DataPacket task){
        currentNode.getContent().execute(task);
    }


    /**
     * Starts executing the DataPackets.
     */
    public void start(){
        if(!started) {
            started = true;
            executer.start();
        }
    }

    /**
     * Separates the String-Parts with an ':'
     * @param parts Strings to separate
     * @return
     */
    public static String content(String... parts){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < parts.length;i++) {
            String part = parts[i];
            sb.append(part);
            if (i < parts.length - 1)
                sb.append(SPLIT);
        }
        return sb.toString();
    }

    /**
     * Expands a String to the same length of of the protocols keylength.
     * @param command String to be expanded
     * @return Expanded String.
     */
    public String expand(String command){
        while (command.length() < protocol.getKeyLen()){
            command += EXPAND;
        }
        return command;
    }

    /**
     * Expands a String to the same length of of the given length.
     * @param command String to be expanded
     * @param len Length of the String
     * @return Expanded String.
     */
    public static String expand(int len, String command){
        while (command.length() < len){
            command += EXPAND;
        }
        return command;
    }

    /**
     * Adds a DataPacket to the Queue to be executed.
     * @param task
     */
    public void addDataPacket(DataPacket task){
        if (task == null){
            return;
        }
        packets.add(task);
        synchronized (packets){
            packets.notify();
        }
    }


    /**
     *
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * +                                                                                        +
     * +                                                                                        +
     * +                                   STATIC METHODS                                       +
     * +                                                                                        +
     * +                                                                                        +
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *
     */

    /**
     * Concats two byte arrays.
     * @param one
     * @param two
     * @return
     */
    public static byte[] addByteArray(byte[] one, byte[] two){
        byte[] end = new byte[one.length + two.length];
        System.arraycopy(one, 0, end, 0, one.length);
        System.arraycopy(two, 0, end, one.length, two.length);
        return end;
    }

    /**
     * Converts a long variable into a byte array.
     * @param val Long variable to be converted
     * @return byte array which represents the given variable
     */
    public static byte[] longToByteArray(long val){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(val);
        return buffer.array();
    }

    /**
     * Converts a byte array into a long variable.
     * @param bytes byte array to be converted
     * @return long variable which represents the given byte array
     */
    public static int longFromByteArray(byte[] bytes){
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return (int)buffer.getLong();
    }

    /**
     * Converts a int variable into a byte array.
     * @param val int variable to be converted
     * @return byte array which represents the given variable
     */
    public static byte[] intToByteArray(int val){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(val);
        return buffer.array();
    }

    /**
     * Converts a byte array into a int variable.
     * @param bytes byte array to be converted
     * @return int variable which represents the given byte array
     */
    public static int intFromByteArray(byte[] bytes){
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }

    private String getGenericName() {
        String className = getClass().getGenericSuperclass().getTypeName();
        System.out.println("!" + className);

        className = className.split("<", 2)[1];
        className = className.split(">")[0];
        return className;
    }

}
