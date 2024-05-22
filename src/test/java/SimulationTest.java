import org.junit.*;
import types.Resource;

import java.util.*;
public class SimulationTest {
    static final int READY = 1;
    static final int BLOCKED = 0;

    @Test
    public void defaultInitTestCheckPCB(){
        Simulation sim = new Simulation();
        sim.init_default();
        ArrayList<types.Process> PCB = sim.get_PCB();

        //Check PCB: size --> 1,
        // 0 process priority --> 0,
        // 0 process state --> READY,
        // 0 process parent --> null
        // 0 process children --> []
        // 0 process resources --> []
        Assert.assertEquals(PCB.size(), 1);
        Assert.assertEquals(PCB.get(0).getPriority(), 0);
        Assert.assertEquals(PCB.get(0).getState(), READY );
        Assert.assertNull(PCB.get(0).getParent());
        Assert.assertEquals(PCB.get(0).getChildren().size(), 0 );
        Assert.assertEquals(PCB.get(0).getResources().size(), 0);
    }
    @Test
    public void defaultInitTestCheckRCB(){
        Simulation sim = new Simulation();
        sim.init_default();
        ArrayList<types.Resource> RCB = sim.get_RCB();

        // RCB size --> 4
        // resource 0 inventory/state --> 1
        // resource 1 inventory/state --> 1
        // resource 2 inventory/state --> 2
        // resource 3 inventory/state --> 3
        // Check all resources have empty waitLists

        Assert.assertEquals(RCB.size(), 4);
        Assert.assertEquals(RCB.get(0).getInventory(), 1);
        Assert.assertEquals(RCB.get(0).getState(), 1);
        Assert.assertEquals(RCB.get(1).getInventory(), 1);
        Assert.assertEquals(RCB.get(1).getState(), 1);
        Assert.assertEquals(RCB.get(2).getInventory(), 2);
        Assert.assertEquals(RCB.get(2).getState(), 2);
        Assert.assertEquals(RCB.get(3).getInventory(), 3);
        Assert.assertEquals(RCB.get(3).getState(), 3);

        for(int i = 0; i<4; i++){
            Assert.assertEquals(RCB.get(i).getWaitlist().size(), 0);
        }

    }
    @Test
    public void defaultInitTestCheckRL(){
        Simulation sim = new Simulation();
        sim.init_default();
        ArrayList<LinkedList<Integer>> RL = sim.get_RL();
        //RL size --> 3
        //RL[0] size --> 1
        //RL[1], RL[2] is empty
        //RL[0][0] --> process 0
        Assert.assertEquals(RL.size(), 3);
        Assert.assertEquals(RL.get(0).size(), 1);
        Assert.assertEquals(RL.get(1).size(), 0);
        Assert.assertEquals(RL.get(2).size(), 0);
        Assert.assertEquals((RL.get(0).getFirst()).intValue(), 0);

    }
    @Test
    public void customInitTest(){
        Simulation sim = new Simulation();
        sim.init(4,1,2,3,4);
        ArrayList<LinkedList<Integer>> RL = sim.get_RL();
        ArrayList<types.Resource> RCB = sim.get_RCB();

        Assert.assertEquals(RL.size(), 4);
        Assert.assertEquals(RCB.get(0).getInventory(), 1);
        Assert.assertEquals(RCB.get(1).getInventory(), 2);
        Assert.assertEquals(RCB.get(2).getInventory(), 3);
        Assert.assertEquals(RCB.get(3).getInventory(), 4);
    }
    @Test
    public void createTestPCB(){
        //0 --> 1 (1)
        //1 --> 2 (1), 3(2)
        //3 --> 4 (1), 5(1)
        //
        //0: 0
        //1: 1, 2, 4, 5
        //2: 3
        Simulation sim = new Simulation();
        sim.init_default();
        sim.create(1);
        sim.create(1);
        sim.create(2);
        sim.create(1);
        sim.create(1);

        ArrayList<types.Process> PCB = sim.get_PCB();

        //PCB[0]: priority = 0, state = READY, parent = null, children = [1]
        types.Process processZero = PCB.get(0);
        Assert.assertEquals(processZero.getPriority(), 0);
        Assert.assertEquals(processZero.getState(), READY);
        Assert.assertNull(processZero.getParent());
        Assert.assertEquals(processZero.getChildren().size(), 1);
        Assert.assertEquals(processZero.getChildren().getFirst().intValue(), 1);

        //PCB[1]: priority = 1, state = READY, parent = 0, children = [2, 3]
        types.Process processOne = PCB.get(1);
        Assert.assertEquals(processOne.getPriority(), 1);
        Assert.assertEquals(processOne.getState(), READY);
        Assert.assertEquals(processOne.getParent().intValue(), 0);
        Assert.assertEquals(processOne.getChildren().size(), 2);
        Assert.assertEquals(processOne.getChildren().get(0).intValue(), 2);
        Assert.assertEquals(processOne.getChildren().get(1).intValue(), 3);

        //PCB[2]: priority = 1, state = READY, parent = 1, children = []
        types.Process processTwo = PCB.get(2);
        Assert.assertEquals(processTwo.getPriority(), 1);
        Assert.assertEquals(processTwo.getState(), READY);
        Assert.assertEquals(processTwo.getParent().intValue(), 1);
        Assert.assertEquals(processTwo.getChildren().size(), 0);

        //PCB[3]: priority = 2, state = READY, parent = 1, children = [4, 5]
        types.Process processThree = PCB.get(3);
        Assert.assertEquals(processThree.getPriority(), 2);
        Assert.assertEquals(processThree.getState(), READY);
        Assert.assertEquals(processThree.getParent().intValue(), 1);
        Assert.assertEquals(processThree.getChildren().size(), 2);
        Assert.assertEquals(processThree.getChildren().get(0).intValue(), 4);
        Assert.assertEquals(processThree.getChildren().get(1).intValue(), 5);

        //PCB[4]: priority = 1, state = READY, parent = 3 children = []
        types.Process processFour = PCB.get(4);
        Assert.assertEquals(processFour.getPriority(), 1);
        Assert.assertEquals(processFour.getState(), READY);
        Assert.assertEquals(processFour.getParent().intValue(), 3);
        Assert.assertEquals(processFour.getChildren().size(), 0);
        //PCB[5]: priority = 1, state = READY, parent = 3, children = []
        types.Process processFive = PCB.get(5);
        Assert.assertEquals(processFive.getPriority(), 1);
        Assert.assertEquals(processFive.getState(), READY);
        Assert.assertEquals(processFive.getParent().intValue(), 3);
        Assert.assertEquals(processFive.getChildren().size(), 0);
    }
    @Test
    public void createTestRL(){
        Simulation sim = new Simulation();
        sim.init_default();
        sim.create(1);
        sim.create(1);
        sim.create(2);
        sim.create(1);
        sim.create(1);

        ArrayList<LinkedList<Integer>> RL = sim.get_RL();
        //Level 0 --> 0
        LinkedList<Integer> level0  = RL.get(0);
        Assert.assertEquals(level0.size(), 1);
        Assert.assertEquals(level0.getFirst().intValue(), 0);

        //Level 1 --> 1,2,4,5
        LinkedList<Integer> level1 = RL.get(1);
        Assert.assertEquals(level1.size(), 4);
        Assert.assertEquals(level1.get(0).intValue(), 1);
        Assert.assertEquals(level1.get(1).intValue(), 2);
        Assert.assertEquals(level1.get(2).intValue(), 4);
        Assert.assertEquals(level1.get(3).intValue(), 5);

        //Level 2 --> 3
        LinkedList<Integer> level2 = RL.get(2);
        Assert.assertEquals(level2.size(), 1);
        Assert.assertEquals(level2.getFirst().intValue(), 3);

    }
    @Test
    public void requestAvailableResourcePCB(){
        Simulation sim = new Simulation();
        sim.init_default();
        sim.create(1);
        sim.create(1);
        sim.create(2);
        sim.create(1);
        sim.create(1);
        sim.request(0,1);
        sim.request(2,2);
        sim.request(3, 2);

        ArrayList<types.Process> PCB = sim.get_PCB();

        //PCB[0,1,2,4,5] --> 0 resources
        Assert.assertEquals(PCB.get(0).getResources().size(), 0);
        Assert.assertEquals(PCB.get(1).getResources().size(), 0);
        Assert.assertEquals(PCB.get(2).getResources().size(), 0);
        Assert.assertEquals(PCB.get(4).getResources().size(), 0);
        Assert.assertEquals(PCB.get(5).getResources().size(), 0);

        //PCB[3] --> resource: [0,1], [2,2], [3,2]
        LinkedList<types.AcquiredResource> p3Resources = PCB.get(3).getResources();
        Assert.assertEquals(p3Resources.size(), 3);
        Assert.assertEquals(p3Resources.get(0).getResource(), 0);
        Assert.assertEquals(p3Resources.get(0).getUnits(), 1);
        Assert.assertEquals(p3Resources.get(1).getResource(), 2);
        Assert.assertEquals(p3Resources.get(1).getUnits(), 2);
        Assert.assertEquals(p3Resources.get(2).getResource(), 3);
        Assert.assertEquals(p3Resources.get(2).getUnits(), 2);
        
    }
    @Test
    public void requestAvailableResourceRCB(){
        Simulation sim = new Simulation();
        sim.init_default();
        sim.create(1);
        sim.create(1);
        sim.create(2);
        sim.create(1);
        sim.create(1);
        sim.request(0,1);
        sim.request(2,2);
        sim.request(3, 2);

        ArrayList<types.Resource> RCB = sim.get_RCB();
        //RCB[0]: state --> 0, inventory --> 1, waitList --> empty
        types.Resource r0 = RCB.get(0);
        Assert.assertEquals(r0.getState(), 0);
        Assert.assertEquals(r0.getInventory(), 1);
        Assert.assertEquals(r0.getWaitlist().size(), 0);
        //RCB[2]: state --> 0, inventory --> 2, waitList --> empty
        types.Resource r2 = RCB.get(2);
        Assert.assertEquals(r2.getState(), 0);
        Assert.assertEquals(r2.getInventory(), 2);
        Assert.assertEquals(r2.getWaitlist().size(), 0);
        //RCB[3]: state --> 1, inventory --> 3, waitList --> empty
        types.Resource r3 = RCB.get(3);
        Assert.assertEquals(r3.getState(), 1);
        Assert.assertEquals(r3.getInventory(), 3);
        Assert.assertEquals(r3.getWaitlist().size(), 0);
    }
    @Test
    public void requestUnavailableResourceRCB(){
        Simulation sim = new Simulation();
        sim.init_default();
        //switch from p0 --> p1
        sim.create(1);
        //p1 makes p2
        sim.create(1);
        //p1 acquires resource 3
        sim.request(3,2);
        //switch from p1 --> p3
        sim.create(2);
        //process 3 Blocks requesting 2 units --> RCB waitlist: (3,2), PCB[3].state = 0
        sim.request(3, 2);

        ArrayList<types.Resource> RCB = sim.get_RCB();

        //RCB[3]: state --> 1, waitlist --> [(3,2)]
        types.Resource r3 = RCB.get(3);
        Assert.assertEquals(r3.getState(), 1);
        Assert.assertEquals(r3.getWaitlist().size(), 1);
        Assert.assertEquals(r3.getWaitlist().getFirst().getProcess(), 3);
        Assert.assertEquals(r3.getWaitlist().getFirst().getUnits(), 2);

    }
    @Test
    public void requestUnavailableResourcePCB(){
        Simulation sim = new Simulation();
        sim.init_default();
        //switch from p0 --> p1
        sim.create(1);
        //p1 makes p2
        sim.create(1);
        //p1 acquires resource 3
        sim.request(3,2);
        //switch from p1 --> p3
        sim.create(2);
        //process 3 Blocks requesting 2 units --> RCB waitlist: (3,2), PCB[3].state = BLOCKED
        sim.request(3, 2);

        ArrayList<types.Process> PCB = sim.get_PCB();

        //PCB[1]: Resource --> (3,2)
        //PCB[1]: State --> READY
        types.Process p1 = PCB.get(1);
        Assert.assertEquals(p1.getResources().size(), 1);
        Assert.assertEquals(p1.getResources().getFirst().getResource(), 3);
        Assert.assertEquals(p1.getResources().getFirst().getUnits(), 2);
        Assert.assertEquals(p1.getState(), READY);
        //PCB[3]: Resource --> []
        //PCB[3]: State --> BLOCKED
        types.Process p3 = PCB.get(3);
        Assert.assertEquals(p3.getResources().size(), 0);
        Assert.assertEquals(p3.getState(), BLOCKED);
    }
    @Test
    public void requestUnavailableResourceRL(){
        Simulation sim = new Simulation();
        sim.init_default();
        //switch from p0 --> p1
        sim.create(1);
        //p1 makes p2
        sim.create(1);
        //p1 acquires resource 3
        sim.request(3,2);
        //switch from p1 --> p3
        sim.create(2);
        //process 3 Blocks requesting 2 units --> RCB waitlist: (3,2), PCB[3].state = 0
        sim.request(3, 2);

        ArrayList<LinkedList<Integer>> RL = sim.get_RL();

        Assert.assertEquals(RL.get(2).size(), 0);
    }
    public void requestNonexistentResource(){}
    public void requestAlreadyAcquiredResource(){}
    public void requestMoreThanResourceInventory(){}
    public void requestMadeFromProcess0(){}
    public void requestZeroUnitsOfResources(){}
    public void releaseResourceForLowPriorityProcess(){
        Simulation sim = new Simulation();
        sim.init_default();
        //switch from p0 --> p1
        sim.create(1);
        //p1 makes p2
        sim.create(1);
        sim.request(3,2);
        //switch from p1 --> p3
        sim.create(2);
        //Blocks 3 requesting 2 units --> (3,2)
        sim.request(3, 2);
        //p1 release 2 units from resource 3
        sim.release(3, 2);
    }
    public void releaseResourceForHigherPriorityProcess(){
        Simulation sim = new Simulation();
        sim.init_default();
        //switch from p0 --> p1
        sim.create(1);
        //p1 makes p2
        sim.create(1);
        sim.request(3,2);
        //switch from p1 --> p3
        sim.create(2);
        //Blocks 3 requesting 2 units --> (3,2)
        sim.request(3, 2);
        //p1 release 2 units from resource 3
        sim.release(3, 2);

        // Context switch 3 --> 1 --> 3
    }
    public void releaseResourceNotBeingHeld(){}
    public void releaseMoreResourceThanHeld(){}
    public void releaseNonExistingResource(){}
    public void releaseZeroUnitsOfResources(){}

