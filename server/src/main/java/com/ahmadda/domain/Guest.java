package com.ahmadda.domain;


import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private OrganizationMember participant;

    private Guest(final Event event, final OrganizationMember participant) {
        validateEvent(event);
        validateParticipant(participant);

        this.event = event;
        this.participant = participant;
    }

    public static Guest create(final Event event, final OrganizationMember participant) {

        return new Guest(event, participant);
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "event는 null이 되면 안됩니다.");
    }

    private void validateParticipant(final OrganizationMember participant) {
        Assert.notNull(participant, "participant는 null이 되면 안됩니다.");
    }
}
