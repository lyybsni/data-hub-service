package zone.richardli.datahub.service;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.deprecated.applicant.ApplicantDTO;
import zone.richardli.datahub.deprecated.applicant.ApplicantPO;
import zone.richardli.datahub.deprecated.applicant.ApplicantVO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This service serves as a MongoDB read/writer using ODM.
 * With Datastore stabling the connection from back-end service with database.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ApplicantService implements POSaver<ApplicantPO> {

    private Datastore datastore;

    @Override
    public boolean batchInsertOrUpdate(List<ApplicantPO> pos) {
        pos.forEach(item -> {
            long count = datastore.find(ApplicantPO.class)
                    .filter(Filters.eq("applicantId", item.getApplicantId()))
                    .update(UpdateOperators.set(item))
                    .execute()
                    .getMatchedCount();

            if (count != 1) {
                datastore.save(item);
            }

            log.info("Modifying {}", item);
        });
        return true;
    }

    @Override
    public List<ApplicantPO> batchRead() {
        return StreamSupport
                .stream(datastore.find(ApplicantPO.class).spliterator(), true)
                .collect(Collectors.toList());
    }

    /**
     * Given an applicant input, convert it into storage form.
     * @return the data transferred object of applicant.
     */
    public ApplicantDTO convertApplicant(ApplicantVO vo) {
        ApplicantDTO dto = new ApplicantDTO();

        dto.setApplicantId(vo.getApplicantId());
        dto.setName(convertName(vo.getFirstName(), vo.getLastName()));
        dto.setGender(convertGender(vo.getGender()));
        dto.setAcademicMark(convertMark(vo.getCourseInputs()));

        return dto;
    }

    private String convertName(String firstName, String lastName) {
        return String.format("%s %s", firstName, lastName);
    }

    private String convertGender(Integer gender) {
         switch (gender) {
            case 1: return "Male";
            case 2: return "Female";
            case 3: return "Prefer not to say";
            default: throw new IllegalArgumentException("Not a legal gender index");
        }
    }

    private Float convertMark(ApplicantVO.CourseInput[] courseInputs) {
        int sumOfCredits = 0;
        int sumOfPercentages = 0;

        for (ApplicantVO.CourseInput input: courseInputs) {
            sumOfCredits += input.getCredit();
            sumOfPercentages += input.getPercentage() * input.getCredit();
        }

        return ((float) sumOfPercentages) / sumOfCredits;
    }

}
