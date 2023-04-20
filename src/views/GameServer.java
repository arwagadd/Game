package views;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameServer {
    ServerSocket welcomeSocket;
    ArrayList<Socket> connectionSockets;
    HashMap<Long,Client>clients ;

    public GameServer(){
        try {
            welcomeSocket = new ServerSocket(6436);
            connectionSockets = new ArrayList<>();
            clients = new HashMap<>();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void acceptConnection(){
        while (true) {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream writer = new DataOutputStream(connectionSocket.getOutputStream());
                Long id= System.currentTimeMillis();
                Client client = new Client(id,connectionSocket,reader,writer);
                clients.put(id,client);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    class Client {
        Long id;
        Socket clientSocket;
        DataOutputStream writer;
        BufferedReader reader;
        ActionsReader actionsReader;
        public Client(Long id,Socket socket, BufferedReader reader, DataOutputStream writer) {
            try {
                this.id = id;
                this.clientSocket = socket;
                this.reader = reader;
                this.writer = writer;
                actionsReader = new ActionsReader();
                actionsReader.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void writeToOtherClient(Long id,String action) { 
            try {
                Set<Map.Entry<Long,Client>> entries = clients.entrySet();
                for(Map.Entry<Long,Client> entry:entries){
                    if(entry.getKey().longValue() != id ){
                        entry.getValue().writeToClient(action);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



        public void writeToClient(String action){ 
            try {
                writer.writeBytes(action + '\n');
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        class ActionsReader extends Thread {
            @Override
            public void run() {
                String action;
                while (true) {
                    try {
                        action = reader.readLine();
                        if(action!= null&& !action.isBlank()) {
                            System.out.println("FROM Client: " + action);
                            writeToOtherClient(id,action);
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }



    public static void main(String argv[]) throws Exception {
        GameServer server = new GameServer();
        server.acceptConnection();
    }
}
