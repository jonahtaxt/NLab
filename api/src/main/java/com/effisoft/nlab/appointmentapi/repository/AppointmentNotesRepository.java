package com.effisoft.nlab.appointmentapi.repository;

import com.effisoft.nlab.appointmentapi.entity.AppointmentNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AppointmentNotesRepository extends JpaRepository<AppointmentNotes, Integer> {
    List<AppointmentNotes> findByAppointmentId(Integer appointmentId);
}
