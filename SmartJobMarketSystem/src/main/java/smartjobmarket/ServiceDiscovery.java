/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author camilareginadasilva
 */

package smartjobmarket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class ServiceDiscovery {

    private String requiredServiceType;
    private String requiredServiceName;
    private ServiceInfo foundService;
    private JmDNS jmdns;

    public ServiceDiscovery(String inServiceType, String inServiceName) {
        requiredServiceType = inServiceType;
        requiredServiceName = inServiceName;
    }

    public ServiceInfo discoverService(long timeoutMilliseconds) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            System.out.println("Client: InetAddress.getLocalHost(): " + InetAddress.getLocalHost());

            jmdns.addServiceListener(requiredServiceType, new ServiceListener() {
                @Override
                public void serviceAdded(ServiceEvent event) {
                    System.out.println("Service added: " + event.getInfo());
                }

                @Override
                public void serviceRemoved(ServiceEvent event) {
                    System.out.println("Service removed: " + event.getInfo());
                }

                @Override
                public void serviceResolved(ServiceEvent event) {
                    System.out.println("Service resolved: " + event.getInfo());
                    ServiceInfo serviceInfo = event.getInfo();
                    int port = serviceInfo.getPort();
                    String resolvedServiceName = serviceInfo.getName();
                    System.out.println("Service " + resolvedServiceName + " resolved at port: " + port);
                    foundService = serviceInfo;
                    latch.countDown();
                }
            });

        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        latch.await(timeoutMilliseconds, TimeUnit.MILLISECONDS);
        System.out.println("Discover Service returning: " + foundService);
        return foundService;
    }

    public void close() throws IOException {
        if (jmdns != null) {
            jmdns.close();
        }
    }
}