package com.hms.meenakshi.repository;

import com.hms.meenakshi.entity.VacatedResident;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacatedResidentRepository extends MongoRepository<VacatedResident, String> {

}
