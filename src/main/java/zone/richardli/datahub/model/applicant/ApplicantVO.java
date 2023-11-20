package zone.richardli.datahub.model.applicant;

import lombok.Data;

/**
 * The input schema assumed to be provided by the external parties
 */
@Data
public class ApplicantVO {

    /**
     * ID of the applicant. Unique.
     */
    private String applicantId;

    /**
     * First name of the applicant.
     */
    private String firstName;

    /**
     * Last name of the applicant.
     */
    private String lastName;

    /**
     * Gender selection of the user.
     */
    private Integer gender;

    /**
     * The transcript input for the user.
     */
    private CourseInput[] courseInputs;

    @Data
    public static class CourseInput {
        /**
         * The name of the course.
         */
        private String courseName;

        /**
         * The credit of the user.
         */
        private int credit;

        /**
         * The mark of the course. Will be converted later.
         */
        private int percentage;
    }
}
