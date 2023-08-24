package Server;

import java.net.Socket;
import java.util.List;
import java.util.Map;

public class RequestHandlerThread extends Thread{
    private String path;
    private Socket clientSocket;
    private Map<String, List<String>> dictionary;
}
