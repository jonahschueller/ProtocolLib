package Protocol;

/**
 * Created by jonahschueller on 28.05.17.
 *
 * Integer based Protocol implementation;
 */
public abstract class IntegerProtocol extends Protocol<Integer> {


    public IntegerProtocol() {
    }

    public IntegerProtocol(String name) {
        super(name);
    }

    @Override
    protected Integer convert(byte[] bytes) {
        return ProtocolHandler.intFromByteArray(bytes);
    }

    @Override
    protected Integer expand(Integer val) {
        return val;
    }

    @Override
    protected byte[] convert(Integer val) {
        return ProtocolHandler.intToByteArray(val);
    }
}
