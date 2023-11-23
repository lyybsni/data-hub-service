package zone.richardli.datahub.service;

import com.google.common.collect.ImmutableMap;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filter;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.model.applicant.ApplicantDTO;
import zone.richardli.datahub.model.applicant.ApplicantPO;
import zone.richardli.datahub.model.applicant.ApplicantVO;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ApplicantService implements POSaver<ApplicantPO> {

    private Datastore datastore;

    @Override
    public boolean batchInsertOrUpdate(List<ApplicantPO> pos) {
        pos.forEach(item -> {
            datastore.find(ApplicantPO.class)
                    .filter(Filters.eq("applicantId", item.getApplicantId()))
                    .update(UpdateOperators.set(item))
                    .execute();
            log.info("Modifying {}", item);
        });
        return true;
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
