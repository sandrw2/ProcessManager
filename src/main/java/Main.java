import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        String filePath = "sample-input.txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            Simulation sim = new Simulation();
            // Read each line from the file
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(" ");
                List<String> params = Arrays.stream(splitLine).toList();
                if(line.isEmpty()){
                    sim.writeToFile("\n");
                }
                // Process
                if(params.contains("id")){
                    sim.init_default();
                    if(sim.init_default() == 0){
                        sim.scheduler();
                    }else{
                        sim.writeToFile(String.valueOf(-1));
                    }
                }else if(params.contains("in")){
                    int priorityLevels = Integer.parseInt(params.get(1));
                    int r0 =  Integer.parseInt(params.get(2));
                    int r1 =  Integer.parseInt(params.get(3));
                    int r2 =  Integer.parseInt(params.get(4));
                    int r3 =  Integer.parseInt(params.get(5));
                    if(sim.init(priorityLevels, r0, r1, r2, r3) == 0){
                        sim.scheduler();
                    }else{
                        sim.writeToFile(String.valueOf(-1));
                    }

                }else if(params.contains("cr")){
                    int priority = Integer.parseInt(params.get(1));
                    if(sim.create(priority) == 0){
                        sim.scheduler();
                    }else{
                        sim.writeToFile(String.valueOf(-1));
                    }

                }else if(params.contains("de")){
                    int process = Integer.parseInt(params.get(1));
                    if(sim.destroy(process, sim.get_running_process()) == 0){
                        sim.scheduler();
                    }else{
                        sim.writeToFile(String.valueOf(-1));
                    }

                }else if(params.contains("rq")){
                    int resource = Integer.parseInt(params.get(1));
                    int units = Integer.parseInt(params.get(2));
                    if(sim.request(resource, units)== 0){
                        sim.scheduler();
                    }else{
                        sim.writeToFile(String.valueOf(-1));
                    }
                }else if(params.contains("rl")){
                    int resource = Integer.parseInt(params.get(1));
                    int units = Integer.parseInt(params.get(2));
                    if(sim.release(resource, units) == 0){
                        sim.scheduler();
                    }else{
                        sim.writeToFile(String.valueOf(-1));
                    }
                }else if(params.contains("to")){
                    if(sim.timeout()== 0){
                        sim.scheduler();
                    }else{
                        sim.writeToFile(String.valueOf(-1));
                    }
                }
            }
        } catch (IOException e) {
            // Handle any IO exceptions
            e.printStackTrace();
        }

    }
}

