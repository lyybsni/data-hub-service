package zone.richardli.datahub.deprecated.applicant;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class only transfers data provided by the VO.
 */
@Data
@Deprecated
@EqualsAndHashCode(callSuper = true)
public class ApplicantDTO extends ApplicantPO {}
