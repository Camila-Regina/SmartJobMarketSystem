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

public class JobListingServer {

    private static final Logger logger = Logger.getLogger(JobListingServer.class.getName());

    public static void main(String[] args) {
        JobListingServiceImpl service = new JobListingServiceImpl();
        int port = 50051;
        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(service)
                    .build()
                    .start();
            logger.info("JobListingServer started, listening on port " + port);
            System.out.println("JobListingServer started, listening on port " + port);

            // Register service with jmDNS
            ServiceRegistration registration = ServiceRegistration.getInstance();
            registration.registerService(
                    "_joblistingservice._tcp.local.",
                    "JobListingService",
                    port,
                    "Job Listing gRPC Service"
            );

            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}