    @Test
    public void destroyProcessLargeTreeOfSamePriorityLevel(){
        Simulation sim = new Simulation();
        sim.init(4,1,1,2,3);
        sim.create(1);          //L1 --> 1 | R: P0 --> P1
        sim.create(1);          //L1 --> 1, 2 | R: P1
        sim.create(1);          //L1 --> 1, 2, 3 | R:P1
        sim.timeout();          //R: P1 --> P2
        sim.create(1);          //L1 --> 2, 3, 1, 4 | R:P2
        sim.create(1);          //L1 --> 2, 3, 1, 4, 5 | R:P2
        sim.timeout();          //R: P2 --> P3
        sim.create(1);          //L1 --> 3, 1, 4, 5, 2, 6 | R:P3
        sim.timeout();          //R: P3 --> P1
        sim.timeout();          //R: P1 --> P4
        sim.create(1);          //L1 --> 4, 5, 2, 6, 3, 1, 7
        sim.create(1);          //L1 --> 4, 5, 2, 6, 3, 1, 7, 8
        sim.timeout();          //R: P4 --> P5
        sim.create(1);          //L1 --> 5, 2, 6, 3, 1, 7, 8, 4, 9
        sim.timeout();          //R: P5 --> P2
        sim.timeout();          //R: P2 --> P6
        sim.timeout();          //R: P6 --> P3
        sim.timeout();          //R: P3 --> P1

        // RL --> 1, 7, 8, 4, 9, 5, 2, 6, 3
        LinkedList<Integer> simRL = sim.get_RL().get(1);
        LinkedList<Integer> actualRL = new LinkedList<>(List.of(1, 7, 8, 4, 9, 5, 2, 6, 3));
        Assert.assertEquals(simRL, actualRL);

        //Destroy P2
        sim.destroy(2, sim.get_running_process());

        //CHECK RL
        simRL = sim.get_RL().get(1);
        actualRL = new LinkedList<Integer>(List.of(1, 6, 3));
        Assert.assertEquals(simRL, actualRL);

        //CHECK PCB
        ArrayList<types.Process> simPCB = sim.get_PCB();
        Assert.assertEquals(simPCB.get(2), null);
        Assert.assertEquals(simPCB.get(4), null);
        Assert.assertEquals(simPCB.get(5), null);
        Assert.assertEquals(simPCB.get(7), null);
        Assert.assertEquals(simPCB.get(8), null);
        Assert.assertEquals(simPCB.get(9), null);
    }

