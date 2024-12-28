package com.example.hms_fe1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.FeePayment;
import com.example.hms_fe1.entity.MappingEntity;
import com.example.hms_fe1.entity.Room;
import com.example.hms_fe1.entity.Student;
import com.example.hms_fe1.repository.FeePaymentRepository;
import com.example.hms_fe1.repository.RoomRepository;
import com.example.hms_fe1.repository.StudentRepository;

//@Service
//public class MappingService {
//	
//	@Autowired
//	private MappingRepository mappingRepository;
//
//	public void saveMappings(MappingEntity mappingEntity) {
//		mappingRepository.save(mappingEntity);
//	}
//}
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

@Service
public class MappingService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FeePaymentRepository feePaymentRepository;

    public void saveMappings(MappingEntity mappingEntity) {
        // Create Student entity
        Student student = new Student();
        student.setStu_id(mappingEntity.getStu_id());
        student.setStu_name(mappingEntity.getStu_name());
        student.setStu_phone(mappingEntity.getStu_phone());
        student.setStu_mail(mappingEntity.getStu_id().concat("@kluniversity.in"));

        // Create Room entity
        Room room = new Room();
        room.setRoom_number(mappingEntity.getRoom_number());
        room.setRoom_type(mappingEntity.getRoom_type());
        room.setStudent(student);

        // Decrement the room capacity
        room.decrementCapacity();

        // Create FeePayment entity
        FeePayment feePayment = new FeePayment();
        feePayment.setFee_amount(mappingEntity.getFee_amount());
        feePayment.setAmount_paid(mappingEntity.getAmount_paid());
        feePayment.setBalance_amount(mappingEntity.getFee_amount() - mappingEntity.getAmount_paid());
        feePayment.setLast_paid_date(mappingEntity.getLast_paid_date());
        feePayment.setNext_due_date(mappingEntity.getNext_due_date());
        feePayment.setStudent(student); // Set the student for fee payment

        // Save to repositories
        studentRepository.save(student);
        roomRepository.save(room);
        feePaymentRepository.save(feePayment);
    }

}
