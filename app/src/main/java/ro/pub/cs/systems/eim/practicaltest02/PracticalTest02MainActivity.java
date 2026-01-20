package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.content.Context;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPort, clientAddress, clientPort;
    private EditText input1, input2, input3;
    private Button connectButton, actionButton;
    private TextView resultTextView;

    private ServerThread serverThread = null;
    private NsdManager nsdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        // Legam elementele din XML
        serverPort = findViewById(R.id.server_port);
        clientAddress = findViewById(R.id.client_address);
        clientPort = findViewById(R.id.client_port);
        input1 = findViewById(R.id.input1_edit_text);
        input2 = findViewById(R.id.input2_edit_text);
        input3 = findViewById(R.id.input3_edit_text);
        connectButton = findViewById(R.id.connect_button);
        actionButton = findViewById(R.id.action_button);
        resultTextView = findViewById(R.id.result_text_view);

        // BUTON START SERVER
        connectButton.setOnClickListener(v -> {
            String port = serverPort.getText().toString();
            if (port.isEmpty()) return;

            serverThread = new ServerThread(Integer.parseInt(port));
            serverThread.start();

            // Inregistram serviciul pt Wireshark
            registerService(Integer.parseInt(port));
            Toast.makeText(this, "Server Started!", Toast.LENGTH_SHORT).show();
        });

        // BUTON CLIENT
        actionButton.setOnClickListener(v -> {
            String addr = clientAddress.getText().toString();
            String port = clientPort.getText().toString();

            String p1 = input1.getText().toString();
            String p2 = input2.getText().toString();
            String p3 = input3.getText().toString();

            ClientThread clientThread = new ClientThread(addr, Integer.parseInt(port), p1, p2, p3, resultTextView);
            clientThread.start();
        });
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("TestEIM_Examen");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);

        nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
            @Override public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) { Log.d("EIM", "Service Registered"); }
            @Override public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) { Log.e("EIM", "Reg Failed"); }
            @Override public void onServiceUnregistered(NsdServiceInfo arg0) {}
            @Override public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {}
        });
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) serverThread.stopThread();
        if (nsdManager != null) nsdManager.unregisterService(null);
        super.onDestroy();
    }
}