package Protocol;

import java.net.Socket;

/**
 * Created by jonahschueller on 24.05.17.
 */
public interface Content<T> {


    /**
     * In this Method should be specified what to do with the given DataPacket.
     * @param packet
     */
    void execute(DataPacket<T> packet);

}
