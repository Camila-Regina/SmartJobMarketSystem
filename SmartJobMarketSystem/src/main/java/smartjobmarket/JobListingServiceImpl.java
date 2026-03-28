/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author camilareginadasilva
 */

package smartjobmarket;

import generated.grpc.jobListing.JobListingServiceGrpc;
import generated.grpc.jobListing.JobRequest;
import generated.grpc.jobListing.JobResponse;
import generated.grpc.jobListing.SearchRequest;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JobListingServiceImpl extends JobListingServiceGrpc.JobListingServiceImplBase {

    private static final Logger logger = Logger.getLogger(JobListingServiceImpl.class.getName());
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

    // Unary RPC - Get a specific job by ID
    @Override
    public void getJob(JobRequest request, StreamObserver<JobResponse> responseObserver) {
        System.out.println("getJob called with ID: " + request.getJobId());

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

        String keyword = request.getKeyword().toLowerCase();
        String location = request.getLocation().toLowerCase();
        boolean found = false;

        for (JobResponse job : jobDatabase) {
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
                    .withDescription("No jobs found for keyword: "
                            + request.getKeyword()
                            + " in location: " + request.getLocation())
                    .asRuntimeException());
        } else {
            responseObserver.onCompleted();
        }
    }
}