    @Test
    public void destroyRunningProcess(){}

    @Test
    public void destroyProcessLargeTreeOfSamePriorityLevelWithResources(){
        Simulation sim = new Simulation();
        sim.init(4,1,1,2,3);
        sim.create(1);          //L1 --> 1 | R: P0 --> P1
        sim.create(1);          //L1 --> 1, 2 | R: P1
        sim.create(1);          //L1 --> 1, 2, 3 | R:P1
        sim.timeout();          //R: P1 --> P2
        sim.request(0, 1);
        sim.create(1);          //L1 --> 2, 3, 1, 4 | R:P2
        sim.create(1);          //L1 --> 2, 3, 1, 4, 5 | R:P2
        sim.timeout();          //R: P2 --> P3
        sim.create(1);          //L1 --> 3, 1, 4, 5, 2, 6 | R:P3
        sim.timeout();          //R: P3 --> P1
        sim.timeout();          //R: P1 --> P4
        sim.request(1,1);
        sim.create(1);          //L1 --> 4, 5, 2, 6, 3, 1, 7
        sim.create(1);          //L1 --> 4, 5, 2, 6, 3, 1, 7, 8
        sim.timeout();          //R: P4 --> P5
        sim.request(2,1);
        sim.create(1);          //L1 --> 5, 2, 6, 3, 1, 7, 8, 4, 9
        sim.timeout();          //R: P5 --> P2
        sim.timeout();          //R: P2 --> P6
        sim.timeout();          //R: P6 --> P3
        sim.timeout();          //R: P3 --> P1
        sim.timeout();          //R: P1 --> P7
        sim.request(3, 1);
        sim.timeout();          //R: P7 --> P8
        sim.request(3, 1);
        sim.timeout();          //R: P8 --> P4
        sim.timeout();          // P4 --> P9
        sim.request(2, 1);
        sim.timeout();          //R: P9 --> P5
        sim.timeout();          //R: P5 --> P2
        sim.timeout();          //R: P2 --> P6
        sim.timeout();          //R: P6 --> P3
        sim.timeout();          //R: P3 --> P1

        //CHECK RL BEFORE--> 1, 7, 8, 4, 9, 5, 2, 6, 3
        LinkedList<Integer> simRL = sim.get_RL().get(1);
        LinkedList<Integer> actualRL = new LinkedList<>(List.of(1, 7, 8, 4, 9, 5, 2, 6, 3));
        Assert.assertEquals(simRL, actualRL);

        //CHECK PCB BEFORE
        ArrayList<types.Process> simPCB = sim.get_PCB();
        Assert.assertEquals(simPCB.get(2).getResources().size(), 1);
        Assert.assertEquals(simPCB.get(4).getResources().size(), 1);
        Assert.assertEquals(simPCB.get(5).getResources().size(), 1);
        Assert.assertEquals(simPCB.get(7).getResources().size(), 1);
        Assert.assertEquals(simPCB.get(8).getResources().size(), 1);
        Assert.assertEquals(simPCB.get(9).getResources().size(), 1);

        //CHECK RCB BEFORE
        ArrayList<types.Resource> simRCB = sim.get_RCB();
        simRCB = sim.get_RCB();
        Assert.assertEquals(simRCB.get(0).getState(), 0);
        Assert.assertEquals(simRCB.get(1).getState(), 0);
        Assert.assertEquals(simRCB.get(2).getState(), 0);
        Assert.assertEquals(simRCB.get(3).getState(), 1);

        //Destroy P2
        sim.destroy(2, sim.get_running_process());

        //CHECK RL
        simRL = sim.get_RL().get(1);
        actualRL = new LinkedList<Integer>(List.of(1, 6, 3));
        Assert.assertEquals(simRL, actualRL);

        //CHECK PCB
        simPCB = sim.get_PCB();
        Assert.assertEquals(simPCB.get(2), null);
        Assert.assertEquals(simPCB.get(4), null);
        Assert.assertEquals(simPCB.get(5), null);
        Assert.assertEquals(simPCB.get(7), null);
        Assert.assertEquals(simPCB.get(8), null);
        Assert.assertEquals(simPCB.get(9), null);

        //CHECK RCB
        simRCB = sim.get_RCB();
        Assert.assertEquals(simRCB.get(0).getState(), 1);
        Assert.assertEquals(simRCB.get(1).getState(), 1);
        Assert.assertEquals(simRCB.get(2).getState(), 2);
        Assert.assertEquals(simRCB.get(3).getState(), 3);
    }

