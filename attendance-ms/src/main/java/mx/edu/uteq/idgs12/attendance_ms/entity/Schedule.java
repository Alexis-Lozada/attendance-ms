package mx.edu.uteq.idgs12.attendance_ms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "schedules")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSchedule;

    @Column(nullable = false, length = 20)
    private String dayOfWeek;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Column(nullable = false, length = 100)
    private String classroom;

    @Column(nullable = false)
    private Integer idGroupCourse; // relación al grupo-curso
}
