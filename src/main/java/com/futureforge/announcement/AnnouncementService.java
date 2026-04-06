package com.futureforge.announcement;

import java.time.Instant;
import java.util.List;

import com.futureforge.common.ResourceNotFoundException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }

    public List<Announcement> findActive() {
        return announcementRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public Announcement getById(Long announcementId) {
        return announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));
    }

    public Announcement create(CreateAnnouncementDto dto) {
        Announcement announcement = new Announcement();

        announcement.setTitle(dto.title());
        announcement.setContent(dto.content());
        announcement.setActive(dto.active() == null || dto.active());

        Instant now = Instant.now();
        announcement.setCreatedAt(now);
        announcement.setUpdatedAt(now);

        return announcementRepository.save(announcement);
    }

    public Announcement update(Long announcementId, CreateAnnouncementDto dto) {
        Announcement announcement = getById(announcementId);

        announcement.setTitle(dto.title());
        announcement.setContent(dto.content());
        announcement.setActive(dto.active() == null || dto.active());

        announcement.setUpdatedAt(Instant.now());

        return announcementRepository.save(announcement);
    }

    public void delete(Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            throw new ResourceNotFoundException("Announcement not found");
        }
        announcementRepository.deleteById(announcementId);
    }
}