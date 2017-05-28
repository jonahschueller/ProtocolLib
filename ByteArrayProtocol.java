package Protocol;

/**
 * Created by jonahschueller on 27.05.17.
 *
 * Byte array based Protocol implementation;
 */
public abstract class ByteArrayProtocol extends Protocol<byte[]>{


    public ByteArrayProtocol() {
    }

    public ByteArrayProtocol(String name) {
        super(name);
    }

    @Override
    protected byte[] convert(byte[] bytes) {
        return bytes;
    }

    @Override
    protected byte[] expand(byte[] val) {
        return expandArray(getKeyLen(), val);
    }

    private static byte[] expandArray(int newlen, byte[] bytes){
        if (newlen <= bytes.length)
            return bytes;
        byte[] arr = new byte[newlen];
        System.arraycopy(bytes, 0, arr, newlen - bytes.length, newlen);
        return arr;
    }
}
