import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import types.AcquiredResource;
import types.Process;
import types.Resource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {

    /*
     * basically restarts simulation
     * Set the priority levels in RL
     * All PCB entries are initialized to free except PCB[0].
     * PCB[0] is initialized to be a running process with no parent, no children,
     * and no resources.
     * All RCB entries are initialized to free.
     * RL contains process 0
     */
    // Every Level is a FIFO queue
    // RL = [[0] --> Linked list<Int>,
    // [1] --> Linked list<Int>,
    // [2] --> Linked list<Int>]

    static final int MAX_PCB_SIZE = 16;
    static final int MAX_RCB_SIZE = 4;
    static final int READY = 1;
    static final int BLOCKED = 0;
    private ArrayList<LinkedList<Integer>> RL;
    private ArrayList<Process> PCB;
    private ArrayList<Resource> RCB;

    private int levels;
    private boolean DEBUG = false;

    BufferedWriter writer;
    public Simulation() {
        String filePath = "output.txt";
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                // Create a new file
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            System.out.println("Could not create writer");
        }
    }

    int init(int levels, int r0, int r1, int r2, int r3) {
        // reset all variables
        this.levels = levels;
        RL = new ArrayList<LinkedList<Integer>>(levels);
        PCB = new ArrayList<Process>(MAX_PCB_SIZE);
        RCB = new ArrayList<Resource>(MAX_RCB_SIZE);

        // PCB ----
        // Initialize  all PCB entries to be free if there are any
        // Initialize PCB[0] running with no parent, children, or resources
        Process processZero = new Process(0, READY, null);
        PCB.add(processZero);

        // RCB ----
        // Set all types.Resource inventory
        // Set all Resources to be available
        // Set all Resource waitlists to be null
        RCB.add(new Resource(r0, r0));
        RCB.add(new Resource(r1, r1));
        RCB.add(new Resource(r2, r2));
        RCB.add(new Resource(r3, r3));
        // DEBUG: Print RCB to check for clearing

        // RL ----
        // RL lowest level, 0, contains 1 process (process 0)
        // priority levels 1 and 2 are empty
        for(int i = 0; i<levels; i++){
            RL.add( new LinkedList<Integer>());
        }
        Integer zero = 0;
        RL.get(0).add(zero);
        return 0;
    }

    int init_default() {
        return init(3, 1, 1, 2, 3);
    }

    int create(int priority) {
        /*
         * allocate new PCB[j]
         * state = ready
         * parent = i
         * children = NULL
         * resources = NULL
         * Set priority
         * insert j into list of children of i
         * insert j into RL
         * display: “process j created”
         * call scheduler --> context switch if created process has higher priority than
         * currently running process
         */
        if(priority >= levels){
            return 1;
        }
        // create new process p
        Integer parent = get_running_process();
        Process p = new Process(priority, READY, parent);
        // add into PCB[j]
        PCB.add(p);
        Integer child = PCB.indexOf(p);
        // insert j into list of children of i
        set_children(parent, child);
        // insert j into end of priority level of RL
        RL.get(priority).add(child);

        if(DEBUG){
            // display message
            String message = String.format("Process %d created", child);
            System.out.println(message);
        }
        // call scheduler for possible context switch
        return 0;

    }

    int destroy(int processIndex, int parent) {
        Process parentProcess = PCB.get(parent);
        //error checking
        if(!PCBContains(processIndex)||(!parentProcess.getChildren().contains(processIndex) && processIndex != parent)){
            return 1;
        }else {
            Process processP = PCB.get(processIndex);
            int num_destroyed = 0;
            // remove p from parent's list
            parentProcess.removeChildren(Integer.valueOf(processIndex));

            // remove p from RL or waiting list
            LinkedList<Integer> level = RL.get(processP.getPriority());
            level.remove(Integer.valueOf(processIndex));

            // release all resources of p
            while (!processP.getResources().isEmpty()) {
                types.AcquiredResource resource = processP.getResources().getFirst();
                destroyRelease(processIndex, resource.getResource(), resource.getUnits());
            }

            // get rid of p on all resource waitlists
            for (types.Resource r : RCB) {
                r.removeRequest(processIndex);
            }

            // Destroy from old --> youngest (FIFO Order)
            while (!processP.getChildren().isEmpty()) {
                // for all k in children of p destroy(k)
                destroy(processP.getChildren().getFirst(), processIndex);
            }


            // free PCB of p
            PCB.set(processIndex, null);
            num_destroyed++;

            if (DEBUG) {
                // display: “n processes destroyed”
                String message = String.format("process %d destroyed", processIndex);
                System.out.println(message);
            }

            // call scheduler --> context switch if destroyed processes releases a resource
            // that allows reactivation of process with higher priority than currently
            // running process
            //scheduler();
        }
        return 0;
    }

    int request(int resourceIndex, int units) {
        //Error checking
        Integer processIndex = get_running_process();
        types.Process process = PCB.get(processIndex);
        if(resourceIndex <0 ||
            resourceIndex>=MAX_RCB_SIZE ||
            processIndex == 0 ||
            units > RCB.get(resourceIndex).getInventory()||
            units <=0){
            return 1;
        }else{
            types.Resource resource = RCB.get(resourceIndex);
            types.AcquiredResource r = process.findResource(resourceIndex);
            if(r != null){
                if(r.getUnits() + units > resource.getInventory()){
                    return 1;
                }
            }
            // if state of r is free
            if (resource.getState() >= units) {
                // allocate to requested units to process
                resource.setState(resource.getState() - units);
                // insert r into list of resources of process i
                process.addResource(resourceIndex, units);

                if(DEBUG){
                    // display: “resource r allocated”
                    String message = String.format("resource %d allocated", resourceIndex);
                    System.out.println(message);
                }
            }else {
                // state of i = blocked
                process.setState(BLOCKED);
                //remove i from RL
                RL.get(process.getPriority()).remove(processIndex);
                //insert (i, k) into r.waitlist
                types.Request request = new types.Request(processIndex, units);
                resource.addToWaitlist(request);

                if (DEBUG) {
                    // display: “process i blocked”
                    String message = String.format("Process %d blocked", processIndex);
                    System.out.println(message);
                }
            }
            return 0;
        }
    }

    int destroyRelease(int processIndex, int resourceIndex, int unitNum) {
        types.Process p = PCB.get(processIndex);
        //error checking
        if(resourceIndex<0|| resourceIndex >= MAX_RCB_SIZE|| processIndex == 0||
        p.findResource(resourceIndex) == null||
                unitNum > p.findResource(resourceIndex).getUnits() || unitNum <=0){
            return 1;
        }else {
            types.Resource resource = RCB.get(resourceIndex);
            // remove (r,k) from resources list of process i
            p.removeResource(resourceIndex, unitNum);
            // update state of r = state of r + units released
            resource.setState(resource.getState() + unitNum);

            if (DEBUG) {
                String message = String.format("Process %d released Resource %d, %d Units", processIndex, resourceIndex, unitNum);
                System.out.println(message);
            }

            // while there is a process on waitlist
            types.Request request;
            while (!resource.getWaitlist().isEmpty() && resource.getState() > 0) {
                //get next request (j, k) from r.waitlist
                request = resource.getNext();
                //if there are enough units for allocation
                if (resource.getState() >= request.getUnits()) {
                    //update resource state after allocating
                    resource.setState(resource.getState() - request.getUnits());
                    //insert(r, k) into j.resources
                    int allocatedProcessIndex = request.getProcess();
                    types.Process allocatedProcess = PCB.get(allocatedProcessIndex);
                    allocatedProcess.addResource(resourceIndex, request.getUnits());
                    //j.state = ready
                    allocatedProcess.setState(READY);
                    //remove(j, k) from r.waitlist
                    resource.removeFirst();
                    //insert j into RL
                    RL.get(allocatedProcess.getPriority()).add(allocatedProcessIndex);

                    if (DEBUG) {
                        String message = String.format("Process %d acquired Resource %d, %d Units", allocatedProcessIndex, resourceIndex, request.getUnits());
                        System.out.println(message);
                    }

                } else {
                    break;
                }
            }
        }
        return 0;
    }

    int release(int resourceIndex, int unitNum) {
        types.Process p = PCB.get(get_running_process());
        //error checking
        if(resourceIndex<0|| resourceIndex >= MAX_RCB_SIZE|| get_running_process() == 0||
                p.findResource(resourceIndex) == null||
                unitNum > p.findResource(resourceIndex).getUnits() || unitNum <=0){
            return 1;
        }else {
            types.Resource resource = RCB.get(resourceIndex);
            // remove (r,k) from resources list of process i
            p.removeResource(resourceIndex, unitNum);
            // update state of r = state of r + units released
            resource.setState(resource.getState() + unitNum);

            if (DEBUG) {
                String message = String.format("Process %d released Resource %d, %d Units", get_running_process(), resourceIndex, unitNum);
                System.out.println(message);
            }

            // while there is a process on waitlist
            types.Request request;
            while (!resource.getWaitlist().isEmpty() && resource.getState() > 0) {
                //get next request (j, k) from r.waitlist
                request = resource.getNext();
                //if there are enough units for allocation
                if (resource.getState() >= request.getUnits()) {
                    //update resource state after allocating
                    resource.setState(resource.getState() - request.getUnits());
                    //insert(r, k) into j.resources
                    int allocatedProcessIndex = request.getProcess();
                    types.Process allocatedProcess = PCB.get(allocatedProcessIndex);
                    allocatedProcess.addResource(resourceIndex, request.getUnits());
                    //j.state = ready
                    allocatedProcess.setState(READY);
                    //remove(j, k) from r.waitlist
                    resource.removeFirst();
                    //insert j into RL
                    RL.get(allocatedProcess.getPriority()).add(allocatedProcessIndex);

                    if (DEBUG) {
                        String message = String.format("Process %d acquired Resource %d, %d Units", allocatedProcessIndex, resourceIndex, request.getUnits());
                        System.out.println(message);
                    }

                } else {
                    break;
                }
            }
            return 0;
        }
    }

    int timeout() {
        // move process i from head of RL level to end of RL level
        types.Process runningProcess  = PCB.get(get_running_process());
        LinkedList<Integer> level = RL.get(runningProcess.getPriority());
        Integer front = level.pop();
        level.add(front);
        return 0;
    }

    boolean PCBContains(int process){
        if(process>= PCB.size() || PCB.get(process) == null){
            return false;
        }else{
            return true;
        }
    }

    void scheduler() {
        // find highest priority ready process j
        // display: “process j running”
        //print_RL();
        int running_process = get_running_process();
        writeToFile(String.valueOf(running_process));
    }

    void writeToFile(String str){
        try{
            if(str.equals("\n")){
                writer.newLine();
            }else{
                writer.write(str + " ");
                writer.flush();}
        }catch(IOException e){
            System.out.println("An error occurred while writing to the file");
        }
    }

    // Helpers
    Integer get_running_process() {
        int max_level = RL.size() - 1;
        for(int i = max_level; i>=0; i--){
            if(!RL.get(i).isEmpty()){
                return RL.get(i).getFirst();
            }
        }
        return null;
    }

    void set_children(Integer parent, Integer child) {
        PCB.get(parent).addChildren(child);
    }

    // Getter functions for private class variables
    ArrayList<LinkedList<Integer>> get_RL() {
        return RL;
    }

    ArrayList<Process> get_PCB() {
        return PCB;
    }

    ArrayList<Resource> get_RCB() {
        return RCB;
    }

    // Pretty printing for Debugging
    void print_RCB() {
        System.out.println("===PRINTING RCB===");
        String message;
        int counter = 0;
        for(Resource r: RCB){
            message = String.format("Resource: %d, State: %d, Inventory: %d, Waitlist: %s",
                    counter, r.getState(), r.getInventory(), print_waitlist(r.getWaitlist()));
            System.out.println(message);
            counter++;
        }
        System.out.println();
    }

    void print_PCB() {
        System.out.println("===PRINTING PCB===");
        String message;
        int counter = 0;
        for(Process p: PCB){
            if(p!=null){
                message = String.format("Process: %d, Priority: %d, State: %d, Parent: %d, " +
                                "Children: %s, Resources: %s", counter, p.getPriority(), p.getState(),
                        p.getParent(), p.getChildren(), print_resources(p.getResources()));
                System.out.println(message);
            }else{
                message = String.format("null");
                System.out.println(message);
            }

            counter++;
        }
        System.out.println();

    }

    StringBuilder print_resources(LinkedList<AcquiredResource> resources){
        StringBuilder message = new StringBuilder();
        if(!resources.isEmpty()){
            for(types.AcquiredResource resource: resources){
                message.append(String.format("(%d, %d)  ", resource.getResource(), resource.getUnits()));
            }
        }else{
            message.append("[]");
        }

        return message;
    }

    StringBuilder print_waitlist(LinkedList<types.Request> waitlist){
        StringBuilder message = new StringBuilder();
        if(!waitlist.isEmpty()){
            for(types.Request request: waitlist){
                message.append(String.format("(%d, %d)  ", request.getProcess(), request.getUnits()));
            }
        }else{
            message.append("[]");
        }

        return message;
    }

    void print_RL() {
        System.out.println("===PRINTING RL===");
        int counter = 0;
        String message;
        for (LinkedList<Integer> level : RL){
            message = String.format("Level %d: ", counter);
            System.out.println(message);
            StringBuilder process_num = new StringBuilder();
            for(Integer process: level){
                process_num.append(String.format("%d  ", process));
            }
            System.out.println(process_num);
            counter++;
        }
        System.out.println();
    }
}
