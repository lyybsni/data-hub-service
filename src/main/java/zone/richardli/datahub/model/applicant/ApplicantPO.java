package zone.richardli.datahub.model.applicant;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Data;

@Data
@Entity("applicants")
public class ApplicantPO {

    /**
     * ID of the applicant. Unique.
     */
    @Id
    private String id;

    @Property
    private String applicantId;

    /**
     * Full name of the applicant.
     * (First Name, Last Name) => First Name + Last Name
     */
    @Property
    private String name;

    /**
     * Converted gender type.
     * Integer => Enum
     */
    @Property
    private String gender;

    /**
     * Converted mark average.
     * [Credits, Percentage] => Weighted Average
     */
    private Float academicMark;

}
