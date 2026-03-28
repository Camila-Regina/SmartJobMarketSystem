/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartjobmarket;

/**
 *
 * @author camilareginadasilva
 */

import generated.grpc.economicIndicator.EconomicIndicatorServiceGrpc;
import generated.grpc.economicIndicator.IndicatorRequest;
import generated.grpc.economicIndicator.IndicatorResponse;
import generated.grpc.jobListing.JobListingServiceGrpc;
import generated.grpc.jobListing.JobRequest;
import generated.grpc.jobListing.JobResponse;
import generated.grpc.jobListing.SearchRequest;
import generated.grpc.workerProfile.SkillRequest;
import generated.grpc.workerProfile.SkillSummaryResponse;
import generated.grpc.workerProfile.WorkerProfileServiceGrpc;
import generated.grpc.workerProfile.WorkerRequest;
import generated.grpc.workerProfile.WorkerResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jmdns.ServiceInfo;

public class SmartJobMarketClient {

    private static final Logger logger = Logger.getLogger(SmartJobMarketClient.class.getName());

    private ManagedChannel jobListingChannel;
    private ManagedChannel workerProfileChannel;
    private ManagedChannel economicIndicatorChannel;

    private JobListingServiceGrpc.JobListingServiceBlockingStub jobListingStub;
    private WorkerProfileServiceGrpc.WorkerProfileServiceBlockingStub workerProfileStub;
    private WorkerProfileServiceGrpc.WorkerProfileServiceStub workerProfileAsyncStub;
    private EconomicIndicatorServiceGrpc.EconomicIndicatorServiceBlockingStub economicIndicatorStub;
    private EconomicIndicatorServiceGrpc.EconomicIndicatorServiceStub economicIndicatorAsyncStub;

