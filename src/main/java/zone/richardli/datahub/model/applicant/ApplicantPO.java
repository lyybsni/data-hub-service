package zone.richardli.datahub.model.applicant;

import lombok.Data;

@Data
public class ApplicantPO {

    /**
     * ID of the applicant. Unique.
     */
    private String applicantId;

    /**
     * Full name of the applicant.
     * (First Name, Last Name) => First Name + Last Name
     */
    private String fullName;

    /**
     * Converted gender type.
     * Integer => Enum
     */
    private String gender;

    /**
     * Converted mark average.
     * [Credits, Percentage] => Weighted Average
     */
    private Float academicMark;

}
