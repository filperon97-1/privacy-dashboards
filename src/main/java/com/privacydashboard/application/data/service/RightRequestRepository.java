package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public interface RightRequestRepository extends JpaRepository<RightRequest, UUID> {
    List<RightRequest> findAllByReceiver(User user);
    List<RightRequest> findAllBySenderAndHandled(User user, Boolean handled);

    @Modifying
    @Query("UPDATE RightRequest SET handled=:newHandled WHERE id=:id")
    @Transactional
    void changeHandled(@Param("id") UUID id, @Param("newHandled") Boolean newHandled);

}
