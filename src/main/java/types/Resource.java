package types;

import java.util.LinkedList;
import java.util.List;

public class Resource {
    // state --> number of currently available units
    // waitlist --> list of (requesting process, units requested)
    // inventory --> 0, 1, or 2
    private int state;
    private int inventory;
    private LinkedList<Request> waitlist = new LinkedList<Request>();

    public Resource(int state, int inventory) {
        this.state = state;
        this.inventory = inventory;
    }

    public void setState(int s) {
        this.state = s;
    }

    public void setInventory(int i) {
        this.inventory = i;
    }

    public void addToWaitlist(Request r) {
        waitlist.addLast(r);
    }

    public int getState(){
        return state;
    }

    public int getInventory(){
        return inventory;
    }

    public LinkedList<Request> getWaitlist(){
        return waitlist;
    }

    public types.Request getNext (){
        return waitlist.getFirst();
    }

    public void removeFirst(){
        waitlist.pop();
    }

    public void removeRequest(int processIndex){
        for(Request r : waitlist){
           if(processIndex == r.getProcess()){
               waitlist.remove(r);
           }
        }
    }

}
