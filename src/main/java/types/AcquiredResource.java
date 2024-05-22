package types;

import java.util.List;

public class AcquiredResource {
    private final int resource;
    private int units;

    public AcquiredResource(int resource, int units){
        this.resource = resource;
        this.units = units;
    }

    public int getResource(){
        return resource;
    }

    public void setUnits(int units){this.units = units;}

    public int getUnits(){
        return units;
    }

    public List<Integer> listFormat(){
        return List.of(resource, units);
    }
}
