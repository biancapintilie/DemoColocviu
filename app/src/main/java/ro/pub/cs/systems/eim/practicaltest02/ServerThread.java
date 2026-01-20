package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    private int port = 0;
    private ServerSocket serverSocket = null;

    // "Memoria" serverului (Dictionar / Cache)
    private HashMap<String, String> data = null;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("PracticalTest02", "Error: " + ioException.getMessage());
        }
        // Initializam dictionarul
        this.data = new HashMap<>();
        // Putem popula cu date de test (pt subiectul Dictionar)
        this.data.put("java", "Best language");
        this.data.put("examen", "Passed");
    }

    // Metode pentru accesarea memoriei (Cache)
    public synchronized String getData(String key) {
        return data.get(key);
    }

    public synchronized void setData(String key, String value) {
        data.put(key, value);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("PracticalTest02", "[SERVER] Waiting for connection...");
                Socket socket = serverSocket.accept();
                Log.i("PracticalTest02", "[SERVER] Connected!");

                // Acum CommunicationThread exista, deci nu mai e rosu
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException ioException) {
            Log.e("PracticalTest02", "Error: " + ioException.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try { serverSocket.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }
}