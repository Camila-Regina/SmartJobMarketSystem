/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author camilareginadasilva
 */

package smartjobmarket;

import generated.grpc.workerProfile.WorkerProfileServiceGrpc;
import generated.grpc.workerProfile.WorkerRequest;
import generated.grpc.workerProfile.WorkerResponse;
import generated.grpc.workerProfile.SkillRequest;
import generated.grpc.workerProfile.SkillSummaryResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class WorkerProfileServiceImpl extends WorkerProfileServiceGrpc.WorkerProfileServiceImplBase {

    private static final Logger logger = Logger.getLogger(WorkerProfileServiceImpl.class.getName());
    private final Map<String, WorkerResponse> workerDatabase = new HashMap<>();

    public WorkerProfileServiceImpl() {
        workerDatabase.put("W001", WorkerResponse.newBuilder()
                .setWorkerId("W001")
                .setName("John Murphy")
                .setAge(28)
                .setEmail("john.murphy@email.com")
                .setEmployabilityScore(85.0f)
                .build());

        workerDatabase.put("W002", WorkerResponse.newBuilder()
                .setWorkerId("W002")
                .setName("Sarah O'Brien")
                .setAge(32)
                .setEmail("sarah.obrien@email.com")
                .setEmployabilityScore(90.0f)
                .build());

        workerDatabase.put("W003", WorkerResponse.newBuilder()
                .setWorkerId("W003")
                .setName("Liam Walsh")
                .setAge(24)
                .setEmail("liam.walsh@email.com")
                .setEmployabilityScore(70.0f)
                .build());
    }

    // Unary RPC - Get a specific worker profile by ID
    @Override
    public void getWorkerProfile(WorkerRequest request, StreamObserver<WorkerResponse> responseObserver) {
        System.out.println("getWorkerProfile called with ID: " + request.getWorkerId());

        WorkerResponse worker = workerDatabase.get(request.getWorkerId());

        if (worker != null) {
            responseObserver.onNext(worker);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Worker with ID " + request.getWorkerId() + " not found")
                    .asRuntimeException());
        }
    }

    // Client Streaming RPC - Upload multiple skills for a worker
    @Override
    public StreamObserver<SkillRequest> uploadSkills(StreamObserver<SkillSummaryResponse> responseObserver) {
        System.out.println("uploadSkills called");

        return new StreamObserver<SkillRequest>() {
            private final List<SkillRequest> skills = new ArrayList<>();

            @Override
            public void onNext(SkillRequest skillRequest) {
                System.out.println("Received skill: " + skillRequest.getSkillName()
                        + " level: " + skillRequest.getProficiencyLevel());
                skills.add(skillRequest);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error receiving skills: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                int totalSkills = skills.size();
                float totalScore = 0;
                for (SkillRequest skill : skills) {
                    totalScore += skill.getProficiencyLevel();
                }
                float employabilityScore = totalSkills > 0 ? (totalScore / totalSkills) * 20 : 0;

                SkillSummaryResponse summary = SkillSummaryResponse.newBuilder()
                        .setTotalSkills(totalSkills)
                        .setEmployabilityScore(employabilityScore)
                        .build();

                responseObserver.onNext(summary);
                responseObserver.onCompleted();
                System.out.println("Skills upload completed. Total: " + totalSkills
                        + " Score: " + employabilityScore);
            }
        };
    }
}