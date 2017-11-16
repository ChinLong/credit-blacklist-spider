package org.chinlong.models

import org.chinlong.models.types.Sex
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user")
data class User(
        @Id
        @Column(name = "id", length = 32)
        val id: String,
        @Column(name = "name", length = 32, nullable = false)
        val name: String,
        @Column(name = "sex", nullable = false)
        val sex: Int = Sex.MALE.flg,
        @Column(name = "birthday", nullable = false)
        val birthday: LocalDate,
        @Column(name = "tel_no", length = 11, nullable = false)
        val telNo: String,
        @Column(name = "address", length = 6, nullable = false)
        val address: String,
        @Column(name = "money")
        val money: Int,
        @Column(name = "should_pay_at")
        val shouldPayAt: LocalDate
) {
    constructor() : this("", "", Sex.MALE.flg, LocalDate.of(1949, 10, 1), "", "", 0, LocalDate.now())
}