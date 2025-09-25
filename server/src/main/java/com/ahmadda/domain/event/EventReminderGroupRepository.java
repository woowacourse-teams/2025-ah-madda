package com.ahmadda.domain.event;

import com.ahmadda.domain.organization.OrganizationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventReminderGroupRepository extends JpaRepository<EventReminderGroup, Long> {

    @Query("""
                select g
                from EventReminderGroup erg
                join erg.group g
                where erg.event = :event
            """)
    List<OrganizationGroup> findGroupsByEvent(final Event event);
}