    public SmartJobMarketClient() {
        try {
            // Discover and connect to JobListingService
            ServiceDiscovery jobDiscovery = new ServiceDiscovery(
                    "_joblistingservice._tcp.local.", "JobListingService");
            ServiceInfo jobService = jobDiscovery.discoverService(5000);
            if (jobService != null) {
                int port = jobService.getPort();
                jobListingChannel = ManagedChannelBuilder.forAddress("localhost", port)
                        .usePlaintext().build();
                jobListingStub = JobListingServiceGrpc.newBlockingStub(jobListingChannel);
                System.out.println("Connected to JobListingService on port " + port);
            } else {
                jobListingChannel = ManagedChannelBuilder.forAddress("localhost", 50051)
                        .usePlaintext().build();
                jobListingStub = JobListingServiceGrpc.newBlockingStub(jobListingChannel);
                System.out.println("JobListingService not discovered, using default port 50051");
            }

            // Discover and connect to WorkerProfileService
            ServiceDiscovery workerDiscovery = new ServiceDiscovery(
                    "_workerprofileservice._tcp.local.", "WorkerProfileService");
            ServiceInfo workerService = workerDiscovery.discoverService(5000);
            if (workerService != null) {
                int port = workerService.getPort();
                workerProfileChannel = ManagedChannelBuilder.forAddress("localhost", port)
                        .usePlaintext().build();
                workerProfileStub = WorkerProfileServiceGrpc.newBlockingStub(workerProfileChannel);
                workerProfileAsyncStub = WorkerProfileServiceGrpc.newStub(workerProfileChannel);
                System.out.println("Connected to WorkerProfileService on port " + port);
            } else {
                workerProfileChannel = ManagedChannelBuilder.forAddress("localhost", 50052)
                        .usePlaintext().build();
                workerProfileStub = WorkerProfileServiceGrpc.newBlockingStub(workerProfileChannel);
                workerProfileAsyncStub = WorkerProfileServiceGrpc.newStub(workerProfileChannel);
                System.out.println("WorkerProfileService not discovered, using default port 50052");
            }

            // Discover and connect to EconomicIndicatorService
            ServiceDiscovery economicDiscovery = new ServiceDiscovery(
                    "_economicindicatorservice._tcp.local.", "EconomicIndicatorService");
            ServiceInfo economicService = economicDiscovery.discoverService(5000);
            if (economicService != null) {
                int port = economicService.getPort();
                economicIndicatorChannel = ManagedChannelBuilder.forAddress("localhost", port)
                        .usePlaintext().build();
                economicIndicatorStub = EconomicIndicatorServiceGrpc.newBlockingStub(economicIndicatorChannel);
                economicIndicatorAsyncStub = EconomicIndicatorServiceGrpc.newStub(economicIndicatorChannel);
                System.out.println("Connected to EconomicIndicatorService on port " + port);
            } else {
                economicIndicatorChannel = ManagedChannelBuilder.forAddress("localhost", 50053)
                        .usePlaintext().build();
                economicIndicatorStub = EconomicIndicatorServiceGrpc.newBlockingStub(economicIndicatorChannel);
                economicIndicatorAsyncStub = EconomicIndicatorServiceGrpc.newStub(economicIndicatorChannel);
                System.out.println("EconomicIndicatorService not discovered, using default port 50053");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error connecting to services: " + e.getMessage());
        }
    }

    // Unary RPC - Get Job by ID
    public String getJob(String jobId) {
        try {
            JobRequest request = JobRequest.newBuilder().setJobId(jobId).build();
            JobResponse response = jobListingStub.getJob(request);
            return "Job ID: " + response.getJobId() + "\n"
                    + "Title: " + response.getTitle() + "\n"
                    + "Company: " + response.getCompany() + "\n"
                    + "Location: " + response.getLocation() + "\n"
                    + "Salary: €" + response.getSalary() + "\n"
                    + "Description: " + response.getDescription();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return "Error: " + e.getStatus().getDescription();
        }
    }

    // Server Streaming RPC - Search Jobs
    public String searchJobs(String keyword, String location) {
        try {
            SearchRequest request = SearchRequest.newBuilder()
                    .setKeyword(keyword)
                    .setLocation(location)
                    .build();
            Iterator<JobResponse> responses = jobListingStub.searchJobs(request);
            StringBuilder result = new StringBuilder();
            while (responses.hasNext()) {
                JobResponse job = responses.next();
                result.append("Job ID: ").append(job.getJobId()).append("\n")
                        .append("Title: ").append(job.getTitle()).append("\n")
                        .append("Company: ").append(job.getCompany()).append("\n")
                        .append("Location: ").append(job.getLocation()).append("\n")
                        .append("Salary: €").append(job.getSalary()).append("\n")
                        .append("Description: ").append(job.getDescription()).append("\n\n");
            }
            return result.length() > 0 ? result.toString() : "No jobs found.";
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return "Error: " + e.getStatus().getDescription();
        }
    }

    // Unary RPC - Get Worker Profile
    public String getWorkerProfile(String workerId) {
        try {
            WorkerRequest request = WorkerRequest.newBuilder().setWorkerId(workerId).build();
            WorkerResponse response = workerProfileStub.getWorkerProfile(request);
            return "Worker ID: " + response.getWorkerId() + "\n"
                    + "Name: " + response.getName() + "\n"
                    + "Age: " + response.getAge() + "\n"
                    + "Email: " + response.getEmail() + "\n"
                    + "Employability Score: " + response.getEmployabilityScore();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return "Error: " + e.getStatus().getDescription();
        }
    }

    // Client Streaming RPC - Upload Skills
    public String uploadSkills(String skillsInput) {
        final StringBuilder result = new StringBuilder();
        final CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<SkillSummaryResponse> responseObserver = new StreamObserver<SkillSummaryResponse>() {
            @Override
            public void onNext(SkillSummaryResponse response) {
                result.append("Total Skills: ").append(response.getTotalSkills()).append("\n")
                        .append("Employability Score: ").append(response.getEmployabilityScore());
            }

            @Override
            public void onError(Throwable t) {
                result.append("Error: ").append(t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<SkillRequest> requestObserver = workerProfileAsyncStub.uploadSkills(responseObserver);
        String[] skills = skillsInput.split(",");
        for (String skill : skills) {
            String[] parts = skill.trim().split(":");
            if (parts.length == 2) {
                String skillName = parts[0].trim();
                int level = Integer.parseInt(parts[1].trim());
                requestObserver.onNext(SkillRequest.newBuilder()
                        .setSkillName(skillName)
                        .setProficiencyLevel(level)
                        .build());
            }
        }
        requestObserver.onCompleted();

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result.length() > 0 ? result.toString() : "No skills processed.";
    }

    // Unary RPC - Get Economic Indicator Snapshot
    public String getIndicatorSnapshot(String region) {
        try {
            IndicatorRequest request = IndicatorRequest.newBuilder().setRegion(region).build();
            IndicatorResponse response = economicIndicatorStub.getIndicatorSnapshot(request);
            return "Region: " + response.getRegion() + "\n"
                    + "Unemployment Rate: " + response.getUnemploymentRate() + "%\n"
                    + "Average Salary: €" + response.getAverageSalary() + "\n"
                    + "GDP Growth: " + response.getGdpGrowth() + "%\n"
                    + "Timestamp: " + response.getTimestamp();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return "Error: " + e.getStatus().getDescription();
        }
    }

    // Bidirectional Streaming RPC - Monitor Indicators
    public String monitorIndicators(String regionsInput) {
        final StringBuilder result = new StringBuilder();
        final CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<IndicatorResponse> responseObserver = new StreamObserver<IndicatorResponse>() {
            @Override
            public void onNext(IndicatorResponse response) {
                result.append("Region: ").append(response.getRegion()).append("\n")
                        .append("Unemployment Rate: ").append(response.getUnemploymentRate()).append("%\n")
                        .append("Average Salary: €").append(response.getAverageSalary()).append("\n")
                        .append("GDP Growth: ").append(response.getGdpGrowth()).append("%\n")
                        .append("Timestamp: ").append(response.getTimestamp()).append("\n\n");
            }

            @Override
            public void onError(Throwable t) {
                result.append("Error: ").append(t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<IndicatorRequest> requestObserver = economicIndicatorAsyncStub.monitorIndicators(responseObserver);
        String[] regions = regionsInput.split(",");
        for (String region : regions) {
            requestObserver.onNext(IndicatorRequest.newBuilder()
                    .setRegion(region.trim())
                    .build());
        }
        requestObserver.onCompleted();

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result.length() > 0 ? result.toString() : "No data found.";
    }
}