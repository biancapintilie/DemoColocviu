package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Importuri pentru HTTP (Vreme, Autocomplete, Bitcoin)
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) return;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            // Citim parametrii (chiar daca nu ii folosim pe toti, ii citim ca sa nu ramana pe teava)
            String p1 = br.readLine(); // Termen 1 sau Cuvant sau Oras sau URL
            String p2 = br.readLine(); // Termen 2 sau Tip Info
            String p3 = br.readLine(); // Operator

            String result = "";

            /* ================================================================================
               ZONA DE LOGICA - DECOMENTEZI DOAR CE AI NEVOIE LA EXAMEN
               ================================================================================ */

            // --- VARIANTA 1: CALCULATOR (Default activ acum) ---
//            if (p1 != null && p2 != null && p3 != null) {
//                try {
//                    double d1 = Double.parseDouble(p1);
//                    double d2 = Double.parseDouble(p2);
//                    if (p3.equals("add") || p3.equals("+")) result = String.valueOf(d1 + d2);
//                    else if (p3.equals("mul") || p3.equals("*")) result = String.valueOf(d1 * d2);
//                    else result = "Invalid Operator";
//                } catch (Exception e) {
//                    result = "Error calculation"; // Poate a fost alt subiect
//                }
//            }

//            // --- VARIANTA 2: DICTIONAR / CACHE (Decomenteaza daca pica asta) ---
//            // Verificam daca avem raspunsul in memoria serverului
//            String cachedData = serverThread.getData(p1);
//            if (cachedData != null) {
//                result = "DIN CACHE: " + cachedData;
//            } else {
//                result = "Nu exista in dictionar!";
//                // Daca era subiectul cu URL BODY si nu e in cache, aici faceam HTTP Request si salvam
//            }


            // --- VARIANTA 3: HTTP REQUEST (VREMEA - OpenWeather) ---
            // 1. Construim URL-ul cu Orasul (p1) si Cheia API din PDF
            // Cheia din PDF: e03c3b32cfb5a6f7069f2ef29237d87e
            String oras = p1;
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + oras + "&appid=e03c3b32cfb5a6f7069f2ef29237d87e&units=metric";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            String pageSourceCode = httpClient.execute(httpGet, new BasicResponseHandler());

            // Returnam tot JSON-ul (ca sa fie simplu si sigur)
            result = pageSourceCode;

            // Daca vrei sa fii "fancy" si sa parsezi (doar daca ai timp):
            /*
            JSONObject content = new JSONObject(pageSourceCode);
            JSONObject main = content.getJSONObject("main");
            result = "Temp: " + main.getString("temp") + " Umiditate: " + main.getString("humidity");
            */

            // --- VARIANTA 4: AUTOCOMPLETE GOOGLE ---
//            HttpClient httpClient = new DefaultHttpClient();
//            String url = "https://www.google.com/complete/search?client=chrome&q=" + p1;
//            String pageSourceCode = httpClient.execute(new HttpGet(url), new BasicResponseHandler());
//            JSONArray responseArray = new JSONArray(pageSourceCode);
//            JSONArray suggestions = responseArray.getJSONArray(1);
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < suggestions.length(); i++) sb.append(suggestions.getString(i)).append(",");
//            result = sb.toString();


            // ================================================================================

            // Daca nu am calculat nimic (poate a picat alt subiect si am uitat sa decomentam), evitam null
            if (result.isEmpty()) result = "Comanda primita: " + p1;

            // Trimitem raspunsul inapoi la client
            pw.println(result);
            socket.close();

        } catch (Exception ioException) {
            Log.e("PracticalTest02", "Error: " + ioException.getMessage());
        }
    }
}