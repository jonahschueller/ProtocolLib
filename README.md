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




