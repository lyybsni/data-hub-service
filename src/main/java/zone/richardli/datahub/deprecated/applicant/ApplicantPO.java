package zone.richardli.datahub.deprecated.applicant;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Data;

import java.util.Date;

@Deprecated
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

    @Property
    private long phone;

    @Property
    private Education education;

    @Data
    @Embedded
    public static class Education {
        @Property
        private Date graduationDate;
    }

}


