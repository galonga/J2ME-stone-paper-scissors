package game;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class BTClient implements DiscoveryListener {

    private LocalDevice localDevice;
    private DiscoveryAgent discoveryAgent;
    private UUID[] uuidSet;
    private BTStreamHandler handler;
    private int serverFound = 2;
    private int searchID;

    void findServer(BTStreamHandler handler) {

        this.handler = handler;

        try {
            this.localDevice = LocalDevice.getLocalDevice();
            this.discoveryAgent = localDevice.getDiscoveryAgent();

            // initialize some optimization variables
            this.uuidSet = new UUID[2];

            // ok, we are interesting in btspp services only
            this.uuidSet[0] = new UUID(0x1101);

            // and only known ones, that allows pictures
            this.uuidSet[1] = Constants.SAMPLE_SERVER_UUID;

            discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);

        } catch (Exception ex) {
            // something wrong - what's a pitty...
        }

    }

    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        try {
            // device found - go ahead
            // search services on this device
            searchID = discoveryAgent.searchServices(null, uuidSet, btDevice, this);
        } catch (BluetoothStateException ex) {
        }
    }

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        try {

            // got a number of service record - take first one
            String url = servRecord[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            StreamConnection conn = (StreamConnection) Connector.open(url);

            // ok, found a server - lets handle this connection

            this.handler.handleStream(conn.openDataInputStream(), conn.openDataOutputStream());
        } catch (Exception e) {
            // ignore
        }
    }

    public void serviceSearchCompleted(int transID, int respCode) {
        // nothing todo
    }

    public void inquiryCompleted(int discType) {
        if (searchID != 0) {
            serverFound = 1;
        } else {
            serverFound = 0;
        }
    }

    public int isServerFound() {
        return serverFound;
    }
}
