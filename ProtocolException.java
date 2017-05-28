package Protocol;



/**
 * Created by jonahschueller on 20.04.17.
 */
public class ProtocolException extends Exception{


    public ProtocolException(Object message) {
        super("Description " + message + " not found...");
    }
    public ProtocolException(String msg){
        super(msg);
    }
    public ProtocolException(int len) {
        super("A command length of " + len + " is too short. Min/ Max. " + 8 + " chars.");
    }


}
