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
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}