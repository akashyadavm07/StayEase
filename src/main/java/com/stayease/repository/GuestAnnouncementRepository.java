package com.stayease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stayease.model.GuestAnnouncement;

public interface GuestAnnouncementRepository extends JpaRepository<GuestAnnouncement, Long> {

    List<GuestAnnouncement> findByActiveTrueOrderByCreatedAtDesc();

    List<GuestAnnouncement> findAllByOrderByCreatedAtDesc();
}