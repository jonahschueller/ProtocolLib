package Protocol;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by jonahschueller on 23.05.17.
 *
 * The Class DataPacket represents a DataPacket which is used by the class ProtocolHandler class.
 * DataPacket's are used to store data from the Socket or to send data through the Socket.
 *
 * Example send a DataPacket:
 * (Based on the example code shown in the ProtocolHandler annotation)
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *
 * Socket socket = new Socket("localhost", 9999);
 *
 * Datapacket<String> packet1 = new DataPacket<String>(socket);
 * packet1.append("nodeOne");
 * packet1.setData("Hello, world!");
 *
 * DataPacket<String> packet2 = DataPacket.stringPacketBuilder(socket, "Hello, world!", "nodeTwo");
 *
 * //Objects initialised in the ProtocolHandler class annotation
 * handler.send(packet1);
 * handler.send(packet2);
 *
 *
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */
public class DataPacket<T>{

    private ArrayList<T> header;
    private byte[] data;
    private int index, next;
    private Socket owner;

    public DataPacket(Socket owner){
        header = new ArrayList<>();
        this.owner = owner;
    }


    /**
     * Sets the data part of the DataPacket.
     * @param data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * Adds a header part to the DataPacket.
     * @param data
     */
    public void append(T data){
        header.add(data);
    }

    /**
     * Sets the header at the given index to the given data
     * @param data
     * @param i
     */
    public void set(T data ,int i){
        if (i < header.size() && i >= 0){
            header.set(i, data);
        }else{
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    /**
     * Returns a part of the header at the given index.
     * @param i
     * @return
     */
    public T get(int i){
        if (i < header.size() && i >= 0){
            return header.get(i);
        }else{
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    /**
     * Size of the header
     * @return
     */
    public int size(){
        return header.size();
    }

    public void resetNext(){
        next = 0;
    }

    public void reset(){
        index = 0;
    }

    public Socket getOwner() {
        return owner;
    }

    public ArrayList<T> getHeader() {
        return header;
    }


    /**
     * Builds a Object based DataPacket.
     * @param owner DataPacket Owner
     * @param data DataPacket data
     * @param header DataPacket header
     * @return
     */
    public static  DataPacket packetBuilder(Socket owner, byte[] data, Object... header){
        DataPacket packet = new DataPacket(owner);
        for (Object obj :
                header) {
            packet.append(obj);
        }
        packet.setData(data);
        return packet;
    }


    /**
     * Builds a String based DataPacket.
     * @param owner DataPacket Owner
     * @param data DataPacket data
     * @param header DataPacket header
     * @return
     */
    public static DataPacket<String> stringPacketBuilder(Socket owner, byte[] data, String... header){
        DataPacket<String> packet = new DataPacket(owner);
        for (String obj :
                header) {
            packet.append(obj);
        }
        packet.setData(data);
        return packet;
    }

    /**
     * Builds a String based DataPacket.
     * @param owner DataPacket Owner
     * @param data DataPacket data
     * @param header DataPacket header
     * @return
     */
    public static DataPacket<String> stringPacketBuilder(Socket owner, String data, String... header){
        return stringPacketBuilder(owner, data.getBytes(), header);
    }


    /**
     * Builds a Integer based DataPacket.
     * @param owner DataPacket Owner
     * @param data DataPacket data
     * @param header DataPacket header
     * @return
     */
    public static DataPacket<Integer> integerPacketBuilder(Socket owner, byte[] data, Integer... header){
        DataPacket<Integer> packet = new DataPacket(owner);
        for (Integer obj :
                header) {
            packet.append(obj);
        }
        packet.setData(data);
        return packet;
    }


    /**
     * Builds a byte array based DataPacket.
     * @param owner DataPacket Owner
     * @param data DataPacket data
     * @param header DataPacket header
     * @return
     */
    public static DataPacket<byte[]> byteArrayPacketBuilder(Socket owner, byte[] data, byte[]... header){
        DataPacket<byte[]> packet = new DataPacket(owner);
        for (byte[] obj :
                header) {
            packet.append(obj);
        }
        packet.setData(data);
        return packet;
    }
}
