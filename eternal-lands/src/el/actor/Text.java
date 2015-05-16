package el.actor;

import java.util.List;

public class Text {
    public int channel;
    public List<Span> spans;

    public Text(int channel, List<Span> spans) {
        this.channel = channel;
        this.spans = spans;
    }
}
