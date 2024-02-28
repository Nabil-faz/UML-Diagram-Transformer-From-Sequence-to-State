import java.util.ArrayList;
import java.util.Arrays;

public class State {
    int  index;
    String name;
    String quelle;
    String ziel;
    String sendMessage;
    String recievedMessage;
    ArrayList<State> subState = new ArrayList<>();


    public State(int index,String name, String quelle, String ziel, String recievedMessage, String sendMessage) {
        this.index = index;
        this.name = name;
        this.quelle = quelle;
        this.ziel = ziel;
        this.recievedMessage= recievedMessage;
        this.sendMessage = sendMessage;

    }

    public State(int index, String name, String quelle, String ziel, String sendMessage, String recievedMessage, ArrayList<State> subState) {
        this.index = index;
        this.name = name;
        this.quelle = quelle;
        this.ziel = ziel;
        this.sendMessage = sendMessage;
        this.recievedMessage = recievedMessage;
        this.subState = subState;
    }

    @Override
    public String toString() {
        return "State{" +
                "index=" + index +
                ", name=" + name + '\''+
                ", quelle='" + quelle + '\'' +
                ", ziel='" + ziel + '\'' +
                ", recievedMessage='" + recievedMessage + '\'' +
                ", sendMessage='" + sendMessage + '\'' +
                ",Substate ='" + Arrays.toString(subState.toArray())+
                "}\n";
    }
}
