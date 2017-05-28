# ProtocolLib
A Java Library for Network Protocols. You just have to specify your protocol, setup a network connection and start the protocol handler. 

The Library receives automatically DataPackets from the Socket. 
All you have to do:
  - Setup your protocol with the Protocol Class (You can use the preimplemantations (String-, Integer, ByteArrayProtocol            Classes))
    - Specify the header length and your nodes
  - Create a ProtocolHandler with your protocol
  - Make the ProtocolHandler listen on your Socket and start the protocolHandler.

The file Example.java shows a example of how to use this lib. 

Classes you need are Protocol<T>, ProtocolHandler<T>, DataPacket<T>. You can find a "Documentation" below. 



Protocol Class:

The Protocol class represents a protocol for network communication.
Each Protocol consists of a header of nodes. These nodes can be created with the createNode() Method.
For each node must have a unique reference and a Content-interface-implementation.
The reference is necessary to differentiate each node. In the content-interface-implementation is defined what to do
if the reference is called.
At the end of every Protocol will automatically be added a data part.
To specify a Protocol a few things need to be set up:
 - Header parts and each length in byte (Method: addHeaderItem(byteLen))
 - node reference position in header (Method: setKeyPos(pos))
 - Each nodes with reference and content-implementation
Example Code how to set up a protocol:
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Protocol<String> protocol = new Protocol<String>("MyProtocol") {
         //In this Method it needs to be defined how to convert a byte array into the generic type of the Protocol
         //In this case the generic type is java.lang.String.
         @Override
         protected String convert(byte[] bytes) {
             return new String(bytes);
         }
         //In this Method it needs to be defined how to convert the generic type of the Protocol into a byte array
         //In this case the generic type is java.lang.String.
         @Override
         protected byte[] convert(String val) {
             return val.getBytes();
         }
    //In this Method it needs to be defined how to expand the generic type of the Protocol to the keyLength of the protocol
         //In this case the generic type is java.lang.String.
         @Override
         protected String expand(String val) {
             return ProtocolHandler.expand(getKeyLen(), val);
         }
         //This Method is used to set up the protocol.
         @Override
         protected void protocolSetup() {
             //Set the keyPosition to 0
             setKeyPos(0);
             // Add 8 byte to the header length
             addHeaderItem(8);
             //add the node "node1"
             createNode("node1", packet ->{
                 System.out.println("Node1 is called, Data: " + new String(packet.getData()));
             })
             //You could add further nodes
                 
         }
         //The Method evaluate is used to extract the protocol node reference out of a DataPacket
         @Override
         protected String evaluate(DataPacket<String> packet) {
             //In this case the node reference is the fist part of the DataPacket
             return packet.get(0);
         }
     };
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
This Protocol has a header of 8 bytes and a header, which length is variable (It is the length of the byte array from the DataPacket to send or receive (It happens automatically))









ProtocolHandler Class: 





The Class ProtocolHandler represents a Helper-Class to read DataPackets from a Socket Connection.
To make a ProtocolHandler work you have to specify a Protocol with the Protocol Class.
With the Method listen(Socket) the DataPacket's will automatically be read from the network connection and executed.
Example Code:
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
StringProtocol protocol = new StringProtocol("MyProtocol"){
     @Override
     protected void protocolSetup() {
         setKeyPos(0);
         addHeaderItem(8);
         createNode("nodeOne", packet -> {
             System.out.println("NodeOne got called: Data: " + new String(packet.getData()));
         });
         createNode("nodeTwo", packet -> {
             System.out.println("NodeTwo got called: Data: " + new String(packet.getData()));
         });
     }
     @Override
     protected String evaluate(DataPacket<String> packet) {
         return packet.get(0);
     }
}
ProtocolHandler<String> handler = new ProtocolHandler<String>(protocol);
handler.start();

//Create a network connection
Socket socket = new Socket("localhost", 9999);

//Makes the handler listen for DatPackets
handler.listen(socket, ()-> handler.stopListening());


++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
This is an example where the String based protocol has two nodes: ["nodeOne", "nodeTwo"]. The protocol has a 8 byte header and the automatically added data part.







DataPacket Class:


The Class DataPacket represents a DataPacket which is used by the class ProtocolHandler class.
DataPacket's are used to store data from the Socket or to send data through the Socket.
Example send a DataPacket:
(Based on the example code shown in the ProtocolHandler annotation)
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
Socket socket = new Socket("localhost", 9999);
Datapacket<String> packet1 = new DataPacket<String>(socket);
packet1.append("nodeOne");
packet1.setData("Hello, world!");
DataPacket<String> packet2 = DataPacket.stringPacketBuilder(socket, "Hello, world!", "nodeTwo");
//Objects initialised in the ProtocolHandler class annotation
handler.send(packet1);
handler.send(packet2);
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


















