/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author camilareginadasilva
 */

package smartjobmarket;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;

public class WorkerProfileServer {

    private static final Logger logger = Logger.getLogger(WorkerProfileServer.class.getName());

    public static void main(String[] args) {
        WorkerProfileServiceImpl service = new WorkerProfileServiceImpl();
        int port = 50052;
        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(service)
                    .build()
                    .start();
            logger.info("WorkerProfileServer started, listening on port " + port);
            System.out.println("WorkerProfileServer started, listening on port " + port);

            // Register service with jmDNS
            ServiceRegistration registration = ServiceRegistration.getInstance();
            registration.registerService(
                    "_workerprofileservice._tcp.local.",
                    "WorkerProfileService",
                    port,
                    "Worker Profile gRPC Service"
            );

            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}