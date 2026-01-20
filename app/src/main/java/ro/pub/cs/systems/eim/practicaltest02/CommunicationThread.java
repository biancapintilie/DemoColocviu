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
import cz.msebera.android.httpclient.client.ResponseHandler;
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

//@Override
//public void run() {
//    if (socket == null) return;
//    try {
//        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
//
//        String oras = br.readLine(); // p1
//        String informatieSolicitata = br.readLine(); // p2 (ex: "all", "temp")
//        String p3 = br.readLine(); // ignorat la acest subiect
//
//        if (oras == null || oras.isEmpty()) {
//            pw.println("Eroare: Orasul nu a fost specificat.");
//            socket.close();
//            return;
//        }
//
//        String result = "";
//
//        // 1. Verificăm în Cache (Cerința 3.a din subiect)
//        String dataDinCache = serverThread.getData(oras);
//        if (dataDinCache != null) {
//            Log.i("PracticalTest02", "[SERVER] Informația a fost găsită în cache.");
//            result = parseazaSauReturneaza(dataDinCache, informatieSolicitata);
//        } else {
//            // 2. Dacă nu e în cache, accesăm serviciul Internet (Cerința 3.b) [cite: 53]
//            Log.i("PracticalTest02", "[SERVER] Accesare serviciu Internet pentru: " + oras);
//            try {
//                HttpClient httpClient = new DefaultHttpClient();
//                // Cheia și URL-ul din subiect [cite: 40, 41]
//                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + oras + "&appid=e03c3b32cfb5a6f7069f2ef29237d87e&units=metric";
//
//                HttpGet httpGet = new HttpGet(url);
//                ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                String pageSourceCode = httpClient.execute(httpGet, responseHandler);
//
//                // Salvăm în cache pentru viitor [cite: 43]
//                serverThread.setData(oras, pageSourceCode);
//
//                result = parseazaSauReturneaza(pageSourceCode, informatieSolicitata);
//            } catch (Exception e) {
//                Log.e("PracticalTest02", "Eroare la request HTTP: " + e.getMessage());
//                result = "Eroare la preluarea datelor de pe internet.";
//            }
//        }
//
//        // 3. Trimitem răspunsul către client [cite: 55]
//        pw.println(result);
//        socket.close();
//
//    } catch (IOException e) {
//        Log.e("PracticalTest02", "Error: " + e.getMessage());
//    }
//}
//
//    // Metodă utilă pentru a extrage doar ce cere userul (temp, wind, all etc.) [cite: 44, 48]
//    private String parseazaSauReturneaza(String jsonRaw, String info) {
//        if (info == null || info.equalsIgnoreCase("all")) {
//            return jsonRaw; // Returnăm tot dacă se cere "all" [cite: 48]
//        }
//        try {
//            JSONObject content = new JSONObject(jsonRaw);
//            if (info.equalsIgnoreCase("temperature")) {
//                return content.getJSONObject("main").getString("temp");
//            } else if (info.equalsIgnoreCase("humidity")) {
//                return content.getJSONObject("main").getString("humidity");
//            }
//            // Poți adăuga restul: wind_speed, condition, pressure [cite: 44]
//            return jsonRaw;
//        } catch (Exception e) {
//            return jsonRaw;
//        }
//    }
}