package types;
import java.util.ArrayList;
import java.util.LinkedList;

public class Process {
    // Priority: 0 < 1 < 2 (0 is reserved for init process)
    // State: 1 = ready, 0 = blocked
    // Note: Running process is implied by putting running process at the head of
    // the RL
    private int priority;
    private int state;
    private Integer parent;
    private LinkedList<Integer> children = new LinkedList<Integer>();
    private LinkedList<AcquiredResource> resources = new LinkedList<>();

    // constructor
    public Process(int priority, int state, Integer parent) {
        this.priority = priority;
        this.state = state;
        this.parent = parent;
    }

    public void setState(int s){
        state = s;
    }
    public void addChildren(Integer p) {
        children.add(p);
    }

    public void removeChildren(Integer p) {
        children.remove(p);
    }

    public void addResource(int r, int u) {
        if(findResource(r) == null){
            resources.add(new AcquiredResource(r,u));
        }else{
            AcquiredResource acquiredResource = findResource(r);
            acquiredResource.setUnits(acquiredResource.getUnits()+u);
        }

    }

    public void removeResource(int r, int u){
        for(types.AcquiredResource resource: resources){
            if(resource.getResource() == r){
                // if remove all held units of resource
                if(resource.getUnits() == u){
                    resources.remove(resource);
                }else{
                    //if remove partial held units of resource
                    resource.setUnits(resource.getUnits() - u);
                }
            }
        }
    }

    public Integer getParent(){return parent;}

    public LinkedList<Integer> getChildren(){return children;}

    public LinkedList<AcquiredResource> getResources(){return resources;}

    public int getPriority(){return priority;}

    public int getState(){return state;}

    public AcquiredResource findResource(int r){
        for(AcquiredResource resource: resources){
            if(resource.getResource() == r){
                return resource;
            }
        }
        return null;
    }

}
