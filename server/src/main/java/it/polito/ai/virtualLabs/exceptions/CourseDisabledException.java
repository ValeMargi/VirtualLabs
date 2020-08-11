package it.polito.ai.virtualLabs.exceptions;

public class CourseDisabledException extends VLServiceException {
    public CourseDisabledException() {
        super("Course disabled");
    }
}
