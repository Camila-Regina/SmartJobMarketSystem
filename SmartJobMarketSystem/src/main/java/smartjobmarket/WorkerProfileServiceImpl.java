/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartjobmarket;

/**
 *
 * @author camilareginadasilva
 */

import generated.grpc.workerProfile.WorkerProfileServiceGrpc;
import generated.grpc.workerProfile.WorkerRequest;
import generated.grpc.workerProfile.WorkerResponse;
import generated.grpc.workerProfile.SkillRequest;
import generated.grpc.workerProfile.SkillSummaryResponse;
import io.grpc.Context;
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

        // Error handling - validate input
        if (request.getWorkerId() == null || request.getWorkerId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Worker ID cannot be empty")
                    .asRuntimeException());
            return;
        }

        // Check if context is cancelled
        if (Context.current().isCancelled()) {
            responseObserver.onError(Status.CANCELLED
                    .withDescription("Request was cancelled by the client")
                    .asRuntimeException());
            return;
        }

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
                // Error handling - validate proficiency level
                if (skillRequest.getProficiencyLevel() < 1 || skillRequest.getProficiencyLevel() > 5) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Proficiency level must be between 1 and 5")
                            .asRuntimeException());
                    return;
                }

                if (skillRequest.getSkillName() == null || skillRequest.getSkillName().isEmpty()) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("Skill name cannot be empty")
                            .asRuntimeException());
                    return;
                }

                System.out.println("Received skill: " + skillRequest.getSkillName()
                        + " level: " + skillRequest.getProficiencyLevel());
                skills.add(skillRequest);
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error receiving skills: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                if (skills.isEmpty()) {
                    responseObserver.onError(Status.INVALID_ARGUMENT
                            .withDescription("No skills were provided")
                            .asRuntimeException());
                    return;
                }

                int totalSkills = skills.size();
                float totalScore = 0;
                for (SkillRequest skill : skills) {
                    totalScore += skill.getProficiencyLevel();
                }
                float employabilityScore = (totalScore / totalSkills) * 20;

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