package com.zerobase.foodlier.module.member.chef.domain.model;

import com.zerobase.foodlier.common.jpa.audit.Audit;
import com.zerobase.foodlier.module.member.chef.type.GradeType;
import com.zerobase.foodlier.module.member.member.domain.model.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chef_member")
public class ChefMember extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Member member;
    private long exp;
    @Enumerated(EnumType.STRING)
    private GradeType gradeType;
    private String introduce;
    private double starAvg;
    private int starSum;
    private int reviewCount;

    public void plusExp(int exp){
        this.exp += exp;
    }

    public void plusStar(int star){
        this.starSum += star;
        this.reviewCount++;
        this.calcAvg();
    }

    private void calcAvg(){
        if(this.reviewCount == 0){
            this.starAvg = 0;
            return;
        }
        this.starAvg = (double) this.starSum / this.reviewCount;
    }

}