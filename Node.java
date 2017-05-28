package Protocol;

/**
 * Created by jonahschueller on 24.05.17.
 */
public class Node<T> {

    private T ref;

    private Content content;

    public Node(T ref, Content content) {
        this.ref = ref;
        this.content = content;
    }

    public T getRef() {
        return ref;
    }

    public Content getContent() {
            return content;
        }

    protected void setRef(T ref) {
        this.ref = ref;
    }

    protected void setContent(Content content) {
        this.content = content;
    }
}