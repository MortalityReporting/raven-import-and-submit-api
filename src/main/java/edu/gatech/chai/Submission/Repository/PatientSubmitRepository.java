package edu.gatech.chai.Submission.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.gatech.chai.Submission.Entity.PatientIdentifier;
import edu.gatech.chai.Submission.Entity.PatientSubmit;

@Repository
public interface PatientSubmitRepository extends CrudRepository<PatientSubmit, PatientIdentifier>{
	List<PatientSubmit> findByPatientIdentifierSystemAndPatientIdentifierCode(String system, String code);
}