    @Test
    public void destroyProcessLargeTreeOfDifferentPriorityLevelsWithResources(){
        Simulation sim = new Simulation();
        sim.init(4,1,1,2,3);
        sim.create(1);          //Create P1 | P0 --> P1
        sim.create(1);          //Create P2 | P1
        sim.timeout();          //P1 --> P2
        sim.create(1);          //Create P3
        sim.request(2,1);   //P3 request (2,1)
        sim.timeout();          //P2 --> P1
        sim.timeout();          //P1 -->P3
        sim.create(1);          //Create P4
        sim.request(0,1);   //P3 request (0,1)
        sim.timeout();          //P3 --> P2
        sim.timeout();          //P2 --> P1
        sim.timeout();          //P1 --> P4
        sim.request(2,1);   //P4 requests (2,1)
        sim.timeout();          //P4 --> P3
        sim.timeout();          //P3 --> P2
        sim.create(2);          //Create P5, P2 --> P5
        sim.create(1);          //Create P6
        sim.create(2);          //Create P7
        sim.request(3,2);   //P5 request (3,2)
        sim.timeout();          //P5 --> P7
        sim.create(1);          //Create P8
        sim.create(2);          //Create P9
        sim.request(1,1);   //P7 request (1,1)
        sim.timeout();          //P7 --> P5
        sim.timeout();          //P5 --> P9
        sim.create(3);          //Create P10, P9 --> P10
        sim.request(3,2);   //Block P10, P10 --> P9
        sim.request(2,1);   //Block P9, P9 --> P7
        sim.request(2,1);   //Block P7, P7--> P5
        sim.request(0,1);   //Block P5, P5 --> P2
        sim.timeout();          //P2 --> P1

        //CHECK RL BEFORE
        LinkedList<Integer> simLevel1 = sim.get_RL().get(1);
        //expected LEVEL 1 --> [1,4,3,6,8,2]
        LinkedList<Integer> expectedLevel1 = new LinkedList<Integer>(List.of(1,4,3,6,8,2));
        Assert.assertEquals(simLevel1, expectedLevel1);

        //CHECK PCB BEFORE
        ArrayList<types.Process> simPCB = sim.get_PCB();
        //P1 --> Children: 2
        Assert.assertEquals(new LinkedList<Integer>(List.of(2)), simPCB.get(1).getChildren());
        //P2 --> Children: 3,5 | Resources:(2,1)
        Assert.assertEquals(new LinkedList<Integer>(List.of(3,5)), simPCB.get(2).getChildren());
        Assert.assertEquals(List.of(2,1), simPCB.get(2).getResources().getFirst().listFormat());
        //P3 --> Children: 4 | Resources: (0,1)
        Assert.assertEquals(new LinkedList<Integer>(List.of(4)), simPCB.get(3).getChildren());
        Assert.assertEquals(List.of(0,1), simPCB.get(3).getResources().getFirst().listFormat());
        //P4 --> Children: null | Resources:(2,1)
        Assert.assertEquals(0, simPCB.get(4).getChildren().size());
        Assert.assertEquals(List.of(2,1), simPCB.get(4).getResources().getFirst().listFormat());
        //P5 --> State: BLOCKED |Children: 6, 7 | Resources: (3,2)
        Assert.assertEquals(BLOCKED, simPCB.get(5).getState());
        Assert.assertEquals(new LinkedList<Integer>(List.of(6,7)), simPCB.get(5).getChildren());
        Assert.assertEquals(List.of(3,2), simPCB.get(5).getResources().getFirst().listFormat());
        //P7 --> State: BLOCKED | Children: 8, 9, Resources --> (1,1)
        Assert.assertEquals(BLOCKED, simPCB.get(7).getState());
        Assert.assertEquals(new LinkedList<Integer>(List.of(8, 9)), simPCB.get(7).getChildren());
        Assert.assertEquals(List.of(1,1), simPCB.get(7).getResources().getFirst().listFormat());
        //P9 --> State: BLOCKED | Children: 10
        Assert.assertEquals(BLOCKED, simPCB.get(9).getState());
        Assert.assertEquals(new LinkedList<Integer>(List.of(10)), simPCB.get(9).getChildren());
        //P10 --> State: BLOCKED
        Assert.assertEquals(BLOCKED, simPCB.get(10).getState());

        //CHECK RCB BEFORE
        ArrayList<types.Resource> simRCB = sim.get_RCB();
        //r0 --> State: 0 | Waitlist: (5, 1)
        Assert.assertEquals(0, simRCB.get(0).getState());
        Assert.assertEquals(1, simRCB.get(0).getWaitlist().size());
        Assert.assertEquals(List.of(5,1), simRCB.get(0).getWaitlist().getFirst().listFormat());
        //r1 --> State: 0 | Waitlist size = 0
        Assert.assertEquals(0, simRCB.get(1).getState());
        Assert.assertEquals(0, simRCB.get(1).getWaitlist().size());
        //r2 --> State: 0 | Waitlist: (7,1), (9,1)
        Assert.assertEquals(0, simRCB.get(2).getState());
        Assert.assertEquals(2, simRCB.get(2).getWaitlist().size());
        Assert.assertEquals(List.of(9,1), simRCB.get(2).getWaitlist().get(0).listFormat());
        Assert.assertEquals(List.of(7,1), simRCB.get(2).getWaitlist().get(1).listFormat());
        //r3 --> State: 1 | Waitlist: (10, 2)
        Assert.assertEquals(1, simRCB.get(3).getState());
        Assert.assertEquals(1, simRCB.get(3).getWaitlist().size());
        Assert.assertEquals(List.of(10,2), simRCB.get(3).getWaitlist().getFirst().listFormat());

        //DESTROY
        sim.destroy(2, sim.get_running_process());

        //CHECK RL
        simLevel1 = sim.get_RL().get(1);
        LinkedList<Integer> simLevel2 = sim.get_RL().get(2);
        LinkedList<Integer> simLevel3 = sim.get_RL().get(3);

        //Level 1 --> 1
        Assert.assertEquals(new LinkedList<Integer>(List.of(1)), simLevel1);
        //Level 2 --> empty
        Assert.assertEquals(0, simLevel2.size());
        //Level 3 --> EMPTY
        Assert.assertEquals(0, simLevel3.size());

        //CHECK PCB
        simPCB = sim.get_PCB();
        //P1 --> CHILDREN: empty
        Assert.assertEquals(0, simPCB.get(1).getChildren().size());
        // All other processes null
        for(types.Process p: simPCB.subList(2, simPCB.size())){
            Assert.assertNull(p);
        }

        //CHECK RCB
        simRCB = sim.get_RCB();
        //R0 --> state = 1, waitlist = []
        Assert.assertEquals(1, simRCB.get(0).getState());
        Assert.assertEquals(0, simRCB.get(0).getWaitlist().size());

        //R1 --> state = 1, waitlist = []
        Assert.assertEquals(1, simRCB.get(1).getState());
        Assert.assertEquals(0, simRCB.get(1).getWaitlist().size());

        //R2 --> state = 2, waitlist = []
        Assert.assertEquals(2, simRCB.get(2).getState());
        Assert.assertEquals(0, simRCB.get(2).getWaitlist().size());

        //R3 --> state = 3, waitlist = []
        Assert.assertEquals(3, simRCB.get(3).getState());
        Assert.assertEquals(0, simRCB.get(3).getWaitlist().size());


    }
    public void destroyProcessThatIsNotChild(){
        Simulation sim = new Simulation();
        sim.init(4,1,1,2,3);
        sim.create(1);          //Create P1 | P0 --> P1
        sim.create(1);          //Create P2 | P1
        sim.timeout();          //P1 --> P2
        sim.create(1);          //Create P3
        sim.request(2,1);   //P3 request (2,1)
        sim.timeout();          //P2 --> P1
        sim.timeout();          //P1 -->P3
        sim.create(1);          //Create P4
        sim.request(0,1);   //P3 request (0,1)
        sim.timeout();          //P3 --> P2
        sim.timeout();          //P2 --> P1
        sim.timeout();          //P1 --> P4
        sim.request(2,1);   //P4 requests (2,1)
        sim.timeout();          //P4 --> P3
        sim.timeout();          //P3 --> P2
        sim.create(2);          //Create P5, P2 --> P5
        sim.create(1);          //Create P6
        sim.create(2);          //Create P7
        sim.request(3,2);   //P5 request (3,2)
        sim.timeout();          //P5 --> P7
        sim.create(1);          //Create P8
        sim.create(2);          //Create P9
        sim.request(1,1);   //P7 request (1,1)
        sim.timeout();          //P7 --> P5
        sim.timeout();          //P5 --> P9
        sim.create(3);          //Create P10, P9 --> P10
        sim.request(3,2);   //Block P10, P10 --> P9
        sim.request(2,1);   //Block P9, P9 --> P7
        sim.request(2,1);   //Block P7, P7--> P5
        sim.request(0,1);   //Block P5, P5 --> P2
        sim.timeout();          //P2 --> P1

        //CHECK RL BEFORE
        LinkedList<Integer> simLevel1 = sim.get_RL().get(1);
        //expected LEVEL 1 --> [1,4,3,6,8,2]
        LinkedList<Integer> expectedLevel1 = new LinkedList<Integer>(List.of(1,4,3,6,8,2));
        Assert.assertEquals(simLevel1, expectedLevel1);

        //CHECK PCB BEFORE
        ArrayList<types.Process> simPCB = sim.get_PCB();
        //P1 --> Children: 2
        Assert.assertEquals(new LinkedList<Integer>(List.of(2)), simPCB.get(1).getChildren());
        //P2 --> Children: 3,5 | Resources:(2,1)
        Assert.assertEquals(new LinkedList<Integer>(List.of(3,5)), simPCB.get(2).getChildren());
        Assert.assertEquals(List.of(2,1), simPCB.get(2).getResources().getFirst().listFormat());
        //P3 --> Children: 4 | Resources: (0,1)
        Assert.assertEquals(new LinkedList<Integer>(List.of(4)), simPCB.get(3).getChildren());
        Assert.assertEquals(List.of(0,1), simPCB.get(3).getResources().getFirst().listFormat());
        //P4 --> Children: null | Resources:(2,1)
        Assert.assertEquals(0, simPCB.get(4).getChildren().size());
        Assert.assertEquals(List.of(2,1), simPCB.get(4).getResources().getFirst().listFormat());
        //P5 --> State: BLOCKED |Children: 6, 7 | Resources: (3,2)
        Assert.assertEquals(BLOCKED, simPCB.get(5).getState());
        Assert.assertEquals(new LinkedList<Integer>(List.of(6,7)), simPCB.get(5).getChildren());
        Assert.assertEquals(List.of(3,2), simPCB.get(5).getResources().getFirst().listFormat());
        //P7 --> State: BLOCKED | Children: 8, 9, Resources --> (1,1)
        Assert.assertEquals(BLOCKED, simPCB.get(7).getState());
        Assert.assertEquals(new LinkedList<Integer>(List.of(8, 9)), simPCB.get(7).getChildren());
        Assert.assertEquals(List.of(1,1), simPCB.get(7).getResources().getFirst().listFormat());
        //P9 --> State: BLOCKED | Children: 10
        Assert.assertEquals(BLOCKED, simPCB.get(9).getState());
        Assert.assertEquals(new LinkedList<Integer>(List.of(10)), simPCB.get(9).getChildren());
        //P10 --> State: BLOCKED
        Assert.assertEquals(BLOCKED, simPCB.get(10).getState());

        //CHECK RCB BEFORE
        ArrayList<types.Resource> simRCB = sim.get_RCB();
        //r0 --> State: 0 | Waitlist: (5, 1)
        Assert.assertEquals(0, simRCB.get(0).getState());
        Assert.assertEquals(1, simRCB.get(0).getWaitlist().size());
        Assert.assertEquals(List.of(5,1), simRCB.get(0).getWaitlist().getFirst().listFormat());
        //r1 --> State: 0 | Waitlist size = 0
        Assert.assertEquals(0, simRCB.get(1).getState());
        Assert.assertEquals(0, simRCB.get(1).getWaitlist().size());
        //r2 --> State: 0 | Waitlist: (7,1), (9,1)
        Assert.assertEquals(0, simRCB.get(2).getState());
        Assert.assertEquals(2, simRCB.get(2).getWaitlist().size());
        Assert.assertEquals(List.of(9,1), simRCB.get(2).getWaitlist().get(0).listFormat());
        Assert.assertEquals(List.of(7,1), simRCB.get(2).getWaitlist().get(1).listFormat());
        //r3 --> State: 1 | Waitlist: (10, 2)
        Assert.assertEquals(1, simRCB.get(3).getState());
        Assert.assertEquals(1, simRCB.get(3).getWaitlist().size());
        Assert.assertEquals(List.of(10,2), simRCB.get(3).getWaitlist().getFirst().listFormat());

        //DESTROY
        sim.destroy(1, sim.get_running_process());

        //CHECK RL
        simLevel1 = sim.get_RL().get(1);
        LinkedList<Integer> simLevel2 = sim.get_RL().get(2);
        LinkedList<Integer> simLevel3 = sim.get_RL().get(3);

        //Level 1 --> 1
        Assert.assertEquals(0, simLevel1.size());
        //Level 2 --> empty
        Assert.assertEquals(0, simLevel2.size());
        //Level 3 --> EMPTY
        Assert.assertEquals(0, simLevel3.size());

        //CHECK PCB
        simPCB = sim.get_PCB();
        // All other processes null
        for(types.Process p: simPCB){
            Assert.assertNull(p);
        }

        //CHECK RCB
        simRCB = sim.get_RCB();
        //R0 --> state = 1, waitlist = []
        Assert.assertEquals(1, simRCB.get(0).getState());
        Assert.assertEquals(0, simRCB.get(0).getWaitlist().size());

        //R1 --> state = 1, waitlist = []
        Assert.assertEquals(1, simRCB.get(1).getState());
        Assert.assertEquals(0, simRCB.get(1).getWaitlist().size());

        //R2 --> state = 2, waitlist = []
        Assert.assertEquals(2, simRCB.get(2).getState());
        Assert.assertEquals(0, simRCB.get(2).getWaitlist().size());

        //R3 --> state = 3, waitlist = []
        Assert.assertEquals(3, simRCB.get(3).getState());
        Assert.assertEquals(0, simRCB.get(3).getWaitlist().size());


    }

}
