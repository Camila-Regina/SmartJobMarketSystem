package smartjobmarket;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;

public class EconomicIndicatorServer {

    private static final Logger logger = Logger.getLogger(EconomicIndicatorServer.class.getName());

    public static void main(String[] args) {
        EconomicIndicatorServiceImpl service = new EconomicIndicatorServiceImpl();
        int port = 50053;
        try {
            Server server = ServerBuilder.forPort(port)
                    .addService(service)
                    .build()
                    .start();
            logger.info("EconomicIndicatorServer started, listening on port " + port);
            System.out.println("EconomicIndicatorServer started, listening on port " + port);
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}