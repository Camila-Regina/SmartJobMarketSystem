/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartjobmarket;

/**
 *
 * @author camilareginadasilva
 */

import generated.grpc.jobListing.JobListingServiceGrpc;
import generated.grpc.jobListing.JobRequest;
import generated.grpc.jobListing.JobResponse;
import generated.grpc.jobListing.SearchRequest;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JobListingServiceImpl extends JobListingServiceGrpc.JobListingServiceImplBase {

    private static final Logger logger = Logger.getLogger(JobListingServiceImpl.class.getName());

    // Metadata key for client ID
    public static final Metadata.Key<String> CLIENT_ID_KEY =
            Metadata.Key.of("client-id", Metadata.ASCII_STRING_MARSHALLER);

    private final List<JobResponse> jobDatabase = new ArrayList<>();

    public JobListingServiceImpl() {
        jobDatabase.add(JobResponse.newBuilder()
                .setJobId("J001")
                .setTitle("Software Engineer")
                .setCompany("TechCorp Ireland")
                .setLocation("Dublin")
                .setSalary(65000)
                .setDescription("Develop and maintain Java applications")
                .build());

        jobDatabase.add(JobResponse.newBuilder()
                .setJobId("J002")
                .setTitle("Data Analyst")
                .setCompany("DataSolutions Ltd")
                .setLocation("Cork")
                .setSalary(50000)
                .setDescription("Analyse data and generate reports")
                .build());

        jobDatabase.add(JobResponse.newBuilder()
                .setJobId("J003")
                .setTitle("Junior Software Developer")
                .setCompany("StartupHub")
                .setLocation("Dublin")
                .setSalary(42000)
                .setDescription("Build web applications using modern technologies")
                .build());

        jobDatabase.add(JobResponse.newBuilder()
                .setJobId("J004")
                .setTitle("Network Engineer")
                .setCompany("ConnectIreland")
                .setLocation("Galway")
                .setSalary(55000)
                .setDescription("Manage and maintain network infrastructure")
                .build());

        jobDatabase.add(JobResponse.newBuilder()
                .setJobId("J005")
                .setTitle("Software Engineer")
                .setCompany("GlobalTech")
                .setLocation("Limerick")
                .setSalary(60000)
                .setDescription("Design and develop distributed software systems")
                .build());
    }

    // Server Interceptor for Metadata
    public static class MetadataInterceptor implements ServerInterceptor {
        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
                ServerCall<ReqT, RespT> call,
                Metadata headers,
                ServerCallHandler<ReqT, RespT> next) {

            String clientId = headers.get(CLIENT_ID_KEY);
            if (clientId != null) {
                logger.info("Request received from client: " + clientId);
            } else {
                logger.info("Request received from unknown client");
            }
            return Contexts.interceptCall(Context.current(), call, headers, next);
        }
    }

    // Unary RPC - Get a specific job by ID
    @Override
    public void getJob(JobRequest request, StreamObserver<JobResponse> responseObserver) {
        System.out.println("getJob called with ID: " + request.getJobId());

        // Error handling - validate input
        if (request.getJobId() == null || request.getJobId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Job ID cannot be empty")
                    .asRuntimeException());
            return;
        }

        // Check if context is cancelled (deadline exceeded)
        if (Context.current().isCancelled()) {
            responseObserver.onError(Status.CANCELLED
                    .withDescription("Request was cancelled by the client")
                    .asRuntimeException());
            return;
        }

        JobResponse found = null;
        for (JobResponse job : jobDatabase) {
            if (job.getJobId().equals(request.getJobId())) {
                found = job;
                break;
            }
        }

        if (found != null) {
            responseObserver.onNext(found);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Job with ID " + request.getJobId() + " not found")
                    .asRuntimeException());
        }
    }

    // Server Streaming RPC - Search jobs by keyword and location
    @Override
    public void searchJobs(SearchRequest request, StreamObserver<JobResponse> responseObserver) {
        System.out.println("searchJobs called with keyword: " + request.getKeyword()
                + " location: " + request.getLocation());

        // Error handling - validate input
        if ((request.getKeyword() == null || request.getKeyword().isEmpty()) &&
            (request.getLocation() == null || request.getLocation().isEmpty())) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("At least one search parameter (keyword or location) must be provided")
                    .asRuntimeException());
            return;
        }

        String keyword = request.getKeyword().toLowerCase();
        String location = request.getLocation().toLowerCase();
        boolean found = false;

        for (JobResponse job : jobDatabase) {
            // Check if context is cancelled (deadline exceeded)
            if (Context.current().isCancelled()) {
                responseObserver.onError(Status.CANCELLED
                        .withDescription("Request was cancelled")
                        .asRuntimeException());
                return;
            }

            boolean matchesKeyword = keyword.isEmpty()
                    || job.getTitle().toLowerCase().contains(keyword)
                    || job.getDescription().toLowerCase().contains(keyword);

            boolean matchesLocation = location.isEmpty()
                    || job.getLocation().toLowerCase().contains(location);

            if (matchesKeyword && matchesLocation) {
                responseObserver.onNext(job);
                found = true;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!found) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No jobs found for keyword: " + request.getKeyword()
                            + " in location: " + request.getLocation())
                    .asRuntimeException());
        } else {
            responseObserver.onCompleted();
        }
    }
}