package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "atm_case", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ATMCase {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Integer id;

    @Column(name = "case_id", nullable = false)
    private Integer caseId;

    @Column(name = "atm_id", nullable = false)
    private String atmId;

    @Column(name = "cause")
    private String cause;

    @Column(name = "date_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateEnd;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "channel")
    private String channel;
}
