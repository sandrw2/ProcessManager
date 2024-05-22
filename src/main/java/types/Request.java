package types;

import java.util.List;

public class Request {
    private final int process;
    private final int units;

    public Request(int process, int units){
        this.process = process;
        this.units = units;
    }

    public int getProcess(){
        return process;
    }

    public int getUnits(){
        return units;
    }

    public List<Integer> listFormat(){
        return List.of(process, units);
    }
}
