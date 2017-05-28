package Protocol;

/**
 * Created by jonahschueller on 24.05.17.
 */
public class KeyAlreadyInUseException extends Exception{

    public KeyAlreadyInUseException(Object message) {
        super("Node-reference " + message + " is already in use..");
    }

}
