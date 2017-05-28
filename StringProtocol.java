package Protocol;

/**
 * Created by jonahschueller on 27.05.17.
 *
 * String based Protocol implementation;
 *
 *
 */
public abstract class StringProtocol extends Protocol<String>{

    public StringProtocol() {
        super();
    }

    public StringProtocol(String name) {
        super(name);
    }

    @Override
    protected String convert(byte[] bytes) {
        return new String(bytes);
    }

    @Override
    protected String expand(String val) {
        return ProtocolHandler.expand(getKeyLen(), val);
    }

    @Override
    protected byte[] convert(String val) {
        String string = val;
        return string.getBytes();
    }
}
