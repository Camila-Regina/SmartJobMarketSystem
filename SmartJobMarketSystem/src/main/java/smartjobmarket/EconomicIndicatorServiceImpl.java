/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author camilareginadasilva
 */

package smartjobmarket;

import generated.grpc.economicIndicator.EconomicIndicatorServiceGrpc;
import generated.grpc.economicIndicator.IndicatorRequest;
import generated.grpc.economicIndicator.IndicatorResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EconomicIndicatorServiceImpl extends EconomicIndicatorServiceGrpc.EconomicIndicatorServiceImplBase {

    private static final Logger logger = Logger.getLogger(EconomicIndicatorServiceImpl.class.getName());
    private final Map<String, IndicatorResponse> indicatorDatabase = new HashMap<>();

    public EconomicIndicatorServiceImpl() {
        indicatorDatabase.put("dublin", IndicatorResponse.newBuilder()
                .setRegion("Dublin")
                .setUnemploymentRate(4.2f)
                .setAverageSalary(55000.0)
                .setGdpGrowth(3.5f)
                .setTimestamp(getCurrentTimestamp())
                .build());

        indicatorDatabase.put("cork", IndicatorResponse.newBuilder()
                .setRegion("Cork")
                .setUnemploymentRate(5.1f)
                .setAverageSalary(45000.0)
                .setGdpGrowth(2.8f)
                .setTimestamp(getCurrentTimestamp())
                .build());

        indicatorDatabase.put("galway", IndicatorResponse.newBuilder()
                .setRegion("Galway")
                .setUnemploymentRate(6.0f)
                .setAverageSalary(42000.0)
                .setGdpGrowth(2.5f)
                .setTimestamp(getCurrentTimestamp())
                .build());

        indicatorDatabase.put("limerick", IndicatorResponse.newBuilder()
                .setRegion("Limerick")
                .setUnemploymentRate(5.5f)
                .setAverageSalary(43000.0)
                .setGdpGrowth(2.6f)
                .setTimestamp(getCurrentTimestamp())
                .build());
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Unary RPC - Get a snapshot of economic indicators for a region
    @Override
    public void getIndicatorSnapshot(IndicatorRequest request, StreamObserver<IndicatorResponse> responseObserver) {
        System.out.println("getIndicatorSnapshot called for region: " + request.getRegion());

        IndicatorResponse indicator = indicatorDatabase.get(request.getRegion().toLowerCase());

        if (indicator != null) {
            responseObserver.onNext(indicator);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No data found for region: " + request.getRegion())
                    .asRuntimeException());
        }
    }

    // Bidirectional Streaming RPC - Monitor indicators for multiple regions
    @Override
    public StreamObserver<IndicatorRequest> monitorIndicators(StreamObserver<IndicatorResponse> responseObserver) {
        System.out.println("monitorIndicators called");

        return new StreamObserver<IndicatorRequest>() {
            @Override
            public void onNext(IndicatorRequest request) {
                System.out.println("Monitoring region: " + request.getRegion());

                IndicatorResponse indicator = indicatorDatabase.get(request.getRegion().toLowerCase());

                if (indicator != null) {
                    // Send updated response with current timestamp
                    IndicatorResponse updated = indicator.toBuilder()
                            .setTimestamp(getCurrentTimestamp())
                            .build();
                    responseObserver.onNext(updated);
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("No data found for region: " + request.getRegion())
                            .asRuntimeException());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error in monitorIndicators: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
                System.out.println("monitorIndicators completed");
            }
        };
    }
}